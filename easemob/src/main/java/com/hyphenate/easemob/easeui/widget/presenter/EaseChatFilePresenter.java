package com.hyphenate.easemob.easeui.widget.presenter;

import android.content.Context;
import android.content.Intent;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.ui.EaseFilePreviewActivity;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRowFile;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by zhangsong on 17-10-12.
 */

public abstract class EaseChatFilePresenter extends EaseChatRowPresenter {

    private Context mContext;

    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, EaseMessageAdapter adapter) {
        this.mContext = cxt;
        return new EaseChatRowFile(cxt, message, position, adapter);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        getContext().startActivity(new Intent(getContext(), EaseFilePreviewActivity.class).putExtra("msg", message));
//        EMNormalFileMessageBody fileMessageBody = (EMNormalFileMessageBody) message.getBody();
//        String filePath = fileMessageBody.getLocalUrl();
//        File file = new File(filePath);
//        if (file.exists()) {
//            // open files if it exist
//            String suffix = "";
//            try{
//                String fileName = file.getName();
//                suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
//            }catch (Exception e){}
//            try{
//                EaseCommonUtils.openFileEx(file, EaseCommonUtils.getMap(suffix), mContext);
//            }catch (Exception e){
//                Toast.makeText(mContext, "未安装能打开此文件的软件", Toast.LENGTH_SHORT).show();
//            }
//
//        } else {
//            // download the file
//            getContext().startActivity(new Intent(getContext(), EaseShowNormalFileActivity.class).putExtra("msg", message));
//        }
        if (message.direct() == EMMessage.Direct.RECEIVE && !message.isAcked() && message.getChatType() == EMMessage.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
            } catch (HyphenateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }



}
