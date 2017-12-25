package com.lws.allenglish.base;

public abstract class LazyLoadFragment extends BaseFragment {
    /**
     * Fragment当前状态是否可见
     */
    protected boolean mIsVisible;
    /**
     * 标志位，标志已经初始化完成
     */
    protected boolean mIsPrepared = false;
    /**
     * 是否已被加载过一次，第二次就不再去请求数据了
     */
    protected boolean mHasLoadedOnce = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            mIsVisible = true;
            onVisible();
        } else {
            mIsVisible = false;
            onInvisible();
        }
    }

    /**
     * 可见
     */
    protected void onVisible() {
        lazyLoad();
    }

    /**
     * 不可见
     */
    protected void onInvisible() {
    }

    /**
     * 延迟加载
     * 子类必须重写此方法
     */
    protected abstract void lazyLoad();
}
