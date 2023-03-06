package com.hyphenate.easemob.im.officeautomation.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.ui.BaseDialogFragment;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.easemob.im.officeautomation.adapter.ItemsDialogAdapter;
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class ItemsDialogFragment extends BaseDialogFragment implements View.OnClickListener{

    private RecyclerView recyclerView;
    private List<String> items;
    private ItemsDialogAdapter adapter;
    private OnItemSelectListener listener;

    @Override
    public void onStart() {
        super.onStart();
        //宽度填满，高度自适应
        try {
            Window dialogWindow = getDialog().getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialogWindow.setAttributes(lp);

            View view = getView();
            if(view != null) {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                if(params instanceof FrameLayout.LayoutParams) {
                    int margin = (int) EaseCommonUtils.dip2px(mContext, 30);
                    ((FrameLayout.LayoutParams) params).setMargins(margin, 0, margin, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int showAllowingStateLoss(@NonNull FragmentTransaction transaction, @Nullable String tag) {
        try {
            Field dismissed = ItemsDialogFragment.class.getDeclaredField("mDismissed");
            dismissed.setAccessible(true);
            dismissed.set(this, false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            Field shown = ItemsDialogFragment.class.getDeclaredField("mShownByMe");
            shown.setAccessible(true);
            shown.set(this, true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        transaction.add(this, tag);
        try {
            Field viewDestroyed = ItemsDialogFragment.class.getDeclaredField("mViewDestroyed");
            viewDestroyed.setAccessible(true);
            viewDestroyed.set(this, false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        int mBackStackId = transaction.commitAllowingStateLoss();
        try {
            Field backStackId = ItemsDialogFragment.class.getDeclaredField("mBackStackId");
            backStackId.setAccessible(true);
            backStackId.set(this, mBackStackId);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return mBackStackId;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_items;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ItemsDialogAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setListener(new ItemsDialogAdapter.OnItemClickListener() {
            @Override
            public void onClickItem(int position) {
                if(listener != null){
                    dismiss();
                    listener.onSelect(position);
                }
            }
        });

        if(items != null && items.size() > 0){
            adapter.setData(items);
        }
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
    }

    private void setItems(List<String> items){
        this.items = items;
    }

    private void setItemClickListener(OnItemSelectListener listener){
        this.listener = listener;
    }

    public interface OnItemSelectListener {
        void onSelect(int position);
    }


    public static class Builder {
        public BaseActivity context;
        private Bundle bundle;
        private List<String> items;
        private OnItemSelectListener listener;

        public Builder(BaseActivity context) {
            this.context = context;
        }

        public Builder setArgument(Bundle bundle) {
            this.bundle = bundle;
            return this;
        }

        public Builder setItems(String[] array){
            items = Arrays.asList(array);
            return this;
        }

        public Builder setItemSelectListener(OnItemSelectListener listener){
            this.listener = listener;
            return this;
        }

        public ItemsDialogFragment build() {
            ItemsDialogFragment fragment = getFragment();
            fragment.setItems(items);
            fragment.setItemClickListener(listener);
            fragment.setArguments(bundle);
            return fragment;
        }

        protected ItemsDialogFragment getFragment() {
            return new ItemsDialogFragment();
        }

        public ItemsDialogFragment show() {
            ItemsDialogFragment fragment = build();
            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragment.showAllowingStateLoss(transaction, null);
            return fragment;
        }
    }
}
