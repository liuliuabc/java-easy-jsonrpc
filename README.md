# java-easy-jsonrpc

支持http和websocket的jsonrpc工具库

## 安装步骤：

### 安装
```bash
maven或者直接引入jar
```

## 使用

### 初始化jsonrpc引擎
```java
//最好在你的web应用初始化时候调用该方法，将会遍历path下的路由文件
  try {
				RpcController.init("com.jsonrpc.action");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
  }
  //init 第一个参数传入路由文件目录  第二个参数：接受的params默认类型（参数校验）

```

### action文件编写
```java
//注解不传参数就以类名或方法名为标准
package com.jsonrpc.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easy.jsonrpc.annotation.Action;
import com.easy.jsonrpc.annotation.AddRule;
import com.easy.jsonrpc.annotation.CanNull;
import com.easy.jsonrpc.annotation.Method;
import com.easy.jsonrpc.annotation.Rule;
import com.easy.jsonrpc.bean.MethodType;
import com.easy.jsonrpc.bean.RpcUser;
import com.easy.jsonrpc.controller.BaseController;
import com.jsonrpc.util.StringUtil;
@Method(MethodType.Get)
@Method(MethodType.Post)
@CanNull
public class Test extends BaseController {
	@AddRule("auth")
	@Action
	public Object login(JSONObject params,RpcUser user){
		return params;
	}
	@Action
	public Object login2(JSONArray params,RpcUser user){
		return params;
	}
	@Action
	public Object login3(long params,RpcUser user) {
		return params;
	}
	@Action
	public Object login4(int params,RpcUser user) {
		return params;
	}
	@Action
	public Object login5(Boolean params,RpcUser user) {
		return params;
	}
	@Action
	public Object login6(String params,RpcUser user){
		return params;
	}
	@Action
	public Object login7(String name,RpcUser user){
		return name;
	}
	@Rule
	public void auth( String name,RpcUser user) {
		System.out.println(name);
	}
}


```

### 相关http/websocket请求转发到路由

```javascript
  RpcRequest rpcRequest = this.parseRequest(request);
		new RpcUser() {
			@Override
			public void send(RpcResponse result) {
				// 返回结果根据实际情况更改
				try {
					response.setContentType("text/json;charset=UTF-8");
					response.setCharacterEncoding("UTF-8");
					PrintWriter out = response.getWriter();
					String json = JSONObject.toJSONString(result);
					out.write(json);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}.call(rpcRequest);

```