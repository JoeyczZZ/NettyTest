package com.cmb.netty.webSocket.entity.property;

import java.util.Objects;

public class SpecialGroupProperty {
    private String label;

    private String content;

    //防止数据源切换将其它企业数据推送到错误企业中，前端传输最好加上企业号,除非content全数据库唯一
    private String entCode;

    private Boolean replace;

    private String type;

    public SpecialGroupProperty() {
    }

    public SpecialGroupProperty(String label, String content, String entCode, Boolean replace, String type) {
        this.label = label;
        this.content = content;
        this.entCode = entCode;
        this.replace = replace;
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEntCode() {
        return entCode;
    }

    public void setEntCode(String entCode) {
        this.entCode = entCode;
    }

    public Boolean getReplace() {
        return replace;
    }

    public void setReplace(Boolean replace) {
        this.replace = replace;
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
        SpecialGroupProperty that = (SpecialGroupProperty) o;
        return Objects.equals(label, that.label) &&
                Objects.equals(content, that.content) &&
                Objects.equals(entCode, that.entCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, content, entCode);
    }

    @Override
    public String toString() {
        return "SpecialGroupProperty{" +
                "label='" + label + '\'' +
                ", content='" + content + '\'' +
                ", entCode='" + entCode + '\'' +
                ", replace=" + replace +
                ", type='" + type + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String label;

        private String content;

        private String entCode;

        private Boolean replace;

        private String type;

        public Builder() {
        }

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder entCode(String entCode) {
            this.entCode = entCode;
            return this;
        }

        public Builder replace(Boolean replace) {
            this.replace = replace;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public SpecialGroupProperty build() {
            return new SpecialGroupProperty(label, content, entCode, replace, type);
        }
    }

}
