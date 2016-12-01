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
