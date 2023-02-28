package com.hyphenate.easemob.easeui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMError;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.model.EaseVoiceRecorder;
import com.hyphenate.easemob.easeui.player.MediaManager;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRowVoicePlayer;
import com.hyphenate.easemob.imlibs.easeui.prefs.PreferenceUtils;

/**
 * Voice recorder view
 *
 */
public class EaseVoiceRecorderView extends RelativeLayout {
    protected Context context;
    protected LayoutInflater inflater;
    protected Drawable[] micImages;
    protected EaseVoiceRecorder voiceRecorder;

    protected PowerManager.WakeLock wakeLock;
    protected ImageView micImage;
    protected TextView recordingHint;

    protected Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            // change image
            int index = msg.what;
            if (index < 0 || index > micImages.length - 1) {
                return;
            }
            micImage.setImageDrawable(micImages[index]);
        }
    };

    public EaseVoiceRecorderView(Context context) {
        super(context);
        init(context);
    }

    public EaseVoiceRecorderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EaseVoiceRecorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @SuppressLint("InvalidWakeLockTag")
    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.ease_widget_voice_recorder, this);

        micImage = findViewById(R.id.mic_image);
        recordingHint = findViewById(R.id.recording_hint);

        voiceRecorder = new EaseVoiceRecorder(micImageHandler);
        // animation resources, used for recording
//        micImages = new Drawable[] { getResources().getDrawable(R.drawable.ease_record_animate_01),
//                getResources().getDrawable(R.drawable.ease_record_animate_02),
//                getResources().getDrawable(R.drawable.ease_record_animate_03),
//                getResources().getDrawable(R.drawable.ease_record_animate_04),
//                getResources().getDrawable(R.drawable.ease_record_animate_05),
//                getResources().getDrawable(R.drawable.ease_record_animate_06),
//                getResources().getDrawable(R.drawable.ease_record_animate_07),
//                getResources().getDrawable(R.drawable.ease_record_animate_08),
//                getResources().getDrawable(R.drawable.ease_record_animate_09),
//                getResources().getDrawable(R.drawable.ease_record_animate_10),
//                getResources().getDrawable(R.drawable.ease_record_animate_11),
//                getResources().getDrawable(R.drawable.ease_record_animate_12),
//                getResources().getDrawable(R.drawable.ease_record_animate_13),
//                getResources().getDrawable(R.drawable.ease_record_animate_14), };
        micImages = new Drawable[] { getResources().getDrawable(R.drawable.mp_ic_record_anim_1),
                getResources().getDrawable(R.drawable.mp_ic_record_anim_2),
                getResources().getDrawable(R.drawable.mp_ic_record_anim_3),
                getResources().getDrawable(R.drawable.mp_ic_record_anim_4),
                getResources().getDrawable(R.drawable.mp_ic_record_anim_5),
                getResources().getDrawable(R.drawable.mp_ic_record_anim_6),
                getResources().getDrawable(R.drawable.mp_ic_record_anim_7)
        };

        wakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
    }

    private EaseVoiceRecorderCallback recorderCallback;

    /**
     * on speak button touched
     *
     * @param v
     * @param event
     */
    public boolean onPressToSpeakBtnTouch(View v, MotionEvent event, EaseVoiceRecorderCallback recorderCallback) {
        this.recorderCallback = recorderCallback;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                try {
                    MediaManager.getManager().forceComplete();
                    EaseChatRowVoicePlayer voicePlayer = EaseChatRowVoicePlayer.getInstance(context);
                    if (voicePlayer.isPlaying())
                        voicePlayer.stop();
                    v.setPressed(true);
                    startRecording();
                } catch (Exception e) {
                    v.setPressed(false);
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
                discardRecording();
                return true;
            case MotionEvent.ACTION_POINTER_1_DOWN:
                return true;
            case MotionEvent.ACTION_POINTER_1_UP:
                return true;
            case MotionEvent.ACTION_POINTER_INDEX_MASK:
                return true;
            case MotionEvent.ACTION_POINTER_2_DOWN:
                return true;
            case MotionEvent.ACTION_POINTER_2_UP:
                return true;
            case MotionEvent.ACTION_MOVE:
                if (event.getY() < 0) {
                    showReleaseToCancelHint();
                } else {
                    showMoveUpToCancelHint();
                }
                return true;
            case MotionEvent.ACTION_UP:
                v.setPressed(false);
                if (event.getY() < 0) {
                    // discard the recorded audio.
                    discardRecording();
                } else {
                    // stop recording and send voice file
                    try {
                        int length = stopRecoding();
                        if (length > 0) {
                            if (recorderCallback != null) {
                                recorderCallback.onVoiceRecordComplete(getVoiceFilePath(), length);
                            }
                        } else if (length == EMError.FILE_INVALID) {
                            Toast.makeText(context, R.string.Recording_without_permission, Toast.LENGTH_SHORT).show();
                        } else if (length == -1){}else {
                            Toast.makeText(context, R.string.The_recording_time_is_too_short, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, R.string.send_failure_please, Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            default:
                discardRecording();
                return false;
        }
    }

    public interface EaseVoiceRecorderCallback {
        /**
         * on voice record complete
         *
         * @param voiceFilePath
         *            录音完毕后的文件路径
         * @param voiceTimeLength
         *            录音时长
         */
        void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength);
    }

    public void startRecording() {
        resetRecordTimer();
        if (!EaseCommonUtils.isSdcardExist()) {
            Toast.makeText(context, R.string.Send_voice_need_sdcard_support, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            wakeLock.acquire();
            this.setVisibility(View.VISIBLE);
            recordingHint.setText(context.getString(R.string.move_up_to_cancel));
            recordingHint.setBackgroundColor(Color.TRANSPARENT);
            voiceRecorder.startRecording(context);

            startTimer();
        } catch (Exception e) {
            e.printStackTrace();
            if (wakeLock.isHeld())
                wakeLock.release();
            if (voiceRecorder != null)
                voiceRecorder.discardRecording();
            this.setVisibility(View.INVISIBLE);
            Toast.makeText(context, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void startTimer(){
        recordTimer.cancel();
        recordTimer.start();
    }

    private void cancelTimer(){
        recordTimer.cancel();
    }

    private CountDownTimer recordTimer;

    private void resetRecordTimer(){
        recordTimer = new CountDownTimer(PreferenceUtils.getInstance().getVoiceDuration() * 1000, 1000) {
            @SuppressLint("StringFormatMatches")
            @Override
            public void onTick(long millisUntilFinished) {
                int second = (int) (millisUntilFinished / 1000);
                if (second < 10){
                    recordingHint.setText(context.getString(R.string.remain_time, second));
                }
            }

            @Override
            public void onFinish() {
                // stop recording and send voice file
                try {
                    int length = stopRecoding();
                    if (length > 0) {
                        if (recorderCallback != null) {
                            recorderCallback.onVoiceRecordComplete(getVoiceFilePath(), length);
                        }
                    } else if (length == EMError.FILE_INVALID) {
                        Toast.makeText(context, R.string.Recording_without_permission, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, R.string.The_recording_time_is_too_short, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, R.string.send_failure_please, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    public void showReleaseToCancelHint() {
        recordingHint.setText(context.getString(R.string.release_to_cancel));
        recordingHint.setBackgroundResource(R.drawable.ease_recording_text_hint_bg);
    }

    public void showMoveUpToCancelHint() {
        recordingHint.setText(context.getString(R.string.move_up_to_cancel));
        recordingHint.setBackgroundColor(Color.TRANSPARENT);
    }

    public void discardRecording() {
        if (wakeLock.isHeld())
            wakeLock.release();
        try {
            cancelTimer();
            // stop recording
            if (voiceRecorder.isRecording()) {
                voiceRecorder.discardRecording();
                this.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
        }
    }

    public int stopRecoding() {
        this.setVisibility(View.INVISIBLE);
        if (wakeLock.isHeld())
            wakeLock.release();
        cancelTimer();
        return voiceRecorder.stopRecoding();
    }

    public String getVoiceFilePath() {
        return voiceRecorder.getVoiceFilePath();
    }

    public String getVoiceFileName() {
        return voiceRecorder.getVoiceFileName();
    }

    public boolean isRecording() {
        return voiceRecorder.isRecording();
    }

}
