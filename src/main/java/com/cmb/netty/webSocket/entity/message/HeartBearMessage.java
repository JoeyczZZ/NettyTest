package com.cmb.netty.webSocket.entity.message;

public class HeartBearMessage {
    private String responseType;

    public HeartBearMessage(String responseType) {
        this.responseType = responseType;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String responseType;

        public Builder() {
        }

        public Builder responseType(String responseType) {
            this.responseType = responseType;
            return this;
        }

        public HeartBearMessage build() {
            return new HeartBearMessage(responseType);
        }
    }
}
