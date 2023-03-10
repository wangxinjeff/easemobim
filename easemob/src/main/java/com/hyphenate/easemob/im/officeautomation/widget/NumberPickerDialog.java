package com.hyphenate.easemob.im.officeautomation.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.hyphenate.easemob.R;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 01/08/2018
 * 原载于 http://www.gaohaiyan.com/1824.html
 */

public class NumberPickerDialog extends AlertDialog implements DialogInterface.OnClickListener, NumberPicker.OnValueChangeListener {

	private final String maxValue = "最大值";
	private final String minValue = "最小值";
	private final String currentValue = "当前值";

	private final NumberPicker mNumberPicker;
	private final NumberPicker.OnValueChangeListener mCallback;

	private int newVal;
	private int oldVal;

	/**
	 * @param context            上下文
	 * @param callBack           回调器
	 * @param maxValueNumber     最大值
	 * @param minValueNumber     最小值
	 * @param currentValueNumber 当前值
	 */
	public NumberPickerDialog(Context context, NumberPicker.OnValueChangeListener callBack, int maxValueNumber, int minValueNumber, int currentValueNumber) {
		super(context, 0);

		mCallback = callBack;

		setIcon(0);
		setTitle("设置小时");


		Context themeContext = getContext();
		setButton(BUTTON_POSITIVE, context.getString(R.string.ok), this);
		setButton(BUTTON_NEGATIVE, context.getString(R.string.cancel), this);

		LayoutInflater inflater = (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.view_number_picker_dialog, null);
		setView(view);
		mNumberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);

		mNumberPicker.setMaxValue(maxValueNumber);
		mNumberPicker.setMinValue(minValueNumber);
		mNumberPicker.setValue(currentValueNumber);
		mNumberPicker.setOnValueChangedListener(this);
	}

	@Override
	public Bundle onSaveInstanceState() {
		Bundle state = super.onSaveInstanceState();
		state.putInt(maxValue, mNumberPicker.getMaxValue());
		state.putInt(minValue, mNumberPicker.getMinValue());
		state.putInt(currentValue, mNumberPicker.getValue());
		return state;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int max = savedInstanceState.getInt(maxValue);
		int min = savedInstanceState.getInt(minValue);
		int cur = savedInstanceState.getInt(currentValue);
		mNumberPicker.setMaxValue(max);
		mNumberPicker.setMinValue(min);
		mNumberPicker.setValue(cur);
	}

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		this.oldVal = oldVal;
		this.newVal = newVal;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
			case BUTTON_POSITIVE:
				mCallback.onValueChange(mNumberPicker, oldVal, newVal);
				break;
		}
	}

	/**
	 * <b>功能</b>: setCurrentValue，设置NumberPicker的当前值<br/>
	 *
	 * @return
	 * @author : weiyou.com <br/>
	 */
	public NumberPickerDialog setCurrentValue(int value) {

		mNumberPicker.setValue(value);
		newVal = value;

		return this;
	}

}
