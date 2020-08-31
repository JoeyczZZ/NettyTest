package com.cmb.netty.gateWay.syncResponse;

import com.cmb.netty.gateWay.dto.NettyMessageProto;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SyncWaitResponseFuture implements SyncWaitFuture<NettyMessageProto.NettyMessage> {
    private final CountDownLatch latch = new CountDownLatch(1);
    private NettyMessageProto.NettyMessage nettyMessage;
    private final String requestId;
    private boolean writeResult;
    private Throwable cause;

    public SyncWaitResponseFuture(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String requestId() {
        return requestId;
    }

    @Override
    public void setMessage(NettyMessageProto.NettyMessage message) {
        this.nettyMessage = message;
        latch.countDown();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public NettyMessageProto.NettyMessage get() throws InterruptedException, ExecutionException {
        latch.await();
        return nettyMessage;
    }

    @Override
    public NettyMessageProto.NettyMessage get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (latch.await(timeout, unit)) {
            return nettyMessage;
        }
        return null;
    }

    @Override
    public Throwable cause() {
        return cause;
    }

    @Override
    public boolean isWriteSuccess() {
        return writeResult;
    }

    @Override
    public boolean setWriteSuccess(boolean writeSuccess) {
        return this.writeResult = writeSuccess;
    }

    @Override
    public void setCause(Throwable cause) {
        this.cause = cause;
    }
}
