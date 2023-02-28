package com.hyphenate.easemob.im.mp.widget.loc;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.easemob.R;
import java.util.ArrayList;
import java.util.List;

public class LocAvatarGroup extends LinearLayout {

    private List<ImageView> avatarLists = new ArrayList<>();
    private LocImageClickListener listener;

    public LocAvatarGroup(Context context) {
        this(context, null);
    }

    public LocAvatarGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocAvatarGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);
    }

    public void setListener(LocImageClickListener listener) {
        this.listener = listener;
    }

    public void addLocImageView(String username, String avatarUrl, double lat, double lng, float radius, float direction) {
        LocImageView imgView = createImageView();
        imgView.setUsername(username);
        imgView.setLat(lat);
        imgView.setLng(lng);
        imgView.setRadius(radius);
        imgView.setDirection(direction);
        avatarLists.add(imgView);
        addView(imgView);
        Glide.with(this).load(avatarUrl).apply(RequestOptions.circleCropTransform().error(R.drawable.ease_default_avatar)).into(imgView);
    }

    public void addLocImageView(String username, int avatarRes, double lat, double lng, float radius, float direction) {
        LocImageView imgView = createImageView();
        imgView.setUsername(username);
        imgView.setLat(lat);
        imgView.setLng(lng);
        imgView.setRadius(radius);
        imgView.setDirection(direction);
        avatarLists.add(imgView);
        addView(imgView);
        Glide.with(this).load(avatarRes).apply(RequestOptions.circleCropTransform()).into(imgView);
    }

    private LocImageView createImageView() {
        LocImageView imgView = new LocImageView(getContext());
        int pxVal = getResources().getDimensionPixelSize(R.dimen.dp_45);
        LayoutParams params = new LayoutParams(pxVal, pxVal);
        MarginLayoutParams layoutParams = new MarginLayoutParams(params);
        int marginVal = getResources().getDimensionPixelSize(R.dimen.dp_2);

        layoutParams.leftMargin = marginVal;
        layoutParams.topMargin = marginVal;
        layoutParams.rightMargin = marginVal;
        layoutParams.bottomMargin = marginVal;

        imgView.setLayoutParams(layoutParams);
        imgView.setClickable(true);
        imgView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(imgView);
                }
            }
        });
        return imgView;
    }


    public interface LocImageClickListener {
        void onClick(LocImageView locIV);
    }

}
