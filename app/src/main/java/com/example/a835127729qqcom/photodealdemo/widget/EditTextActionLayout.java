package com.example.a835127729qqcom.photodealdemo.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.a835127729qqcom.photodealdemo.R;

/**
 * Created by 835127729qq.com on 16/9/19.
 */
public class EditTextActionLayout extends FrameLayout implements StickerView.BeginAddTextListener {
    private RelativeLayout mBlackLayout;
    private EditText mEditText;
    private TextView mTextCount;
    private Context mContext;
    private StopAddTextListener mStopAddTextListener;
    private InputMethodManager inputManager;
    public EditTextActionLayout(Context context) {
        super(context);
        init(context);
    }

    public EditTextActionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EditTextActionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        inputManager = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        initViews();
    }

    private void initViews() {
        LayoutInflater.from(mContext).inflate(R.layout.edittext_action_layout, this);
        mEditText = (EditText) findViewById(R.id.edit_text);
        mTextCount = (TextView)findViewById(R.id.text_count);
        mBlackLayout = (RelativeLayout) findViewById(R.id.black_layout);
        setUpEditText();
        setUpBlackLayout();
    }

    private void setUpBlackLayout() {
        mBlackLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.clearFocus();
                inputManager.hideSoftInputFromWindow(mEditText.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                EditTextActionLayout.this.setVisibility(GONE);
                String content = mEditText.getText().toString();
                mStopAddTextListener.onStopEditText(content);
            }
        });
    }

    private void setUpEditText() {
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==24){
                    mTextCount.setTextColor(Color.RED);
                }else{
                    mTextCount.setTextColor(Color.WHITE);
                }
                mTextCount.setText(s.length()+"/24");
            }
        });
    }

    @Override
    public void onStartEditText(String text) {
        this.setVisibility(VISIBLE);
        mEditText.setText(text);
        mEditText.setSelection(mEditText.length());
        showSoftInput();
    }

    /**
     * 弹出软键盘
     */
    private void showSoftInput() {
        mEditText.requestFocus();
        mEditText.setFocusable(true);
        mEditText.setFocusableInTouchMode(true);
        mEditText.requestFocus();
        inputManager.showSoftInput(mEditText, 0);
    }

    public void setmStopAddTextListener(StopAddTextListener mStopAddTextListener) {
        this.mStopAddTextListener = mStopAddTextListener;
    }
}
