package com.hyphenate.easemob.easeui.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.easemob.R;

/**
 * input menu fire
 */
public class EaseChatInputMenuFire extends LinearLayout {
    private ChatInputMenuFireListener listener;
    private Context context;
    private LayoutInflater layoutInflater;
    private CustomizeEditText tvContent;

    public EaseChatInputMenuFire(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public EaseChatInputMenuFire(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EaseChatInputMenuFire(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.ease_widget_chat_input_menu_fire, this);
        tvContent = findViewById(R.id.et_sendmessage);
        final View btnSend = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    if(listener.onSendMessage(tvContent.getText().toString())) {
                        tvContent.setText("");
                    }
                }

            }
        });
        tvContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                                event.getAction() == KeyEvent.ACTION_DOWN)) {
                    if (listener != null) {
                        listener.onSendMessage(v.getText().toString());
                    }
                    tvContent.setText("");
                    return true;
                } else {
                    return false;
                }
            }
        });
        findViewById(R.id.btn_destory_exit).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onFireClosed();
                }
            }
        });
        tvContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    btnSend.setVisibility(View.VISIBLE);
                } else {
                    btnSend.setVisibility(View.GONE);
                }
            }
        });
        findViewById(R.id.btn_destory_image).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onImageClicked();
                }
            }
        });
    }

    public void clearTxt(){
        tvContent.setText("");
        tvContent.requestFocus();
    }

    public void showKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(tvContent, InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }catch (Exception ignored){}
    }


    public void setChatInputMenuListener(ChatInputMenuFireListener listener) {
        this.listener = listener;
    }

    public interface ChatInputMenuFireListener {

        /**
         * when send message button pressed
         *
         * @param content message content
         * @return
         */
        boolean onSendMessage(String content);

        /**
         * image clicked
         */
        void onImageClicked();

        /**
         * 阅后即焚关闭
         */
        void onFireClosed();
    }

}
