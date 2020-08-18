package com.cmb.netty.webSocket;

import java.util.Objects;

public class ChannelGroupProperty {
    private String entCode;

    private String type;

    private ChannelGroupProperty(String entCode, String type) {
        this.entCode = entCode;
        this.type = type;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelGroupProperty that = (ChannelGroupProperty) o;
        return Objects.equals(entCode, that.entCode) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entCode, type);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String entCode;

        private String type;

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

        public ChannelGroupProperty build() {
            return new ChannelGroupProperty(entCode, type);
        }
    }
}
