package com.cmb.netty.webSocket;

public class ResponseMessage {
    private String entCode;

    private String type;

    private String channelName;

    private String responseType;

    private String dataType;

    private Object data;

    public String getEntCode() {
        return entCode;
    }

    public void setEntCode(String entCode) {
        this.entCode = entCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    private ResponseMessage(String entCode, String type, String channelName, String responseType, String dataType, Object data) {
        this.entCode = entCode;
        this.type = type;
        this.channelName = channelName;
        this.responseType = responseType;
        this.dataType = dataType;
        this.data = data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String entCode;

        private String type;

        private String channelName;

        private String responseType;

        private String dataType;

        private Object data;

        public Builder() {
        }

        public Builder entCode(String entCode) {
            this.entCode = entCode;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder channelName(String channelName) {
            this.channelName = channelName;
            return this;
        }

        public Builder responseType(String responseType) {
            this.responseType = responseType;
            return this;
        }

        public Builder dataType(String dataType) {
            this.dataType = dataType;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public ResponseMessage build() {
            return new ResponseMessage(entCode, type, channelName, responseType, dataType, data);
        }
    }
}
