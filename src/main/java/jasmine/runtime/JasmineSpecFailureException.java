package jasmine.runtime;

import org.mozilla.javascript.NativeObject;

public class JasmineSpecFailureException extends Exception {
    private final NativeObject result;

    public JasmineSpecFailureException(NativeObject specResultItem) {
        this.result = specResultItem;
	}

	@Override
	public String getMessage() {
		return String.valueOf(result.get("message", result));
	}

}
