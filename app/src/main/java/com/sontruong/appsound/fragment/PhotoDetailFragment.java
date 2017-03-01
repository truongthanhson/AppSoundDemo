
package com.sontruong.appsound.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sontruong.appsound.PhotoActivity;
import com.sontruong.appsound.R;
import com.sontruong.appsound.listener.HomeActivityDelegate;
import com.sontruong.appsound.soundfile.VoiceRecorder;
import com.sontruong.appsound.utils.Constants;
import com.sontruong.appsound.utils.Database;

public class PhotoDetailFragment extends Fragment implements OnClickListener, View.OnTouchListener {
    private View mView;
    private ImageView mImageView;
    private ImageButton mEditButton;
    private EditText mEditText;
    private TextView mLangText;
    private TextView mTimerText;
    private FrameLayout mRecordButton;
    private HomeActivityDelegate delegate;
    private int mPhotoId;
    private String mRecordPath;
    private static final String TAG = "PhotoDetailFragment";

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
        Bundle args = getArguments();
        if (args != null) {
            mPhotoId = args.getInt(Constants.KEY_PHOTO_DETAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ((PhotoActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.photo_detail));
        mView = inflater.inflate(R.layout.fragment_photo_detail_layout, container, false);
        initView();
        return mView;
    }

    private void initView() {
        mEditText = (EditText) mView.findViewById(R.id.description_et_id);
        if (Database.getInstance().checkDescriptionLanguage(mPhotoId)) {
            mEditText.setText(Database.getInstance().getDescriptionLanguage(mPhotoId));
        }

        mLangText = (TextView) mView.findViewById(R.id.language_tv_id);
        if (Database.getInstance().checkActiveLanguage(mPhotoId)) {
            mLangText.setText(Database.getInstance().getActiveLanguage(mPhotoId));
        }

        mImageView = (ImageView) mView.findViewById(R.id.photo_detail_img_id);
        Glide.with(this)
                .load(Database.getInstance().getPhotoList(mPhotoId))
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mImageView);

        mEditButton = (ImageButton) mView.findViewById(R.id.photo_edit_btn_id);
        mEditButton.setOnClickListener(this);

        mRecordButton = (FrameLayout) mView.findViewById(R.id.record_fl_id);
        mRecordButton.setOnTouchListener(this);
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
        switch (v.getId()) {
            case R.id.photo_edit_btn_id:
                delegate.onStartPhotoGalleryFragment(mPhotoId);
                break;
            default:
                break;
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu != null) {
            MenuItem searchItem = menu.findItem(R.id.action_camera);
            if (searchItem != null) {
                searchItem.setVisible(false);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mView.findViewById(R.id.record_normal_fl_id).setVisibility(View.GONE);
                mView.findViewById(R.id.record_press_fl_id).setVisibility(View.VISIBLE);
                mRecordPath = Environment.getExternalStorageDirectory() + Constants.PATH_APP + "/" + Constants.PATH_SOUND + "/" + mPhotoId + "_record.3gpp";
                VoiceRecorder.getInstance().startRecord(mRecordPath);
                scaleView(view, 1.5f, 1f);
                break;
            case MotionEvent.ACTION_UP:
                mView.findViewById(R.id.record_normal_fl_id).setVisibility(View.VISIBLE);
                mView.findViewById(R.id.record_press_fl_id).setVisibility(View.GONE);
                VoiceRecorder.getInstance().endRecord();
                Database.getInstance().updatePhotoRecord(mPhotoId, mRecordPath);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            default:
                return false;
        }
        return true;
    }

    public void scaleView(View v, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(
                startScale, endScale, // Start and end values for the X axis scaling
                startScale, endScale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(1000);
        v.startAnimation(anim);
    }
}
