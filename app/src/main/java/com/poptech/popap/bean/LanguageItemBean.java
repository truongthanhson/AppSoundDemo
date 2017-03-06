package com.poptech.popap.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class LanguageItemBean implements Parcelable {
    private String mLanguageItemName;
    private String mLanguageItemComment;
    private String mLanguageItemId;

    public void setLanguageItemId(String id) {
        this.mLanguageItemId = id;
    }

    public String getLanguageItemId() {
        return this.mLanguageItemId;
    }

    public void setLanguageItemName(String name) {
        this.mLanguageItemName = name;
    }

    public String getLanguageItemName() {
        return this.mLanguageItemName;
    }

    public void setLanguageItemComment(String comment) {
        this.mLanguageItemComment = comment;
    }

    public String getLanguageItemComment() {
        return this.mLanguageItemComment;
    }

    public LanguageItemBean() {
        mLanguageItemId = "";
        mLanguageItemName = "";
        mLanguageItemComment = "";
    }

    protected LanguageItemBean(Parcel in) {
        mLanguageItemId = in.readString();
        mLanguageItemName = in.readString();
        mLanguageItemComment = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mLanguageItemId);
        dest.writeString(mLanguageItemName);
        dest.writeString(mLanguageItemComment);
    }

    @SuppressWarnings("unused")
    public static final Creator<LanguageItemBean> CREATOR = new Creator<LanguageItemBean>() {
        @Override
        public LanguageItemBean createFromParcel(Parcel in) {
            return new LanguageItemBean(in);
        }

        @Override
        public LanguageItemBean[] newArray(int size) {
            return new LanguageItemBean[size];
        }
    };
}