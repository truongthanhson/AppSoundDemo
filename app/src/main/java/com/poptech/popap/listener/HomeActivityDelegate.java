package com.poptech.popap.listener;

public interface HomeActivityDelegate {
    public void onStartPhotoDetailFragment(String id);

    public void onStartPhotoListFragment();

    public void onStartPhotoGalleryFragment(String id);

    public void onStartLanguageFragment(String id);
}
