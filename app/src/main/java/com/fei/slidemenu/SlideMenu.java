package com.fei.slidemenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
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

    private static final String TAG = "SlideMenu";
    private float mRightPadding = 50;//默认离右边的距离
    private View mMenuView;//菜单View
    private View mContentView;//内容View
    private int mMenuWidth = 0;//菜单宽度
    private boolean mIsMenuClose = true;//一进来时关闭状态
    private GestureDetector mGestureDetector;//拦截快速手势

    public SlideMenu(Context context) {
        this(context, null);
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //获取属性值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlideMenu);
        mRightPadding = typedArray.getDimension(R.styleable.SlideMenu_rightPadding, dp2px(mRightPadding));
        Log.e(TAG, "padding宽度" + mRightPadding);
        typedArray.recycle();

        mGestureDetector = new GestureDetector(context, mSimpleOnGestureListener);
    }

    /**
     * 拦截手指快速滑动
     */
    private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.e(TAG, "velocityX = " + velocityX);

            // 判断手势快速滑动时，当前的状态去改变原有状态，所以不是判断scrollX,而是判断当前状态
            if (e2.getAction() == MotionEvent.ACTION_UP) {
                if (mIsMenuClose && velocityX > 0) {
                    //如果当前状态是关闭，且快速向右滑动，就需要打开
                    openMenu();
                    return true;
                } else if (!mIsMenuClose && velocityX < 0) {
                    //如果当前状态是打开，且快速向左滑动，就需要关闭
                    closeMenu();
                    return true;
                }
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    };

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
        Log.e(TAG, "屏幕宽度:" + screenWidth);

        //重新设置menu宽度
        ViewGroup.LayoutParams layoutParams = mMenuView.getLayoutParams();
        mMenuWidth = (int) (screenWidth - mRightPadding);
        Log.e(TAG, "菜单栏宽度:" + mMenuWidth);
        layoutParams.width = mMenuWidth;
        mMenuView.setLayoutParams(layoutParams);

        //重新设置content宽度
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
            //隐藏menu
            scrollTo(mMenuWidth, 0);
            mIsMenuClose = true;

            //设置缩放中心点
            mMenuView.setPivotX(mMenuWidth);
            mMenuView.setPivotY(getMeasuredHeight() / 2);
            mContentView.setPivotX(0);
            mContentView.setPivotY(getMeasuredHeight() / 2);
        }
    }

    /**
     * 监听滚动
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        moveAnimation(l);
    }

    /**
     * 拦截菜单栏打开时，触摸内容栏会出发关闭菜单栏功能
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            //菜单栏打开的状态且点击区域在内容栏
            if (!mIsMenuClose && ev.getX() >= mMenuWidth) {
                closeMenu();
                return false;//不继续分发下去
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (mGestureDetector.onTouchEvent(ev)) {
            return true;
        }

        //不能在dispatch做拦截，因为这样每次都会进来这里，要滚动后才进来这里
        int scrollX = getScrollX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                //本来在这里监听滑动时动画效果，发现手指抬起后smoothScrollTo，没有调用动画效果，所以监听onScrollChanged
//                moveAnimation(scrollX);
                break;
            case MotionEvent.ACTION_UP:
                //手指抬起，判断菜单栏和内容需要滚动到哪个内容
                Log.e(TAG, "scrollX" + scrollX);
                if (scrollX >= mMenuWidth / 2) {
                    //如果滚动条大于菜单宽度一半就关闭
                    closeMenu();
                } else {
                    //反之打开菜单
                    openMenu();
                }
                return true;//抬起后不走系统代码，直接返回
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 左右两边动画效果
     */
    private void moveAnimation(int scrollX) {
        //scrollX的取值范围是0~mMenuWidth之间来回
        float percent = scrollX * 1.0f / mMenuWidth;
        //percent 1~0
        //如果菜单栏关闭，scrollX等于mMenuWidth,手指只能向右移动
        //菜单栏 透明度0.7~1，缩放从0.7~1，translateX 0.2~0
        float alpha = 0.7f + (1 - percent) * 0.3f;
        mMenuView.setAlpha(alpha);
        float translateX = percent * 0.2f * mMenuWidth;
        mMenuView.setTranslationX(translateX);
        float menuViewScale = alpha;//跟透明度一样
        mMenuView.setScaleX(menuViewScale);
        mMenuView.setScaleY(menuViewScale);
        //内容   缩放1~0.7
        float contentViewScale = 0.7f + percent * 0.3f;
        mContentView.setScaleX(contentViewScale);
        mContentView.setScaleY(contentViewScale);
    }


    /**
     * 打开菜单栏
     */
    private void openMenu() {
        smoothScrollTo(0, 0);
        mIsMenuClose = false;
    }

    /**
     * 关闭菜单栏
     */
    private void closeMenu() {
        smoothScrollTo(mMenuWidth, 0);
        mIsMenuClose = true;
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
