(function () {

  if (!jasmine) {
    throw new Exception("jasmine library does not exist in global namespace!");
  }

  var RestReporter = function () {
  };

  RestReporter.prototype = {
    reportRunnerStarting:function (runner) {
      var oReq = new XMLHttpRequest();
      oReq.open("POST", "http://localhost:9001/lifecycle", false);
      oReq.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
      oReq.send(JSON.stringify({state: "Ready"}));

      console.log(runner);
    },

    reportSpecStarting:function (spec) {
      console.log(spec);
    },

    reportSpecResults:function (spec) {
      var oReq = new XMLHttpRequest();
      oReq.open("PUT", "http://localhost:9001/notification", false);
      oReq.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
      oReq.send(JSON.stringify({spec: {id: spec.suite.id + "-" + spec.id, name: spec.description}, passed: spec.results().passed()}));

      console.log(spec);
    },

    reportSuiteResults:function (suite) {
      console.log(suite);
    },

    reportRunnerResults:function (runner) {
      var oReq = new XMLHttpRequest();
      oReq.open("PUT", "http://localhost:9001/lifecycle", false);
      oReq.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
      oReq.send(JSON.stringify({state: "Complete"}));

      console.log(runner);
    }
  };

  jasmine.RestReporter = RestReporter
})();
