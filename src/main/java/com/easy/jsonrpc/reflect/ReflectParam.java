package com.easy.jsonrpc.reflect;

public class ReflectParam extends ReflectBase {
	public String parameterName;
	public Class parameterClass;

	public ReflectParam(String parameterName, Class parameterClass) {
		super();
		this.parameterName = parameterName;
		this.parameterClass = parameterClass;
	}

	public ReflectParam(String parameterName, Class parameterClass, boolean canNull) {
		super();
		this.parameterName = parameterName;
		this.parameterClass = parameterClass;
		this.paramCanNull = canNull;
	}

}
