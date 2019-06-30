package com.easy.jsonrpc.reflect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import com.easy.jsonrpc.annotation.Action;
import com.easy.jsonrpc.annotation.AddPublicRule;
import com.easy.jsonrpc.annotation.AddRule;
import com.easy.jsonrpc.annotation.CanNull;
import com.easy.jsonrpc.annotation.PublicRule;
import com.easy.jsonrpc.annotation.Rule;
import com.easy.jsonrpc.bean.MethodType;
import com.easy.jsonrpc.controller.RpcController;
import com.easy.jsonrpc.util.StringUtil;

public class ReflectInstance<T> extends ReflectBase {
	public T instance;
	public HashMap<String, ReflectMethod> actions = new HashMap();
	public HashMap<String, ReflectMethod> rules = new HashMap();
	public ArrayList<String> addPublicRules = new ArrayList();
	public ArrayList<String> addRules = new ArrayList();
	public HashMap<String, ReflectMethod> allRules = new HashMap();

	public ReflectInstance(T instance) {
		super();
		this.instance = instance;
		this.init();
		this.initAllRules();
	}

	public ReflectMethod getActionMethod(String name) {
		return this.actions.get(name);
	}

	public void initAllRules() {
		for (String ruleName : addPublicRules) {
			ReflectMethod reflectMethod = RpcController.publicRules.get(ruleName);
			if (reflectMethod == null) {
				throw new RuntimeException("no publicRule name:" + ruleName);
			}
			this.allRules.put(ruleName, reflectMethod);
		}
		for (String ruleName : addRules) {
			ReflectMethod reflectMethod = this.rules.get(ruleName);
			if (reflectMethod == null) {
				throw new RuntimeException("no rule name:" + ruleName);
			}
			this.allRules.put(ruleName, reflectMethod);
		}
		for (ReflectMethod method : actions.values()) {
			method.initAllRules();
		}
	}

	public void init() {
		Class c = this.instance.getClass();
		AddPublicRule[] addPublicRules = (AddPublicRule[]) c.getAnnotationsByType(AddPublicRule.class);
		for (AddPublicRule addPublicRule : addPublicRules) {
			this.addPublicRules.add("public-" + addPublicRule.value());
		}
		AddRule[] addRules = (AddRule[]) c.getAnnotationsByType(AddRule.class);
		for (AddRule addRule : addRules) {
			this.addRules.add(addRule.value());
		}
		CanNull canNull = (CanNull) c.getAnnotation(CanNull.class);
		if (canNull != null) {
			this.paramCanNull = canNull.value();
		}
		com.easy.jsonrpc.annotation.Method[] methodAnnos = (com.easy.jsonrpc.annotation.Method[]) c
				.getAnnotationsByType(com.easy.jsonrpc.annotation.Method.class);
		for (com.easy.jsonrpc.annotation.Method methodAnno : methodAnnos) {
			MethodType methodType = methodAnno.value();
			this.methodTypes.add(methodType);
		}

		Method[] methods = c.getMethods();
		for (Method method : methods) {
			ReflectMethod reflectMethod = new ReflectMethod(this, method);
			Action[] actions = method.getAnnotationsByType(Action.class);
			for (Action action : actions) {
				String actionName = action.value();
				if (StringUtil.isEmpty(actionName)) {
					actionName = method.getName();
				}
				this.actions.put(actionName.toLowerCase(), reflectMethod);
			}
			Rule[] rules = method.getAnnotationsByType(Rule.class);
			for (Rule rule : rules) {
				String ruleName = rule.value();
				if (StringUtil.isEmpty(ruleName)) {
					ruleName = method.getName();
				}
				this.rules.put(ruleName, reflectMethod);
			}
			PublicRule[] publicRules = method.getAnnotationsByType(PublicRule.class);
			for (PublicRule publicRule : publicRules) {
				String ruleName = publicRule.value();
				if (StringUtil.isEmpty(ruleName)) {
					ruleName = method.getName();
				}
				RpcController.publicRules.put("public-" + ruleName, reflectMethod);
			}
			reflectMethod.initAnno();
		}
	}
}
