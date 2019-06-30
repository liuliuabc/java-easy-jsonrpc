package com.easy.jsonrpc.bean;
public class RpcResponse {
	public String jsonrpc = "2.0";
	public Object result;
	public Object error;
	public Object id;
	public RpcResponse() {}
	public RpcResponse(Object id,Object result) {
	  this.id = id;
	  this.result = result;
	}
	public RpcResponse(Object id,RpcError error) {
		  this.id = id;
		  this.error = error;
	}
}
