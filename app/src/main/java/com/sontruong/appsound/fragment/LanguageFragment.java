
package com.sontruong.appsound.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.sontruong.appsound.PhotoActivity;
import com.sontruong.appsound.R;
import com.sontruong.appsound.adapter.LanguageAdapter;
import com.sontruong.appsound.adapter.PhotoGalleryAdapter;
import com.sontruong.appsound.utils.Constants;
import com.sontruong.appsound.utils.Database;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LanguageFragment extends Fragment implements OnClickListener {
    private View mView;

    private String mFilePathString;
    private int mPhotoId;

    private ListView mLanguages;

    private static final String TAG = "LanguageFragment";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mPhotoId = args.getInt(Constants.KEY_LANGUAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ((PhotoActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.language));
        mView = inflater.inflate(R.layout.fragment_language_layout, container, false);
        initView();
        return mView;
    }

    private void initView() {
        ArrayList<String> language = Database.getInstance().getPhotoLanguage(mPhotoId);
        ArrayList<Boolean> checked = new ArrayList<>();
        for (int i = 0; i < language.size(); i++) {
            if (language.get(i).equals(Database.getInstance().getActiveLanguage(mPhotoId))) {
                checked.add(true);
            } else {
                checked.add(false);
            }
        }
        mLanguages = (ListView) mView.findViewById(R.id.language_lv_id);
        LanguageAdapter adapter = new LanguageAdapter(getActivity(), mPhotoId, language, checked);
        mLanguages.setAdapter(adapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu != null) {
            MenuItem searchItem = menu.findItem(R.id.action_camera);
            if (searchItem != null) {
                searchItem.setVisible(false);
            }
            MenuItem plusItem = menu.findItem(R.id.action_plus);
            if (plusItem != null) {
                plusItem.setVisible(false);
            }
        }
    }
}
