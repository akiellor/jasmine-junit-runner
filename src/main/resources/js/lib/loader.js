(function(global){
  var context = org.mozilla.javascript.Context.getCurrentContext();

  var loader = new Packages.jasmine.rhino.Loader(global, context, __VIRTUAL_FILESYSTEM__);

  global.load = function(path){
    loader.loadFromVirtualFileSystem(path);
  }

  global.loadAll = function(path){
    loader.loadAllFromVirtualFileSystem(path);
  }
})(this)