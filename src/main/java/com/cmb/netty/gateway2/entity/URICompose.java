package com.cmb.netty.gateway2.entity;

import java.util.Objects;

public class URICompose {
    private String scheme;
    private String host;
    private Integer port;

    private URICompose() {
    }

    private URICompose(String scheme, String host, Integer port) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
    }

    @Override
    public String toString() {
        return scheme + "://" + host + ":" + port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        URICompose that = (URICompose) o;
        return Objects.equals(scheme, that.scheme) &&
                Objects.equals(host, that.host) &&
                Objects.equals(port, that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheme, host, port);
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String scheme;
        private String host;
        private Integer port;

        public Builder() {
        }

        public Builder scheme(String scheme) {
            this.scheme = scheme;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(Integer port) {
            this.port = port;
            return this;
        }

        public URICompose build() {
            return new URICompose(scheme, host, port);
        }
    }
}
