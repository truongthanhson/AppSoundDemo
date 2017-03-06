
package com.poptech.popap.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import com.poptech.popap.PhotoActivity;
import com.poptech.popap.R;
import com.poptech.popap.adapter.LanguageAdapter;
import com.poptech.popap.bean.LanguageBean;
import com.poptech.popap.database.PopapDatabase;
import com.poptech.popap.utils.Constants;
import com.poptech.popap.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class LanguageFragment extends Fragment implements OnClickListener, SearchView.OnQueryTextListener {
    private View mView;
    private String mItemId;
    private ListView mListView;
    private List<String> mLanguageList;
    private List<Boolean> mLanguageChecked;
    private LanguageAdapter mAdapter;
    private LanguageBean mLanguageBean;
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
            mItemId = args.getString(Constants.KEY_LANGUAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ((PhotoActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.language));
        mView = inflater.inflate(R.layout.fragment_language_layout, container, false);
        initData();
        initView();
        return mView;
    }

    private void initData() {
        if (PopapDatabase.getInstance(getActivity()).checkLanguageExist(mItemId)) {
            mLanguageBean = PopapDatabase.getInstance(getActivity()).getLanguage(mItemId);
        } else {
            mLanguageBean = new LanguageBean();
            mLanguageBean.setLanguageId(mItemId);
            PopapDatabase.getInstance(getActivity()).insertLanguage(mLanguageBean);
        }
        mLanguageChecked = new ArrayList<>();
        mLanguageList = Utils.getLanguages();
        for (int i = 0; i < mLanguageList.size(); i++) {
            if (mLanguageList.get(i).equals(mLanguageBean.getLanguageActive())) {
                mLanguageChecked.add(true);
            } else {
                mLanguageChecked.add(false);
            }
        }
    }

    private void initView() {
        mListView = (ListView) mView.findViewById(R.id.language_lv_id);
        mAdapter = new LanguageAdapter(getActivity(), mItemId, mLanguageList, mLanguageChecked);
        mListView.setAdapter(mAdapter);
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
            MenuItem searchItem = menu.findItem(R.id.action_search);
            if (searchItem != null) {
                searchItem.setVisible(true);
                SearchView searchView = (SearchView) searchItem.getActionView();
                if (searchView != null) {
                    searchView.setOnQueryTextListener(this);
                    searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                        @Override
                        public boolean onClose() {
                            InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            in.hideSoftInputFromWindow(getActivity().getCurrentFocus().getApplicationWindowToken(), 0);
                            onQueryTextChange("");
                            return false;
                        }
                    });
                }
            }
            MenuItem cameraItem = menu.findItem(R.id.action_camera);
            if (cameraItem != null) {
                cameraItem.setVisible(false);
            }
            MenuItem plusItem = menu.findItem(R.id.action_plus);
            if (plusItem != null) {
                plusItem.setVisible(false);
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        onQueryTextChange("");
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (mAdapter != null && newText != null) {
            mAdapter.getFilter().filter(newText);
        }
        return false;
    }
}
