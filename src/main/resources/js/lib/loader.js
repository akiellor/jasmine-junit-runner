(function(global){
  var context = org.mozilla.javascript.Context.getCurrentContext();

  var loader = new Packages.jasmine.runtime.rhino.Loader(global, context, __VIRTUAL_FILESYSTEM__);

  global.load = function(path){
    loader.loadFromVirtualFileSystem(path);
  }
})(this)