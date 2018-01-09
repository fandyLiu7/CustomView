package com.fandy.customview.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 公司:北京同道伟业体育科技有限公司
 * 作者：Shinelon
 * 时间:on 2018/1/9 13:25
 * 说明:自定义一个类似于ViewPager的横向滑动的控件
 * 可以和listview嵌套使用
 */
public class HorizontalScrollViewEx extends ViewGroup {

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mChildSize;
    private int mChildWidth;
    //分别记录上次滑动的坐标
    private int mLastXIntercept = 0;
    private int mLastYIntercept = 0;

    private int mChildIndex;

    private int mLastX = 0;
    private int mLastY = 0;

    public HorizontalScrollViewEx(Context context) {
        this(context, null);
    }

    public HorizontalScrollViewEx(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public HorizontalScrollViewEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化操作
     */
    private void init() {
        if (mScroller == null) {
            mScroller = new Scroller(getContext());
            //主要用于跟踪屏幕操作的速度
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    /**
     * 采用的是外部拦截的方式,解决滑动冲突
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        //获取初始的参数xy
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                intercepted = false;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    //下边这段代码还是不要的好,太影响滑动体验了
                    //intercepted = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastXIntercept;
                int detlaY = y - mLastYIntercept;
                if (Math.abs(deltaX) > (Math.abs(detlaY))) {//表示的是横向的滑动
                    intercepted = true;
                    System.out.println("拦截了");
                } else {
                    intercepted = false;
                    System.out.println("不拦截了");
                }
                break;
            case MotionEvent.ACTION_UP:
                intercepted = false;
                break;
            default:
                break;
        }

        mLastX = x;
        mLastY = y;
        mLastYIntercept = y;
        mLastXIntercept = x;
        return intercepted;
    }

    /**
     * 处理事件  横向滑动的事件的处理 ,,,滑动事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastX;
                int detlaY = y - mLastY;
                scrollBy(-deltaX, 0);
                break;
            case MotionEvent.ACTION_UP:
                //主要就是在这个动作里边真正的做操作
                int scrollX = getScrollX();
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocity = mVelocityTracker.getXVelocity();
                if (Math.abs(xVelocity) >= 50) {
                    mChildIndex = xVelocity > 0 ? mChildIndex - 1 : mChildIndex + 1;
                } else {
                    mChildIndex = (scrollX + mChildWidth / 2) / mChildWidth;
                }
                mChildIndex = Math.max(0, Math.min(mChildIndex, mChildSize - 1));
                int dx = mChildIndex * mChildWidth - scrollX;
                smoothScrollBy(dx, 0);
                mVelocityTracker.clear();
                break;
            default:
                break;
        }

        mLastX = x;
        mLastY = y;
        return true;
    }

    private void smoothScrollBy(int dx, int i) {
        mScroller.startScroll(getScrollX(), 0, dx, i, 500);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    /**
     * 测量
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = 0;
        int measuredHeight = 0;
        //获取子的个数
        int childCount = getChildCount();
        //测量子布局
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        //获取大小和模式
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        /**
         * 根据各种情况作出单独的判断操作
         */
        if (childCount == 0) {
            setMeasuredDimension(0, 0);
        } else if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            View childView = getChildAt(0);
            measuredWidth = childView.getMeasuredWidth() * childCount;
            measuredHeight = childView.getMeasuredHeight();
            setMeasuredDimension(measuredWidth, measuredHeight);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            //高度是warp_content
            View childView = getChildAt(0);
            measuredHeight = childView.getMeasuredHeight();
            setMeasuredDimension(widthSpecSize, measuredHeight);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            View childView = getChildAt(0);
            measuredWidth = childView.getMeasuredWidth() * childCount;
            setMeasuredDimension(measuredWidth, heightSpecSize);
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = 0;
        int childCount = getChildCount();
        mChildSize = childCount;

        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() != View.GONE) {
                int measuredWidth = childView.getMeasuredWidth();
                mChildWidth = measuredWidth;
                childView.layout(childLeft, 0, childLeft + measuredWidth, childView.getMeasuredHeight());
                childLeft += measuredWidth;
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        //回收避免资源浪费
        mVelocityTracker.recycle();
        super.onDetachedFromWindow();
    }
}
