package com.hyphenate.easemob.easeui.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;

import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.widget.EaseTitleBar;

public abstract class EaseBaseFragment extends Fragment{
    protected EaseTitleBar titleBar;
    protected InputMethodManager inputMethodManager;
    private boolean switchPage = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //noinspection ConstantConditions
        titleBar = (EaseTitleBar) getView().findViewById(R.id.title_bar);

        initView();
        setUpView();
    }

    public void showTitleBar(){
        if(titleBar != null){
            titleBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideTitleBar(){
        if(titleBar != null){
            titleBar.setVisibility(View.GONE);
        }
    }

    protected void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    protected abstract void initView();

    protected abstract void setUpView();

    @Override
    public void onDestroy() {
        super.onDestroy();
        inputMethodManager = null;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if(!switchPage){
            switchPage = true;
            super.startActivityForResult(intent, requestCode);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        if(!switchPage) {
            switchPage = true;
            super.startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        switchPage = false;
        super.onResume();
    }
}
