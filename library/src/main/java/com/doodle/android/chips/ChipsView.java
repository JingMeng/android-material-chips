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

package com.doodle.android.chips;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.doodle.android.chips.model.Contact;
import com.doodle.android.chips.util.Common;
import com.doodle.android.chips.views.ChipsEditText;
import com.doodle.android.chips.views.ChipsVerticalLinearLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChipsView extends ScrollView implements ChipsEditText.InputConnectionWrapperInterface {

    //<editor-fold desc="Static Fields">
    private static final String TAG = "ChipsView";
    private static final int CHIP_HEIGHT = 32; // dp
    private static final int SPACING_TOP = 4; // dp
    private static final int SPACING_BOTTOM = 4; // dp
    public static final int DEFAULT_VERTICAL_SPACING = 1; // dp
    private static final int DEFAULT_MAX_HEIGHT = -1;
    //</editor-fold>

    //<editor-fold desc="Resources">
    private int mChipsBgRes = R.drawable.chip_background;
    //</editor-fold>

    //<editor-fold desc="Attributes">
    private int mMaxHeight; // px
    private int mVerticalSpacing;

    private int mChipsColor;
    private int mChipsColorClicked;
    private int mChipsColorErrorClicked;
    private int mChipsBgColor;
    private int mChipsBgColorIndelible;
    private int mChipsBgColorClicked;
    private int mChipsBgColorErrorClicked;
    private int mChipsTextColor;
    private int mChipsTextColorIndelible;
    private int mChipsTextColorClicked;
    private int mChipsTextColorErrorClicked;
    private int mChipsPlaceholderResId;
    private
    @ColorInt
    int mChipsPlaceholderTint;
    private int mChipsDeleteResId;
    private String mChipsHintText;

    private int mChipsMargin;
    //</editor-fold>

    //<editor-fold desc="Private Fields">
    private float mDensity;
    private RelativeLayout mChipsContainer;
    private ChipsListener mChipsListener;
    private ChipsEditText mEditText;
    private ChipsVerticalLinearLayout mRootChipsLayout;
    private EditTextListener mEditTextListener;
    private List<Chip> mChipList = new ArrayList<>();
    private Object mCurrentEditTextSpan;
    private ChipValidator mChipsValidator;
    private Typeface mTypeface;

    // initials
    private boolean mUseInitials = false;
    private int mInitialsTextSize;
    private Typeface mInitialsTypeface;
    @ColorInt
    private int mInitialsTextColor;
    //</editor-fold>

    //<editor-fold desc="Constructors">
    public ChipsView(Context context) {
        super(context);
        init();
    }

    public ChipsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        init();
    }

    public ChipsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChipsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(context, attrs);
        init();
    }
    //</editor-fold>

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMaxHeight != DEFAULT_MAX_HEIGHT) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        return true;
    }

    //<editor-fold desc="Initialization">
    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ChipsView, 0, 0);
        try {
            mMaxHeight = a.getDimensionPixelSize(R.styleable.ChipsView_cv_max_height, DEFAULT_MAX_HEIGHT);
            mVerticalSpacing = a.getDimensionPixelSize(R.styleable.ChipsView_cv_vertical_spacing, (int) (DEFAULT_VERTICAL_SPACING * mDensity));
            mChipsColor = a.getColor(R.styleable.ChipsView_cv_color, ContextCompat.getColor(context, R.color.base30));
            mChipsColorClicked = a.getColor(R.styleable.ChipsView_cv_color_clicked, ContextCompat.getColor(context, R.color.colorPrimaryDark));
            mChipsColorErrorClicked = a.getColor(R.styleable.ChipsView_cv_color_error_clicked, ContextCompat.getColor(context, R.color.color_error));
            mChipsBgColor = a.getColor(R.styleable.ChipsView_cv_bg_color, ContextCompat.getColor(context, R.color.base10));
            mChipsBgColorClicked = a.getColor(R.styleable.ChipsView_cv_bg_color_clicked, ContextCompat.getColor(context, R.color.blue));
            mChipsBgColorIndelible = a.getColor(R.styleable.ChipsView_cv_bg_color_indelible, mChipsBgColor);
            mChipsBgColorErrorClicked = a.getColor(R.styleable.ChipsView_cv_bg_color_clicked, ContextCompat.getColor(context, R.color.color_error));
            mChipsTextColor = a.getColor(R.styleable.ChipsView_cv_text_color, Color.BLACK);
            mChipsTextColorClicked = a.getColor(R.styleable.ChipsView_cv_text_color_clicked, Color.WHITE);
            mChipsTextColorErrorClicked = a.getColor(R.styleable.ChipsView_cv_text_color_clicked, Color.WHITE);
            mChipsTextColorIndelible = a.getColor(R.styleable.ChipsView_cv_text_color_indelible, mChipsTextColor);
            mChipsPlaceholderResId = a.getResourceId(R.styleable.ChipsView_cv_icon_placeholder, R.drawable.ic_person_24dp);
            mChipsPlaceholderTint = a.getColor(R.styleable.ChipsView_cv_icon_placeholder_tint, 0);
            mChipsDeleteResId = a.getResourceId(R.styleable.ChipsView_cv_icon_delete, R.drawable.ic_close_24dp);
            mChipsHintText = a.getString(R.styleable.ChipsView_cv_text_hint);
            mChipsMargin = a.getDimensionPixelSize(R.styleable.ChipsView_cv_chips_margin, 0);
        } finally {
            a.recycle();
        }
    }

    private void init() {
        mDensity = getResources().getDisplayMetrics().density;

        mChipsContainer = new RelativeLayout(getContext());
        addView(mChipsContainer);

        // Dummy item to prevent AutoCompleteTextView from receiving focus
        LinearLayout linearLayout = new LinearLayout(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(0, 0);
        linearLayout.setLayoutParams(params);
        linearLayout.setFocusable(true);
        linearLayout.setFocusableInTouchMode(true);

        mChipsContainer.addView(linearLayout);

        mEditText = new ChipsEditText(getContext(), this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = (int) (SPACING_TOP * mDensity);
        layoutParams.bottomMargin = (int) (SPACING_BOTTOM * mDensity) + mVerticalSpacing;
        mEditText.setLayoutParams(layoutParams);
        mEditText.setMinHeight((int) (CHIP_HEIGHT * mDensity));
        mEditText.setPadding(0, 0, 0, 0);
        mEditText.setLineSpacing(mVerticalSpacing, (CHIP_HEIGHT * mDensity) / mEditText.getLineHeight());
        mEditText.setBackgroundColor(Color.argb(0, 0, 0, 0));
        mEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_ACTION_UNSPECIFIED);
        mEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mEditText.setHint(mChipsHintText);

        //直接添加了到RelativeLayout
        mChipsContainer.addView(mEditText);

        //这个是那个标签流(TAG)的布局，感觉好简单
        mRootChipsLayout = new ChipsVerticalLinearLayout(getContext(), mVerticalSpacing);
        mRootChipsLayout.setOrientation(LinearLayout.VERTICAL);
        mRootChipsLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mRootChipsLayout.setPadding(0, (int) (SPACING_TOP * mDensity), 0, 0);
        mChipsContainer.addView(mRootChipsLayout);

        initListener();

        Log.i(TAG, "==========" + isInEditMode());
        /**
         * 默认这个地方是 false 这样在编译器中就能看到默认添加的这两个了
         * Indicates whether this View is currently in edit mode. A View is usually
         *in edit mode when displayed within a developer tool.
         */
        if (isInEditMode()) {
            // preview chips
            LinearLayout editModeLinLayout = new LinearLayout(getContext());
            editModeLinLayout.setOrientation(LinearLayout.HORIZONTAL);
            mChipsContainer.addView(editModeLinLayout);

            View view = new Chip("Test Chip", null, new Contact(null, null, "Test", "asd@asd.de", null)).getView();
            view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            editModeLinLayout.addView(view);

            View view2 = new Chip("Indelible", null, new Contact(null, null, "Test", "asd@asd.de", null), true).getView();
            view2.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            editModeLinLayout.addView(view2);
        }
    }

    private void initListener() {
        mChipsContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.requestFocus();
                unselectAllChips();
            }
        });

        mEditTextListener = new EditTextListener();
        mEditText.addTextChangedListener(mEditTextListener);
        mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    unselectAllChips();
                }
            }
        });
    }
    //</editor-fold>

    //<editor-fold desc="Public Methods">
    public void addChip(String displayName, String avatarUrl, Contact contact) {
        addChip(displayName, Uri.parse(avatarUrl), contact);
    }

    public void addChip(String displayName, Uri avatarUrl, Contact contact) {
        addChip(displayName, avatarUrl, contact, false);
        mEditText.setText("");
        addLeadingMarginSpan();
    }

    public void addChip(String displayName, Uri avatarUrl, Contact contact, boolean isIndelible) {
        Chip chip = new Chip(displayName, avatarUrl, contact, isIndelible);
        mChipList.add(chip);
        if (mChipsListener != null) {
            mChipsListener.onChipAdded(chip);
        }

        mEditText.setHint(null);

        onChipsChanged(true);
        post(new Runnable() {
            @Override
            public void run() {
                fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    public void setTypeface(@NonNull Typeface typeface) {
        this.mTypeface = typeface;
        if (mEditText != null) {
            mEditText.setTypeface(mTypeface);
        }
    }

    /**
     * Use Initials instead of the person icon.
     *
     * @param textSize         in SP
     * @param initialsTypeface Nullable typeface
     */
    public void useInitials(int textSize, @Nullable Typeface initialsTypeface, @ColorInt int textColor) {
        this.mUseInitials = true;
        this.mInitialsTextSize = textSize;
        this.mInitialsTypeface = initialsTypeface;
        this.mInitialsTextColor = textColor;
    }

    public void clearText() {
        mEditText.setText("");
        onChipsChanged(true);
    }

    @NonNull
    public List<Chip> getChips() {
        return Collections.unmodifiableList(mChipList);
    }

    public boolean removeChipBy(Contact contact) {
        for (int i = 0; i < mChipList.size(); i++) {
            if (mChipList.get(i).mContact != null && mChipList.get(i).mContact.equals(contact)) {
                mChipList.remove(i);
                if (mChipList.isEmpty()) {
                    mEditText.setHint(mChipsHintText);
                }
                onChipsChanged(true);
                return true;
            }
        }
        return false;
    }

    public Contact tryToRecognizeAddress() {
        String text = mEditText.getText().toString();
        if (!TextUtils.isEmpty(text)) {
            if (Common.isValidEmail(text)) {
                return new Contact(text, "", null, text, null);
            }
        }
        return null;
    }

    public void setChipsListener(ChipsListener chipsListener) {
        this.mChipsListener = chipsListener;
    }

    public void setChipsValidator(ChipValidator chipsValidator) {
        mChipsValidator = chipsValidator;
    }

    public EditText getEditText() {
        return mEditText;
    }
    //</editor-fold>

    //<editor-fold desc="Private Methods">

    /**
     * rebuild all chips and place them right
     * <p>
     * onChipAdded 被调用了就会执行这个方法，执行摆放，位置处理
     */
    private void onChipsChanged(final boolean moveCursor) {
        ChipsVerticalLinearLayout.TextLineParams textLineParams = mRootChipsLayout.onChipsChanged(mChipList);

        // if null then run another layout pass    这一块不应该发生的，还是书写了一块兼容代码，这是好代码还是坏代码？？？
        if (textLineParams == null) {
            post(new Runnable() {
                @Override
                public void run() {
                    onChipsChanged(moveCursor);
                }
            });
            return;
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mEditText.getLayoutParams();
        //仅仅处理了一个顶部的边距======== 顶部边距+ 每一行的行高+ 这几行的间距
        params.topMargin = (int) ((SPACING_TOP + textLineParams.row * CHIP_HEIGHT) * mDensity) + textLineParams.row * mVerticalSpacing;
        mEditText.setLayoutParams(params);
        addLeadingMarginSpan(textLineParams.lineMargin + mChipsMargin * textLineParams.chipsCount);
        if (moveCursor) {
            mEditText.setSelection(mEditText.length());
        }
    }

    /**
     * 解析一下这个的作用：应该是很有用，但是你没有想到的方法
     * ------
     * todo 2022年6月15日11:13:13
     * 就是这个效果，前面通过spannable 占用了一部分  ，那删除的时候没有影响吗？？？？
     * 添加或者删除某一个chiips的时候是没有影响的，因为都会调用到这一部分来更新一次数据，进而能够完美的实现校验
     * <p>
     * 这个好好看看实践一下
     * https://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0120/2335.html
     */
    private void addLeadingMarginSpan(int margin) {
        Spannable spannable = mEditText.getText();
        if (mCurrentEditTextSpan != null) {
            spannable.removeSpan(mCurrentEditTextSpan);
        }
        mCurrentEditTextSpan = new android.text.style.LeadingMarginSpan.LeadingMarginSpan2.Standard(margin, 0);
        spannable.setSpan(mCurrentEditTextSpan, 0, 0, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        mEditText.setText(spannable);
    }

    private void addLeadingMarginSpan() {
        Spannable spannable = mEditText.getText();
        if (mCurrentEditTextSpan != null) {
            spannable.removeSpan(mCurrentEditTextSpan);
        }
        spannable.setSpan(mCurrentEditTextSpan, 0, 0, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        mEditText.setText(spannable);
    }

    /**
     * 从这个方法反推，  因为这个就是验证，验证通过就可以输入到面盘了
     * 看了一下这个地方主要处理了 粘贴的情形
     * return true if the text should be deleted
     * <p>
     * fixme 这个返回值还是有点不确定正确的含义
     */
    private boolean onEnterPressed(String text) {
        boolean shouldDeleteText = true;
        if (text != null && text.length() > 0) {
            //检查是否是邮箱
            if (Common.isValidEmail(text)) {
                //是邮箱
                onEmailRecognized(text);
            } else {
                //不是邮箱
                shouldDeleteText = onNonEmailRecognized(text);
            }
            if (shouldDeleteText) {
                mEditText.setSelection(0);
            }
        }
        return shouldDeleteText;
    }

    private void onEmailRecognized(String email) {
        onEmailRecognized(new Contact(email, "", null, email, null));
    }

    private void onEmailRecognized(Contact contact) {
        //这个控件是view和数据额结合体，类似于ViewHolder
        Chip chip = new Chip(contact.getDisplayName(), null, contact);
        mChipList.add(chip);
        if (mChipsListener != null) {
            mChipsListener.onChipAdded(chip);
        }
        post(new Runnable() {
            @Override
            public void run() {
                onChipsChanged(true);
            }
        });
    }

    /**
     * 这个地方是邮箱确认
     */
    private boolean onNonEmailRecognized(String text) {
        if (mChipsListener != null) {
            return mChipsListener.onInputNotRecognized(text);
        }
        return true;
    }

    private void selectOrDeleteLastChip() {
        if (mChipList.size() > 0) {
            onChipInteraction(mChipList.size() - 1);
        }
    }

    private void onChipInteraction(int position) {
        try {
            Chip chip = mChipList.get(position);
            if (chip != null) {
                onChipInteraction(chip, true);
            }
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "Out of bounds", e);
        }
    }

    /**
     * 这个就是那个删除的业务逻辑
     * 先解释当前的业务：
     * <p>
     * 如果没有选择，执行删除按钮的话，我们的操作是什么都不做，但是把当前的最后一个tag设置为选中
     * 再次执行删除的时候执行删除操作
     * <p>
     * 这个有一个前提操作就是，你删除不动，如果都在内容栏目里面这种操作你是无法实现的 ------------- 这句话，存在问题
     * 因为删除是执行的默认操作，我们拦截了默认操作 所以就是输入在输入框内也是没有问题的
     * <p>
     * -------------
     * <p>
     * 在我们的@案例中，删除的操作是判断节点类型，如果节点类型是@ 的就整体删除，否则按照EditText的默认操作来执行
     */
    private void onChipInteraction(Chip chip, boolean nameClicked) {
        unselectChipsExcept(chip);
        if (chip.isSelected()) {
            mChipList.remove(chip);
            if (mChipsListener != null) {
                mChipsListener.onChipDeleted(chip);
            }
            onChipsChanged(true);
            // todo 2022年6月15日10:19:24  这个地方啊就可以设置是否一下子全部删除
            if (false) {
                //这个地方做一个测试
                mEditText.setText("");
            } else if (nameClicked) {
                mEditText.setText(chip.getContact().getEmailAddress());
            }

            if (nameClicked) {
                addLeadingMarginSpan();
                mEditText.requestFocus();
                mEditText.setSelection(mEditText.length());
            }
        } else {
            chip.setSelected(true);
            onChipsChanged(false);
        }
    }

    private void unselectChipsExcept(Chip rootChip) {
        for (Chip chip : mChipList) {
            if (chip != rootChip) {
                chip.setSelected(false);
            }
        }
        onChipsChanged(false);
    }

    private void unselectAllChips() {
        unselectChipsExcept(null);
    }
    //</editor-fold>

    //<editor-fold desc="InputConnectionWrapperInterface Implementation">
    @Override
    public InputConnection getInputConnection(InputConnection target) {
        return new KeyInterceptingInputConnection(target);
    }
    //</editor-fold>

    //<editor-fold desc="Inner Classes / Interfaces">
    private class EditTextListener implements TextWatcher {

        private boolean mIsPasteTextChange = false;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (count > 1) {
                mIsPasteTextChange = true;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mIsPasteTextChange) {
                mIsPasteTextChange = false;
                // todo handle copy/paste text here

            } else {
                // no paste text change
                if (s.toString().contains("\n")) {
                    String text = s.toString();
                    text = text.replace("\n", "");
                    while (text.contains("  ")) {
                        text = text.replace("  ", " ");
                    }
                    if (text.length() > 1) {
                        s.clear();
                        if (!onEnterPressed(text)) {
                            s.append(text);
                        }
                    } else {
                        s.clear();
                        s.append(text);
                    }
                }
            }
            if (mChipsListener != null) {
                mChipsListener.onTextChanged(s);
            }
        }
    }

    private class KeyInterceptingInputConnection extends InputConnectionWrapper {

        public KeyInterceptingInputConnection(InputConnection target) {
            super(target, true);
        }

        /**
         * 输入字符
         */
        @Override
        public boolean commitText(CharSequence text, int newCursorPosition) {
            return super.commitText(text, newCursorPosition);
        }

        /**
         * 注入按键
         */
        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if (mEditText.length() == 0) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    //删除事件处理
                    if (event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                        selectOrDeleteLastChip();
                        return true;
                    }
                }
            }
            //输入事件处理
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                //就是拼接了一个换行，为什么要这么作
                mEditText.append("\n");
                return true;
            }

            return super.sendKeyEvent(event);
        }

        /**
         * 删除输入的字符
         * <p>
         * 这个稍微有一点问题，需要自己删除，
         */
        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            Log.i(TAG, "====deleteSurroundingText=======" + System.currentTimeMillis());
            // magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
            if (mEditText.length() == 0 && beforeLength == 1 && afterLength == 0) {
                // backspace
                return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                        && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            }

            return super.deleteSurroundingText(beforeLength, afterLength);
        }
    }

    /**
     * todo： 备注，思考
     * 这一部分使用adapter模式 向外开放，能够展现出不同的view实现
     * 至于 是否使用了对象池复用技术就不需要再去计算了
     */
    public class Chip implements OnClickListener {

        private static final int MAX_LABEL_LENGTH = 30;

        private String mLabel;
        private final Uri mPhotoUri;
        private final Contact mContact;
        /**
         * 这边还有一个参数可以设置是否可以编辑
         * <p>
         * Indelible 不可磨灭，默认应该是false
         * <p>
         * 这个换一个此就是 isDeleteAble
         */
        private final boolean mIsIndelible;

        private RelativeLayout mView;
        private View mIconWrapper;
        private TextView mTextView;
        private TextView mInitials;

        private ImageView mAvatarView;
        private ImageView mPersonIcon;
        private ImageView mCloseIcon;

        private ImageView mErrorIcon;

        private boolean mIsSelected = false;

        public Chip(String label, Uri photoUri, Contact contact) {
            this(label, photoUri, contact, false);
        }

        public Chip(String label, Uri photoUri, Contact contact, boolean isIndelible) {
            this.mLabel = label;
            this.mPhotoUri = photoUri;
            this.mContact = contact;
            this.mIsIndelible = isIndelible;

            if (mLabel == null) {
                mLabel = contact.getEmailAddress();
            }

            if (mLabel.length() > MAX_LABEL_LENGTH) {
                mLabel = mLabel.substring(0, MAX_LABEL_LENGTH) + "...";
            }
        }

        public View getView() {
            if (mView == null) {
                mView = (RelativeLayout) inflate(getContext(), R.layout.chips_view, null);

                // Layout Params + margins
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (CHIP_HEIGHT * mDensity));
                layoutParams.setMargins(0, 0, mChipsMargin, 0);
                mView.setLayoutParams(layoutParams);

                mAvatarView = (ImageView) mView.findViewById(R.id.ri_ch_avatar);
                mIconWrapper = mView.findViewById(R.id.rl_ch_avatar);
                mTextView = (TextView) mView.findViewById(R.id.tv_ch_name);
                mInitials = (TextView) mView.findViewById(R.id.tv_ch_initials);
                mPersonIcon = (ImageView) mView.findViewById(R.id.iv_ch_person);
                mCloseIcon = (ImageView) mView.findViewById(R.id.iv_ch_close);

                mErrorIcon = (ImageView) mView.findViewById(R.id.iv_ch_error);

                // set initial res & attrs
                if (mTypeface != null) {
                    mTextView.setTypeface(mTypeface);
                }
                mView.setBackgroundResource(mChipsBgRes);
                if (mIsIndelible) {
                    ((GradientDrawable) mView.getBackground()).setColor(mChipsBgColorIndelible);
                } else {
                    ((GradientDrawable) mView.getBackground()).setColor(mChipsBgColor);
                }
                mIconWrapper.setBackgroundResource(R.drawable.circle);
                if (mIsIndelible) {
                    mTextView.setTextColor(mChipsTextColorIndelible);
                } else {
                    mTextView.setTextColor(mChipsTextColor);
                }

                // set icon resources
                mPersonIcon.setImageResource(mChipsPlaceholderResId);
                if (mChipsPlaceholderTint != 0) {
                    mPersonIcon.setColorFilter(mChipsPlaceholderTint, PorterDuff.Mode.SRC_ATOP);
                }
                mCloseIcon.setBackgroundResource(mChipsDeleteResId);

                // USE INITIALS INSTEAD OF PERSON ICON
                if (mUseInitials) {
                    mPersonIcon.setVisibility(GONE);
                    mInitials.setVisibility(VISIBLE);
                    if (mInitialsTypeface != null) {
                        mInitials.setTypeface(mInitialsTypeface);
                    }
                    if (mInitialsTextColor != 0) {
                        mInitials.setTextColor(mInitialsTextColor);
                    }
                    if (mInitialsTextSize != 0) {
                        mInitials.setTextSize(TypedValue.COMPLEX_UNIT_SP, mInitialsTextSize);
                    }
                } else {
                    mPersonIcon.setVisibility(VISIBLE);
                    mInitials.setVisibility(GONE);
                }

                mView.setOnClickListener(this);
                mIconWrapper.setOnClickListener(this);
            }

            //设置基本的数据
            updateViews();
            return mView;
        }

        private void updateViews() {
            mTextView.setText(mLabel);
            if (mUseInitials) {
                mInitials.setText(getInitials());
            }
            if (mTypeface != null) {
                mTextView.setTypeface(mTypeface);
            }
            if (mPhotoUri != null) {
                Picasso.with(getContext())
                        .load(mPhotoUri)
                        .noPlaceholder()
                        .into(mAvatarView, new Callback() {
                            @Override
                            public void onSuccess() {
                                mPersonIcon.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {

                            }
                        });
            }
            if (isSelected()) {
                if (mChipsValidator != null && !mChipsValidator.isValid(mContact)) {
                    // not valid & show error
                    ((GradientDrawable) mView.getBackground()).setColor(mChipsBgColorErrorClicked);
                    mTextView.setTextColor(mChipsTextColorErrorClicked);
                    mIconWrapper.getBackground().setColorFilter(mChipsColorErrorClicked, PorterDuff.Mode.SRC_ATOP);
                    mErrorIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                } else {
                    ((GradientDrawable) mView.getBackground()).setColor(mChipsBgColorClicked);
                    mTextView.setTextColor(mChipsTextColorClicked);
                    mIconWrapper.getBackground().setColorFilter(mChipsColorClicked, PorterDuff.Mode.SRC_ATOP);
                }
                if (mUseInitials) {
                    mInitials.animate().alpha(0.0f).setDuration(200).start();
                } else {
                    mPersonIcon.animate().alpha(0.0f).setDuration(200).start();
                }
                mAvatarView.animate().alpha(0.0f).setDuration(200).start();
                mCloseIcon.animate().alpha(1f).setDuration(200).setStartDelay(100).start();

            } else {
                if (mChipsValidator != null && !mChipsValidator.isValid(mContact)) {
                    // not valid & show error
                    mErrorIcon.setVisibility(View.VISIBLE);
                    mErrorIcon.setColorFilter(null);
                } else {
                    mErrorIcon.setVisibility(View.GONE);
                }
                if (mIsIndelible) {
                    ((GradientDrawable) mView.getBackground()).setColor(mChipsBgColorIndelible);
                    mTextView.setTextColor(mChipsTextColorIndelible);
                } else {
                    ((GradientDrawable) mView.getBackground()).setColor(mChipsBgColor);
                    mTextView.setTextColor(mChipsTextColor);
                }
                mIconWrapper.getBackground().setColorFilter(mChipsColor, PorterDuff.Mode.SRC_ATOP);

                if (mUseInitials) {
                    mInitials.animate().alpha(1f).setDuration(200).setStartDelay(100).start();
                } else {
                    mPersonIcon.animate().alpha(1f).setDuration(200).setStartDelay(100).start();
                }
                mAvatarView.animate().alpha(1f).setDuration(200).setStartDelay(100).start();
                mCloseIcon.animate().alpha(0.0f).setDuration(200).start();
            }
        }

        @NonNull
        private String getInitials() {
            if (mLabel != null) {
                if (mLabel.trim().contains(" ")) {
                    String[] split = mLabel.trim().split(" ");
                    return String.format("%s%s", String.valueOf(split[0].charAt(0)), String.valueOf(split[split.length - 1].charAt(0)));
                } else {
                    return String.valueOf(mLabel.charAt(0));
                }
            } else {
                return "";
            }
        }

        /**
         * 因为实现了 OnClickListener ，所有才有这个方法
         * todo
         * 但是不知道这个方法有什么用处
         *
         * @param v
         */
        @Override
        public void onClick(View v) {
            mEditText.clearFocus();
            Log.i(TAG, v.getId() + "=====onClick=========onClick===========" + mView.getId());
            if (v.getId() == mView.getId()) {
                onChipInteraction(this, true);
            } else {
                onChipInteraction(this, false);
            }
        }

        public boolean isSelected() {
            return mIsSelected;
        }

        public void setSelected(boolean isSelected) {
            if (mIsIndelible) {
                return;
            }
            this.mIsSelected = isSelected;
        }

        public Contact getContact() {
            return mContact;
        }

        @Override
        public boolean equals(Object o) {
            if (mContact != null && o instanceof Contact) {
                return mContact.equals(o);
            }
            return super.equals(o);
        }

        @Override
        public String toString() {
            return "{"
                    + "[Contact: " + mContact + "]"
                    + "[Label: " + mLabel + "]"
                    + "[PhotoUri: " + mPhotoUri + "]"
                    + "[IsIndelible" + mIsIndelible + "]"
                    + "}"
                    ;
        }
    }

    /**
     * 这个暂时不知道怎么作用的
     * <p>
     * 数据回调 ，这边添加了什么曹锁，fragment或者Activity能够针对数据做一定的响应
     */
    public interface ChipsListener {
        void onChipAdded(Chip chip);

        void onChipDeleted(Chip chip);

        void onTextChanged(CharSequence text);

        /**
         * return true to delete the invalid text.
         */
        boolean onInputNotRecognized(String text);
    }

    public static abstract class ChipValidator {
        public abstract boolean isValid(Contact contact);
    }
    //</editor-fold>
}
