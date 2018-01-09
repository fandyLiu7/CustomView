package com.fandy.customview;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

/**
 * 自定义view的学习
 * <p>
 * 1大点:获取view的宽高的几种方式
 */
public class MainActivity extends Activity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.tv);
        //获取view宽高的几种方式
        /**
         * 在onCreat方法中无法获取view的宽高的,,因为这个时候,view还没有完成layout的工作
         * 这里可以使用如下的集中方式来获取view的宽高
         */
        int i = DensityUtils.dip2px(this, 200);
        int y = DensityUtils.dip2px(this, 150);
        System.out.println("实际的宽=" + i + "  实际的高=" + y);
        method();
        method2();
        method4();
    }

    /**
     * 第四种方式
     * view.measure()方法
     * 多种情况分情况讨论
     * TODO ;暂时还有问题,,暂未解决
     */
    private void method4() {
      /*  int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(width, height);
        int measuredHeight = textView.getMeasuredHeight();
        int measuredWidth = textView.getMeasuredWidth();

        int height1 = textView.getHeight();
        int width1 = textView.getWidth();
        System.out.println("************************method4*******************************");
        System.out.println("宽 =" + measuredWidth + "   高=" + measuredHeight);*/
    }


    /**
     * 第三种方式,添加视图树的方式
     */
    private void method2() {
        textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int measuredHeight = textView.getMeasuredHeight();
                int measuredWidth = textView.getMeasuredWidth();
                int height = textView.getHeight();
                int width = textView.getWidth();
                System.out.println("************************addOnGlobalLayoutListener*******************************");
                System.out.println("宽 =" + width + "   高=" + height);
                System.out.println("Measure宽 =" + measuredWidth + "   Measure高=" + measuredHeight);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    textView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    /**
     * 第二种获取宽高的方式
     * 通过View.post将一个runnable对象,
     * 放入到消息队列的尾部,等到Lopper调用该方法的时候,
     * 已经完成了view的初始化工作
     */
    private void method() {
        textView.post(new Runnable() {
            @Override
            public void run() {
                int measuredHeight = textView.getMeasuredHeight();
                int measuredWidth = textView.getMeasuredWidth();
                int height = textView.getHeight();
                int width = textView.getWidth();
                System.out.println("************************post*******************************");
                System.out.println("宽 =" + width + "   高=" + height);
                System.out.println("Measure宽 =" + measuredWidth + "   Measure高=" + measuredHeight);
            }
        });
    }

    public void click(View view) {
        startActivity(new Intent(this, SecondActivity.class));
    }

    public void click1(View view) {
        startActivity(new Intent(this, ThirdActivity.class));
    }
    /**
     * 第一种方式:
     * 这个方法会被多次调用,
     * 当activity的焦点发生改变的时候,
     * 就会被执行,如果频繁的焦点改变,那么就会频繁的触发该方法
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            int width = textView.getWidth();
            int height = textView.getHeight();
            int measuredHeight = textView.getMeasuredHeight();
            int measuredWidth = textView.getMeasuredWidth();
            System.out.println("************************onWindowFocusChanged*******************************");
            System.out.println("宽 =" + width + "   高=" + height);
            System.out.println("Measure宽 =" + measuredWidth + "   Measure高=" + measuredHeight);
        }
    }
}
