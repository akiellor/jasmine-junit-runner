jasmine.ExpectationResult = function(params) {
  this.type = 'expect';
  this.matcherName = params.matcherName;
  this.passed_ = params.passed;
  this.expected = params.expected;
  this.actual = params.actual;
  this.message = this.passed_ ? 'Passed.' : params.message;

  var trace = params.trace;

  try {
    LKJHLKJHLKJH
  }catch(e){
    var sb = java.lang.StringBuilder()
    e.rhinoException.getScriptStack().forEach(function(frame){
      frame.renderJavaStyle(sb);
      sb.append("\n")
    });
    var exception = new Error(this.message);
    exception.stack = sb.toString();
    trace = trace || exception;
  }

  this.trace = this.passed_ ? '' : trace;
};

jasmine.ExpectationResult.prototype.toString = function () {
  return this.message;
};

jasmine.ExpectationResult.prototype.passed = function () {
  return this.passed_;
};
