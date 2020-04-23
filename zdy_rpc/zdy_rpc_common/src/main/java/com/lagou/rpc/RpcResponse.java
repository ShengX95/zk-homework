package com.lagou.rpc;

/**
 * @author shengx
 * @date 2020/4/14 22:02
 */
public class RpcResponse {
    /**
     * 请求对象的ID
     */
    private String requestId;
    /**
     * 返回结果
     */
    private Object result;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "requestId='" + requestId + '\'' +
                ", result=" + result +
                '}';
    }
}
