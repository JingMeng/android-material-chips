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
import android.view.inputmethod.InputConnectionWrapper;

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
        Log.i(TAG, (mInputConnectionWrapperInterface != null) + "==========onCreateInputConnection=======这个被执行了吗？======");
        if (mInputConnectionWrapperInterface != null) {
            InputConnectionWrapper connection = (InputConnectionWrapper) mInputConnectionWrapperInterface.getInputConnection(super.onCreateInputConnection(outAttrs));

            if (false) {
                //三方借鉴别人的多调用了这么一个方法  --因为我们已经在上面传递了 target ，就不需要在传递了，所以这个方法没有必要设置成为true
                connection.setTarget(super.onCreateInputConnection(outAttrs));
            }
            return connection;
        }

        return super.onCreateInputConnection(outAttrs);
    }

    public interface InputConnectionWrapperInterface {
        InputConnection getInputConnection(InputConnection target);
    }
}
