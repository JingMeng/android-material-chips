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

package com.doodle.android.chips.views;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.doodle.android.chips.ChipsView;

import java.util.ArrayList;
import java.util.List;

public class ChipsVerticalLinearLayout extends LinearLayout {

    private List<LinearLayout> mLineLayouts = new ArrayList<>();

    private float mDensity;
    private int mRowSpacing;

    /**
     * 基本配置
     */
    public ChipsVerticalLinearLayout(Context context, int rowSpacing) {
        super(context);

        mDensity = getResources().getDisplayMetrics().density;
        mRowSpacing = rowSpacing;

        init();
    }

    private void init() {
        setOrientation(VERTICAL);
    }

    /**
     * 返回最后一行的参数设置， 这么做的目的主要是为了最后一行和EditText做交互使用的
     */
    public TextLineParams onChipsChanged(List<ChipsView.Chip> chips) {
        clearChipsViews();

        int width = getWidth();
        if (width == 0) {
            return null;
        }
        int widthSum = 0;
        int chipsCount = 0;
        int rowCounter = 0;

        LinearLayout ll = createHorizontalView();

        for (ChipsView.Chip chip : chips) {
            View view = chip.getView();
            /**
             *   测量view
             *   我们之前是按照这个来测量的
             *   measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, heightUsed);
             *
             *   heightUsed 这个 heightUsed 是我们的这个控件的父布局可能不是滑动的(高度固定的)  这个更多的是为了适配warp的情形吧
             *   widthUsed 我们没有使用，是我们自己手偶东借宿那折行的
             *
             */
            view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

            // if width exceed current width. create a new LinearLayout
            if (widthSum + view.getMeasuredWidth() > width) {
                //达到换行的条件，行数要+1了
                rowCounter++;
                //每一行的数据重新设置，上一次的计算量就是这一行的最大了
                widthSum = 0;
                chipsCount = 0;
                ll = createHorizontalView();
            }

            //每一行的开始或者继续操作
            widthSum += view.getMeasuredWidth();
            chipsCount++;
            ll.addView(view);
        }

        // check if there is enough space left
        if (width - widthSum < width * 0.1f) {
            widthSum = 0;
            chipsCount = 0;
            rowCounter++;
        }
        if (width == 0) {
            rowCounter = 0;
        }
        return new TextLineParams(rowCounter, widthSum, chipsCount);
    }

    /**
     * 水平方向添加
     * 我们是自己定义的onMeasure和onLayout
     * 这样就会导致 整个计算相对复杂一点
     * <p>
     * 这个地方的自定义是通过，一个垂直的LinearLayout
     * 加上N个水平的LinearLayout 来实现标签的动态处理
     */
    private LinearLayout createHorizontalView() {
        LinearLayout ll = new LinearLayout(getContext());
        ll.setPadding(0, 0, 0, mRowSpacing);
        ll.setOrientation(HORIZONTAL);
        addView(ll);
        mLineLayouts.add(ll);
        return ll;
    }


    /**
     * 清除视图
     */
    private void clearChipsViews() {
        for (LinearLayout linearLayout : mLineLayouts) {
            linearLayout.removeAllViews();
        }
        mLineLayouts.clear();
        removeAllViews();
    }

    /**
     * 这个创建的并不是一个LayoutParam对象，而是一个类似于我们自定义View的时候的一个记忆对象
     */
    public static class TextLineParams {
        public int row;
        public int lineMargin;
        public int chipsCount = 0;

        /**
         * new TextLineParams(rowCounter, widthSum, chipsCount)
         *
         * @param row        有多少行数据  -----------  因为在这个项目中最多占用了一行，所以我们记录一下这个行数就能知道上面有多少高度 也劲儿确定了顶部的margin使用
         *                   如果是能折行的话，需要我们手机计算出上面的正确高度，也就是会出现上面的那个我们自己写的那个TagLayout的的高度计算问题
         * @param lineMargin 这一行占用的宽度--------也就是需要针对EditText要加一下leftMargin这个操作
         * @param chipsCount 最后一行有多少个 view
         */
        public TextLineParams(int row, int lineMargin, int chipsCount) {
            this.row = row;
            this.lineMargin = lineMargin;
            this.chipsCount = chipsCount;
        }
    }
}
