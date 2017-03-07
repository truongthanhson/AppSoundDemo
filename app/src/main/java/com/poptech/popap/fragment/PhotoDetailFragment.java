
package com.poptech.popap.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.poptech.popap.PhotoActivity;
import com.poptech.popap.R;
import com.poptech.popap.bean.LanguageBean;
import com.poptech.popap.bean.LanguageItemBean;
import com.poptech.popap.bean.PhotoBean;
import com.poptech.popap.bean.SoundBean;
import com.poptech.popap.database.PopapDatabase;
import com.poptech.popap.listener.HomeActivityDelegate;
import com.poptech.popap.sound.SamplePlayer;
import com.poptech.popap.sound.SoundFile;
import com.poptech.popap.sound.VoiceRecorder;
import com.poptech.popap.sound.WaveformManager;
import com.poptech.popap.utils.AndroidUtilities;
import com.poptech.popap.utils.AnimationUtils;
import com.poptech.popap.utils.Constants;
import com.poptech.popap.utils.CountUpTimer;
import com.poptech.popap.utils.StringUtils;
import com.poptech.popap.utils.Utils;
import com.poptech.popap.view.AudioWaveFormTimelineView;

import java.io.File;

public class PhotoDetailFragment extends Fragment implements OnClickListener, View.OnTouchListener {
    private View mView;
    private ImageView mImageView;
    private ImageButton mEditButton;
    private EditText mEditText;
    private TextView mLangButton;
    private TextView mSubmitButton;
    private TextView mTimerText;
    private FrameLayout mRecordButton;
    private HomeActivityDelegate delegate;
    private String mItemId;
    private ImageView mWaveformImg;
    private WaveformManager waveformManager;
    private SoundFile mSoundFile;
    private int mHeight;
    private AudioWaveFormTimelineView mTimelineView;
    private Handler mHandler;
    private File mFile;
    private boolean mLoadingKeepGoing;
    private SamplePlayer mPlayer;
    private AlertDialog mAlertDialog;
    private ProgressDialog mProgressDialog;
    private long mLoadingLastUpdateTime;
    private Thread mLoadSoundFileThread;
    private float[] mSegmentPos = new float[]{0.0f, 0.5f, 1.0f};
    private CountUpTimer mTimer;
    private PhotoBean mPhotoBean;
    private SoundBean mSoundBean;
    private LanguageBean mLanguageBean;
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
            mItemId = args.getString(Constants.KEY_PHOTO_DETAIL);
        }
    }

    private void onReloadPositionMark() {
        if (mSoundBean == null || StringUtils.isNullOrEmpty(mSoundBean.getSoundMark())) {
            setDefaultValueMark();
        } else {
            String[] marks = mSoundBean.getSoundMark().split(";");
            if (marks == null || marks.length != 3) {
                setDefaultValueMark();
            } else {
                try {
                    mSegmentPos[0] = Float.valueOf(marks[0]);
                    mSegmentPos[1] = Float.valueOf(marks[1]);
                    mSegmentPos[2] = Float.valueOf(marks[2]);
                } catch (Exception e) {
                    setDefaultValueMark();
                }
            }
        }


        mTimelineView.setProgressLeft(mSegmentPos[0]);
        mTimelineView.setProgressMidle(mSegmentPos[1]);
        mTimelineView.setProgressRight(mSegmentPos[2]);
    }

    private void setDefaultValueMark() {
        mSegmentPos[0] = 0.0f;
        mSegmentPos[1] = 0.5f;
        mSegmentPos[2] = 1.0f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ((PhotoActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.photo_detail));
        mView = inflater.inflate(R.layout.fragment_photo_detail_layout, container, false);
        initData();
        initView();
        return mView;
    }

    private void initData() {
        if (PopapDatabase.getInstance(getActivity()).checkPhotoExist(mItemId)) {
            mPhotoBean = PopapDatabase.getInstance(getActivity()).getPhoto(mItemId);
        } else {
            mPhotoBean = new PhotoBean();
            mPhotoBean.setPhotoId(mItemId);
            PopapDatabase.getInstance(getActivity()).insertPhoto(mPhotoBean);
        }

        if (PopapDatabase.getInstance(getActivity()).checkSoundExist(mItemId)) {
            mSoundBean = PopapDatabase.getInstance(getActivity()).getSound(mItemId);
        } else {
            mSoundBean = new SoundBean();
            mSoundBean.setSoundId(mItemId);
            PopapDatabase.getInstance(getActivity()).insertSound(mSoundBean);
        }

        if (PopapDatabase.getInstance(getActivity()).checkLanguageExist(mItemId)) {
            mLanguageBean = PopapDatabase.getInstance(getActivity()).getLanguage(mItemId);
        } else {
            mLanguageBean = new LanguageBean();
            mLanguageBean.setLanguageId(mItemId);
            PopapDatabase.getInstance(getActivity()).insertLanguage(mLanguageBean);
        }

        if (PopapDatabase.getInstance(getActivity()).checkLanguageItemExist(mItemId)) {
            mLanguageBean.setLanguageItem(PopapDatabase.getInstance(getActivity()).getItemLanguage(mLanguageBean.getLanguageId()));
        }
    }

    private void initView() {
        // Edit text
        mEditText = (EditText) mView.findViewById(R.id.description_et_id);
        setDescription();

        // Language button
        mLangButton = (TextView) mView.findViewById(R.id.language_button_id);
        if (!StringUtils.isNullOrEmpty(mLanguageBean.getLanguageActive())) {
            mLangButton.setText(mLanguageBean.getLanguageActive());
        }
        mLangButton.setOnClickListener(this);

        // Submit button
        mSubmitButton = (TextView) mView.findViewById(R.id.submit_button_id);
        mSubmitButton.setOnClickListener(this);

        // Photo view
        mImageView = (ImageView) mView.findViewById(R.id.photo_detail_img_id);
        Glide.with(this)
                .load(mPhotoBean.getPhotoPath())
                .centerCrop()
                .placeholder(R.color.white)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mImageView);
        mImageView.setOnClickListener(this);

        // Edit photo
        mEditButton = (ImageButton) mView.findViewById(R.id.photo_edit_btn_id);
        mEditButton.setOnClickListener(this);

        // Waveform view
        mWaveformImg = (ImageView) mView.findViewById(R.id.waveform);
        mTimelineView = (AudioWaveFormTimelineView) mView.findViewById(R.id.timeline);
        mTimelineView.setDelegate(new AudioWaveFormTimelineView.AudioWaveFormTimelineViewDelegate() {
            @Override
            public void onLeftProgressChanged(float progress) {
                mSegmentPos[0] = progress;
                mSoundBean.setSoundMark(mSegmentPos[0] + ";" + mSegmentPos[1] + ";" + mSegmentPos[2]);
                handlePause();
            }

            @Override
            public void onMidleProgressChanged(float progress) {
                mSegmentPos[1] = progress;
                mSoundBean.setSoundMark(mSegmentPos[0] + ";" + mSegmentPos[1] + ";" + mSegmentPos[2]);
                handlePause();
            }

            @Override
            public void onRightProgressChanged(float progress) {
                mSegmentPos[2] = progress;
                mSoundBean.setSoundMark(mSegmentPos[0] + ";" + mSegmentPos[1] + ";" + mSegmentPos[2]);
                handlePause();
            }
        });
        mView.findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playNextSegment();
            }
        });
        mView.findViewById(R.id.ffwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play2NextSegment();
            }
        });
        mHandler = new Handler();
        waveformManager = new WaveformManager(getActivity());

        // Record view
        mRecordButton = (FrameLayout) mView.findViewById(R.id.record_fl_id);
        mRecordButton.setOnTouchListener(this);
        mTimerText = (TextView) mView.findViewById(R.id.timer_tv_id);
        mTimer = new CountUpTimer(Constants.MAX_AUDIO_RECORD_TIME_MS) {
            public void onTick(int second) {
                mTimerText.setText(String.valueOf(Utils.formatTime(second)));
            }
        };
    }

    private void setDescription() {
        if (!StringUtils.isNullOrEmpty(mLanguageBean.getLanguageActive())) {
            mEditText.setText(mLanguageBean.getLanguageItemComment());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        mLoadingKeepGoing = false;
        closeThread(mLoadSoundFileThread);
        mLoadSoundFileThread = null;
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }

        if (mPlayer != null) {
            if (mPlayer.isPlaying() || mPlayer.isPaused()) {
                mPlayer.stop();
            }
            mPlayer.release();
            mPlayer = null;
        }

        super.onDestroy();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        setDescription();
        onReloadPositionMark();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveCurrentMarkPositionToDatabase();
    }

    private void saveCurrentMarkPositionToDatabase() {
        String content = mSegmentPos[0] + ";" + mSegmentPos[1] + ";" + mSegmentPos[2];
        PopapDatabase.getInstance(getActivity()).updateSoundMark(mItemId, content);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo_edit_btn_id:
                delegate.onStartPhotoGalleryFragment(mItemId);
                break;
            case R.id.photo_detail_img_id:
                onWaveformShow();
                break;
            case R.id.language_button_id:
                delegate.onStartLanguageFragment(mItemId);
                break;
            case R.id.submit_button_id:
                if (!StringUtils.isNullOrEmpty(mLanguageBean.getLanguageActive())) {
                    if (PopapDatabase.getInstance(getActivity()).checkLanguageItemExist(mItemId, mLanguageBean.getLanguageActive())) {
                        PopapDatabase.getInstance(getActivity()).updateLanguageItemComment(
                                mItemId,
                                mLanguageBean.getLanguageActive(),
                                mEditText.getText().toString()
                        );
                        mLanguageBean.updateLanguageComment(mEditText.getText().toString());
                    } else {
                        LanguageItemBean item = new LanguageItemBean();
                        item.setLanguageItemId(mItemId);
                        item.setLanguageItemName(mLanguageBean.getLanguageActive());
                        item.setLanguageItemComment(mEditText.getText().toString());
                        PopapDatabase.getInstance(getActivity()).insertLanguageItem(item);
                        mLanguageBean.addLanguageItem(item);
                    }
                    Toast.makeText(getActivity(), "Submitted photo description", Toast.LENGTH_LONG).show();
                } else {
                    mEditText.setError(getString(R.string.login_error_language));
                    AnimationUtils.shake(getActivity().getApplicationContext(), mEditText);
                }
                break;
            default:
                break;
        }
    }

    private void closeThread(Thread thread) {
        if (thread != null && thread.isAlive()) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }
    }

    private void onWaveformShow() {
        if (waveformAvailable()) {
            loadFromFile(mSoundBean.getSoundPath());
        } else {
            mView.findViewById(R.id.waveform_container).setVisibility(View.GONE);
            mImageView.setVisibility(View.VISIBLE);
            mEditButton.setVisibility(View.VISIBLE);
            showDialogWaveformNotAvailable();
        }
    }

    private void showDialogWaveformNotAvailable() {
        Toast.makeText(getActivity(), "Recorded file not added yet", Toast.LENGTH_SHORT).show();
    }

    private boolean waveformAvailable() {
        return !StringUtils.isNullOrEmpty(mSoundBean.getSoundPath());
    }

    private void loadFromFile(String audioPath) {
        mFile = new File(audioPath);
        mLoadingLastUpdateTime = getCurrentTime();
        mLoadingKeepGoing = true;
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle(R.string.progress_dialog_loading);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
            }
        });
        mProgressDialog.show();

        final SoundFile.ProgressListener listener = new SoundFile.ProgressListener() {
            public boolean reportProgress(double fractionComplete) {
                long now = getCurrentTime();
                if (now - mLoadingLastUpdateTime > 100) {
                    mProgressDialog.setProgress(
                            (int) (mProgressDialog.getMax() * fractionComplete));
                    mLoadingLastUpdateTime = now;
                }
                return mLoadingKeepGoing;
            }
        };

        // Load the sound file in a background thread
        mLoadSoundFileThread = new Thread() {
            public void run() {
                try {
                    mSoundFile = SoundFile.create(mFile.getAbsolutePath(), listener);

                    if (mSoundFile == null) {
                        mProgressDialog.dismiss();
                        return;
                    }
                    mPlayer = new SamplePlayer(mSoundFile);
                } catch (final Exception e) {
                    mProgressDialog.dismiss();
                    e.printStackTrace();
                    return;
                }
                mProgressDialog.dismiss();
                if (mLoadingKeepGoing) {
                    Runnable runnable = new Runnable() {
                        public void run() {
                            finishOpeningSoundFile();
                        }
                    };
                    mHandler.post(runnable);
                }
            }
        };
        mLoadSoundFileThread.start();
    }

    private synchronized void handlePause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
    }

    private long getCurrentTime() {
        return System.nanoTime() / 1000000;
    }

    private void playNextSegment() {
        int startPlayTime = (int) (mSoundFile.getFileDuration() * mSegmentPos[0] / 1000f);
        int endPlayTime = (int) (mSoundFile.getFileDuration() * mSegmentPos[1] / 1000f);
        mPlayer.seekTo(startPlayTime);
        mPlayer.setPlaybackEnd(endPlayTime);

        mPlayer.start();
    }

    private void play2NextSegment() {
        int startPlayTime = (int) (mSoundFile.getFileDuration() * mSegmentPos[0] / 1000f);
        int endPlayTime = (int) (mSoundFile.getFileDuration() * mSegmentPos[2] / 1000f);
        mPlayer.seekTo(startPlayTime);
        mPlayer.setPlaybackEnd(endPlayTime);

        mPlayer.start();
    }

    private void finishOpeningSoundFile() {
        waveformManager.setSoundFile(mSoundFile);
        mHeight = AndroidUtilities.dp(104);
        Bitmap b = waveformManager.drawBitmap(mHeight);
        mWaveformImg.setImageBitmap(b);

        mView.findViewById(R.id.waveform_container).setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.GONE);
        mEditButton.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu != null) {
            MenuItem searchItem = menu.findItem(R.id.action_search);
            if (searchItem != null) {
                searchItem.setVisible(false);
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
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mSoundBean.setSoundPath(Environment.getExternalStorageDirectory() + Constants.PATH_APP + "/" + Constants.PATH_SOUND + "/" + mItemId + "_record.3gpp");
                mView.findViewById(R.id.record_normal_fl_id).setVisibility(View.GONE);
                mView.findViewById(R.id.record_press_fl_id).setVisibility(View.VISIBLE);
                VoiceRecorder.getInstance().startRecord(mSoundBean.getSoundPath());
                Utils.scaleView(view, 1.5f, 1f);
                mTimer.start();
                break;
            case MotionEvent.ACTION_UP:
                mView.findViewById(R.id.record_normal_fl_id).setVisibility(View.VISIBLE);
                mView.findViewById(R.id.record_press_fl_id).setVisibility(View.GONE);
                VoiceRecorder.getInstance().endRecord();
                mTimer.cancel();
                PopapDatabase.getInstance(getActivity()).updateSoundPath(mItemId, mSoundBean.getSoundPath());
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            default:
                return false;
        }
        return true;
    }
}
