package com.hyphenate.easemob.im.mp.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 项目中经常遇到短信验证等计时的button，特此封装一个timerButton类
 * 1-provide duration setting
 * -提供计时时长设置
 * 2-provide forward and reverse timing mode，default reverse mode；
 * -提供正序和逆序计时，默认逆序倒数计时
 * 3-provide timing start callback，it receive a variable of boolean type to decide whether response onClick event this time；
 * -提供计时开始回调，接收布尔值，return true则点击开始计时；return false则不开始，用于一些场景业务逻辑的判断需要，比如两次输入密码是否一致后发送验证码
 * 4-provide timing finish callback
 * -提供计时完成回调onTimeFinish
 * 5-provide every second callback
 * -提供每一秒计时回调onEverySecond
 * 6-provide public method to control start and cancel
 * -提供开始计时和结束计时方法
 * How to use
 * -1，setTimerDuration;2,.autoMode() or .manusalMode();3,setOnEverySecondeLIstener;
 * -使用方法：1，setTimerDuration；2，autoMode（自动或手动）；3，setOnEverySecondListener；
 * Created by TT on 2016/10/30.
 */
@SuppressLint("AppCompatCustomView")
public class TimerButton extends Button{

	private int duration = 10;//记录用户设置的计时时长，默认30秒
	private int sec = 10;//计时时长默认30秒
	public static int ALONG = 11;
	public static int INVERSE = 22;
	public int timingMode = 22;
	private boolean controlMode = false;//手动控制计时还是自动,默认自动
	private boolean isTiming = false;
	private int alongSec;
	private Timer timer;
	private TimerTask task;

	public TimerButton(Context context) {
		super(context);
	}

	public TimerButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TimerButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 1:
					alongSec ++;
					if (alongSec < sec) {
						if (onEverySecondListener != null) {
							onEverySecondListener.onEverySecond(alongSec);
						}
					} else {
						finishTiming();
					}
					break;
				case 2:
					//倒序计时
					sec--;
					if (sec > 0) {
						if (onEverySecondListener != null) {
							onEverySecondListener.onEverySecond(sec);
						}
					} else {
						finishTiming();
					}
					break;
			}
		}
	};

	public void autoMode() {
		controlMode = false;
	}
	public void manualMode() {
		controlMode = true;
	}
	public void go() {
		timeRunning();
	}
	public void stop() {
		finishTiming();
	}
	public boolean isTiming() {
		return isTiming;
	}
	/**
	 * 设置计时时长
	 * @param duration
	 */
	public void setTimerDuration (int duration) {
		if (duration <= 0) {
			throw new RuntimeException("TimerButton exception:duration illegal, please set dutation > 0");
		}
		sec = duration;
		this.duration = duration;
	}

	/**
	 * 设置计时模式，正序或倒序
	 * @param timingMode
	 */
	public void setTimingMode(int timingMode) {
		this.timingMode = timingMode;
	}
	/**
	 * 完成计时，初始化各参数，调整回调
	 */
	private void finishTiming() {
		isTiming = false;
		setClickable(true);
		if (onEverySecondListener != null) {
			onEverySecondListener.onTimingFinish();
		}
		if (timer != null) {
			timer.cancel();
		}
		timer = null;
		task = null;
		alongSec = 0;
		sec = duration;
	}

	public interface OnEverySecondListener{
		boolean onTimingStart();
		void onEverySecond(int second);
		void onTimingFinish();
	}
	private OnEverySecondListener onEverySecondListener;//每秒回调监听器
	/**
	 * 设置每秒回调监听
	 * @param listener
	 */
	public void setOnEverySecondListener(OnEverySecondListener listener) {
		onEverySecondListener = listener;
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onEverySecondListener.onTimingStart()) {
					if (!controlMode) {//自动模式则直接开始计时
						timeRunning();
					}
				}
			}
		});
	}
	public void timeRunning() {
		isTiming = true;
		setClickable(false);
		timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				switch (timingMode) {
					//正序
					case 11:
						handler.sendEmptyMessage(1);
						break;
					//倒序
					case 22:
						handler.sendEmptyMessage(2);
						break;
				}
			}
		};
		timer.schedule(task, 1000, 1000);
		switch (timingMode) {
			case 11://正序
				onEverySecondListener.onEverySecond(alongSec);
				break;
			case 22://逆序
				onEverySecondListener.onEverySecond(sec);
				break;
		}
	}
}
