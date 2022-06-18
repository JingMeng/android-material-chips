package com.doodle.android.chips.sample.test2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Gravity;
import android.widget.Toast;

import com.doodle.android.chips.sample.R;

public class Test2Activity extends AppCompatActivity {

    private TEditText editText;
    TInputConnection.BackspaceListener backspaceListener = new TInputConnection.BackspaceListener() {
        @Override
        public boolean onBackspace() {
            Editable editable = editText.getText();

            if(editable.length() == 0){
                return false;
            }

            int index = Math.max(0,editText.getSelectionStart() - 1);

            if(editable.charAt(index) == '@'){
                Toast toast = Toast.makeText(Test2Activity.this,"无法删除@字符~",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                return true;
            }
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        editText = (TEditText) findViewById(R.id.edit_text2);
        editText.setBackSpaceLisetener(backspaceListener);
    }

}