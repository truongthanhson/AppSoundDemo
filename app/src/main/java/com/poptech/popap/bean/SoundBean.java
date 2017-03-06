package com.poptech.popap.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class SoundBean implements Parcelable {
    private String mSoundId;
    private String mSoundPath;
    private String mSoundMark;

    public void setSoundId(String id) {
        this.mSoundId = id;
    }

    public String getSoundId() {
        return this.mSoundId;
    }

    public void setSoundPath(String path) {
        this.mSoundPath = path;
    }

    public String getSoundPath() {
        return this.mSoundPath;
    }

    public void setSoundMark(String mark) {
        this.mSoundMark = mark;
    }

    public String getSoundMark() {
        return this.mSoundMark;
    }

    public SoundBean() {
        mSoundId = "";
        mSoundPath = "";
        mSoundMark = "";
    }

    protected SoundBean(Parcel in) {
        mSoundId = in.readString();
        mSoundPath = in.readString();
        mSoundMark = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mSoundId);
        dest.writeString(mSoundPath);
        dest.writeString(mSoundMark);
    }

    @SuppressWarnings("unused")
    public static final Creator<SoundBean> CREATOR = new Creator<SoundBean>() {
        @Override
        public SoundBean createFromParcel(Parcel in) {
            return new SoundBean(in);
        }

        @Override
        public SoundBean[] newArray(int size) {
            return new SoundBean[size];
        }
    };
}