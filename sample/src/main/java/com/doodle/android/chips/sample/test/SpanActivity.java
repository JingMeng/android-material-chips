package com.doodle.android.chips.sample.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.doodle.android.chips.sample.MainActivity;
import com.doodle.android.chips.sample.R;
import com.doodle.android.chips.sample.test.demo.SpanDemoActivity;
import com.doodle.android.chips.sample.test2.Test2Activity;


/**
 * 网络上的另外的一种监听方案 但是最后还是使用了我们这种
 * <p>
 * https://www.jianshu.com/p/97a00548adab
 */
public class SpanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_span);


        /**
         * 在这个方法中 deleteSurroundingText 也是不会被调用到的
         */
        findViewById(R.id.demo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SpanActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        /**
         * 分析了首行缩进
         * 还有一个tag的布局没有分析
         */
        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo 这个是起作用的，我们测试没有问题的
                Intent intent = new Intent(SpanActivity.this, SpanDemoActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.manager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fixme 这个是我们自己思考写的，但是不起作用
                Intent intent = new Intent(SpanActivity.this, EditInputManagerActivity.class);
                startActivity(intent);
            }
        });
        /**
         * 在这个里面其实 也米有调用deleteSurroundingText 这个方法
         *
         * 但是调用了 sendKeyEvent
         *
         *     if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
         *       if (mBackspaceListener != null && mBackspaceListener.onBackspace()) {
         *           return true;
         *       }
         *     }
         *
         *     但是这个可以监听输入，监听你的回车，但是 sendKeyEvent 无法监听输入@或者某一个字符
         */
        findViewById(R.id.demo1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo 这个是网络的案例，做删除监听，也是起作用的，我们测试没有问题的
                Intent intent = new Intent(SpanActivity.this, Test2Activity.class);
                startActivity(intent);
            }
        });
    }
}