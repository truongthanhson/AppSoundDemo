
package com.poptech.popap.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.poptech.popap.PhotoActivity;
import com.poptech.popap.R;
import com.poptech.popap.adapter.PhotoListAdapter;
import com.poptech.popap.bean.PhotoBean;
import com.poptech.popap.database.PopapDatabase;
import com.poptech.popap.listener.HomeActivityDelegate;
import com.poptech.popap.utils.Constants;
import com.poptech.popap.utils.Database;
import com.poptech.popap.view.ItemDecorationColumns;

public class PhotoListFragment extends Fragment implements OnClickListener {
    private View mView;
    private HomeActivityDelegate delegate;
    private static final String TAG = "PhotoListFragment";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            delegate = (HomeActivityDelegate) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ((PhotoActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.photo_list));
        mView = inflater.inflate(R.layout.fragment_photo_list_layout, container, false);
        initView();
        return mView;
    }

    private void initView() {
        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.photo_recycle_id);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), Constants.GRID_COLUMN_COUNT);
        ItemDecorationColumns itemDecoration = new ItemDecorationColumns(Constants.GRID_COLUMN_COUNT, getResources().getDimensionPixelSize(R.dimen.grid_divider), true);
        PhotoListAdapter adapter = new PhotoListAdapter(getActivity(), PopapDatabase.getInstance(getActivity()).getPhotos());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
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
                plusItem.setVisible(true);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_plus) {
            PhotoBean photo = new PhotoBean();
            photo.setPhotoId(Long.toString(PopapDatabase.getInstance(getActivity()).getPhotoId()));
            PopapDatabase.getInstance(getActivity()).insertPhoto(photo);
            delegate.onStartPhotoDetailFragment(photo.getPhotoId());
        }
        return super.onOptionsItemSelected(item);
    }
}
