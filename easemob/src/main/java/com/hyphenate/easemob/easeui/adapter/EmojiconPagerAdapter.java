package com.hyphenate.easemob.easeui.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class EmojiconPagerAdapter extends PagerAdapter{

    private List<View> views;

    public EmojiconPagerAdapter(List<View> views) {
        this.views = views;
    }

    public void clear(){
        views.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (views == null){
            return 0;
        }
        return views.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        if (views == null) return null;
        container.addView(views.get(position));
        return views.get(position);
    }

    @Override
//    public Object instantiateItem(View arg0, int arg1) {
//        View view = views.get(arg1);
//        ((ViewPager) arg0).addView(view);
//        return view;
//    }



    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
//        return null != views && views.size() == 0 ? POSITION_NONE : super.getItemPosition(object);
        return POSITION_NONE;
//
//        if (views != null && views.size()==0) {
//            return POSITION_NONE;
//        }
//        return super.getItemPosition(object);
    }
}
