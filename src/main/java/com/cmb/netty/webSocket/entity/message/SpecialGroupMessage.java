package com.cmb.netty.webSocket.entity.message;


import java.util.List;

public class SpecialGroupMessage {
    private String label;

    private List<String> contents;

    //防止数据源切换将其它企业数据推送到错误企业中，前端传输最好加上企业号,除非content全数据库唯一
    private String entCode;

    private Boolean replace;

    private String type;

    public SpecialGroupMessage() {
    }

    public SpecialGroupMessage(String label, List<String> contents, String entCode, Boolean replace, String type) {
        this.label = label;
        this.contents = contents;
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

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
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
    public String toString() {
        return "SpecialGroupMessage{" +
                "label='" + label + '\'' +
                ", content='" + contents + '\'' +
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

        private List<String> contents;

        private String entCode;

        private Boolean replace;

        private String type;

        public Builder() {
        }

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder content(List<String> contents) {
            this.contents = contents;
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

        public SpecialGroupMessage build() {
            return new SpecialGroupMessage(label, contents, entCode, replace, type);
        }
    }
}
