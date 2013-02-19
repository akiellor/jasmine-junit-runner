(function(global){
  var context = org.mozilla.javascript.Context.getCurrentContext();

  var loader = new Packages.be.klak.junit.jasmine.Loader(global, context);

  global.load = function(path){
    loader.load(path);
  }
})(this)