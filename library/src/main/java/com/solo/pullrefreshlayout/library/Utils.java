package com.solo.pullrefreshlayout.library;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.AbsListView;

/**
 * Created by lingyiyong on 2016/9/14.
 */
public class Utils {
    public static boolean isScrollToBottom(PullRefreshLayout pullRefreshLayout, View taget) {
        if (taget instanceof RecyclerView) {
            return isRecyclerViewToBottom(pullRefreshLayout, (RecyclerView) taget);
        } else if (taget instanceof AbsListView) {
            return isAbsListViewToBottom(pullRefreshLayout, (AbsListView) taget);
        }
        return false;
    }

    public static boolean isRecyclerViewToBottom(PullRefreshLayout pullRefreshLayout, RecyclerView recyclerView) {
        if (recyclerView != null) {
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager == null || manager.getItemCount() == 0) {
                return false;
            }

            if (manager instanceof LinearLayoutManager) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) manager;

                if (layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1) {

                    if (pullRefreshLayout != null) {
                        View lastChild = layoutManager.getChildAt(layoutManager.findLastCompletelyVisibleItemPosition());
                        if (lastChild == null) {
                            return true;
                        } else {
                            int[] location = new int[2];
                            lastChild.getLocationOnScreen(location);
                            int lastChildBottomOnScreen = location[1] + lastChild.getMeasuredHeight();
                            pullRefreshLayout.getLocationOnScreen(location);
                            int stickyNavLayoutBottomOnScreen = location[1] + pullRefreshLayout.getMeasuredHeight();
                            return lastChildBottomOnScreen <= stickyNavLayoutBottomOnScreen;
                        }
                    } else {
                        return true;
                    }
                }
            } else if (manager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) manager;

                int[] out = layoutManager.findLastCompletelyVisibleItemPositions(null);
                int lastPosition = layoutManager.getItemCount() - 1;
                for (int position : out) {
                    if (position == lastPosition) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isAbsListViewToBottom(PullRefreshLayout pullRefreshLayout, AbsListView absListView) {
        if (absListView != null && absListView.getAdapter() != null && absListView.getChildCount() > 0 && absListView.getLastVisiblePosition() == absListView.getAdapter().getCount() - 1) {
            View lastChild = absListView.getChildAt(absListView.getChildCount() - 1);

            if (pullRefreshLayout != null) {
                int[] location = new int[2];
                lastChild.getLocationOnScreen(location);
                int lastChildBottomOnScreen = location[1] + lastChild.getMeasuredHeight();
                pullRefreshLayout.getLocationOnScreen(location);
                int stickyNavLayoutBottomOnScreen = location[1] + pullRefreshLayout.getMeasuredHeight();
                return lastChildBottomOnScreen + absListView.getPaddingBottom() <= stickyNavLayoutBottomOnScreen;
            } else {
                return lastChild.getBottom() <= absListView.getMeasuredHeight();
            }
        }
        return false;
    }
}
