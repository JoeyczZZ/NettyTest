package com.cmb.netty.gateWay.syncResponse;


import java.util.concurrent.Future;

public interface SyncWaitFuture<T> extends Future<T> {
    Throwable cause();

    void setMessage(T message);

    boolean isWriteSuccess();

    boolean setWriteSuccess(boolean writeSuccess);

    void setCause(Throwable cause);

    String requestId();
}
