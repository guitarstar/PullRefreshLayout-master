## Running Affect
![image](https://github.com/guitarstar/PullRefreshLayout-master/blob/master/screenshot/GIF.gif?raw=true)

## how to use in Gradle

### Step 1. Add the JitPack repository to your build file
```groovy
	allprojects {
		repositories {
			maven { url "https://jitpack.io" }
		}
	}
```
### Step 2. Add the dependency

```groovy
	dependencies {
	        compile 'com.github.guitarstar:PullRefreshLayout-master:v1.0'
	}
```
###Step 3. use in xml
```xml
    <com.solo.pullrefreshlayout.library.PullRefreshLayout
        android:id="@+id/mRefreshLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <android.support.v7.widget.RecyclerView
            android:id="@+id/mRecyclerView"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="@android:color/white"/>
    </com.solo.pullrefreshlayout.library.PullRefreshLayout>
```

### Step 4. and then
```java
            mPullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            //mPullRefreshLayout.setComplete();
            }
            @Override
            public void onLoadMore() {
            //mPullRefreshLayout.setComplete();
            //mPullRefreshLayout.setNoMoreData(true);
        });
        mPullRefreshLayout.setRefreshing(true);//set refreshing by manual
        mPullRefreshLayout.setAutoLoadMore(false); //set loading more auto
```
### you also can use custom refresh view or loading more view by this way:
```java
 mPullRefreshLayout.setRefreshViewAdapter(createRefreshViewAdapter());
 mPullRefreshLayout.setLoadMoreViewAdapter(createRefreshViewAdapter());
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
```
## Notice
 RefreshViewAdapter and LoadMoreViewAdapter can not use the same instance.
# That is all!
contact qq:1270085013