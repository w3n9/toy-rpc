package online.stringtek.distributed.toy.rpc.demo.consumer.core.common;

import lombok.Data;

@Data
public class RpcRequest {

    /**
     * 请求对象的ID
     */

    private String requestId;

    /**
     * 类名
     */

    private Class<?> clazz;

    /**
     * 方法名
     */

    private String methodName;

    /**
     * 参数类型
     */

    private Class<?>[] parameterTypes;

    /**
     * 入参
     */

    private Object[] parameters;


}
