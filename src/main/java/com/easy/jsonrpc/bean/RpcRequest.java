package com.easy.jsonrpc.bean;

public class RpcRequest {
	public String jsonrpc = "2.0";
	public Object params;
	public String method;
	public MethodType methodType;
	public Object id;

	public RpcRequest() {
	}

	public RpcRequest(Object id, String method, Object params) {
		this.id = id;
		this.method = method;
		this.params = params;
	}

	public RpcRequest(Object id, String method, Object params, MethodType methodType) {
		this.id = id;
		this.method = method;
		this.params = params;
		this.methodType = methodType;
	}
}
