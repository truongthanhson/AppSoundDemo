package com.sontruong.appsound;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.sontruong.appsound.soundfile.SamplePlayer;
import com.sontruong.appsound.soundfile.SoundFile;
import com.sontruong.appsound.soundfile.WaveformManager;
import com.sontruong.appsound.utils.AndroidUtilities;
import com.sontruong.appsound.view.AudioWaveFormTimelineView;

import java.io.File;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int FILE_SELECT_CODE = 0;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions(this);
        setContentView(R.layout.activity_main);
        findViewById(R.id.rew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
        mTimelineView = (AudioWaveFormTimelineView) findViewById(R.id.timeline);
        mTimelineView.setDelegate(new AudioWaveFormTimelineView.AudioWaveFormTimelineViewDelegate() {
            @Override
            public void onLeftProgressChanged(float progress) {
                Log.e("sontt","left -- >" + progress);
                mSegmentPos[0] = progress;
                handlePause();
            }

            @Override
            public void onMidleProgressChanged(float progress) {
                Log.e("sontt","middle -- >" + progress);
                mSegmentPos[1] = progress;
                handlePause();
            }

            @Override
            public void onRightProgressChanged(float progress) {
                Log.e("sontt","right -- >" + progress);
                mSegmentPos[2] = progress;
                handlePause();
            }
        });
        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playNextSegment();
            }
        });

        findViewById(R.id.ffwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play2NextSegment();
            }
        });

        waveformManager = new WaveformManager(this);
        mHandler = new Handler();
        mFilePath = getFilePath();
        loadFromFile(mFilePath);
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
       ((ImageView)findViewById(R.id.bitmap)).setImageBitmap(b);
    }

    private void loadFromFile(String mFilename) {
        mFile = new File(mFilename);
        mLoadingLastUpdateTime = getCurrentTime();
        mLoadingKeepGoing = true;
        mProgressDialog = new ProgressDialog(this);
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


    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public String getFilePath() {
        return Environment.getExternalStorageDirectory() + "/son.mp3";
    }

    /** Called when the activity is finally destroyed. */
    @Override
    protected void onDestroy() {
        Log.v("Ringdroid", "EditActivity OnDestroy");

        mLoadingKeepGoing = false;
        closeThread(mLoadSoundFileThread);
        mLoadSoundFileThread = null;
        if(mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        if(mAlertDialog != null) {
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

    private void closeThread(Thread thread) {
        if (thread != null && thread.isAlive()) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }
    }


    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d("sontt", "File Uri: " + uri.toString());
                    // Get the path
                    String path = null;
                    try {
                        path = getPath(this, uri);
                        mFilePath = path;
                        loadFromFile(mFilePath);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    Log.d("sontt", "File Path: " + path);
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
}
