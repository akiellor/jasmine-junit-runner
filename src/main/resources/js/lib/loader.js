(function(global){
  var context = org.mozilla.javascript.Context.getCurrentContext();

  var loader = new Packages.be.klak.junit.jasmine.Loader(global, context, __VIRTUAL_FILESYSTEM__);

  global.load = function(path){
    loader.load(path);
  }
})(this)