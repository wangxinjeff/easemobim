package com.hyphenate.easemob.easeui.widget.chatrow;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.domain.EaseEmojicon;
import com.hyphenate.easemob.easeui.glide.GlideUtils;

/**
 * big emoji icons
 *
 */
public class EaseChatRowBigExpression extends EaseChatRowText{

    private ImageView imageView;


    public EaseChatRowBigExpression(Context context, EMMessage message, int position, EaseMessageAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_received_bigexpression : R.layout.ease_row_sent_bigexpression, this);
    }

    @Override
    protected void onFindViewById() {
        percentageView = (TextView) findViewById(R.id.percentage);
        imageView = (ImageView) findViewById(R.id.image);
    }


    @Override
    public void onSetUpView() {
        String emojiconId = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null);
        EaseEmojicon emojicon = null;
        if(EaseUI.getInstance().getEmojiconInfoProvider() != null){
            emojicon =  EaseUI.getInstance().getEmojiconInfoProvider().getEmojiconInfo(emojiconId);
        }
        if(emojicon != null){
            if(emojicon.getBigIcon() != 0){
//                Glide.with(activity).load(emojicon.getBigIcon()).apply(RequestOptions.errorOf(R.drawable.ease_default_expression)).into(imageView);
                GlideUtils.load(activity, emojicon.getBigIcon(), imageView);
            }else if(emojicon.getBigIconPath() != null){
//                Glide.with(activity).load(emojicon.getBigIconPath()).apply(RequestOptions.errorOf(R.drawable.ease_default_expression)).into(imageView);
                GlideUtils.load(activity, emojicon.getBigIconPath(), imageView);
            }else{
                imageView.setImageResource(R.drawable.ease_default_expression);
            }
        }
    }
}
