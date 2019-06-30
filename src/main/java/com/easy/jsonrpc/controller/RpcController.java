package com.easy.jsonrpc.controller;

import java.util.HashMap;
import java.util.List;

import com.easy.jsonrpc.annotation.Controller;
import com.easy.jsonrpc.bean.MethodType;
import com.easy.jsonrpc.bean.RpcError;
import com.easy.jsonrpc.bean.RpcUser;
import com.easy.jsonrpc.reflect.ReflectInstance;
import com.easy.jsonrpc.reflect.ReflectMethod;
import com.easy.jsonrpc.util.PackageUtil;
import com.easy.jsonrpc.util.StringUtil;

public class RpcController {
	public static HashMap<String, ReflectInstance> controllers = new HashMap();
	public static HashMap<String, ReflectMethod> publicRules = new HashMap();

	public static void init(String packageName) throws Exception {
		List<Class<?>> list = PackageUtil.getClasses(packageName);
		for (Class controllerClass : list) {
			Controller[] controllerAnnos = (Controller[]) controllerClass.getAnnotationsByType(Controller.class);
			if (controllerAnnos.length > 0) {
				BaseController controller = (BaseController) controllerClass.newInstance();
				ReflectInstance reflectContoller = new ReflectInstance(controller);
				for (Controller controllerAnno : controllerAnnos) {
					String controllerName = controllerAnno.value();
					if (StringUtil.isEmpty(controllerName)) {
						controllerName = controllerClass.getSimpleName();
					}
					controllers.put(controllerName.toLowerCase(), reflectContoller);
				}
			}
		}
	}

	public static void init(String[] packageNames) throws Exception {
		for (String packageName : packageNames) {
			init(packageName);
		}
	}

	public static Object requestAction(String method, Object params, MethodType methodType, RpcUser user) {
		if (StringUtil.isEmpty(method)) {
			throw RpcError.MethodNotFound;
		}
		method = method.toLowerCase();
		if (method.charAt(0) + "" == "/") {
			method = method.substring(1);
		}
		if (method.charAt(method.length() - 1) + "" == "/") {
			method = method.substring(0, method.length() - 1);
		}
		String controllerName = "";
		String methodName = "";
		if (method.indexOf("/") >= 0) {
			controllerName = method.substring(0, method.lastIndexOf("/"));
			methodName = method.substring(method.lastIndexOf("/") + 1);
		} else {
			controllerName = method;
		}
		ReflectInstance<BaseController> reflectController = controllers.get(controllerName);
		if (reflectController != null) {
			return executeAction(reflectController, !StringUtil.isEmpty(methodName) ? methodName : "/", params,
					methodType, user);
		} else {
			throw RpcError.MethodNotFound;
		}
	};

	public static Object executeAction(ReflectInstance reflectController, String methodName, Object params,
			MethodType methodType, RpcUser user) {
		if (StringUtil.isEmpty(methodName) || methodName == "/") {
			methodName = "index";
		}
		ReflectMethod method = reflectController.getActionMethod(methodName);
		if (method == null) {
			method = reflectController.getActionMethod("default");
		}
		if (method == null) {
			throw RpcError.MethodNotFound;
		} else {
			if (method.methodTypes.contains(MethodType.All) || method.methodTypes.contains(methodType)) {
				return method.invoke(params, user);
			} else {
				throw RpcError.MethodNotFound;
			}
		}
	}
}
