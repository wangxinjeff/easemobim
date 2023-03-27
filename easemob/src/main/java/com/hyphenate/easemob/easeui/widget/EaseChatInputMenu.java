package com.hyphenate.easemob.easeui.widget;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.domain.EaseEmojicon;
import com.hyphenate.easemob.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easemob.easeui.domain.ImageItem;
import com.hyphenate.easemob.easeui.utils.EaseSmileUtils;
import com.hyphenate.easemob.easeui.utils.ImageDataSource;
import com.hyphenate.easemob.easeui.widget.EaseChatExtendMenu.EaseChatExtendMenuItemClickListener;
import com.hyphenate.easemob.easeui.widget.EaseChatPrimaryMenuBase.EaseChatPrimaryMenuListener;
import com.hyphenate.easemob.easeui.widget.emojicon.EaseEmojiconMenu;
import com.hyphenate.easemob.easeui.widget.emojicon.EaseEmojiconMenuBase;
import com.hyphenate.easemob.easeui.widget.emojicon.EaseEmojiconMenuBase.EaseEmojiconMenuListener;

import java.util.ArrayList;
import java.util.List;

/**
 * input menu
 * 
 * including below component:
 *    EaseChatPrimaryMenu: main menu bar, text input, send button
 *    EaseChatExtendMenu: grid menu with image, file, location, etc
 *    EaseEmojiconMenu: emoji icons
 */
public class EaseChatInputMenu extends LinearLayout implements ImageDataSource.OnImagesLoadedListener {
    FrameLayout primaryMenuContainer, emojiconMenuContainer;
    protected EaseChatPrimaryMenuBase chatPrimaryMenu;
    protected EaseEmojiconMenuBase emojiconMenu;
    protected EaseChatExtendMenu chatExtendMenu;
    protected FrameLayout chatExtendMenuContainer;
    protected LayoutInflater layoutInflater;
    protected ImageItem lastestImage;
    public static ImageItem mLastRecommendImage;
    private EaseRecommendPhotoPop recommendPhotoPop = null;

    private Handler handler = new Handler();
    private ChatInputMenuListener listener;
    private Context context;
    private boolean inited;

    public EaseChatInputMenu(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public EaseChatInputMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EaseChatInputMenu(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.ease_widget_chat_input_menu, this);
        primaryMenuContainer = (FrameLayout) findViewById(R.id.primary_menu_container);
        emojiconMenuContainer = (FrameLayout) findViewById(R.id.emojicon_menu_container);
        chatExtendMenuContainer = (FrameLayout) findViewById(R.id.extend_menu_container);

         // extend menu
         chatExtendMenu = (EaseChatExtendMenu) findViewById(R.id.extend_menu);

         if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
             if (getContext() instanceof FragmentActivity){
                 FragmentActivity activity = (FragmentActivity) getContext();
                 new ImageDataSource(activity, null, this);
             }
         }
    }

    /**
     * init view 
     * 
     * This method should be called after registerExtendMenuItem(), setCustomEmojiconMenu() and setCustomPrimaryMenu().
     * @param emojiconGroupList --will use default if null
     */
    @SuppressLint("InflateParams")
    public void init(List<EaseEmojiconGroupEntity> emojiconGroupList) {
        if(inited){
            return;
        }
        // primary menu, use default if no customized one
        if(chatPrimaryMenu == null){
            chatPrimaryMenu = (EaseChatPrimaryMenu) layoutInflater.inflate(R.layout.ease_layout_chat_primary_menu, null);
        }
        primaryMenuContainer.addView(chatPrimaryMenu);

        // emojicon menu, use default if no customized one
        if(emojiconMenu == null){
            emojiconMenu = (EaseEmojiconMenu) layoutInflater.inflate(R.layout.ease_layout_emojicon_menu, null);
            if(emojiconGroupList == null){
                emojiconGroupList = new ArrayList<EaseEmojiconGroupEntity>();
            }
            ((EaseEmojiconMenu)emojiconMenu).init(emojiconGroupList);
        }
        emojiconMenuContainer.addView(emojiconMenu);

        processChatMenu();
        chatExtendMenu.init();
        
        inited = true;
    }
    
    public void init(){
        init(null);
    }
    
    /**
     * set custom emojicon menu
     * @param customEmojiconMenu
     */
    public void setCustomEmojiconMenu(EaseEmojiconMenuBase customEmojiconMenu){
        this.emojiconMenu = customEmojiconMenu;
    }
    
    /**
     * set custom primary menu
     * @param customPrimaryMenu
     */
    public void setCustomPrimaryMenu(EaseChatPrimaryMenuBase customPrimaryMenu){
        this.chatPrimaryMenu = customPrimaryMenu;
    }
    
    public EaseChatPrimaryMenuBase getPrimaryMenu(){
        return chatPrimaryMenu;
    }
    
    public EaseChatExtendMenu getExtendMenu(){
        return chatExtendMenu;
    }
    
    public EaseEmojiconMenuBase getEmojiconMenu(){
        return emojiconMenu;
    }

    public ImageItem getLastestImage(){
        return lastestImage;
    }

    /**
     * register menu item
     * 
     * @param name
     *            item name
     * @param drawableRes
     *            background of item
     * @param itemId
     *             id
     * @param listener
     *            on click event of item
     */
    public void registerExtendMenuItem(String name, int drawableRes, int itemId,
            EaseChatExtendMenuItemClickListener listener) {
        chatExtendMenu.registerMenuItem(name, drawableRes, itemId, listener);
    }

    /**
     * register menu item
     * 
     * @param nameRes
     *            resource id of item name
     * @param drawableRes
     *            background of item
     * @param itemId
     *             id
     * @param listener
     *            on click event of item
     */
    public void registerExtendMenuItem(int nameRes, int drawableRes, int itemId,
            EaseChatExtendMenuItemClickListener listener) {
        chatExtendMenu.registerMenuItem(nameRes, drawableRes, itemId, listener);
    }


    protected void processChatMenu() {
        // send message button
        chatPrimaryMenu.setChatPrimaryMenuListener(new EaseChatPrimaryMenuListener() {

            @Override
            public boolean onSendBtnClicked(String content) {
                if (listener != null)
                    return listener.onSendMessage(content);
                return true;
            }

            @Override
            public void onTyping(CharSequence s, int start, int before, int count) {
                if (listener != null) {
                    listener.onTyping(s, start, before, count);
                }
            }

            @Override
            public boolean onCheckVoicePermission() {
                if(listener != null){
                    return listener.onCheckVoicePermission();
                }
                return false;
            }

            @Override
            public void onToggleVoiceBtnClicked() {
                hideExtendMenuContainer();
            }

            @Override
            public void onToggleExtendClicked() {
                toggleMore();
            }

            @Override
            public void onToggleEmojiconClicked() {
                toggleEmojicon();
            }

            @Override
            public void onEditTextClicked() {
                hideExtendMenuContainer();
                chatPrimaryMenu.getEditText().requestFocus();
            }

            @Override
            public void onCancelReference() {
                if(listener != null){
                    listener.onCancelReference();
                }
            }

            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                if(listener != null){
                    return listener.onPressToSpeakBtnTouch(v, event);
                }
                return false;
            }
        });

        // emojicon menu
        emojiconMenu.setEmojiconMenuListener(new EaseEmojiconMenuListener() {

            @Override
            public void onExpressionClicked(EaseEmojicon emojicon) {
                if(emojicon.getType() == EaseEmojicon.Type.NORMAL){
                    if(emojicon.getEmojiText() != null){
                        chatPrimaryMenu.onEmojiconInputEvent(EaseSmileUtils.getSmiledText(context,emojicon.getEmojiText()));
                    }
                }else if (emojicon.getType() == EaseEmojicon.Type.STICKER) {
                    if (listener != null){
                        listener.onStickerClicked(emojicon);
                    }
                } else {
                    if(listener != null){
                        listener.onBigExpressionClicked(emojicon);
                    }
                }
            }

            @Override
            public void onDeleteImageClicked() {
                chatPrimaryMenu.onEmojiconDeleteEvent();
            }

            @Override
            public void onAddImageClicked() {
                if (listener != null){
                    listener.onAddStickerClicked();
                }
            }
        });

    }
    
   
    /**
     * insert text
     * @param text
     */
    public void insertText(String text){
        getPrimaryMenu().onTextInsert(text);
    }

    private void checkRecommendPhoto(){
        if (lastestImage != null && !TextUtils.isEmpty(lastestImage.path) && chatPrimaryMenu.getMoreView() != null){
            if (mLastRecommendImage != null && mLastRecommendImage.equals(lastestImage)){
                return;
            }
            if (System.currentTimeMillis() /1000 - lastestImage.addTime < 30){
                mLastRecommendImage = lastestImage;
                chatPrimaryMenu.getMoreView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recommendPhotoPop = EaseRecommendPhotoPop.recommendPhoto(getContext(), chatPrimaryMenu.getMoreView(), lastestImage.path, new EaseRecommendPhotoPop.RecommendPhotoListener() {
                            @Override
                            public void onPhotoClicked(String path) {
                                if (listener != null){
                                    listener.onRecommendPhotoClicked(path);
                                }
                                if (recommendPhotoPop != null && recommendPhotoPop.isShowing()){
                                    recommendPhotoPop.dismiss();
                                }
                            }
                        });
                        lastestImage = null;
                    }
                }, 500);
            }
        }
    }


    /**
     * show or hide extend menu
     * 
     */
    protected void toggleMore() {
        if (chatExtendMenuContainer.getVisibility() == View.GONE) {
            hideKeyboard();
            checkRecommendPhoto();
            handler.postDelayed(new Runnable() {
                public void run() {
                    chatExtendMenuContainer.setVisibility(View.VISIBLE);
                    chatExtendMenu.setVisibility(View.VISIBLE);
                    emojiconMenu.setVisibility(View.GONE);
                }
            }, 50);
        } else {
            if (emojiconMenu.getVisibility() == View.VISIBLE) {
                emojiconMenu.setVisibility(View.GONE);
                chatExtendMenu.setVisibility(View.VISIBLE);
            } else {
                chatExtendMenuContainer.setVisibility(View.GONE);
                showKeyboard();
            }
        }
    }

    /**
     * show or hide emojicon
     */
    protected void toggleEmojicon() {
        if (chatExtendMenuContainer.getVisibility() == View.GONE) {
            hideKeyboard();
            handler.postDelayed(new Runnable() {
                public void run() {
                    chatExtendMenuContainer.setVisibility(View.VISIBLE);
                    chatExtendMenu.setVisibility(View.GONE);
                    emojiconMenu.setVisibility(View.VISIBLE);
                }
            }, 50);
        } else {
            if (emojiconMenu.getVisibility() == View.VISIBLE) {
                chatExtendMenuContainer.setVisibility(View.GONE);
                emojiconMenu.setVisibility(View.GONE);
                showKeyboard();
            } else {
                chatExtendMenu.setVisibility(View.GONE);
                emojiconMenu.setVisibility(View.VISIBLE);
            }

        }
    }

    /**
     * hide keyboard
     */
    private void hideKeyboard() {
        chatPrimaryMenu.hideKeyboard();
    }

    private void showKeyboard(){
        chatPrimaryMenu.showKeyboard();
    }

    /**
     * hide extend menu
     */
    public void hideExtendMenuContainer() {
        chatExtendMenu.setVisibility(View.GONE);
        emojiconMenu.setVisibility(View.GONE);
        chatExtendMenuContainer.setVisibility(View.GONE);
        chatPrimaryMenu.onExtendMenuContainerHide();
    }

    /**
     * 输入栏显示引用消息
     * @param message
     */
    public void showReference(EMMessage message){
        chatPrimaryMenu.showReference(message);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showKeyboard();
            }
        }, 500);
    }

    /**
     * 输入栏隐藏引用消息
     */
    public void hideReference(){
        chatPrimaryMenu.hideReference();
    }
    /**
     * when back key pressed
     * 
     * @return false--extend menu is on, will hide it first
     *         true --extend menu is off 
     */
    public boolean onBackPressed() {
        if (chatExtendMenuContainer.getVisibility() == View.VISIBLE) {
            hideExtendMenuContainer();
            return false;
        } else {
            return true;
        }

    }
    

    public void setChatInputMenuListener(ChatInputMenuListener listener) {
        this.listener = listener;
    }

    @Override
    public void onImagesLoaded(ImageItem imageItem) {
        lastestImage = imageItem;
    }

    public interface ChatInputMenuListener {

        /**
         * when typing on the edit-text layout.
         */
        void onTyping(CharSequence s, int start, int before, int count);

        /**
         * when send message button pressed
         * 
         * @param content
         *            message content
         */
        boolean onSendMessage(String content);
        
        /**
         * when big icon pressed
         * @param emojicon
         */
        void onBigExpressionClicked(EaseEmojicon emojicon);

        /**
         * stick clicked
         * @param emojicon
         */
        void onStickerClicked(EaseEmojicon emojicon);

        /**
         * request voice permission
         */
        boolean onCheckVoicePermission();

        /**
         * when speak button is touched
         * @param v
         * @param event
         * @return
         */
        boolean onPressToSpeakBtnTouch(View v, MotionEvent event);

        /**
         * 推荐图片被点击事件
         */
        void onRecommendPhotoClicked(String path);

        /**
         * Sticker add
         */
        void onAddStickerClicked();

        void onCancelReference();
    }
    
}
