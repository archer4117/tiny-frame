package com.example.wing.rpc.client;

import com.example.wing.rpc.common.RpcRequest;
import com.example.wing.rpc.common.RpcResponse;
import com.example.wing.rpc.register.RpcDiscover;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author qxs on 2019/3/4.
 * 动态代理类,用于获取到每个类的代理对象
 */
@Component
public class RpcProxy {

    @Resource
    private RpcDiscover rpcDiscover;

    @SuppressWarnings("all")
    public <T> T getInstance(Class<T> interfaceClass){
        T instance = (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //创建请求对象
                RpcRequest rpcRequest = new RpcRequest();
                //获取到被调用的类名 和RPC-Server中的serviceMap中的key进行匹配
                String className=method.getDeclaringClass().getName();
                //获取到方法的参数列表
                Class<?>[] parameterTypes = method.getParameterTypes();
                //生成一个请求的id
                rpcRequest.setRequestId(UUID.randomUUID().toString());
                rpcRequest.setClassName(className);//类名
                rpcRequest.setParameterTypes(parameterTypes);//参数类型列表
                rpcRequest.setParameters(args);//参数列表
                rpcRequest.setMethodName(method.getName());//调用的放方法名称
                System.out.println("client send rpc request :" +rpcRequest);
                RpcResponse rpcResponse = new RpcClient(rpcRequest, rpcDiscover).send();//创建一个RPCclient对象,并且发送消息到服务端
                //返回调用结果
                return rpcResponse.getResult();
            }
        });
        //返回一个代理对象
        return instance;
    }
}
