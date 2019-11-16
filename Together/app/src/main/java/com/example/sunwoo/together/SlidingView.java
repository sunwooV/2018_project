package com.example.sunwoo.together;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.Toast;

public class SlidingView extends ViewGroup{
    private static final String TAG = "SlidingView";
    private VelocityTracker mVelocityTracker = null;
    private static final int SNAP_VELOCITY = 100;
    private int mTouchSlop = 10;
    private Bitmap mWallpaper = null;
    private Paint mPaint = null;
    private Scroller mScroller = null;

    private PointF mLastPoint = null;
    private int mCurPage = 0;
    private int mCurTouchState;
    private static final int TOUCH_STATE_SCROLLING = 0;
    private static final int TOUCH_STATE_NORMAL = 1;
    private Toast mToast;
    boolean isFirstMove = true;

    public static String myText="지폐";


    public SlidingView(Context context) {
        super(context);
        init();
    }

    public SlidingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlidingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mWallpaper = BitmapFactory.decodeResource(getResources(),
                R.drawable.intro);
        mPaint = new Paint();
        mScroller = new Scroller(getContext());
        mLastPoint = new PointF();


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure");
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "onLayout");
        for (int i = 0; i < getChildCount(); i++) {
            int child_left = getChildAt(i).getMeasuredWidth() * i;
            getChildAt(i).layout(child_left, t, child_left + getChildAt(i).getMeasuredWidth(),
                    getChildAt(i).getMeasuredHeight());
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.drawBitmap(mWallpaper, 0, 0, mPaint);
        for (int i = 0; i < getChildCount(); i++) {
            drawChild(canvas, getChildAt(i), 100);
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.d(TAG, "event Action : " + event.getAction());
        if (mVelocityTracker == null)
            mVelocityTracker = VelocityTracker.obtain();

        mVelocityTracker.addMovement(event);
        if(mCurPage == 1){
            myText = "지폐";

        }
        else if(mCurPage == 0){
            myText = "점자";
            //myTTS = new TextToSpeech(this, this);
        }

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastPoint.set(event.getX(), event.getY());
                break;

            case MotionEvent.ACTION_MOVE:
                int x = (int) (event.getX() - mLastPoint.x);
                scrollBy(-x, 0);

                // move 스크롤 이벤트가 가장 처음 일어났을때만
                if (isFirstMove && getChildCount() > 2) {
                    // 현재 전체 페이지보다 상위로 스크롤 했을때, overScroll 이벤트 발생
                    if (x < 0 && mCurPage == getChildCount() - 1) {
                        overScroll();
                    } else if (x > 0 && mCurPage == 0) {
                        // 하위로 스크롤 했을때 underScroll
                        underScroll();
                    }
                }
                isFirstMove = false;

                invalidate();
                mLastPoint.set(event.getX(), event.getY());
                break;

            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000);
                int v = (int) mVelocityTracker.getXVelocity();

                int gap = getScrollX() - mCurPage * getWidth();
                Log.d(TAG, "mVelocityTracker : " + v);
                int nextPage = mCurPage;

                if ((v > SNAP_VELOCITY || gap < -getWidth() / 2) && mCurPage > 0) {
                    nextPage--;
                } else if ((v < -SNAP_VELOCITY || gap > getWidth() / 2) && mCurPage < getChildCount() - 1) {
                    nextPage++;
                }

                int move = 0;
                if (mCurPage != nextPage) {
                    move = getChildAt(0).getWidth() * nextPage - getScrollX();
                } else {
                    move = getWidth() * mCurPage - getScrollX();
                }

                mScroller.startScroll(getScrollX(), 0, move, 0, Math.abs(move));

                if (mToast != null) {
                    mToast.setText("page : " + nextPage);



                } else {
                    mToast = Toast.makeText(getContext(), "page : " + nextPage, Toast.LENGTH_SHORT);

                }


                Log.d(TAG,myText+"~~~~~~~~~~~~~~~~~~~~~~~~!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                mToast.show();
                invalidate();
                mCurPage = nextPage;

                mCurTouchState = TOUCH_STATE_NORMAL;
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                isFirstMove = true; // move 스크롤 체크용 초기화
                break;
        }


        return true;
    }

    @Override
    public void computeScroll() {

        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onInterceptTouchEvent : " + ev.getAction());
        int action = ev.getAction();
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mCurTouchState = mScroller.isFinished() ? TOUCH_STATE_NORMAL
                        : TOUCH_STATE_SCROLLING;
                mLastPoint.set(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                int move_x = Math.abs(x - (int) mLastPoint.x);
                if (move_x > mTouchSlop) {
                    mCurTouchState = TOUCH_STATE_SCROLLING;
                    mLastPoint.set(x, y);
                }
                break;
        }

        return mCurTouchState == TOUCH_STATE_SCROLLING;
    }

    private void underScroll() {
        View v = getChildAt(getChildCount() - 1);
        removeViewAt(getChildCount() - 1);
        addView(v, 0);
        setPage(getChildCount() - 1);
    }

    private void overScroll() {
        View v = getChildAt(0);
        removeViewAt(0);
        setPage(0);
        addView(v);
    }

    public void setPage(int p) {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
        if (p < 0 || p > getChildCount())
            return;
        mCurPage = p;
        scrollTo(getWidth() * p, 0);
    }




    public static String getData(){
        return myText;
    }

    private static ImageClassifier instance = null;

    public static synchronized ImageClassifier getInstance(){
        return instance;
    }
}