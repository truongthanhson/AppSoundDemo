package com.poptech.popap.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class LanguageBean implements Parcelable {
    private String language_id;
    private String language_active;

    public void setLanguageId(String id) {
        this.language_id = id;
    }

    public String getLanguageId() {
        return this.language_id;
    }

    public void setLanguageActive(String language) {
        this.language_active = language;
    }

    public String getLanguageActive() {
        return this.language_active;
    }

    public LanguageBean() {
        language_id = "";
        language_active = "";
    }

    protected LanguageBean(Parcel in) {
        language_id = in.readString();
        language_active = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(language_id);
        dest.writeString(language_active);
    }

    @SuppressWarnings("unused")
    public static final Creator<LanguageBean> CREATOR = new Creator<LanguageBean>() {
        @Override
        public LanguageBean createFromParcel(Parcel in) {
            return new LanguageBean(in);
        }

        @Override
        public LanguageBean[] newArray(int size) {
            return new LanguageBean[size];
        }
    };
}