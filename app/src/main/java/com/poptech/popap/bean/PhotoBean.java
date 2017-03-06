package com.poptech.popap.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class PhotoBean implements Parcelable {
    private String mPhotoId;
    private String mPhotoPath;

    public void setPhotoId(String id) {
        this.mPhotoId = id;
    }

    public String getPhotoId() {
        return this.mPhotoId;
    }

    public void setPhotoPath(String path) {
        this.mPhotoPath = path;
    }

    public String getPhotoPath() {
        return this.mPhotoPath;
    }

    public PhotoBean() {
        mPhotoId = "";
        mPhotoPath = "";
    }

    protected PhotoBean(Parcel in) {
        mPhotoId = in.readString();
        mPhotoPath = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPhotoId);
        dest.writeString(mPhotoPath);
    }

    @SuppressWarnings("unused")
    public static final Creator<PhotoBean> CREATOR = new Creator<PhotoBean>() {
        @Override
        public PhotoBean createFromParcel(Parcel in) {
            return new PhotoBean(in);
        }

        @Override
        public PhotoBean[] newArray(int size) {
            return new PhotoBean[size];
        }
    };
}