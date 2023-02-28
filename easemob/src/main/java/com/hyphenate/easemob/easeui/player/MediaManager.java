package com.hyphenate.easemob.easeui.player;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;

import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.imlibs.easeui.prefs.PreferenceUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by liyuzhao on 16/9/28.
 */
public class MediaManager {

    private static MediaManager sMediaManager;

    public static MediaManager getManager() {
        if (sMediaManager == null) {
            synchronized (MediaManager.class) {
                sMediaManager = new MediaManager();
            }
        }
        return sMediaManager;
    }

    private MediaManager(){
        if (mAudioManager == null){
            mAudioManager = (AudioManager) EaseUI.getInstance().getContext().getSystemService(Context.AUDIO_SERVICE);
        }

    }

    /**
     *  扬声器状态监听器
     *  isSpeakerOn 扬声器是否打开
     */
    public static interface onSpeakerListener {
        void onSpeakerChanged(boolean isSpeakerOn);
    }

    private onSpeakerListener mOnSpeakerListener;
    public void setOnSpeakerListener(onSpeakerListener listener){
        if (listener != null){
            mOnSpeakerListener = listener;
        }
    }

    private SensorManager mSensorManager;  // 传感器管理器
    private Sensor mProximiny;// 传感器实例
    private boolean isEarpiece;
    // 屏幕开关
//    private PowerManager localPowerManager = null; // 电源管理对象
//    private PowerManager.WakeLock localWakeLock = null; // 电源锁

    private boolean isFirstSetSpeaker = true;

    /**
     * 注册距离传感器监听
     * @param context
     */
    public void register(Context context) {
        // 获取系统服务POWER_SERVICE, 返回一个PowerManager对象
//        localPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        // 获取PownerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是Logcat里用的Tag
//        localWakeLock = this.localPowerManager.newWakeLock(32, "MyPower"); // 第一个参数为电源锁级别，第二个是日志tag
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null && mDistanceSensorListener != null){
            mProximiny = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            mSensorManager.registerListener(mDistanceSensorListener, mProximiny, SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    /**
     * 取消注册距离传感器监听
     */
    public void unregister(){
        if (mSensorManager != null && mDistanceSensorListener != null){
            mSensorManager.unregisterListener(mDistanceSensorListener);
        }
        mSensorManager = null;
//        onDestory();
    }
//
//    /**
//     * 退出注销
//     */
//    public void onDestory(){
//        if(mSensorManager != null){
////            if (localWakeLock != null && localWakeLock.isHeld()){
////                localWakeLock.release(); // 释放电源锁，如果不释放finish这个activity后，仍然会有自动锁屏的效果
////            }
////            localWakeLock = null;
////            localPowerManager = null;
//        }
//    }

    /**
     * 距离传感器监听者
     */
    private SensorEventListener mDistanceSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float f_proximiny = event.values[0];
            if (isPlaying()){
                // 扬声器模式
                // 魅蓝E传感器得到的值竟然比最大值都要大
                if (f_proximiny >= mProximiny.getMaximumRange()) {
                    if (isFirstSetSpeaker){
                        isFirstSetSpeaker = false;
//                        if (localWakeLock != null) {
//                            localWakeLock.acquire(); // 申请设备电源锁
//                        }
                    } else {
                        setSpeakerPhoneOn(true);
                        if (mOnSpeakerListener != null){
                            mOnSpeakerListener.onSpeakerChanged(true);
                        }
//                        if (localWakeLock != null){
//                            if (localWakeLock.isHeld()){
//                                return;
//                            } else {
//                                localWakeLock.setReferenceCounted(false);
//                                localWakeLock.release(); // 释放设备电源锁
//                            }
//                        }
                    }
                } else {
                    setSpeakerPhoneOn(false);
                    if (mOnSpeakerListener != null){
                        mOnSpeakerListener.onSpeakerChanged(false);
                    }
//                    if (localWakeLock != null) {
//                        // 听筒模式
//                        if (localWakeLock.isHeld()){
//                            return;
//                        } else {
//                            localWakeLock.acquire(); // 申请设备电源锁
//                        }
//                    }
                }
            } else {
                if (f_proximiny == mProximiny.getMaximumRange()) {
                    setSpeakerPhoneOn(true);
                }
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    /**
     * 听筒、扬声器切换
     *
     * 注释： 敬那些年踩过的坑和那些网上各种千奇百怪坑比方案！！
     *
     * AudioManager设置声音类型有以下几种类型（调节音量用的是这个）:
     *
     * STREAM_ALARM 警报
     * STREAM_MUSIC 音乐回放即媒体音量
     * STREAM_NOTIFICATION 窗口顶部状态栏Notification,
     * STREAM_RING 铃声
     * STREAM_SYSTEM 系统
     * STREAM_VOICE_CALL 通话
     * STREAM_DTMF 双音多频,不是很明白什么东西
     *
     * ------------------------------------------
     *
     * AudioManager设置声音模式有以下几个模式（切换听筒和扬声器时setMode用的是这个）
     *
     * MODE_NORMAL 正常模式，即在没有铃音与电话的情况
     * MODE_RINGTONE 铃响模式
     * MODE_IN_CALL 接通电话模式 5.0以下
     * MODE_IN_COMMUNICATION 通话模式 5.0及其以上
     *
     * @param on
     */
    private void setSpeakerPhoneOn(boolean on){
        isEarpiece = PreferenceUtils.getInstance().isEarpieceOn();
        if (isEarpiece) return;

        // 获得当前类
        try {
            if (on){
                mAudioManager.setSpeakerphoneOn(true);
                mAudioManager.setMode(AudioManager.MODE_NORMAL);
//                mAudioManager.setMicrophoneMute(false);
                // 设置音量，解决有些机型切换后没声音或者声音突然变大的问题
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC), AudioManager.FX_KEY_CLICK);
            } else {
                mAudioManager.setSpeakerphoneOn(false);
                mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                // 5.0 以上
                // 设置音量，解决有些机型切换后没声音或者声音突然变大的问题
                mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        mAudioManager.getStreamMaxVolume(AudioManager.MODE_IN_COMMUNICATION), AudioManager.FX_KEY_CLICK);

//                mAudioManager.setMode(AudioManager.STREAM_MUSIC);
//            mAudioManager.setMicrophoneMute(true);
                int cur = current();
                if (cur < 1500) {
                    cur = 0;
                } else {
                    cur = cur - 1500;
                }

                playSound(filePath, cur, sOnCompletionListener, true);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


//
    }

    private int current(){
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()){
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 是否正在播放
     * @return 正在播放返回true，否则返回false
     */
    public boolean isPlaying(){
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    private MediaPlayer mMediaPlayer;
    private static boolean isPause;
    private MediaPlayer.OnCompletionListener sOnCompletionListener;
    private AudioManager mAudioManager;
    private String filePath;

    /**
     * 播放音乐
     * @param filePath 音乐文件路径
     * @param onCompletionListener 播放回调函数
     */
    public void playSound(String filePath, final int cur, MediaPlayer.OnCompletionListener onCompletionListener){
         this.playSound(filePath, cur, onCompletionListener, false);
    }

    /**
     * 播放音乐
     * @param filePath 音乐文件路径
     * @param onCompletionListener 播放回调函数
     */
    void playSound(String filePath, final int cur, MediaPlayer.OnCompletionListener onCompletionListener, boolean internal){
        this.filePath = filePath;
        try {
            if (mMediaPlayer == null){
                mMediaPlayer = new MediaPlayer();
                if (mMediaPlayer == null){
                    mMediaPlayer = getMediaPlayer(EaseUI.getInstance().getContext());

                    mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener(){

                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            mMediaPlayer.reset();
                            return false;
                        }
                    });
                }

            } else {
                mMediaPlayer.reset();
            }
            sOnCompletionListener = onCompletionListener;
            if(!internal){
                isEarpiece = PreferenceUtils.getInstance().isEarpieceOn();
                if (!isEarpiece) {
                    mAudioManager.setMode(AudioManager.MODE_NORMAL);
                    mAudioManager.setSpeakerphoneOn(true);
                } else {
                    mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                    mAudioManager.setSpeakerphoneOn(false);// 关闭扬声器
                    // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
                }
            }
//            mMediaPlayer.seekTo(cur);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (cur > 0){
                        mp.seekTo(cur);
                    }
                    mp.start();
                }
            });
//            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pause(){
//        if (mMediaPlayer != null && mMediaPlayer.isPlaying()){
//            mMediaPlayer.pause();
//            isPause = true;
//        }
    }


    public void resume(){
//        if (mMediaPlayer != null && isPause){
//            mMediaPlayer.start();
//            isPause = false;
//        }
    }

    public void release(){
        if (mMediaPlayer != null){
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void forceComplete(){
        if (sOnCompletionListener != null && mMediaPlayer != null && mMediaPlayer.isPlaying()){
            sOnCompletionListener.onCompletion(mMediaPlayer);
        }
        release();
    }

    MediaPlayer getMediaPlayer(Context context){

        MediaPlayer mediaplayer = new MediaPlayer();

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            return mediaplayer;
        }

        try {
            Class<?> cMediaTimeProvider = Class.forName( "android.media.MediaTimeProvider" );
            Class<?> cSubtitleController = Class.forName( "android.media.SubtitleController" );
            Class<?> iSubtitleControllerAnchor = Class.forName( "android.media.SubtitleController$Anchor" );
            Class<?> iSubtitleControllerListener = Class.forName( "android.media.SubtitleController$Listener" );

            Constructor constructor = cSubtitleController.getConstructor(Context.class, cMediaTimeProvider, iSubtitleControllerListener);

            Object subtitleInstance = constructor.newInstance(context, null, null);

            Field f = cSubtitleController.getDeclaredField("mHandler");

            f.setAccessible(true);
            try {
                f.set(subtitleInstance, new Handler());
            }
            catch (IllegalAccessException e) {return mediaplayer;}
            finally {
                f.setAccessible(false);
            }

            Method setsubtitleanchor = mediaplayer.getClass().getMethod("setSubtitleAnchor", cSubtitleController, iSubtitleControllerAnchor);

            setsubtitleanchor.invoke(mediaplayer, subtitleInstance, null);
            //Log.e("", "subtitle is setted :p");
        } catch (Exception ignored) {}

        return mediaplayer;
    }

    /**
     * 播放回调接口
     */
    public static interface PlayCallback{

        /** 音乐准备完毕 **/
        void onPrepared();

        /** 音乐播放完毕 **/
        void onComplete();

        void onStop();
    }


}
