package online.stringtek.distributed.toy.rpc.core.common;

import lombok.Data;

@Data
public class RpcRequest<T> {

    /**
     * 请求对象的ID
     */

    private String requestId;

    /**
     * 类名
     */

    private Class<T> clazz;

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
