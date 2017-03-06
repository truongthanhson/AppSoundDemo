
package com.poptech.popap;

import android.app.SearchManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.poptech.popap.fragment.LanguageFragment;
import com.poptech.popap.fragment.PhotoDetailFragment;
import com.poptech.popap.fragment.PhotoLGalleryFragment;
import com.poptech.popap.fragment.PhotoListFragment;
import com.poptech.popap.listener.HomeActivityDelegate;
import com.poptech.popap.utils.Constants;

public class PhotoActivity extends AppCompatActivity implements HomeActivityDelegate {
    private static final String TAG = "PhotoActivity";
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        onStartPhotoListFragment();
    }

    @Override
    public void onStartPhotoDetailFragment(String id) {
        PhotoDetailFragment photoDetailFragment = new PhotoDetailFragment();
        Bundle args = new Bundle();
        args.putString(Constants.KEY_PHOTO_DETAIL, id);
        photoDetailFragment.setArguments(args);
        startFragment(photoDetailFragment, Constants.FRAGMENT_PHOTO_DETAIL_TAG, true);
    }

    @Override
    public void onStartPhotoListFragment() {
        PhotoListFragment photoListFragment = new PhotoListFragment();
        startFragment(photoListFragment, Constants.FRAGMENT_PHOTO_LIST_TAG, true);
    }

    @Override
    public void onStartPhotoGalleryFragment(String id) {
        PhotoLGalleryFragment photoGalleryFragment = new PhotoLGalleryFragment();
        Bundle args = new Bundle();
        args.putString(Constants.KEY_PHOTO_GALLERY, id);
        photoGalleryFragment.setArguments(args);
        startFragment(photoGalleryFragment, Constants.FRAGMENT_PHOTO_GALLERY_TAG, true);
    }

    @Override
    public void onStartLanguageFragment(String id) {
        LanguageFragment languageFragment = new LanguageFragment();
        Bundle args = new Bundle();
        args.putString(Constants.KEY_LANGUAGE, id);
        languageFragment.setArguments(args);
        startFragment(languageFragment, Constants.FRAGMENT_LANGUAGE_TAG, true);
    }


    private void startFragment(Fragment fragment, String tag, boolean isAddBackStack) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        transaction.replace(R.id.container_body, fragment, tag);
        if (isAddBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackCount == 0) {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem cameraItem = menu.findItem(R.id.action_camera);
        if(cameraItem != null) {
            cameraItem.setVisible(false);
        }
        MenuItem plusItem = menu.findItem(R.id.action_plus);
        if (plusItem != null) {
            plusItem.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }
}

