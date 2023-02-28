package com.hyphenate.easemob.easeui.widget.listview.check;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public abstract class CheckViewModel<T> {

    protected Context context;
    protected View contentView;
    protected T data;
    protected Bundle bundle;

    public CheckViewModel(Context context) {
        this.context = context;
        contentView = LayoutInflater.from(context).inflate(getLayout(), null);
        initView();
    }

    public CheckViewModel(Context context, Bundle bundle) {
        this.context = context;
        this.bundle = bundle;
        contentView = LayoutInflater.from(context).inflate(getLayout(), null);
        initView();
    }

    protected abstract int getLayout();
    protected abstract void initView();

    public void setData(T data) {
        this.data = data;
        setDataToView();
    }

    protected abstract void setDataToView();

    public View getContentView() {
        return contentView;
    }

}
