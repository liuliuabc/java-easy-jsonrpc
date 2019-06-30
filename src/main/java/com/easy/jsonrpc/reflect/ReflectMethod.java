package com.easy.jsonrpc.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import com.alibaba.fastjson.JSONObject;
import com.easy.jsonrpc.annotation.AddPublicRule;
import com.easy.jsonrpc.annotation.AddRule;
import com.easy.jsonrpc.annotation.CanNull;
import com.easy.jsonrpc.annotation.OffPublicRule;
import com.easy.jsonrpc.annotation.OffRule;
import com.easy.jsonrpc.bean.MethodType;
import com.easy.jsonrpc.bean.RpcError;
import com.easy.jsonrpc.bean.RpcUser;
import com.easy.jsonrpc.controller.RpcController;
import com.thoughtworks.paranamer.AnnotationParanamer;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;

public class ReflectMethod extends ReflectBase {
	public static Paranamer paranamer = new CachingParanamer(new AnnotationParanamer(new BytecodeReadingParanamer()));
	Method method;
	ReflectInstance reflectClass;
	ArrayList<String> addPublicRules = new ArrayList();
	ArrayList<String> offPublicRules = new ArrayList();
	ArrayList<String> addRules = new ArrayList();
	ArrayList<String> offRules = new ArrayList();
	ArrayList<ReflectParam> parameters = new ArrayList();
	HashMap<String, ReflectMethod> allRules;

	public ReflectMethod(ReflectInstance reflectClass, Method method) {
		super();
		this.paramCanNull = reflectClass.paramCanNull;
		this.methodTypes = reflectClass.methodTypes;
		this.method = method;
		this.reflectClass = reflectClass;
	}
	/*
	 * public static <T> T cast(Class<T> clz, Object o) { if (clz.isInstance(o)) {
	 * return (T) o; } else if (o instanceof String) { String oString = (String) o;
	 * if (clz == int.class || clz == Integer.class) { return (T)
	 * Integer.valueOf(oString); } else if (clz == boolean.class || clz ==
	 * Boolean.class) { return (T) Boolean.valueOf(oString); } else if (clz ==
	 * short.class || clz == Short.class) { return (T) Short.valueOf(oString); }
	 * else if (clz == long.class || clz == Long.class) { return (T)
	 * Long.valueOf(oString); } } return null; }
	 */

	public Object invoke(Object params, RpcUser user) {
		try {
			if (allRules != null) {
				for (ReflectMethod ruleMethod : allRules.values()) {
					ruleMethod.invoke(params, user);
				}
			}
			Object[] args = new Object[parameters.size()];
			for (int i = 0; i < parameters.size(); i++) {
				ReflectParam parameter = parameters.get(i);
				Object param = null;
				if (parameter.parameterClass == RpcUser.class) {
					param = user;
				} else if (parameter.parameterName.equals("params")) {
					if (params == null && !parameter.paramCanNull) {
						throw RpcError.InvalidParams;
					}
					param = params;
					// param = ReflectMethod.cast(parameter.parameterClass, params);
					// param = ReflectMethod.cast(parameter.parameterClass, params);
				} else {
					if (params == null && !parameter.paramCanNull) {
						throw RpcError.InvalidParams;
					}
					if (params != null) {
						JSONObject jsonParams = (JSONObject) params;
						param = jsonParams.get(parameter.parameterName);
						// param = ReflectMethod.cast(parameter.parameterClass,
						// jsonParams.get(parameter.parameterName));
					}
				}
				if (param == null && !parameter.paramCanNull) {
					throw RpcError.InvalidParams;
				}
				args[i] = param;
			}
			return method.invoke(this.reflectClass.instance, args);
		} catch (IllegalAccessException e) {
			throw RpcError.InvalidParams;
		} catch (IllegalArgumentException e) {
			throw RpcError.InvalidParams;
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();// 获取目标异常
			if (t instanceof RpcError) {
				throw (RpcError) t;
			} else {
				throw RpcError.InvalidParams;
			}
		} catch (RpcError e) {
			throw e;
		} catch (Exception e) {
			throw RpcError.InvalidParams;
		}
	}

	public void initAllRules() {
		if (allRules == null) {
			allRules = (HashMap<String, ReflectMethod>) this.reflectClass.allRules.clone();
			for (String ruleName : addPublicRules) {
				ReflectMethod reflectMethod = RpcController.publicRules.get(ruleName);
				if (reflectMethod == null) {
					throw new RuntimeException("no publicRule name:" + ruleName);
				}
				allRules.put(ruleName, reflectMethod);
			}
			for (String ruleName : offPublicRules) {
				allRules.remove(ruleName);
			}
			for (String ruleName : addRules) {
				ReflectMethod reflectMethod = (ReflectMethod) this.reflectClass.rules.get(ruleName);
				if (reflectMethod == null) {
					throw new RuntimeException("no rule name:" + ruleName);
				}
				allRules.put(ruleName, reflectMethod);
			}
			for (String ruleName : offRules) {
				allRules.remove(ruleName);
			}
		}

	}

	public void initAnno() {
		CanNull methodCanNull = method.getAnnotation(CanNull.class);
		if (methodCanNull != null) {
			this.paramCanNull = methodCanNull.value();
		}
		AddPublicRule[] addPublicRules = method.getAnnotationsByType(AddPublicRule.class);
		for (AddPublicRule addPublicRule : addPublicRules) {
			this.addPublicRules.add("public-" + addPublicRule.value());
		}
		AddRule[] addRules = method.getAnnotationsByType(AddRule.class);
		for (AddRule addRule : addRules) {
			this.addRules.add(addRule.value());
		}
		OffPublicRule[] offPublicRules = method.getAnnotationsByType(OffPublicRule.class);
		for (OffPublicRule offPublicRule : offPublicRules) {
			this.offPublicRules.add("public-" + offPublicRule.value());
		}
		OffRule[] offRules = method.getAnnotationsByType(OffRule.class);
		for (OffRule offRule : offRules) {
			this.offRules.add(offRule.value());
		}
		try {
			String[] parameterNames = ReflectMethod.paranamer.lookupParameterNames(method); // throws
																							// ParameterNamesNotFoundException
			Class[] parameterClasses = method.getParameterTypes();
			boolean[] parameterCanNulls = new boolean[parameterNames.length];
			Annotation[][] an = method.getParameterAnnotations();
			for (int i = 0; i < parameterNames.length; i++) {
				boolean canNUll = this.paramCanNull;
				Annotation[] an1 = an[i];
				for (Annotation an2 : an1) {
					if (an2 instanceof CanNull) {
						canNUll = ((CanNull) an2).value();
						break;
					}
				}
				ReflectParam param = new ReflectParam(parameterNames[i], parameterClasses[i], canNUll);
				parameters.add(param);
			}
		} catch (Exception e) {
		}
		com.easy.jsonrpc.annotation.Method[] methodAnnos = method
				.getAnnotationsByType(com.easy.jsonrpc.annotation.Method.class);
		ArrayList<MethodType> newMethodTypes = new ArrayList();
		if (methodAnnos.length > 0) {
			for (com.easy.jsonrpc.annotation.Method methodAnno : methodAnnos) {
				MethodType methodType = methodAnno.value();
				newMethodTypes.add(methodType);
			}
			this.methodTypes = newMethodTypes;
		}

	}
}
