package com.anniu.shandiandaojia.view.listasgrid;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

/**
 * Created by shalafi on 6/6/13.
 */
public abstract class ListAsGridBaseAdapter extends BaseAdapter {

	private class ListItemClickListener implements OnClickListener {

		private int mPosition;

		public ListItemClickListener(int currentPos) {
			mPosition = currentPos;
		}

		@Override
		public void onClick(View v) {
			onGridItemClicked(v, mPosition);
		}
	}

	private int mNumColumns;
	private int mVideoGroupPadding;
	private Context mContext;
	private GridItemClickListener mGridItemClickListener;
	private int mBackgroundResource = -1;

	public ListAsGridBaseAdapter(Context context) {
		mContext = context;
		mNumColumns = 1;
		mVideoGroupPadding = 0;
	}

	public void setBackgroundResource(int drawableResId) {
		mBackgroundResource = drawableResId;
	}

	public final void setOnGridClickListener(GridItemClickListener listener) {
		mGridItemClickListener = listener;
	}

	private final void onGridItemClicked(View v, int position) {
		if (mGridItemClickListener != null) {
			mGridItemClickListener.onGridItemClicked(v, position,
					getItemId(position));
		}
	}

	public final int getNumColumns() {
		return mNumColumns;
	}

	public final void setNumColumns(int numColumns) {
		mNumColumns = numColumns;
		notifyDataSetChanged();
	}
	
	public final int getVideoGroupPadding() {
		return mVideoGroupPadding;
	}

	public final void setVideoGroupPadding(int mVideoGroupPadding) {
		this.mVideoGroupPadding = mVideoGroupPadding;
		notifyDataSetChanged();
	}

	@Override
	public final int getCount() {
		return (int) Math.ceil(getItemCount() * 1f / getNumColumns());
	}

	public abstract int getItemCount();

	protected abstract View getItemView(int position, View view,
			ViewGroup parent);

	@Override
	public final View getView(int position, View view, ViewGroup viewGroup) {
		LinearLayout layout;
		int columnWidth = 0;
		if (viewGroup != null) {
			columnWidth = (viewGroup.getWidth()-mVideoGroupPadding) / mNumColumns;
		} else if (view != null) {
			columnWidth = (viewGroup.getWidth()-mVideoGroupPadding) / mNumColumns;
		}
		// Make it be rows of the number of columns
		if (view == null) {
			// This is items view
			layout = createItemRow(position, viewGroup, columnWidth);
		} else {
			layout = (LinearLayout) view;
			updateItemRow(position, viewGroup, layout, columnWidth);
		}
		return layout;
	}

	@Override
	public int getViewTypeCount() {
		// Not necessary for only 1 column
		if (getNumColumns() == 1)
			return 1;

		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		// Not necessary for only 1 column
		if (getNumColumns() == 1)
			return 0;

		// If we have an odd item count and this is the last row, use the view
		// type with filler views
		if (getItemCount() % 2 != 0 && position == getCount() - 1) {
			return 1;
		}

		return 0;
	}

	private LinearLayout createItemRow(int position, ViewGroup viewGroup,
			int columnWidth) {
		LinearLayout layout;
		layout = new LinearLayout(mContext);
		if (mBackgroundResource > 0) {
			layout.setBackgroundResource(mBackgroundResource);
		}
		int adjustedColumnWidth = columnWidth
				- (layout.getPaddingLeft() + layout.getPaddingRight())
				/ mNumColumns;
		layout.setOrientation(LinearLayout.HORIZONTAL);
		// Now add the sub views to it
		for (int i = 0; i < mNumColumns; i++) {
			int currentPos = position * mNumColumns + i;
			// Get the new View
			View insideView;
			if (currentPos < getItemCount()) {
				insideView = getItemView(currentPos, null, viewGroup);
				insideView.setVisibility(View.VISIBLE);
				View theView = getItemView(currentPos, insideView, viewGroup);
				theView.setOnClickListener(new ListItemClickListener(currentPos));
			} else {
				insideView = new View(mContext);
				insideView.setVisibility(View.INVISIBLE);
			}
			layout.addView(insideView);
			// Set the width of this column
			LayoutParams params = insideView.getLayoutParams();
			params.width = adjustedColumnWidth;
			insideView.setLayoutParams(params);
		}
		return layout;
	}

	private void updateItemRow(int position, ViewGroup viewGroup,
			LinearLayout layout, int columnWidth) {
		int realColumnWidth = columnWidth
				- (layout.getPaddingLeft() + layout.getPaddingRight())
				/ mNumColumns;
		for (int i = 0; i < mNumColumns; i++) {
			int currentPos = position * mNumColumns + i;
			View insideView = layout.getChildAt(i);
			// If there are less views than objects. add a view here
			if (insideView == null) {
				insideView = new View(mContext);
				layout.addView(insideView);
			}
			// Set the width of this column
			LayoutParams params = insideView.getLayoutParams();
			params.width = realColumnWidth;
			insideView.setLayoutParams(params);

			if (currentPos < getItemCount()) {
				insideView.setVisibility(View.VISIBLE);
				// Populate the view
				View theView = getItemView(currentPos, insideView, viewGroup);
				theView.setOnClickListener(new ListItemClickListener(currentPos));
				if (!theView.equals(insideView)) {
					// DO NOT CHANGE THE VIEWS
				}
			} else {
				insideView.setVisibility(View.INVISIBLE);
			}
		}
	}
}