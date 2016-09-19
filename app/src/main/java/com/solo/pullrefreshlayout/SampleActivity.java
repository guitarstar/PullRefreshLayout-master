package com.solo.pullrefreshlayout;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.solo.pullrefreshlayout.library.DefaultLoadMoreViewAdapter;
import com.solo.pullrefreshlayout.library.DefaultRefreshViewAdapter;
import com.solo.pullrefreshlayout.library.PullRefreshLayout;
import com.solo.pullrefreshlayout.library.RefreshViewAdapter;
import com.solo.pullrefreshlayout.library.State;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingyiyong on 2016/8/11.
 */
public class SampleActivity extends Activity {
    PullRefreshLayout mPullRefreshLayout;
    RecyclerView mRecyclerView;
    private List<String> list = new ArrayList<>();
    MyAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        mPullRefreshLayout = (PullRefreshLayout) findViewById(R.id.mRefreshLayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(myAdapter = new MyAdapter());
        RadioGroup mRadioGroup = (RadioGroup) findViewById(R.id.mRadioGroup);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbFollow:
                        mPullRefreshLayout.setMode(PullRefreshLayout.Mode.FOLLOW);
                        break;
                    case R.id.rbForeground:
                        mPullRefreshLayout.setMode(PullRefreshLayout.Mode.FOREGROUND);
                        break;
                }
            }
        });

        mPullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        list.clear();
                        for (int i = 0 ; i < 30 ; i++) {
                            list.add(String.valueOf(i));
                        }
                        myAdapter.notifyDataSetChanged();
                        mPullRefreshLayout.setComplete();
                    }
                } , 3000);

            }

            @Override
            public void onLoadMore() {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int count = list.size() + 30;
                        for (int i = list.size() ; i < count ; i++) {
                            list.add(String.valueOf(i));
                        }
                        myAdapter.notifyDataSetChanged();
                        mPullRefreshLayout.setComplete();
                    }
                } , 3000);
            }
        });
        mPullRefreshLayout.setRefreshing(true);
//        mPullRefreshLayout.setAutoLoadMore(false);


        //set custom refrsh and load more views
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mPullRefreshLayout.setRefreshViewAdapter(createRefreshViewAdapter());
                    mPullRefreshLayout.setLoadMoreViewAdapter(createRefreshViewAdapter());
                } else {
                    mPullRefreshLayout.setRefreshViewAdapter(new DefaultRefreshViewAdapter(SampleActivity.this));
                    mPullRefreshLayout.setLoadMoreViewAdapter(new DefaultLoadMoreViewAdapter(SampleActivity.this));
                }
            }
        });
    }

    private RefreshViewAdapter createRefreshViewAdapter() {
        return new RefreshViewAdapter() {
            ImageView ivProgress;
            RotateAnimation animation;
            @Override
            public View getView() {
                View view = LayoutInflater.from(SampleActivity.this).inflate(R.layout.view_refresh , null);
                ivProgress = (ImageView) view.findViewById(R.id.ivProgress);
                return view;
            }

            @Override
            public void stateChange(State state) {
                switch (state) {
                    case IDEL:
                    case BEFORE_LOAD_MORE:
                    case BEFORE_REFRESH:
                    case BECOMING_TO_LOADING_MORE:
                    case BECOMING_TO_REFRESH:
                        if (animation != null) {
                            animation.cancel();
                        }
                        break;
                    case LOADING_MORE:
                    case REFRESHING:
                        if (animation != null) {
                            animation.cancel();
                        }
                        animation = new RotateAnimation(0f,360f,Animation.RELATIVE_TO_SELF,
                                0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                        animation.setRepeatCount(Animation.INFINITE);
                        animation.setDuration(1000);
                        ivProgress.startAnimation(animation);
                        break;
                }
            }
        };
    }



    class MyAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(SampleActivity.this);
            textView.setPadding(30 , 10 , 30 , 10);
            return new MyViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((MyViewHolder)holder).setData(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv;
            public MyViewHolder(View itemView) {
                super(itemView);
                tv = (TextView) itemView;
            }

            private void setData(final String s) {
                tv.setMinHeight(50);
                tv.setText(s);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(SampleActivity.this , s , Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
