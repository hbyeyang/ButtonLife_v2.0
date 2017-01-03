package com.anniu.shandiandaojia.adapter;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.anniu.shandiandaojia.activity.GoodsCategoryActivity;
import com.anniu.shandiandaojia.db.jsondb.Category;
import com.anniu.shandiandaojia.db.jsondb.Goods;
import com.anniu.shandiandaojia.db.jsondb.GoodsListViewModel;
import com.anniu.shandiandaojia.db.jsondb.Paging;
import com.anniu.shandiandaojia.fragment.MyFragment;
import com.anniu.shandiandaojia.logic.GoodsLogic;
import com.anniu.shandiandaojia.utils.MyLog;
import com.anniu.shandiandaojia.view.pulltorefreshlayout.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabAdapter extends PagerAdapter {

    private static final String TAG = TabAdapter.class.getSimpleName();
    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;

    private ArrayList<Fragment.SavedState> mSavedState = new ArrayList<Fragment.SavedState>();
    private ArrayList<MyFragment> mFragments = new ArrayList<MyFragment>();
    private MyFragment mCurFragment = null;

    private List<Category> categoryList;
    private List<Goods> mAllDatas;

    /**
     * goodstypecode对应的商品集合
     */
    private Map<Integer, List<Goods>> map = new HashMap<Integer, List<Goods>>();

    private GoodsCategoryActivity mActivity;

    public TabAdapter(FragmentManager fm, GoodsCategoryActivity activity) {
        mFragmentManager = fm;
        this.mActivity = activity;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (categoryList != null && categoryList.size() > position) {
            return categoryList.get(position).getName();
        }
        return "";
    }

    public void setPageContents(List<Goods> dataList) {
        mAllDatas = dataList;
        if (mCurFragment != null) {
            mCurFragment.setDatas(dataList, mCurFragment.tempLoadType);
        }
        if (mFragments != null && mFragments.size() > 0) {
            for (Fragment f : mFragments) {
                if (f != null && f != mCurFragment) {
                    ((MyFragment) f).setDatas(dataList, ((MyFragment) f).tempLoadType);
                }
            }
        }
    }

    public void onRefreshComplete(Bundle bundle) {
        int loadtype = bundle.getInt(GoodsLogic.EXTRA_LOADTYPE);
        int typeCode = bundle.getInt(GoodsLogic.EXTRA_TYPECODE);
        GoodsListViewModel goodsModel = (GoodsListViewModel) bundle.getSerializable(GoodsLogic.EXTRA_GOODSTYPELIST);
        ArrayList<Goods> goodsList = (ArrayList<Goods>) goodsModel.getGoodsList();
        putallData(loadtype, typeCode, goodsList);

        MyFragment fragment = findFragment(typeCode);
        if (fragment == null) {
            return;
        }
        if (loadtype == GoodsCategoryActivity.LOAD_TYPE_REFRESH) {
            fragment.stopRefresh();
        } else {
            Paging paging = goodsModel.getPaging();
            int page = paging.getPage();
            int pageCount = paging.getPageCount();

            if (page >= pageCount) {//没有更多数据了
                fragment.stopLoadMore(PullToRefreshLayout.PULL_FINISH_NOMORE);
            } else {//加载成功
                fragment.stopLoadMore(PullToRefreshLayout.SUCCEED);
            }
        }

        List<Goods> goods = map.get(typeCode);
        setFragmentData(fragment, goods, loadtype);
    }

    private void putallData(int loadtype, int typeCode, List goodsList) {
        List<Goods> list = map.get(typeCode);
        if (loadtype == GoodsCategoryActivity.LOAD_TYPE_REFRESH) {
            list = goodsList;
            map.put(typeCode, list);
        } else {
            if (goodsList != null) {
                list.addAll(goodsList);
                map.put(typeCode, list);
            }
        }
    }

    /**
     * 给fragment设置数据
     */
    private void setFragmentData(MyFragment fragment, List goodsList, int loadtype) {
        fragment.setDatas(goodsList, loadtype);
    }

    private MyFragment findFragment(int typeCode) {
        for (int i = 0; i < categoryList.size(); i++) {
            int code = categoryList.get(i).getId();
            if (code == typeCode) {
                return (MyFragment) mFragments.get(i);
            }
        }
        return null;
    }

    @Override
    public Object instantiateItem(View container, int position) {
        // If we already have this item instantiated, there is nothing
        // to do. This can happen when we are restoring the entire pager
        // from its saved state, where the fragment manager has already
        // taken care of restoring the fragments we previously had instantiated.
        if (mFragments.size() > position) {
            Fragment f = mFragments.get(position);
            if (f != null) {
                return f;
            }
        }

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        int goodstype_code = categoryList.get(position).getId();
        MyFragment fragment = MyFragment.newInstance(position, goodstype_code);

        fragment.setListener(mActivity, goodstype_code);
        fragment.setDatas(mAllDatas, fragment.tempLoadType);

        if (mSavedState.size() > position) {
            Fragment.SavedState fss = mSavedState.get(position);
            if (fss != null) {
                fragment.setInitialSavedState(fss);
            }
        }
        while (mFragments.size() <= position) {
            mFragments.add(null);
        }
        fragment.setMenuVisibility(false);
        fragment.setUserVisibleHint(false);
        mFragments.set(position, fragment);
        mCurTransaction.add(container.getId(), fragment);

        return fragment;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        Fragment fragment = (Fragment) object;

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        while (mSavedState.size() <= position) {
            mSavedState.add(null);
        }
        mSavedState.set(position, mFragmentManager.saveFragmentInstanceState(fragment));
        mFragments.set(position, null);

        mCurTransaction.remove(fragment);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        MyFragment fragment = (MyFragment) object;
        if (fragment != mCurFragment) {
            if (mCurFragment != null) {
                mCurFragment.setMenuVisibility(false);
                mCurFragment.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            mCurFragment = fragment;
        }
    }

    public void setCategories(List<Category> categories) {
        this.categoryList = categories;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (categoryList != null) {
            return categoryList.size();
        }
        return 0;
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }

    @Override
    public Parcelable saveState() {
        Bundle state = null;
        if (mSavedState.size() > 0) {
            state = new Bundle();
            Fragment.SavedState[] fss = new Fragment.SavedState[mSavedState.size()];
            mSavedState.toArray(fss);
            state.putParcelableArray("states", fss);
        }
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment f = mFragments.get(i);
            if (f != null) {
                if (state == null) {
                    state = new Bundle();
                }
                String key = "f" + i;
                mFragmentManager.putFragment(state, key, f);
            }
        }
        return state;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if (state != null) {
            Bundle bundle = (Bundle) state;
            bundle.setClassLoader(loader);
            Parcelable[] fss = bundle.getParcelableArray("states");
            mSavedState.clear();
            mFragments.clear();
            if (fss != null) {
                for (int i = 0; i < fss.length; i++) {
                    mSavedState.add((Fragment.SavedState) fss[i]);
                }
            }
            Iterable<String> keys = bundle.keySet();
            for (String key : keys) {
                if (key.startsWith("f")) {
                    int index = Integer.parseInt(key.substring(1));
                    MyFragment f = (MyFragment) mFragmentManager.getFragment(bundle, key);
                    if (f != null) {
                        while (mFragments.size() <= index) {
                            mFragments.add(null);
                        }
                        f.setMenuVisibility(false);
                        mFragments.set(index, f);
                    } else {
                        MyLog.w(TAG, "Bad fragment at key " + key);
                    }
                }
            }
        }
    }
}
