package com.xiaoyehai.viewdragerhelper.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;
import com.xiaoyehai.viewdragerhelper.ColorUtil;

/**
 * 继承FrameLayout
 * 在自定义ViewGroup的时候，如果对子View的测量没有特殊的需求，那么可以继承系统已有的
 * 布局(比如FrameLayout)，目的是为了让已有的布局帮我们实行onMeasure;
 * <p>
 * Created by xiaoyehai on 2018/8/3 0003.
 */

public class DragLayout extends FrameLayout {

    /**
     * ViewDragHelper:它主要用于处理ViewGroup中对子View的拖拽处理,
     * 本质是对触摸事件的解析类;
     */
    private ViewDragHelper mViewDragHelper;

    private View redView;

    private View blueView;

    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    /**
     * 初始化ViewDragHelper
     */
    private void init() {
        mViewDragHelper = ViewDragHelper.create(this, callback);
    }

    /**
     * 当DragLayout的布局文件的结束标签读取完成后会执行该方法，此时会知道自己有几个子控件
     * 一般用来初始化子控件的引用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        redView = getChildAt(0);
        blueView = getChildAt(1);
    }

    /**
     * 确定控件位置
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //放在左上角
        int left = getPaddingLeft();
        int top = getPaddingTop();

        //放在水平居中
        //int left = getPaddingLeft() + getMeasuredWidth() / 2 - redView.getMeasuredWidth() / 2;

        //摆放在左上角
        redView.layout(left, top, left + redView.getMeasuredWidth(),
                top + redView.getMeasuredHeight());

        //摆放在redView下面
        blueView.layout(left, redView.getBottom(), left + blueView.getMeasuredWidth(),
                redView.getBottom() + blueView.getMeasuredHeight());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 让ViewDragHelper帮我们判断是否应该拦截
        boolean result = mViewDragHelper.shouldInterceptTouchEvent(ev);
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 将触摸事件交给ViewDragHelper来解析处理
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    /**
     * 回调类
     */
    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        /**
         * 用于判断是否捕获当前child的触摸事件
         * @param child 当前触摸的子View
         * @param pointerId
         * @return true:捕获并解析  false:不处理
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == blueView || child == redView;
        }

        /**
         * 当view被开始捕获和解析的回调
         * @param capturedChild 当前被捕获的子view
         * @param activePointerId
         */
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        /**
         * 获取view水平方向的拖拽范围,但是目前不能限制边界,返回的值目前用在手指抬起的时候
         *  view缓慢移动的动画时间的计算; 最好不要返回0
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return getMeasuredWidth() - child.getMeasuredWidth();
        }

        /**
         * 获取view垂直方向的拖拽范围,目前不能限制边界，最好不要返回0
         * @param child
         * @return
         */
        @Override
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight() - child.getMeasuredHeight();
        }

        /**
         * 控制child在水平方向的移动
         * @param child 当前触摸的子View
         * @param left 当前child的即将移动到的位置,left=chile.getLeft()+dx
         * @param dx 本次child水平方向移动的距离
         * @return 表示你真正想让child的left变成的值
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (left < 0) {
                left = 0; //限制左边界
            } else if (left > getMeasuredWidth() - child.getMeasuredWidth()) {
                left = getMeasuredWidth() - child.getMeasuredWidth(); //限制右边界
            }
            // return left-dx; //不能移动
            return left;
        }

        /**
         * 控制child在垂直方向的移动
         * @param child
         * @param top  top=child.getTop()+dy
         * @param dy
         * @return
         */
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if (top < 0) {
                top = 0; //限制上边界
            } else if (top > getMeasuredHeight() - child.getMeasuredHeight()) {
                top = getMeasuredHeight() - child.getMeasuredHeight(); //限制下边界
            }
            return top;
        }

        /**
         * 当child的位置改变的时候执行,一般用来做其他子View跟随该view移动
         * @param changedView 当前位置改变的child
         * @param left child当前最新的left
         * @param top child当前最新的top
         * @param dx 本次水平移动的距离
         * @param dy 本次垂直移动的距离
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            //让redView跟着blueView移动
            if (changedView == blueView) {
                redView.layout(redView.getLeft() + dx, redView.getTop() + dy,
                        redView.getRight() + dx, redView.getBottom() + dy);
            } else if (changedView == redView) {
                //让blueView跟着redView移动
                blueView.layout(blueView.getLeft() + dx, blueView.getTop() + dy,
                        blueView.getRight() + dx, blueView.getBottom() + dy);
            }
            //1.计算view移动百分比:当前位置/总长
            float fraction = changedView.getLeft() * 1f / (getMeasuredWidth() - changedView.getMeasuredWidth());
            //2.执行伴随动画
            excuteAnim(fraction);
        }

        /**
         * 手指抬起的执行该方法
         * @param releasedChild 当前抬起的view
         * @param xvel x方向的移动速度有 正：向右移动
         * @param yvel 方向的移动速度
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            int centerLeft = getMeasuredWidth() / 2 - releasedChild.getMeasuredWidth() / 2;
            if (releasedChild.getLeft() < centerLeft) {
                //控件在左半边，向左缓慢移动
                //ViewDragHelper封装了Scroller
                mViewDragHelper.smoothSlideViewTo(releasedChild, 0, releasedChild.getTop());
                ViewCompat.postInvalidateOnAnimation(DragLayout.this); //刷新
            } else {
                //控件在右半边，向右缓慢移动
                mViewDragHelper.smoothSlideViewTo(releasedChild,
                        getMeasuredWidth() - releasedChild.getMeasuredWidth(), releasedChild.getTop());
                ViewCompat.postInvalidateOnAnimation(DragLayout.this); //刷新
            }
        }
    };

    @Override
    public void computeScroll() {
        //如果动画还没结束
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(DragLayout.this); //刷新
        }
    }

    /**
     * 执行移动过程中的伴随动画
     *
     * @param fraction 移动百分比
     */
    private void excuteAnim(float fraction) {
        //缩放
        //        redView.setScaleX(1 + 0.5f * fraction);
        //        redView.setScaleY(1 + 0.5f * fraction);

        //ViewHelper.setScaleX(redView, 1 + 0.5f * fraction);
        //ViewHelper.setScaleX(blueView, 1 + 0.5f * fraction);
        //ViewHelper.setScaleY(redView, 1 + 0.5f * fraction);


        //旋转
        // ViewHelper.setRotation(redView, 720 * fraction); //围绕z抽转
        ViewHelper.setRotationX(redView, 360 * fraction); //围绕x抽转
        ViewHelper.setRotationX(blueView, 360 * fraction); //围绕x抽转
        //ViewHelper.setRotationX(redView, 360 * fraction);
        // ViewHelper.setRotationY(redView,720 * fraction); //围绕y抽转

        //平移
        // ViewHelper.setTranslationX(redView,80 * fraction);

        //透明度
        // ViewHelper.setAlpha(redView,1 - fraction);

        //设置 redView过度颜色的渐变
        //redView.setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction, Color.RED, Color.GREEN));
        //设置该控件过度颜色的渐变
        //setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction, Color.RED, Color.GREEN));
    }

}
