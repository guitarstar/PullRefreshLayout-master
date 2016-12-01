package com.solo.pullrefreshlayout.library;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_default_refresh_layout, null);
        mTextView = (TextView) view.findViewById(R.id.tv);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        mArrow = (ImageView) view.findViewById(R.id.ivArrow);
        return view;
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
