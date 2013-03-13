package jasmine.runtime.webdriver;

import com.google.common.base.Predicate;
import com.google.gson.Gson;
import jasmine.rhino.vfs.VirtualFileSystem;
import jasmine.runtime.Backend;
import jasmine.runtime.Configuration;
import jasmine.runtime.It;
import jasmine.runtime.Notifier;
import jasmine.utils.Exceptions;
import org.apache.commons.io.IOUtils;
import org.junit.runner.Description;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.reflections.vfs.Vfs;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.handler.exceptions.PrintStackTraceExceptionHandler;
import org.webbitserver.netty.NettyWebServer;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class WebDriverBackend implements Backend {
    private static Description rootDescription;
    private final VirtualFileSystem virtualFileSystem;
    private final SynchronisedBus<NotifierHandler.SpecResult> resultsBus;
    private final Lifecycle lifecycle;
    private final RemoteWebDriver webDriver;

    public WebDriverBackend(Configuration configuration, Description rootDescription) {
        this.rootDescription = rootDescription;
        this.virtualFileSystem = new VirtualFileSystem(configuration.getJavascriptPath(), new Predicate<Vfs.File>() {
            @Override public boolean apply(@Nullable Vfs.File input) {
                return input != null && (input.getRelativePath().endsWith("js") || input.getRelativePath().endsWith("html") || input.getRelativePath().endsWith("css"));
            }
        });
        this.lifecycle = new Lifecycle();

        try {
            resultsBus = new SynchronisedBus<NotifierHandler.SpecResult>();
            new NettyWebServer(9001)
                    .add("^/runner", new RunnerHandler(rootDescription))
                    .add("^/vfs/.*", new VirtualFileSystemHandler(virtualFileSystem, "/vfs/"))
                    .add("^/$", new JasmineHtmlHandler(new JasmineSpecRunnerGenerator(configuration)))
                    .add("^/notification", new NotifierHandler(resultsBus))
                    .add("^/lifecycle", new LifecycleHandler(lifecycle))
                    .uncaughtExceptionHandler(new PrintStackTraceExceptionHandler())
                    .start()
                    .get();
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }

        DesiredCapabilities capabilities = new DesiredCapabilities();
        try {
            webDriver = new RemoteWebDriver(new URL("http://localhost:9015"), capabilities);
        } catch (MalformedURLException e) {
            throw Exceptions.unchecked(e);
        }
        new Thread(new Runnable() {
            @Override public void run() {
               webDriver.get("http://localhost:9001/");
            }
        }).start();

        lifecycle.await(Lifecycle.State.Initialized);
    }

    @Override public Description getRootDescription() {
        return rootDescription;
    }

    @Override public void execute(final Notifier notifier) {
        resultsBus.register(new Handler<NotifierHandler.SpecResult>() {
            @Override public synchronized void handle(final NotifierHandler.SpecResult specResult) {
                It spec = new It() {
                    @Override public Description getDescription() {
                        return Description.createSuiteDescription(specResult.spec.getName(), specResult.spec.getId());
                    }
                };

                notifier.started(spec);
                if (specResult.passed) {
                    notifier.pass(spec);
                } else {
                    notifier.fail(spec);
                }
            }
        });

        lifecycle.set(Lifecycle.State.Ready);

        lifecycle.await(Lifecycle.State.Complete);

        webDriver.quit();
    }

    public static class JasmineHtmlHandler implements HttpHandler {
        private final JasmineSpecRunnerGenerator generator;

        public JasmineHtmlHandler(JasmineSpecRunnerGenerator generator) {
            this.generator = generator;
        }

        @Override public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            generator.generate(outputStream);
            response.status(200).content(outputStream.toByteArray()).end();
        }
    }

    public static class NotifierHandler implements HttpHandler {
        private final SynchronisedBus<SpecResult> bus;

        public NotifierHandler(SynchronisedBus<SpecResult> bus) {
            this.bus = bus;
        }

        @Override public synchronized void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
            final SpecResult specResult = new Gson().fromJson(request.body(), SpecResult.class);
            bus.dispatch(specResult);
            response.status(200).end();
        }

        public static class SpecResult {
            private final RunnerHandler.HttpSpec spec;
            private final boolean passed;

            public SpecResult(RunnerHandler.HttpSpec spec, boolean passed) {
                this.spec = spec;
                this.passed = passed;
            }
        }
    }

    public static class VirtualFileSystemHandler implements HttpHandler {
        private final VirtualFileSystem virtualFileSystem;
        private final String mountedPath;

        public VirtualFileSystemHandler(VirtualFileSystem virtualFileSystem, String mountedPath) {
            this.virtualFileSystem = virtualFileSystem;
            this.mountedPath = mountedPath;
        }

        @Override public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
            Vfs.File file = virtualFileSystem.find(request.uri().replaceFirst(mountedPath, ""));
            response.status(200).content(IOUtils.toString(file.openInputStream())).end();
        }
    }

    private static class SynchronisedBus<T> {
        private final Set<Handler<T>> handlers = new CopyOnWriteArraySet<Handler<T>>();

        public SynchronisedBus<T> register(Handler<T> handler) {
            handlers.add(handler);
            return this;
        }

        public void dispatch(T event) {
            for (Handler<T> handler : handlers) {
                handler.handle(event);
            }
        }
    }

    private static interface Handler<T> {
        void handle(T event);
    }

    private static class LifecycleHandler implements HttpHandler {
        private final Lifecycle lifecycle;

        public LifecycleHandler(Lifecycle lifecycle) {
            this.lifecycle = lifecycle;
        }

        @Override public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
            HttpState httpState = new Gson().fromJson(request.body(), HttpState.class);
            if ("PUT".equals(request.method())) {
                lifecycle.set(httpState.state);
                response.status(200).end();
            } else if ("POST".equals(request.method())) {
                lifecycle.await(httpState.state);
                response.status(200).end();
            } else {
                response.status(400).end();
            }
        }

        public static class HttpState {
            public final Lifecycle.State state;

            public HttpState(Lifecycle.State state) {
                this.state = state;
            }
        }
    }
}
