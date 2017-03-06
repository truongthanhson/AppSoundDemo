package com.poptech.popap.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class LanguageBean implements Parcelable {
    private String mLanguageId;
    private String mLanguageActive;
    private List<LanguageItemBean> mLanguageItem;

    public void setLanguageId(String id) {
        this.mLanguageId = id;
    }

    public String getLanguageId() {
        return this.mLanguageId;
    }

    public void setLanguageActive(String language) {
        this.mLanguageActive = language;
    }

    public String getLanguageActive() {
        return this.mLanguageActive;
    }

    public void setLanguageItem(List<LanguageItemBean> languageItem) {
        this.mLanguageItem = languageItem;
    }

    public List<LanguageItemBean> getLanguageItem() {
        return this.mLanguageItem;
    }

    public void updateLanguageComment(String comment) {
        for (int i = 0; i < mLanguageItem.size(); i++) {
            if (mLanguageItem.get(i).getLanguageItemName().equals(mLanguageActive)) {
                mLanguageItem.get(i).setLanguageItemComment(comment);
                break;
            }
        }
    }

    public void addLanguageItem(LanguageItemBean item) {
        mLanguageItem.add(item);
    }

    public String getLanguageItemComment() {
        String comment = "";
        for (int i = 0; i < mLanguageItem.size(); i++) {
            if (mLanguageItem.get(i).getLanguageItemName().equals(mLanguageActive)) {
                comment = mLanguageItem.get(i).getLanguageItemComment();
            }
        }
        return comment;
    }

    public LanguageBean() {
        mLanguageId = "";
        mLanguageActive = "";
        mLanguageItem = new ArrayList<>();
    }

    protected LanguageBean(Parcel in) {
        mLanguageId = in.readString();
        mLanguageActive = in.readString();
        mLanguageItem = new ArrayList<>();
        in.readTypedList(mLanguageItem, LanguageItemBean.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mLanguageId);
        dest.writeString(mLanguageActive);
        dest.writeTypedList(mLanguageItem);
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