package com.doodle.android.chips.sample.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.Toast;

import com.doodle.android.chips.sample.R;

/**
 * //当输入法输入了字符，包括表情，字母、文字、数字和符号等内容，会回调该方法
 * public boolean commitText(CharSequence text, int newCursorPosition)
 * <p>
 * //当有按键输入时，该方法会被回调。比如点击退格键时，搜狗输入法应该就是通过调用该方法，
 * //发送keyEvent的，但谷歌输入法却不会调用该方法，而是调用下面的deleteSurroundingText()方法。
 * public boolean sendKeyEvent(KeyEvent event);
 * <p>
 * //当有文本删除操作时（剪切，点击退格键），会触发该方法
 * public boolean deleteSurroundingText(int beforeLength, int afterLength)
 * <p>
 * //结束组合文本输入的时候，回调该方法
 * public boolean finishComposingText()；
 * ————————————————
 * <p>
 * <p>
 * <p>
 * a看看这个文章
 * https://blog.csdn.net/frained/article/details/78880579
 */
public class EditInputManagerActivity extends AppCompatActivity {

    private static final String TAG = "EditInputManager";
    private CusEditText mCusEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_input_manager);

        mCusEditText = (CusEditText) findViewById(R.id.input_manager);
        //设置EditText的显示方式为多行文本输入
        mCusEditText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        //改变默认的单行模式
        mCusEditText.setSingleLine(false);
        //水平滚动设置为False
        mCusEditText.setHorizontallyScrolling(false);


        //test1();
//        test2();
        test3();


    }

    /**
     * 这个能判断现在添加的文本
     * https://blog.csdn.net/MoLiao2046/article/details/103352149
     * <p>
     * 典型的违禁词过滤和 @ 功能处理
     * fixme
     * 这个不适合做违禁词过滤，违禁词的那个在发送的时候直接过正则一下就好了
     */
    private void test3() {
        mCusEditText.setInputConnectionWrapperInterface(new CusEditText.InputConnectionWrapperInterface() {
            @Override
            public InputConnection getInputConnection(InputConnection target) {
                // 第二个参数不传递，会导致  CusEditText 里面的按个if出现一定的问题(崩溃)
                return new InputConnectionWrapper(target, true) {

                    @Override
                    public boolean commitText(CharSequence text, int newCursorPosition) {
                        Log.i(TAG, text + "=======输入内容=========commitText=============" + System.currentTimeMillis());
                        if (TextUtils.equals(text, "@")) {
                            Toast toast = Toast.makeText(EditInputManagerActivity.this, "输入了@字符~", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            Log.i(TAG, "=====commitText=============" + System.currentTimeMillis());
                            return true;
                        }
                        return super.commitText(text, newCursorPosition);
                    }

                    @Override
                    public boolean sendKeyEvent(KeyEvent event) {

                        /**
                         * 这个删除判断，入股是@ 的话，前提是@前年的那个字符被删除完了
                         *
                         * 也就是在删除的时候出去最后一个内容
                         * 1. 如果是@的部分，那我们直接删除这一个整体
                         * 2. 如果是文本，则取出此文本，修改此文本的内容(我们使用的是对象引用，就去取出来对象，重新赋值)
                         * 当删除到这个对象的最后一个只读的时候，从集合删除这个对象，对下一个对象进行相关的操作
                         */
                        if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {

                            //这个就是针对删除事件进行拦截
                            Editable editable = mCusEditText.getText();
                            if (editable.length() == 0) {
                                return false;
                            }

                            int index = Math.max(0, mCusEditText.getSelectionStart() - 1);
                            char charAt = editable.charAt(index);
                            Log.i(TAG, "===========" + charAt);
                            //删除@事件监听
                            if (charAt == '@') {
                                Toast toast = Toast.makeText(EditInputManagerActivity.this, "无法删除@字符~", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                return true;
                            }
                        }
                        return super.sendKeyEvent(event);

                    }
                };
                //


            }
        });
    }

    private void test2() {
        mCusEditText.setInputConnectionWrapperInterface(new CusEditText.InputConnectionWrapperInterface() {
            @Override
            public InputConnection getInputConnection(InputConnection target) {
                // 第二个参数不传递，会导致  CusEditText 里面的按个if出现一定的问题(崩溃)
                return new InputConnectionWrapper(target, true) {

                    @Override
                    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
                        Log.i(TAG, "=====deleteSurroundingText======" + System.currentTimeMillis());
                        Editable editable = mCusEditText.getText();

                        if (editable.length() == 0) {
                            return false;
                        }

                        int index = Math.max(0, mCusEditText.getSelectionStart() - 1);
                        char charAt = editable.charAt(index);
                        Log.i(TAG, "===========" + charAt);
                        //删除@事件监听
                        if (charAt == '@') {
                            Toast toast = Toast.makeText(EditInputManagerActivity.this, "无法删除@字符~", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return true;
                        }

                        //return 表示消耗这个事件
                        return super.deleteSurroundingText(beforeLength, afterLength);
                    }
                };
                //


            }
        });
    }

    private void test1() {
        mCusEditText.setInputConnectionWrapperInterface(new CusEditText.InputConnectionWrapperInterface() {
            @Override
            public InputConnection getInputConnection(InputConnection target) {
                return new InputConnectionWrapper(target, true) {

                    @Override
                    public boolean sendKeyEvent(KeyEvent event) {

                        if (mCusEditText.length() == 0) {
                            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                //删除事件处理
                                if (event.getKeyCode() == KeyEvent.KEYCODE_DEL) {

                                    Log.i(TAG, "=====这个是执行到最后，没有数据了=================");
                                    return true;
                                }
                            }
                        }
                        Log.i(TAG, event.getAction() + "======@@@@@@@@=====" + event.getKeyCode());
                        //输入事件处理
                        /**
                         *  Log.i(TAG, event.getAction() + "=====这个什么时间触发======1111111===========" + event.getKeyCode());
                         *
                         *  0=====这个什么时间触发======1111111===========66
                         *  =====这个什么时间触发=========2222222========
                         *  1=====这个什么时间触发======1111111===========66
                         *
                         *    public static final int ACTION_DOWN             = 0;
                         *
                         *    public static final int ACTION_UP = 1;
                         *
                         *
                         *   把上面的打印放开(加上下面的打印)，会得到 上面的打印
                         *   todo：
                         *   就是监听回车键
                         */
                        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            //就是拼接了一个换行，为什么要这么作
                            Log.i(TAG, "=====这个什么时间触发=========2222222========");
                            mCusEditText.append("\n");
                            return true;
                        }

                        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_AT) {
                            Log.i(TAG, "======监听了@按键=============");
                            mCusEditText.append("@");
                            return true;
                        }
                        //但是我感觉没什么用，你这样写也行，默认也是这样的吧

                        return super.sendKeyEvent(event);
                    }

                    /**
                     * 看这个参数会明显的发现，这个也是一个回调
                     * @param beforeLength
                     * @param afterLength
                     * @return
                     */
                    @Override
                    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
                        if (mCusEditText.length() == 0 && beforeLength == 1 && afterLength == 0) {
                            // backspace
                            return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                                    && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
                        }


                        Log.i(TAG, "=====================");
                        if (true) {
                            //一个也删除不掉
                            return true;
                        }

                        return super.deleteSurroundingText(beforeLength, afterLength);
                    }
                };
            }
        });
    }


}