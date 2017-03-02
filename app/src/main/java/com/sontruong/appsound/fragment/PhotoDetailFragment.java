
package com.sontruong.appsound.fragment;

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
import com.sontruong.appsound.soundfile.SamplePlayer;
import com.sontruong.appsound.soundfile.SoundFile;
import com.sontruong.appsound.soundfile.WaveformManager;
import com.sontruong.appsound.utils.AndroidUtilities;
import com.sontruong.appsound.soundfile.VoiceRecorder;
import com.sontruong.appsound.utils.Constants;
import com.sontruong.appsound.utils.CountUpTimer;
import com.sontruong.appsound.utils.Database;
import com.sontruong.appsound.utils.StringUtils;
import com.sontruong.appsound.view.AudioWaveFormTimelineView;

import java.io.File;

import java.util.Timer;
import java.util.TimerTask;

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
    private ImageView mWaveformImg;

    private WaveformManager waveformManager;
    private SoundFile mSoundFile;
    private int mHeight;
    private AudioWaveFormTimelineView mTimelineView;
    private Handler mHandler;
    private File mFile;
    private boolean mLoadingKeepGoing;
    private String mFilePath;
    private SamplePlayer mPlayer;
    private AlertDialog mAlertDialog;
    private ProgressDialog mProgressDialog;
    private long mLoadingLastUpdateTime;
    private Thread mLoadSoundFileThread;
    private float[] mSegmentPos = new float[]{0.0f, 0.5f, 1.0f};
    private String mRecordPath;
    private CountUpTimer mTimer;
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
        mImageView.setOnClickListener(this);
        Glide.with(this)
                .load(Database.getInstance().getPhotoList(mPhotoId))
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mImageView);

        mEditButton = (ImageButton) mView.findViewById(R.id.photo_edit_btn_id);
        mEditButton.setOnClickListener(this);

        mWaveformImg = (ImageView)mView.findViewById(R.id.waveform);

        mTimelineView = (AudioWaveFormTimelineView) mView.findViewById(R.id.timeline);
        mTimelineView.setDelegate(new AudioWaveFormTimelineView.AudioWaveFormTimelineViewDelegate() {
            @Override
            public void onLeftProgressChanged(float progress) {
                mSegmentPos[0] = progress;
                handlePause();
            }

            @Override
            public void onMidleProgressChanged(float progress) {
                mSegmentPos[1] = progress;
                handlePause();
            }

            @Override
            public void onRightProgressChanged(float progress) {
                mSegmentPos[2] = progress;
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
        mRecordButton = (FrameLayout) mView.findViewById(R.id.record_fl_id);
        mRecordButton.setOnTouchListener(this);
        mRecordPath = Database.getInstance().getPhotoRecord(mPhotoId);

        mTimerText = (TextView) mView.findViewById(R.id.timer_tv_id);

        mTimer = new CountUpTimer(Constants.MAX_AUDIO_RECORD_TIME_MS) {
            public void onTick(int second) {
                mTimerText.setText(String.valueOf(formatTime(second)));
            }
        };
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
            case R.id.photo_detail_img_id:
                onWaveformShow();
                break;
            default:
                break;
        }
    }

    private void onWaveformShow() {
        if(waveformAvailable()){
            loadFromFile(mRecordPath);
        }else{
            mView.findViewById(R.id.waveform_container).setVisibility(View.GONE);
            mImageView.setVisibility(View.VISIBLE);
            showDialogWaveformNotAvailable();
        }
    }

    private void showDialogWaveformNotAvailable() {

    }

    private boolean waveformAvailable() {
        return !StringUtils.isNullOrEmpty(mRecordPath);
    }

    private void loadFromFile(String audioPath) {
        mFile = new File(audioPath);
        mLoadingLastUpdateTime = getCurrentTime();
        mLoadingKeepGoing = true;
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle(R.string.progress_dialog_loading);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                    }
                });
        mProgressDialog.show();

        final SoundFile.ProgressListener listener =
                new SoundFile.ProgressListener() {
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
                mTimer.start();
                break;
            case MotionEvent.ACTION_UP:
                mView.findViewById(R.id.record_normal_fl_id).setVisibility(View.VISIBLE);
                mView.findViewById(R.id.record_press_fl_id).setVisibility(View.GONE);
                VoiceRecorder.getInstance().endRecord();
                Database.getInstance().updatePhotoRecord(mPhotoId, mRecordPath);
                mTimer.cancel();
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

    public String formatTime(int totalSecs) {
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;
        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return timeString;
    }
}
