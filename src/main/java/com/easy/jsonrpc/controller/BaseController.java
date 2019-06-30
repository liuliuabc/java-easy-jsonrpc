package com.easy.jsonrpc.controller;

import com.easy.jsonrpc.annotation.CanNull;
import com.easy.jsonrpc.annotation.Controller;
import com.easy.jsonrpc.annotation.Method;

@Controller
@CanNull(false)
@Method
public class BaseController {
	/*
	 * @Action("index") public Object onIndex(Object params,RpcUser user) throws
	 * Exception{ user.success("welcome you"); return null; };
	 * 
	 * @Action("default") public Object onDefault(Object params,RpcUser user) throws
	 * Exception{ throw RpcError.MethodNotFound; };
	 */
}
