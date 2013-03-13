function loadAll(regex){
  var oReq = new XMLHttpRequest();
  oReq.open("get", "http://localhost:9001/loadAll/" + regex.toString(), false);
  oReq.send();
  eval(oReq.responseText);
}

function load(path){
  var oReq = new XMLHttpRequest();
  oReq.open("get", "http://localhost:9001/vfs/" + path, false);
  oReq.send();
  eval(oReq.responseText);
}