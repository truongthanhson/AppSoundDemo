package com.poptech.popap.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class SoundBean implements Parcelable {
    private String sound_id;
    private String sound_path;
    private String sound_mark;

    public void setSoundId(String id) {
        this.sound_id = id;
    }

    public String getSoundId() {
        return this.sound_id;
    }

    public void setSoundPath(String path) {
        this.sound_path = path;
    }

    public String getSoundPath() {
        return this.sound_path;
    }

    public void setSoundMark(String mark) {
        this.sound_mark = mark;
    }

    public String getSoundMark() {
        return this.sound_mark;
    }

    public SoundBean() {
        sound_id = "";
        sound_path = "";
        sound_mark = "";
    }

    protected SoundBean(Parcel in) {
        sound_id = in.readString();
        sound_path = in.readString();
        sound_mark = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sound_id);
        dest.writeString(sound_path);
        dest.writeString(sound_mark);
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