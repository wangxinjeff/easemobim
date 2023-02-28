package com.hyphenate.easemob.easeui.widget;

import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.easemob.easeui.utils.EaseSmileUtils;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.easeui.widget.textview.AlignTextView;
import com.hyphenate.easemob.imlibs.easeui.prefs.PreferenceUtils;
import com.hyphenate.util.EMLog;

/**
 * primary menu
 */
public class EaseChatPrimaryMenu extends EaseChatPrimaryMenuBase implements OnClickListener {
    private CustomizeEditText editText;
    private View buttonSetModeKeyboard;
    private RelativeLayout edittext_layout;
    private View buttonSetModeVoice;
    private View buttonSend;
    private View buttonPressToSpeak;
    private TextView buttonPressPrompt;
    private View faceNormal;
    private View faceChecked;
    private View buttonMore;
    private boolean ctrlPress = false;
    private boolean isEnterSendMsg;
    private RelativeLayout referenceLayout;
    private AlignTextView referenceContent;
    private ImageView referenceClose;

    public EaseChatPrimaryMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public EaseChatPrimaryMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseChatPrimaryMenu(Context context) {
        super(context);
        init(context, null);
    }

    private void init(final Context context, AttributeSet attrs) {
//        Context context1 = context;
        LayoutInflater.from(context).inflate(R.layout.ease_widget_chat_primary_menu, this);
        editText = (CustomizeEditText) findViewById(R.id.et_sendmessage);
        buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
        edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
        buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
        buttonSend = findViewById(R.id.btn_send);
        buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
        buttonPressPrompt = findViewById(R.id.tv_press_prompt);
        faceNormal = findViewById(R.id.iv_face_normal);
        faceChecked = findViewById(R.id.iv_face_checked);
        RelativeLayout faceLayout = (RelativeLayout) findViewById(R.id.rl_face);
        buttonMore = findViewById(R.id.btn_more);
//        edittext_layout.setBackgroundResource(R.drawable.ease_input_bar_bg_normal);
        referenceLayout = findViewById(R.id.reference_layout);
        referenceContent = findViewById(R.id.reference_content);
        referenceClose = findViewById(R.id.reference_close);

        referenceClose.setOnClickListener(this);
        buttonSend.setOnClickListener(this);
        buttonSetModeKeyboard.setOnClickListener(this);
        buttonSetModeVoice.setOnClickListener(this);
        buttonMore.setOnClickListener(this);
        faceLayout.setOnClickListener(this);
//        editText.setOnClickListener(this);
        editText.requestFocus();
        isEnterSendMsg = PreferenceUtils.getInstance().isEnterSendMsg();
        if (isEnterSendMsg) {
            editText.setImeOptions(EditorInfo.IME_ACTION_SEND);
            editText.setSingleLine(true);
        } else {
            editText.setSingleLine(false);
            editText.setMaxLines(5);
        }
//        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    edittext_layout.setBackgroundResource(R.drawable.ease_input_bar_bg_active);
//                } else {
//                    edittext_layout.setBackgroundResource(R.drawable.ease_input_bar_bg_normal);
//                }
//
//            }
//        });
        // listen the text change
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    if (!isEnterSendMsg) {
                        buttonSend.setVisibility(View.VISIBLE);
                        buttonMore.setVisibility(View.GONE);
                    }
                } else {
                    if (!isEnterSendMsg) {
                        buttonMore.setVisibility(View.VISIBLE);
                        buttonSend.setVisibility(View.GONE);
                    }
                }

                if (listener != null) {
                    listener.onTyping(s, start, before, count);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                EMLog.d("key", "keyCode:" + keyCode + " action:" + event.getAction());

                // test on Mac virtual machine: ctrl map to KEYCODE_UNKNOWN
                if (keyCode == KeyEvent.KEYCODE_UNKNOWN) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        ctrlPress = true;
                    } else if (event.getAction() == KeyEvent.ACTION_UP) {
                        ctrlPress = false;
                    }
                }
                return false;
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                EMLog.d("key", "keyCode:" + (event == null ? "" : event.getKeyCode()) + " action" + (event == null ? "" : event.getAction()) + " ctrl:" + ctrlPress);
                if (actionId == EditorInfo.IME_ACTION_SEND ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                ctrlPress)) {
                    if (isEnterSendMsg) {
                        String s = editText.getText().toString();
                        if(listener.onSendBtnClicked(s)) {
                            editText.setText("");
                        }
                        return true;
                    }
                    return false;
                } else {
                    return false;
                }
            }
        });
        editText.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                faceNormal.setVisibility(View.VISIBLE);
                faceChecked.setVisibility(View.INVISIBLE);
                if (listener != null)
                    listener.onEditTextClicked();
                return false;
            }
        });


        buttonPressToSpeak.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        buttonPressPrompt.setText(getResources().getString(R.string.release_to_finish));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        buttonPressPrompt.setText(getResources().getString(R.string.button_pushtotalk));
                        break;
                    case MotionEvent.ACTION_POINTER_1_DOWN:
                    case MotionEvent.ACTION_POINTER_1_UP:
                    case MotionEvent.ACTION_POINTER_INDEX_MASK:
                    case MotionEvent.ACTION_POINTER_2_DOWN:
                    case MotionEvent.ACTION_POINTER_2_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        buttonPressPrompt.setText(getResources().getString(R.string.release_to_finish));
                        break;
                    case MotionEvent.ACTION_UP:
                        buttonPressPrompt.setText(getResources().getString(R.string.button_pushtotalk));
                        break;
                    default:
                        buttonPressPrompt.setText(getResources().getString(R.string.button_pushtotalk));
                        break;
                }

                if (listener != null) {
                    return listener.onPressToSpeakBtnTouch(v, event);
                }
                return false;
            }
        });
    }

    /**
     * set recorder view when speak icon is touched
     *
     * @param voiceRecorderView
     */
    public void setPressToSpeakRecorderView(EaseVoiceRecorderView voiceRecorderView) {
        EaseVoiceRecorderView voiceRecorderView1 = voiceRecorderView;
    }

    /**
     * append emoji icon to editText
     *
     * @param emojiContent
     */
    public void onEmojiconInputEvent(CharSequence emojiContent) {
//        editText.append(emojiContent);
        int position = editText.getSelectionStart();
        editText.getText().insert(position, emojiContent);
    }

    /**
     * delete emojicon
     */
    public void onEmojiconDeleteEvent() {
        if (!TextUtils.isEmpty(editText.getText())) {
            KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
            editText.dispatchKeyEvent(event);
        }
    }

    /**
     * on clicked event
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_send) {
            if (listener != null) {
                String s = editText.getText().toString();
                if (listener.onSendBtnClicked(s)) {
                    editText.setText("");
                }
            }
        } else if (id == R.id.btn_set_mode_voice) {
            setModeVoice();
            showNormalFaceImage();
            if (listener != null)
                listener.onToggleVoiceBtnClicked();
        } else if (id == R.id.btn_set_mode_keyboard) {
            setModeKeyboard();
            showNormalFaceImage();
            if (listener != null)
                listener.onToggleVoiceBtnClicked();
        } else if (id == R.id.btn_more) {
            buttonSetModeVoice.setVisibility(View.VISIBLE);
            buttonSetModeKeyboard.setVisibility(View.GONE);
            edittext_layout.setVisibility(View.VISIBLE);
            buttonPressToSpeak.setVisibility(View.GONE);
            showNormalFaceImage();
            if (listener != null)
                listener.onToggleExtendClicked();
        } else if (id == R.id.et_sendmessage) {
//            faceNormal.setVisibility(View.VISIBLE);
//            faceChecked.setVisibility(View.INVISIBLE);
//            if (listener != null)
//                listener.onEditTextClicked();
        } else if (id == R.id.rl_face) {
            toggleFaceImage();
            if (listener != null) {
                listener.onToggleEmojiconClicked();
            }
        } else if (id == R.id.reference_close){
            referenceLayout.setVisibility(GONE);
            if (listener != null) {
                listener.onCancelReference();
            }
        }
    }


    /**
     * show voice icon when speak bar is touched
     */
    protected void setModeVoice() {
        hideKeyboard();
        edittext_layout.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.GONE);
        buttonSetModeKeyboard.setVisibility(View.VISIBLE);
        buttonSend.setVisibility(View.GONE);
        buttonMore.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.VISIBLE);
        faceNormal.setVisibility(View.VISIBLE);
        faceChecked.setVisibility(View.INVISIBLE);

    }

    /**
     * show keyboard
     */
    protected void setModeKeyboard() {
        edittext_layout.setVisibility(View.VISIBLE);
        buttonSetModeKeyboard.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.VISIBLE);
        // mEditTextContent.setVisibility(View.VISIBLE);
        editText.requestFocus();
        showKeyboard();
        // buttonSend.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.GONE);
        if (TextUtils.isEmpty(editText.getText())) {
            buttonMore.setVisibility(View.VISIBLE);
            buttonSend.setVisibility(View.GONE);
        } else {
            buttonMore.setVisibility(View.GONE);
            if (!isEnterSendMsg) {
                buttonSend.setVisibility(View.VISIBLE);
            }

        }

    }


    protected void toggleFaceImage() {
        if (faceNormal.getVisibility() == View.VISIBLE) {
            showSelectedFaceImage();
        } else {
            showNormalFaceImage();
        }
    }

    private void showNormalFaceImage() {
        faceNormal.setVisibility(View.VISIBLE);
        faceChecked.setVisibility(View.INVISIBLE);
    }

    private void showSelectedFaceImage() {
        faceNormal.setVisibility(View.INVISIBLE);
        faceChecked.setVisibility(View.VISIBLE);
        edittext_layout.setVisibility(View.VISIBLE);
        buttonSetModeKeyboard.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.GONE);
        editText.requestFocus();

    }


    @Override
    public void onExtendMenuContainerHide() {
        showNormalFaceImage();
    }

    @Override
    public void onTextInsert(CharSequence text) {
        int start = editText.getSelectionStart();
        Editable editable = editText.getEditableText();
        editable.insert(start, text);
        setModeKeyboard();
    }

    @Override
    public EditText getEditText() {
        return editText;
    }

    @Override
    public View getMoreView() {
        return buttonMore;
    }

    @Override
    public void showReference(EMMessage message){
        if(message != null) {
            String content = EaseCommonUtils.getMessageDigest(message, getContext(), true);
            EaseUser user = EaseUserUtils.getUserInfo(message.getFrom());
//            referenceContent.setText(user != null ? user.getNickname() + ":" + content : message.getFrom() + ":" + content);
            Spannable spannable = EaseSmileUtils.getSmiledText(getContext(), user != null ? user.getNickname() + ":" + content : message.getFrom() + ":" + content);
            referenceContent.setText(spannable, TextView.BufferType.SPANNABLE);
            referenceLayout.setVisibility(VISIBLE);
            setModeKeyboard();
            showNormalFaceImage();
            if (listener != null)
                listener.onToggleVoiceBtnClicked();
        }
    }

    @Override
    public void hideReference() {
        referenceLayout.setVisibility(GONE);
    }
}
