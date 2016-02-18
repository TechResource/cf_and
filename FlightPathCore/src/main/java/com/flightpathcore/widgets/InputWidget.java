package com.flightpathcore.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.flightpathcore.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-21.
 */
@EViewGroup(resName = "widget_input")
public class InputWidget extends FrameLayout {

    @ViewById
    public EditText et;
    @ViewById
    protected TextInputLayout input;

    private boolean isRequired = false;
    private boolean onlyDigits = false;
    private String hint, text;

    public InputWidget(Context context) {
        super(context);
    }

    public InputWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        readAttributes(attrs);
    }

    public InputWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readAttributes(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InputWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        readAttributes(attrs);
    }

    private void readAttributes(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.InputWidget,0,0);
        try {
            isRequired = a.getBoolean(R.styleable.InputWidget_is_required, false);
            onlyDigits = a.getBoolean(R.styleable.InputWidget_only_digits, false);
            text = a.getString(R.styleable.InputWidget_text);
            hint = a.getString(R.styleable.InputWidget_hint);
        }finally {
            a.recycle();
        }
    }

    @AfterViews
    protected void init(){
        if(onlyDigits){
            setOnlyNumbersInput();
        }
        if(text != null){
            setText(text);
        }
        if(hint != null){
            setHint(hint);
        }
    }

    public void setText(String text){
        this.text = text;
        if(text != null)
            et.setText(text);
    }

    public void setText(String text, String hint){
        setText(text);
        setHint(hint);
    }

    public void setHint(String hint){
        this.hint = hint;
        if(hint != null){
//            et.setHint(hint);
            input.setHint(hint + (isRequired ? "*" : ""));
        }
    }

    public void setOnlyNumbersInput(){
        et.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
    }

    public String getValue() {
        return et.getText().toString();
    }

    public void setError(String s) {
        et.setError(s);
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener onEditorActionListener) {
        et.setOnEditorActionListener(onEditorActionListener);
        blockBackBtn = true;
    }

    private boolean blockBackBtn = false;

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if(blockBackBtn && event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.dispatchKeyEventPreIme(event);
    }
}
