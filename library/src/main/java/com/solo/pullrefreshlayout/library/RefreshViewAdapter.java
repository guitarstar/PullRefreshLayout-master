package com.solo.pullrefreshlayout.library;

import android.view.View;

/**
 * Created by lingyiyong on 2016/9/14.
 */
public abstract class RefreshViewAdapter {
    public abstract View getView();
    public abstract void stateChange(State state);
}
