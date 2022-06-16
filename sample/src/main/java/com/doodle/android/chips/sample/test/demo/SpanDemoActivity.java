package com.doodle.android.chips.sample.test.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;

import com.doodle.android.chips.sample.R;

public class SpanDemoActivity extends AppCompatActivity {
    EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_span_demo);

        mEditText = (EditText) findViewById(R.id.edit_text);

        final int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        findViewById(R.id.edit_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spannable spannable = mEditText.getText();
                Object mCurrentEditTextSpan = new android.text.style.LeadingMarginSpan.LeadingMarginSpan2.Standard(size, 0);
                //看这个地方，他是 0到0的，所以不占位置，形成了一个动态的margin操作 ，这个margin 是段落开头的margin，和我门在xml里面设置的不一样
                //另外一个问题，就是顶部的magrin是按照xml里面的计算的
                spannable.setSpan(mCurrentEditTextSpan, 0, 0, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                mEditText.setText(spannable);
            }
        });
    }
}