package com.hyphenate.easemob.im.officeautomation.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.utils.AppUtil;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;

import java.lang.ref.WeakReference;
import java.util.Calendar;


/**
 * Created by qby on 2017/9/19.
 * <p>
 * activity基类
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "BaseFragment";
    public static final int MIN_CLICK_DELAY_TIME = 1000;//这里设置不能超过多长时间
    private long lastClickTime = 0;
    /**
     * 视图是否已经初初始化
     */
    protected boolean isInit = false;
    protected boolean isLoad = false;
    public Activity activity;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        WeakReference<Activity> reference = new WeakReference<>(getActivity());
        activity = reference.get();
        View contentView = inflater.inflate(getLayout(), container, false);
        initView(contentView);
        initListener();
        initData();
        isInit = true;
        isCanLoadData();
        return contentView;
    }

    public abstract int getLayout();

    /**
     * 初始化控件
     *
     * @param view
     */
    public void initView(View view) {

    }

    /**
     * 初始化监听
     */
    public void initListener() {

    }

    /**
     * 初始化数据
     */
    public void initData() {

    }

    @Override
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            AppUtil.hideInputMethod(activity);
            innerClick(view.getId());
        }
    }

    /**
     * 子类继承实现点击回调
     *
     * @param id 控件ID
     */
    public void innerClick(int id) {

    }

    @Override
    public void onStart() {
        super.onStart();
        //设置状态栏颜色
//        ActivityUtil.setStatusBarUpper(activity, R.color.title_black);
    }

    /**
     * 视图是否已经对用户可见，系统的方法
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isCanLoadData();
    }

    /**
     * 是否可以加载数据
     * 可以加载数据的条件：
     * 1.视图已经初始化
     * 2.视图对用户可见
     */
    private void isCanLoadData() {
        if (!isInit) {
            return;
        }

        if (getUserVisibleHint()) {
            lazyLoad();
            isLoad = true;
        } else {
            if (isLoad) {
                stopLoad();
            }
        }
    }

    /**
     * 当视图初始化并且对用户可见的时候去真正的加载数据
     */
    protected void lazyLoad() {
//        MPLog.e(TAG, "baseLazyLoad");
    }

    ;

    /**
     * 当视图已经对用户不可见并且加载过数据，如果需要在切换到其他页面时停止加载数据，可以覆写此方法
     */
    protected void stopLoad() {
    }

    /**
     * 视图销毁的时候讲Fragment是否初始化的状态变为false
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInit = false;
        isLoad = false;

    }

    /**
     * 显示加载框
     */
    public void showProgressDialog() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.show();
        View inflate = View.inflate(activity, R.layout.dialog_loading, null);
        progressDialog.setContentView(inflate);
        progressDialog.setCancelable(false);
    }

    /**
     * 隐藏正在展示的加载框
     */
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

}
