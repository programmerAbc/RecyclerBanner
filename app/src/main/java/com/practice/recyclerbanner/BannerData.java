package com.practice.recyclerbanner;

public class BannerData {
    String title;

    public BannerData() {
    }

    private BannerData(Builder builder) {
        setTitle(builder.title);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static final class Builder {
        private String title;

        private Builder() {
        }

        public Builder title(String val) {
            title = val;
            return this;
        }

        public BannerData build() {
            return new BannerData(this);
        }
    }
}
