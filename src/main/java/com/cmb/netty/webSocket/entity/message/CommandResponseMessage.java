package com.cmb.netty.webSocket.entity.message;

public class CommandResponseMessage {
    private String responseType;

    public CommandResponseMessage(String responseType) {
        this.responseType = responseType;
    }

    public CommandResponseMessage() {
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }


    public static CommandResponseMessage.Builder builder() {
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

        public CommandResponseMessage build() {
            return new CommandResponseMessage(responseType);
        }
    }
}
