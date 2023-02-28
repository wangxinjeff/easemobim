/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hyphenate.easemob.easeui.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.FragmentActivity;

import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.utils.FixMemLeak;

@SuppressLint({"NewApi", "Registered"})
public class EaseBaseActivity extends FragmentActivity {

    protected InputMethodManager inputMethodManager;
    private boolean switchPage = false;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        //http://stackoverflow.com/questions/4341600/how-to-prevent-multiple-instances-of-an-activity-when-it-is-launched-with-differ/
        // should be in launcher activity, but all app use this can avoid the problem
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                getWindow().setStatusBarColor(getColor(R.color.bg_top_bar_2));
//            } else {
////                setTranslucentStatus(true);
//            }
//        }

        setWindowStatusBarColor(this, R.color.bg_top_bar_2);
        setAndroidNativeLightStatusBar(this, true);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    protected void setStatusBarGray(boolean isGray){
        if (isGray) {
            setWindowStatusBarColor(this, R.color.gray_status_bar);
            setAndroidNativeLightStatusBar(this, false);
        } else {
            setWindowStatusBarColor(this, R.color.bg_top_bar_2);
            setAndroidNativeLightStatusBar(this, true);
        }
    }

    public void setWindowStatusBarColor(Activity activity, int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(activity.getResources().getColor(colorResId));

                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }


    @Override
    protected void onResume() {
        switchPage = false;
        super.onResume();
        // cancel the notification
        EaseUI.getInstance().getNotifier().reset();
    }

    protected void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FixMemLeak.create().fixLeak(inputMethodManager);
    }

    /**
     * back
     *
     * @param view
     */
    public void back(View view) {
        finish();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        switchPage = true;
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void startActivity(Intent intent) {
        switchPage = true;
        super.startActivity(intent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (switchPage) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
