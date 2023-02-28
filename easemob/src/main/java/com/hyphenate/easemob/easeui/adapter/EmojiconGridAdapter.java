package com.hyphenate.easemob.easeui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.hyphenate.easemob.easeui.domain.EaseEmojicon;
import com.hyphenate.easemob.easeui.domain.EaseEmojicon.Type;
import com.hyphenate.easemob.easeui.widget.sticker.widget.StickerGridItemView;

import java.util.List;

public class EmojiconGridAdapter extends ArrayAdapter<EaseEmojicon>{

    public EmojiconGridAdapter(Context context, int textViewResourceId, List<EaseEmojicon> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = new StickerGridItemView(getContext());
        }
        EaseEmojicon easeEmojicon = getItem(position);
        if (easeEmojicon.getType() == Type.NORMAL){
            ((StickerGridItemView)convertView).setNumColumns(7);
        }else{
            ((StickerGridItemView)convertView).setNumColumns(4);
        }

        ((StickerGridItemView)convertView).setSticker(getItem(position));
        return convertView;
    }
}
