package com.poptech.popap.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class PhotoBean implements Parcelable {
    private String photo_id;
    private String photo_path;

    public void setPhotoId(String id) {
        this.photo_id = id;
    }

    public String getPhotoId() {
        return this.photo_id;
    }

    public void setPhotoPath(String path) {
        this.photo_path = path;
    }

    public String getPhotoPath() {
        return this.photo_path;
    }

    public PhotoBean() {
        photo_id = "";
        photo_path = "";
    }

    protected PhotoBean(Parcel in) {
        photo_id = in.readString();
        photo_path = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(photo_id);
        dest.writeString(photo_path);
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