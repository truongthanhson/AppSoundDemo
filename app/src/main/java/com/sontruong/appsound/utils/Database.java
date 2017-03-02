package com.sontruong.appsound.utils;

import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by Truong Thanh Son on 11/6/2015.
 */
public class Database {
    private static Database dataBase = null;
    private static final String IMAGE_URLS[] = {
            "http://api.learn2crack.com/android/images/donut.png",
            "http://api.learn2crack.com/android/images/eclair.png",
            "http://api.learn2crack.com/android/images/froyo.png",
            "http://api.learn2crack.com/android/images/ginger.png",
            "http://api.learn2crack.com/android/images/honey.png",
            "http://api.learn2crack.com/android/images/icecream.png",
            "http://api.learn2crack.com/android/images/jellybean.png",
            "http://api.learn2crack.com/android/images/kitkat.png",
            "http://api.learn2crack.com/android/images/lollipop.png",
            "http://api.learn2crack.com/android/images/marshmallow.png"
    };

    private ArrayList<String> mPhotoList;
    private ArrayList<String> mPhotoRecord;
    private ArrayList<String> mActiveLang;
    private ArrayList<List<Pair<String, String>>> mPhotoLang;

    public Database() {
        mPhotoList = new ArrayList<>();
        mPhotoRecord = new ArrayList<>();
        mPhotoLang = new ArrayList<>();
        mActiveLang = new ArrayList<>();

        for (int i = 0; i < IMAGE_URLS.length; i++) {
            mPhotoList.add(IMAGE_URLS[i]);
            mPhotoRecord.add("");
            mPhotoLang.add(getLanguages());
            mActiveLang.add("");
        }
    }

    private List<Pair<String, String>> getLanguages() {
        Locale[] locales = Locale.getAvailableLocales();
        List<Pair<String, String>> languages = new ArrayList<>();
        List<String> localeLang = new ArrayList<>();
        for (Locale l : locales) {
            String lang = l.getDisplayLanguage();
            if (lang != null && !lang.isEmpty())
                localeLang.add(lang);
        }

        List<String> uniqueLang = new ArrayList<>(new HashSet<String>(localeLang));
        Collections.sort(uniqueLang, String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < uniqueLang.size(); i++) {
            languages.add(new Pair<>(uniqueLang.get(i), ""));
        }

        return languages;
    }

    public static Database getInstance() {
        if (dataBase == null) {
            dataBase = new Database();
        }
        return dataBase;
    }

    public ArrayList<String> getPhotoList() {
        return mPhotoList;
    }

    public String getPhotoList(int index) {
        return mPhotoList.get(index);
    }

    public void updatePhotoList(int index, String image) {
        mPhotoList.set(index, image);
    }

    public ArrayList<String> getPhotoRecord() {
        return mPhotoRecord;
    }

    public String getPhotoRecord(int index) {
        return mPhotoRecord.get(index);
    }

    public void updatePhotoRecord(int index, String image) {
        mPhotoRecord.set(index, image);
    }

    public ArrayList<String> getPhotoLanguage(int index) {
        ArrayList<String> language = new ArrayList<>();
        for (int i = 0; i < mPhotoLang.get(index).size(); i++) {
            language.add(mPhotoLang.get(index).get(i).first);
        }
        return language;
    }

    public String getDescriptionLanguage(int index) {
        String description = "";
        List<Pair<String, String>> listLang = mPhotoLang.get(index);
        for (int i = 0; i < listLang.size(); i++) {
            if (listLang.get(i).first.equals(getActiveLanguage(index))) {
                description = listLang.get(i).second;
            }
        }
        return description;
    }

    public boolean checkDescriptionLanguage(int index) {
        return !(getDescriptionLanguage(index).isEmpty());
    }

    public void updatePhotoLanguage(int index, Pair<String, String> pair) {
        List<Pair<String, String>> description = new ArrayList<>();
        description = mPhotoLang.get(index);
        for (int i = 0; i < description.size(); i++) {
            if (description.get(i).first.equals(pair.first)) {
                description.set(i, pair);
                break;
            }
        }
        mPhotoLang.set(index, description);
    }

    public String getActiveLanguage(int index) {
        return mActiveLang.get(index);
    }

    public void updateActiveLanguage(int index, String lang) {
        mActiveLang.set(index, lang);
    }

    public boolean checkActiveLanguage(int index) {
        return !(mActiveLang.get(index).isEmpty());
    }
}
