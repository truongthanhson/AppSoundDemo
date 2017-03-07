
package com.poptech.popap.fragment;

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

import com.poptech.popap.PhotoActivity;
import com.poptech.popap.R;
import com.poptech.popap.adapter.PhotoGalleryAdapter;
import com.poptech.popap.database.PopapDatabase;
import com.poptech.popap.utils.Constants;
import com.poptech.popap.view.ItemDecorationColumns;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PhotoLGalleryFragment extends Fragment implements OnClickListener {
    private View mView;

    private String mFilePathString;
    private String mItemId;

    private static final String TAG = "PhotoGalleryFragment";

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
            mItemId = args.getString(Constants.KEY_PHOTO_GALLERY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ((PhotoActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.photo_gallery));
        mView = inflater.inflate(R.layout.fragment_photo_list_layout, container, false);
        initView();
        return mView;
    }

    private void initView() {
        getAllImages(getActivity());
        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.photo_recycle_id);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), Constants.GRID_COLUMN_COUNT);
        ItemDecorationColumns itemDecoration = new ItemDecorationColumns(Constants.GRID_COLUMN_COUNT, getResources().getDimensionPixelSize(R.dimen.grid_divider), true);
        PhotoGalleryAdapter adapter = new PhotoGalleryAdapter(getActivity(), mItemId, getAllImages(getActivity()));
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

    public ArrayList<String> getAllImages(Context context) {
        ArrayList<Pair<Long, String>> imagePairs = new ArrayList<>();
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATA
        };
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(images, projection, null, null, null);
        if (cursor.moveToFirst()) {
            String date, data;
            int dateColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
            int dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            do {
                date = cursor.getString(dateColumn);
                data = cursor.getString(dataColumn);
                imagePairs.add(new Pair<>(Long.valueOf(date), data));
            } while (cursor.moveToNext());
        }

        Collections.sort(imagePairs, new Comparator<Pair<Long, String>>() {
            @Override
            public int compare(Pair<Long, String> lhs, Pair<Long, String> rhs) {
                return Long.valueOf(rhs.first).compareTo(Long.valueOf(lhs.first));
            }
        });

        ArrayList<String> imageUrls = new ArrayList<>();
        for (int i = 0; i < imagePairs.size(); i++) {
            imageUrls.add(imagePairs.get(i).second);
        }
        return imageUrls;
    }

    private void runCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            Uri mImageCaptureUri;
            mFilePathString = Environment.getExternalStorageDirectory() + Constants.PATH_APP + "/" + Constants.PATH_PHOTO + "/" + System.currentTimeMillis() + ".jpg";
            File iFilePath = new File(mFilePathString);
            mImageCaptureUri = Uri.fromFile(iFilePath);
            takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            takePictureIntent.putExtra("return-data", true);
            startActivityForResult(takePictureIntent, Constants.REQUEST_AVATAR_CAPTURE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_AVATAR_CAPTURE && resultCode == Activity.RESULT_OK) {
            PopapDatabase.getInstance(getActivity()).updatePhotoPath(mItemId, mFilePathString);
            getActivity().onBackPressed();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu != null) {
            MenuItem searchItem = menu.findItem(R.id.action_search);
            if (searchItem != null) {
                searchItem.setVisible(true);
            }
            MenuItem cameraItem = menu.findItem(R.id.action_camera);
            if (cameraItem != null) {
                cameraItem.setVisible(true);
            }
            MenuItem plusItem = menu.findItem(R.id.action_plus);
            if (plusItem != null) {
                plusItem.setVisible(false);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_camera) {
            runCamera();
        }
        return super.onOptionsItemSelected(item);
    }
}
