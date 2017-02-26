package com.sontruong.appsound.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sontruong.appsound.soundfile.SamplePlayer;
import com.sontruong.appsound.utils.AndroidUtilities;

/**
 * Created by Administrator on 26/02/2017.
 */

public class AudioWaveFormTimelineView extends View {
    private Paint paint;
    private Paint paint2;
    private float progressLeft = 0;
    private float progressRight = 1;
    private boolean pressedLeft = false;
    private boolean pressedRight = false;
    private float pressDx = 0;
    private AudioWaveFormTimelineViewDelegate delegate = null;
    private Bitmap mBitmap;
    private SamplePlayer mPlayer;

    public interface AudioWaveFormTimelineViewDelegate {
        void onLeftProgressChanged(float progress);
        void onRightProgressChanged(float progress);
    }

    public AudioWaveFormTimelineView(Context context) {
        super(context);
        init(context);
    }
    public AudioWaveFormTimelineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AudioWaveFormTimelineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private void init(Context context) {
        paint = new Paint();
        paint.setColor(0xff66d1ee);
        paint2 = new Paint();
        paint2.setColor(0x7f000000);
    }

    public void setBackground(Bitmap bitmap){
        this.mBitmap = bitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event == null) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();

        int width = getMeasuredWidth();
        int startX = (int)(width * progressLeft);
        int endX = (int)(width * progressRight);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int additionWidth = AndroidUtilities.dp(12);
            if (startX - additionWidth <= x && x <= startX + additionWidth && y >= 0 && y <= getMeasuredHeight()) {
                pressedLeft = true;
                pressDx = (int)(x - startX);
                getParent().requestDisallowInterceptTouchEvent(true);
                invalidate();
                return true;
            } else if (endX - additionWidth <= x && x <= endX + additionWidth && y >= 0 && y <= getMeasuredHeight()) {
                pressedRight = true;
                pressDx = (int)(x - endX);
                getParent().requestDisallowInterceptTouchEvent(true);
                invalidate();
                return true;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            if (pressedLeft) {
                pressedLeft = false;
                return true;
            } else if (pressedRight) {
                pressedRight = false;
                return true;
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (pressedLeft) {
                startX = (int)(x - pressDx);
                if (startX < 0) {
                    startX = 0;
                } else if (startX > endX) {
                    startX = endX;
                }
                progressLeft = (float)(startX) / (float)width;
                if (delegate != null) {
                    delegate.onLeftProgressChanged(progressLeft);
                }
                invalidate();
                return true;
            } else if (pressedRight) {
                endX = (int)(x - pressDx);
                if (endX < startX) {
                    endX = startX;
                } else if (endX > width) {
                    endX = width;
                }
                progressRight = (float)(endX) / (float)width;
                if (delegate != null) {
                    delegate.onRightProgressChanged(progressRight);
                }
                invalidate();
                return true;
            }
        }
        return false;
    }

    public void setDelegate(AudioWaveFormTimelineViewDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int startX = (int)(width * progressLeft);
        int endX = (int)(width * progressRight);

        canvas.save();
        canvas.clipRect(0, 0, width + AndroidUtilities.dp(4), height);

        canvas.drawRect(0, 0, startX, height, paint2);
        canvas.drawRect(endX + AndroidUtilities.dp(4), 0, + width + AndroidUtilities.dp(4), height, paint2);

        canvas.drawRect(startX, 0, startX + AndroidUtilities.dp(2), height, paint);
        canvas.drawRect(endX + AndroidUtilities.dp(2), 0, endX + AndroidUtilities.dp(4), height, paint);
        canvas.drawRect(startX + AndroidUtilities.dp(2), 0, endX + AndroidUtilities.dp(4), AndroidUtilities.dp(2), paint);
        canvas.drawRect(startX + AndroidUtilities.dp(2), height - AndroidUtilities.dp(2), endX + AndroidUtilities.dp(4), height, paint);
        canvas.restore();

        canvas.drawCircle(startX, getMeasuredHeight() / 2, AndroidUtilities.dp(7), paint);
        canvas.drawCircle(endX + AndroidUtilities.dp(4), getMeasuredHeight() / 2, AndroidUtilities.dp(7), paint);
    }
}