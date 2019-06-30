package com.easy.jsonrpc.bean;

public class RpcNotice {
	public String jsonrpc = "2.0";
	public Object params;
	public String method;
	public MethodType methodType;

	public RpcNotice() {
	}

	public RpcNotice(String method, Object params) {
		this.method = method;
		this.params = params;
	}

	public RpcNotice(String method, Object params, MethodType methodType) {
		this.method = method;
		this.params = params;
		this.methodType = methodType;
	}
}
