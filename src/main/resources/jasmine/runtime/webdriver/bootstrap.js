jasmine.getEnv().addReporter(new jasmine.RestReporter());
jasmine.getEnv().addReporter(new jasmine.TrivialReporter());

var runner = jasmine.getEnv().currentRunner();

function toSpecJson(spec){
  return {
    id: spec.suite.id + "-" + spec.id,
    name: spec.description
  };
}

function toSuiteJson(suite){
  var parentSuite = suite.parentSuite;
  return {
    id: suite.id,
    name: suite.description,
    parentSuiteId: (parentSuite && parentSuite.id),
    specs: suite.specs().map(toSpecJson)
  };
}

var jsonRunner = {suites: runner.suites().map(toSuiteJson)};

var oReq = new XMLHttpRequest();
oReq.open("PUT", "http://localhost:9001/runner", false);
oReq.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
oReq.send(JSON.stringify(jsonRunner));

var oReq = new XMLHttpRequest();
oReq.open("PUT", "http://localhost:9001/lifecycle", false);
oReq.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
oReq.send(JSON.stringify({state: "Initialized"}));

jasmine.getEnv().execute();
