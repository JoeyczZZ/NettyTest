package com.cmb.netty.gateWay.dto;

public class ExceptionMessage {
    private String msg;

    private ExceptionMessage(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String msg;

        public Builder() {
        }

        public Builder msg(String msg) {
            this.msg = msg;
            return this;
        }

        public ExceptionMessage build() {
            return new ExceptionMessage(msg);
        }
    }
}
