package com.cmb.netty.gateWay.entity.body;

public class ProtocolHttpBody {
    private String to;
    private String method;
    private String path;

    public ProtocolHttpBody() {
    }

    private ProtocolHttpBody(String method, String path, String to) {
        this.method = method;
        this.path = path;
        this.to = to;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String method;
        private String path;
        private String to;

        public Builder() {
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder to(String to) {
            this.to = to;
            return this;
        }

        public ProtocolHttpBody build() {
            return new ProtocolHttpBody(method, path, to);
        }

    }
}
