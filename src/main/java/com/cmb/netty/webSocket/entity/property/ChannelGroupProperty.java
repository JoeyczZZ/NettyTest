package com.cmb.netty.webSocket.entity.property;

import com.cmb.netty.utils.StringUtils;

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

    @Override
    public String toString() {
        return "ChannelGroupProperty{" +
                "entCode='" + entCode + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public String toString2() {
        StringBuilder stringBuilder = new StringBuilder("ChannelGroupProperty{");

        if (StringUtils.isNotBlank(entCode)) {
            stringBuilder.append(" entCode='").append(entCode).append('\'');
        }
        if (StringUtils.isNotBlank(type)) {
            stringBuilder.append(" type='").append(type).append('\'');
        }
        stringBuilder.append('}');

        return stringBuilder.toString();
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
