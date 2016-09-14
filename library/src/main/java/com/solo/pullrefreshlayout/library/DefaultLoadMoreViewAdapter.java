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
public class DefaultLoadMoreViewAdapter extends RefreshViewAdapter {
    Context mContext;
    TextView mTextView;
    ProgressBar mProgressBar;
    ImageView mArrow;

    public DefaultLoadMoreViewAdapter(Context context) {
        mContext = context;
    }
    @Override
    public View getView() {
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        mProgressBar = new ProgressBar(mContext , null ,android.R.attr.progressBarStyleLarge);
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
            case BEFORE_LOAD_MORE:
                mTextView.setText("上拉加载");
                mArrow.setRotation(180);
                break;
            case BECOMING_TO_LOADING_MORE:
                mTextView.setText("松开加载");
                mArrow.setRotation(0);
                break;
            case LOADING_MORE:
                mTextView.setText("正在加载");
                break;
        }

        mProgressBar.setVisibility(state == State.LOADING_MORE ? View.VISIBLE : View.GONE);
        mArrow.setVisibility(state == State.LOADING_MORE ? View.GONE : View.VISIBLE);
    }
}
