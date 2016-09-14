package com.solo.pullrefreshlayout.library;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

/**
 * Created by lingyiyong on 2016/8/11.
 */
public class PullRefreshLayout extends RelativeLayout {
    private final String TAG = "RefreshLayout";
    private final float DRAG_RATE = 0.25f;
    private final float DAMPINF_COEFFICIENT = 0.3f;
    private final float LOAD_THRESHOLD = 0.4f;
    private View mRefreshView , mLoadMoreView;
    private boolean isRefreshable = true, isLoadmoreable = true , isNoMoreData , isAutoLoadMore = true;
    private State state = State.IDEL;
    private Mode mMode = Mode.FOLLOW;
    private RefreshViewAdapter mRefreshViewAdapter , mLoadMoreViewAdapter;
    private OnRefreshListener mOnRefreshListener;
    private View mTargetView;
    private float lastY;

    public enum Mode {
        FOREGROUND , FOLLOW
    }
    public PullRefreshLayout(Context context) {
        super(context);
    }

    public PullRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        if (mRefreshViewAdapter == null) {
            mRefreshViewAdapter = new DefaultRefreshViewAdapter(getContext());
        }

        if (mLoadMoreViewAdapter == null) {
            mLoadMoreViewAdapter = new DefaultLoadMoreViewAdapter(getContext());
        }

        setRefreshViewAdapter(mRefreshViewAdapter);
        setLoadMoreViewAdapter(mLoadMoreViewAdapter);
    }

    public RefreshViewAdapter getRefreshViewAdapter() {
        return mRefreshViewAdapter;
    }

    public void setRefreshViewAdapter(RefreshViewAdapter mRefreshViewAdapter) {
        this.mRefreshViewAdapter = mRefreshViewAdapter;
        int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        //refresh view
        if (mRefreshView != null) {
            removeView(mRefreshView);
        }
        mRefreshView = mRefreshViewAdapter.getView();
        mRefreshView.measure(w , h);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT , LayoutParams.WRAP_CONTENT);
        params.topMargin = -mRefreshView.getMeasuredHeight();
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        addView(mRefreshView , params);
    }

    public RefreshViewAdapter getLoadMoreViewAdapter() {
        return mLoadMoreViewAdapter;
    }

    public void setLoadMoreViewAdapter(RefreshViewAdapter mLoadMoreViewAdapter) {
        this.mLoadMoreViewAdapter = mLoadMoreViewAdapter;
        int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        //load more view
        if (mLoadMoreView != null) {
            removeView(mLoadMoreView);
        }
        mLoadMoreView = mLoadMoreViewAdapter.getView();
        mLoadMoreView.measure(w , h);
        LayoutParams loadmoreParams = new LayoutParams(LayoutParams.WRAP_CONTENT , LayoutParams.WRAP_CONTENT);
        loadmoreParams.bottomMargin = -mLoadMoreView.getMeasuredHeight();
        loadmoreParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        loadmoreParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(mLoadMoreView , loadmoreParams);
    }

    /**
     * change current state
     * @param state
     */
    public void setState(State state) {
        if (this.state != state) {
            mRefreshViewAdapter.stateChange(state);
            mLoadMoreViewAdapter.stateChange(state);

            if (mOnRefreshListener != null) {
                switch (state) {
                    case REFRESHING:
                        mOnRefreshListener.onRefresh();
                        setNoMoreData(false);
                        break;
                    case LOADING_MORE:
                        mOnRefreshListener.onLoadMore();
                        break;
                }
            }
        }
        this.state = state;
    }

    int offsetY = 0;
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int count = getChildCount();
        for (int i = 0 ; i < count; i++) {
            mTargetView = getChildAt(i);
            if (mTargetView != mRefreshView && mTargetView != mLoadMoreView) {
                break;
            }
        }
        if (mTargetView != null) {
            offsetY = mTargetView.getTop();
            ViewCompat.setOverScrollMode(mTargetView , ViewCompat.OVER_SCROLL_NEVER);

            if (mTargetView instanceof RecyclerView) {
                ((RecyclerView) mTargetView).addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (isAutoLoadMore && Utils.isScrollToBottom(PullRefreshLayout.this , mTargetView)) {
                            setLoadingMore(true);
                        }
                    }
                });
            } else if (mTargetView instanceof AbsListView) {
                AbsListView mAbsListView = (AbsListView) mTargetView;
                mAbsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        if (isAutoLoadMore && (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING) && Utils.isScrollToBottom(PullRefreshLayout.this , mTargetView)) {
                            setLoadingMore(true);
                        }
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    }
                });
            }
        }

        init();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mMode != Mode.FOLLOW && (state == State.REFRESHING || state == State.LOADING_MORE) ) {
            return super.dispatchTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (state == State.BECOMING_TO_REFRESH) {
                    startTranslateAnim(mRefreshView , mRefreshView.getMeasuredHeight());
                    setState(State.REFRESHING);
                } else if (state == State.BECOMING_TO_LOADING_MORE) {
                    startTranslateAnim(mLoadMoreView , -mLoadMoreView.getMeasuredHeight());
                    setState(State.LOADING_MORE);
                } else if (state != State.REFRESHING && state != State.LOADING_MORE) {
                    setComplete();
                }
                break;
            case MotionEvent.ACTION_DOWN:
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float y = ev.getY();
                float dy = lastY - y;
                boolean canDown = ViewCompat.canScrollVertically(mTargetView , -1);
                boolean canUp = ViewCompat.canScrollVertically(mTargetView , 1);
                if (canDown && canUp) {
                    return super.dispatchTouchEvent(ev);
                }
                float refreshView_translationY = ViewCompat.getTranslationY(mRefreshView);
                float loadMoreView_translationY = ViewCompat.getTranslationY(mLoadMoreView);
                //Log.d(TAG , "canDown:" + canDown + " canUp:" + canUp + " refreshView_translationY:" + refreshView_translationY + " loadMoreView_translationY:" + loadMoreView_translationY + " dy:" + dy);
                if ((isRefreshable && !canDown && dy < 0)
                        || (isLoadmoreable && !isNoMoreData && !canUp && dy > 0)
                        || refreshView_translationY != 0
                        || loadMoreView_translationY != 0) {

                    if (!onTouchEvent(ev)) {
                        return super.dispatchTouchEvent(ev);
                    }
                    return true;
                }
                break;
        }
        lastY = ev.getY();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                float dy = lastY - y;
                dy = dy * DAMPINF_COEFFICIENT;
                lastY = y;
                boolean canDown = ViewCompat.canScrollVertically(mTargetView , -1);
                boolean canUp = ViewCompat.canScrollVertically(mTargetView , 1);

                float refreshView_translationY = ViewCompat.getTranslationY(mRefreshView);
                float loadMoreView_translationY = ViewCompat.getTranslationY(mLoadMoreView);

                Log.d(TAG , "currentState:" + state + " canDown:" + canDown + " canUp:" + canUp + " refreshView_translationY:" + refreshView_translationY + " loadMoreView_translationY:" + loadMoreView_translationY + " dy:" + dy);
                if ( (refreshView_translationY > 0 || !canDown && dy < 0) && loadMoreView_translationY >= 0 && this.state != State.LOADING_MORE) {
                    refreshView_translationY -= dy;
                    int maxTop = (int) (DRAG_RATE * getHeight());
                    if (mMode == Mode.FOLLOW && (this.state == State.REFRESHING || this.state == State.LOADING_MORE)) {
                        maxTop = mRefreshView.getMeasuredHeight();
                    }
                    if (refreshView_translationY > maxTop * LOAD_THRESHOLD) {
                        setStateWhenMoving(State.BECOMING_TO_REFRESH);
                    } else {
                        setStateWhenMoving(State.BEFORE_REFRESH);
                    }
                    if (refreshView_translationY > maxTop) {
                        refreshView_translationY = maxTop;
                    }
                    changeTranslateY(mRefreshView , refreshView_translationY);
                    Log.d(TAG , "onTouchEvent refresh " + refreshView_translationY);
                    return true;
                }

                if (  canLoadMore() && (loadMoreView_translationY < 0 || !canUp && dy > 0) && refreshView_translationY <= 0 && this.state != State.REFRESHING) {
                    loadMoreView_translationY -= dy;
                    int maxTop = (int) (DRAG_RATE * getHeight());
                    if (mMode == Mode.FOLLOW && (this.state == State.REFRESHING || this.state == State.LOADING_MORE)) {
                        maxTop = mLoadMoreView.getMeasuredHeight();
                    }
                    if (loadMoreView_translationY < -maxTop * LOAD_THRESHOLD) {
                        setStateWhenMoving(State.BECOMING_TO_LOADING_MORE);
                    } else {
                        setStateWhenMoving(State.BEFORE_LOAD_MORE);
                    }
                    if (loadMoreView_translationY < -maxTop) {
                        loadMoreView_translationY = -maxTop;
                    }
                    changeTranslateY(mLoadMoreView , loadMoreView_translationY);
                    Log.d(TAG , "onTouchEvent loadMore " + loadMoreView_translationY);
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * set state when moving
     * @param state
     */
    private void setStateWhenMoving(State state) {
        if (mMode == Mode.FOLLOW && (this.state == State.REFRESHING || this.state == State.LOADING_MORE)) {
            return;
        }
        setState(state);
    }

    public boolean isRefreshable() {
        return isRefreshable;
    }

    public void setRefreshable(boolean refreshable) {
        isRefreshable = refreshable;
    }

    public boolean isLoadmoreable() {
        return isLoadmoreable;
    }

    public void setLoadmoreable(boolean loadmoreable) {
        isLoadmoreable = loadmoreable;
    }

    public boolean isAutoLoadMore() {
        return isAutoLoadMore;
    }

    public void setAutoLoadMore(boolean autoLoadMore) {
        isAutoLoadMore = autoLoadMore;
    }

    public void setOnRefreshListener(OnRefreshListener mOnRefreshListener) {
        this.mOnRefreshListener = mOnRefreshListener;
    }

    public Mode getMode() {
        return mMode;
    }

    /**
     * set this mode of pull and refesh
     * @param mMode
     */
    public void setMode(Mode mMode) {
        this.mMode = mMode;
    }

    public void setComplete() {
        setRefreshing(false);
        setLoadingMore(false);
    }

    public void setRefreshing(final boolean isRefreshing) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRefreshing) {
                    if (state != State.REFRESHING) {
                        startTranslateAnim(mRefreshView , mRefreshView.getMeasuredHeight());
                        setState(State.REFRESHING);
                    }
                } else {
                    startTranslateAnim(mRefreshView , 0);
                    setState(State.IDEL);
                }
            }
        } , 100);
    }

    public void setLoadingMore(boolean isLoadingMore) {
        if (isLoadingMore) {
            if (!isNoMoreData && state != State.LOADING_MORE && state != State.REFRESHING) {
                startTranslateAnim(mLoadMoreView , -mLoadMoreView.getMeasuredHeight());
                setState(State.LOADING_MORE);
            }
        } else {
            startTranslateAnim(mLoadMoreView , 0);
            setState(State.IDEL);
        }
    }

    private boolean canLoadMore() {
        boolean isFirstPageLoaded = false;
        if (mTargetView instanceof RecyclerView) {
            isFirstPageLoaded = ((RecyclerView) mTargetView).getChildCount() > 0;
        } else if (mTargetView instanceof AbsListView) {
            isFirstPageLoaded = ((AbsListView) mTargetView).getChildCount() > 0;
        }
        return isFirstPageLoaded && !isNoMoreData;
    }

    public void setNoMoreData(boolean isNoMoreData) {
        this.isNoMoreData = isNoMoreData;
    }

    private void startTranslateAnim(final View view , final float toY) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(view.getTranslationY() , toY);
        valueAnimator.setTarget(view);
        valueAnimator.setDuration(300).start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewCompat.setTranslationY(view , (Float) animation.getAnimatedValue());
            }
        });

        if (mMode == Mode.FOLLOW && (view == mRefreshView || view == mLoadMoreView) ) {
            startTranslateAnim(mTargetView , toY);
        }
    }

    private void changeTranslateY(View view , float toY) {
        switch (mMode) {
            case FOLLOW:
                ViewCompat.setTranslationY(view , toY);
                ViewCompat.setTranslationY(mTargetView, toY);
                break;
            case FOREGROUND:
                ViewCompat.setTranslationY(view , toY);
                break;
        }
    }

    public interface OnRefreshListener {
        void onRefresh();
        void onLoadMore();
    }
}
