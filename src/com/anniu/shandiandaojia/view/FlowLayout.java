package com.anniu.shandiandaojia.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {
	private static final int HORIZONTAL_SPACING = 20;
	private static final int VERTICAL_SPACING = 20;

	// 测量方法 有可能调用多次
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		lines.clear();
		line = null;
		userWidth = 0; // 把之前缓存数据全部清空
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);// 获取到当前FlowLayout的宽的模式
		int widthSize = MeasureSpec.getSize(widthMeasureSpec)-getPaddingLeft()-getPaddingRight();// //  包含padding的 把padding减去
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);// 获取到当前FlowLayout的高的模式
		int heightSize = MeasureSpec.getSize(heightMeasureSpec)-getPaddingTop()-getPaddingBottom();// // 
		// 测量每个孩子
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {

			View child = getChildAt(i);
			// 如果父容器是精确的值 子View就是AT_Most 其余情况和父容器一样
			int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize,
					widthMode == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : widthMode);
			int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
					heightSize,
					heightMode == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : heightMode);
			// 要计算子view规则
			child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
			if(child.getVisibility()==View.GONE){
				continue;
			}
			if (line == null) {
				line = new Line();
			}
			int childWidth = child.getMeasuredWidth();// 获取孩子的宽
			userWidth += childWidth;
			if (userWidth <= widthSize) {
				line.addChild(child);
				userWidth += HORIZONTAL_SPACING;
				if (userWidth > widthSize) {
					// 换行
					newLine();
				}
			} else {
				// 换行
				newLine();
				userWidth += childWidth;
				line.addChild(child);// 把孩子添加到新的一行上
			}
		}
		if (!lines.contains(line)) {
			lines.add(line);// 把最后一行添加到集合中
		}

		int heightTotal = 0;
		for (int i = 0; i < lines.size(); i++) {
			heightTotal += lines.get(i).getHeight();
		}
		heightTotal += (lines.size() - 1) * VERTICAL_SPACING;
		heightTotal+=getPaddingTop();
		heightTotal+=getPaddingBottom();
		setMeasuredDimension(widthSize+getPaddingLeft()+getPaddingRight(),
				resolveSize(heightTotal, heightMeasureSpec));
	}

	// 管理当前控件所有行的集合
	private List<Line> lines = new ArrayList<FlowLayout.Line>();

	private void newLine() {
		lines.add(line);// 把之前的行添加到集合中
		line = new Line();
		userWidth = 0;
	}

	private int userWidth;// 当前行使用的空间
	private Line line;

	class Line {
		private int width;// 行占用的宽度
		private int height;// 当前行的高度
		// 记录当前行管理的view对象
		private List<View> children = new ArrayList<View>();

		public void addChild(View child) {
			children.add(child);
			// 让当前行 等于最高的一个孩子的高度
			height = height < child.getMeasuredHeight() ? child
					.getMeasuredHeight() : height;
			width += child.getMeasuredWidth();
		}

		// 分配行里面每个TextView的位置
		public void layout(int l, int t) {
			width += (children.size() - 1) * HORIZONTAL_SPACING;
			int surplusWidth = getMeasuredWidth()-getPaddingLeft()-getPaddingRight() - width;
			int extraWidth = surplusWidth / children.size();
			for (int i = 0; i < children.size(); i++) {
				View child = children.get(i);
				// 重新测量孩子
				if (extraWidth > 0) {
					int widthMeasureSpec = MeasureSpec.makeMeasureSpec(
							child.getMeasuredWidth() + extraWidth,
							MeasureSpec.EXACTLY);
					int heightMeasureSpec = MeasureSpec.makeMeasureSpec(
							child.getMeasuredHeight(), MeasureSpec.EXACTLY);
					child.measure(widthMeasureSpec, heightMeasureSpec);
				}
				// 分配孩子的位置
				child.layout(l, t, l + child.getMeasuredWidth(),
						t + child.getMeasuredHeight());

				// 让l加上 上一个孩子的宽度
				l += child.getMeasuredWidth();

				l += HORIZONTAL_SPACING;
			}
		}

		public int getHeight() {
			return height;
		}

	}

	// 分配每个孩子的位置
	// l t 代表当前控件 FlowLayout的左上角
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		t+=getPaddingTop();
		for (int i = 0; i < lines.size(); i++) {
			Line line2 = lines.get(i);
			// 第一行左上角的位置 和整个FlowLayout一样的
			
			line2.layout(l+getPaddingLeft(), t); // 分配行的位置 然后再交给每个行去分配位置
			t += line2.getHeight();
			t += VERTICAL_SPACING;
		}
	}

	public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FlowLayout(Context context) {
		super(context);
	}

}
