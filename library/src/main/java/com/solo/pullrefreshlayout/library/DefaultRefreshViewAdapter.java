package com.solo.pullrefreshlayout.library;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by lingyiyong on 2016/9/14.
 */
public class DefaultRefreshViewAdapter extends RefreshViewAdapter  {
    Context mContext;
    TextView mTextView;
    ProgressBar mProgressBar;
    ImageView mArrow;

    public DefaultRefreshViewAdapter(Context context) {
        mContext = context;
    }
    @Override
    public View getView() {
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        mProgressBar = new ProgressBar(mContext);
        RelativeLayout.LayoutParams progressBarParams = new RelativeLayout.LayoutParams(40 , 40);
        mProgressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.refresh_loding));
        mProgressBar.setVisibility(View.GONE);

        mArrow = new ImageView(mContext);
        mArrow.setImageResource(R.mipmap.refresh_head_arrow);
        RelativeLayout.LayoutParams arrowParams = new RelativeLayout.LayoutParams(40 , 40);

        mTextView = new TextView(mContext);

        linearLayout.addView(mArrow , arrowParams);
        linearLayout.addView(mProgressBar , progressBarParams);
        linearLayout.addView(mTextView);
        linearLayout.setPadding(0 , 15 , 0 , 15);
        return linearLayout;
    }

    @Override
    public void stateChange(State state) {
        switch (state) {
            case IDEL:
            case BEFORE_REFRESH:
                mTextView.setText("下拉刷新");
                mArrow.setRotation(0);
                break;
            case BECOMING_TO_REFRESH:
                mTextView.setText("松开刷新");
                mArrow.setRotation(180);
                break;
            case REFRESHING:
                mTextView.setText("正在刷新");
                break;
        }

        mProgressBar.setVisibility(state == State.REFRESHING ? View.VISIBLE : View.GONE);
        mArrow.setVisibility(state == State.REFRESHING ? View.GONE : View.VISIBLE);
    }
}
