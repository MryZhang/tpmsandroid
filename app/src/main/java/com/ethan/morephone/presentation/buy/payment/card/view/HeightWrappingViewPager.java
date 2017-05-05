package com.ethan.morephone.presentation.buy.payment.card.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Ethan on 5/3/17.
 */

public class HeightWrappingViewPager extends ViewPager {

    public HeightWrappingViewPager(Context context) {
        super(context);
    }

    public HeightWrappingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View firstChild = getChildAt(0);
        firstChild.measure(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(firstChild.getMeasuredHeight(), MeasureSpec.EXACTLY));
    }
}