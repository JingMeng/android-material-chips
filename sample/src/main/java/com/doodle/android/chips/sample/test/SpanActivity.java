package com.doodle.android.chips.sample.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.doodle.android.chips.sample.MainActivity;
import com.doodle.android.chips.sample.R;
import com.doodle.android.chips.sample.test.demo.SpanDemoActivity;

public class SpanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_span);

        findViewById(R.id.demo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SpanActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SpanActivity.this, SpanDemoActivity.class);
                startActivity(intent);
            }
        });
    }
}