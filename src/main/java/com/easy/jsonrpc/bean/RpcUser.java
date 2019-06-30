package com.easy.jsonrpc.bean;

import com.easy.jsonrpc.controller.RpcController;

public abstract class RpcUser {
	public RpcRequest request;
	public RpcNotice notice;
	public boolean responsed = false;
	public boolean isNotice;

	public abstract void send(RpcResponse response);

	public void doSend(RpcResponse rpcResponse) {
		if (!this.responsed && !this.isNotice) {
			this.responsed = true;
			rpcResponse.id = this.request.id;
			this.send(rpcResponse);
		}
	}

	public RpcResponse success() {
		return this.success(true);
	};

	public RpcResponse success(Object result) {
		RpcResponse response = new RpcResponse();
		response.result = result;
		this.doSend(response);
		return response;
	};

	public RpcResponse error() {
		return this.error(RpcError.InternalError);
	};

	public RpcResponse error(RpcError error) {
		RpcResponse response = new RpcResponse();
		response.error = error.toJson();
		this.doSend(response);
		return response;
	};

	public Object redirect(String method) throws Exception {
		return this.redirect(method, isNotice ? notice.params : request.params);
	}

	public Object redirect(String method, Object params) throws Exception {
		Object result = RpcController.requestAction(method, params, isNotice ? notice.methodType : request.methodType,
				this);
		return result;
	}

	public void call(RpcRequest request) {
		this.isNotice = false;
		try {
			this.request = request;
			Object result = RpcController.requestAction(request.method, request.params, request.methodType, this);
			this.success(result);
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof RpcError) {
				this.error((RpcError) e);
			} else {
				this.error();
			}
		}
	};

	public void notify(RpcNotice notice) {
		this.isNotice = true;
		try {
			this.notice = notice;
			RpcController.requestAction(notice.method, notice.params, request.methodType, this);
		} catch (Exception e) {
		}
	};

}
