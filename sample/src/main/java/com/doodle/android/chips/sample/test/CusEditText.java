/*
 * Copyright (C) 2016 Doodle AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.doodle.android.chips.sample.test;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class CusEditText extends AppCompatEditText {

    private static final String TAG = "CusEditText";

    public CusEditText(Context context) {
        super(context);
    }

    public CusEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CusEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private InputConnectionWrapperInterface mInputConnectionWrapperInterface;

    public void setInputConnectionWrapperInterface(InputConnectionWrapperInterface inputConnectionWrapperInterface) {
        mInputConnectionWrapperInterface = inputConnectionWrapperInterface;
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        //这个地方被覆盖了
        Log.i(TAG, "==========onCreateInputConnection=======这个被执行了吗？======");
        if (mInputConnectionWrapperInterface != null) {
            return mInputConnectionWrapperInterface.getInputConnection(super.onCreateInputConnection(outAttrs));
        }

        return super.onCreateInputConnection(outAttrs);
    }

    public interface InputConnectionWrapperInterface {
        InputConnection getInputConnection(InputConnection target);
    }
}
