package com.easy.jsonrpc.bean;

import java.util.HashMap;
import java.util.Map;

public class RpcError extends RuntimeException{
	public static RpcError ParseError=new RpcError(-32700,"数据解析失败");
	public static RpcError InvalidRequest=new RpcError(-32600,"无效的请求");
	public static RpcError MethodNotFound=new RpcError(-32601,"找不到方法");
	public static RpcError InvalidParams=new RpcError(-32602,"无效或错误的的参数");
	public static RpcError InternalError=new RpcError(-32603,"服务器内部错误");
	public static RpcError RequestTimeout=new RpcError(-34567,"请求超时");
	public int code;
	public String message;
	public Object data;
	public RpcError(int code,String message) {
		super(message);
		this.code=code;
		this.message=message;
	}
	public RpcError(int code,String message,Object data) {
		super(message);
		this.code=code;
		this.message=message;
		this.data=data;
	}
	public Map<String, Object>  toJson(){
		HashMap<String, Object> map=new HashMap();
		map.put("code", code);
		map.put("message", message);
		if(data!=null) {
			map.put("data", data);
		}
        return map;
	}
}
