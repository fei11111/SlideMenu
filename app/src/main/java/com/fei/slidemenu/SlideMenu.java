package com.fei.slidemenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * @ClassName: SlideMenu
 * @Description: 描述
 * @Author: Fei
 * @CreateDate: 2020/12/25 15:26
 * @UpdateUser: Fei
 * @UpdateDate: 2020/12/25 15:26
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */

public class SlideMenu extends HorizontalScrollView {

    private float mRightPadding = 50;//默认离右边的距离
    private View mMenuView;//菜单View
    private View mContentView;//内容View
    private int mMenuWidth = 0;//菜单宽度

    public SlideMenu(Context context) {
        super(context);
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //获取属性值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlideMenu);
        mRightPadding = typedArray.getDimension(R.styleable.SlideMenu_rightPadding, dp2px(mRightPadding));
        typedArray.recycle();

    }

    /**
     * 布局完成时会回调，重新设置布局宽度
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        int count = getChildCount();
        if (count > 1) {
            throw new RuntimeException("scrollview 只能包含一个child,而且只能是LinearLayout");
        }
        LinearLayout container = (LinearLayout) getChildAt(0);
        int childCount = container.getChildCount();
        if (childCount < 2) {
            throw new RuntimeException("LinearLayout必须包含两个child");
        }
        mMenuView = container.getChildAt(0);
        mContentView = container.getChildAt(1);

        int screenWidth = getScreenWidth();
        Log.e("TAG", "屏幕宽度:" + screenWidth);

        ViewGroup.LayoutParams layoutParams = mMenuView.getLayoutParams();
        mMenuWidth = (int) (screenWidth - mRightPadding);
        layoutParams.width = mMenuWidth;
        mMenuView.setLayoutParams(layoutParams);

        layoutParams = mContentView.getLayoutParams();
        layoutParams.width = screenWidth;
        mContentView.setLayoutParams(layoutParams);
    }

    /**
     * 在Activity onResume后 测量后 才回调
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            scrollTo(mMenuWidth, 0);
        }
    }

    /**
     * dp 转 px
     */
    private float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    //获取屏幕宽度
    private int getScreenWidth() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }
}
