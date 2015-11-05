package com.huanggusheng.flemerun;

import com.huanggusheng.flemerun.R;
import android.R.attr;
import android.content.Context;
import android.content.res.TypedArray;
import android.hardware.display.DisplayManager;

import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class Slidingmenu extends HorizontalScrollView  {

	private LinearLayout mWapper;
	private ViewGroup mMenu;
	private ViewGroup mContent;
	private int mScreenWidth;
	
	private int mMenuRightPadding=150;
	private int mMenuWidth;
	private boolean once;
	private boolean isOpen;

//	private RelativeLayout r1,r2,r3,r4,r5;

	/**
	 * 
	 * 
	 * @param context
	 * @param attrs
	 */
	public Slidingmenu(Context context, AttributeSet attrs) {
		this(context,attrs,0);
	}
	
	public Slidingmenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		TypedArray a=context.getTheme().obtainStyledAttributes(attrs, 
				R.styleable.SlidingMenu, defStyle, 0);
		int n=a.getIndexCount();
		for(int i=0;i<n;i++)
		{
			int attr=a.getIndex(i);
			switch (attr) {
			case R.styleable.SlidingMenu_rightPadding:
				mMenuRightPadding=a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
						, 20,context.getResources().getDisplayMetrics()));
				break;
			}
		}
		
		a.recycle();
		WindowManager wm=(WindowManager) context.getSystemService(Context
				.WINDOW_SERVICE);
		DisplayMetrics outMetrics=new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWidth=outMetrics.widthPixels;

	}

	public Slidingmenu(Context context) {
		this(context,null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		if(!once){
			mWapper=(LinearLayout) getChildAt(0);
			mMenu=(ViewGroup) mWapper.getChildAt(0);
			mContent=(ViewGroup) mWapper.getChildAt(1);
			mMenuWidth=mMenu.getLayoutParams().width=mScreenWidth-mMenuRightPadding;
			mContent.getLayoutParams().width=mScreenWidth;
			once =true;
		}
		 	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * 
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if(changed)
		{
		this.scrollTo(mMenuWidth, 0);
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action=ev.getAction();
		switch (action) 
		{
		case MotionEvent.ACTION_UP:
			int scrollX=getScrollX();			
			if(scrollX>=mMenuWidth/5){
				this.smoothScrollTo(mMenuWidth, 0);
				isOpen=false;
			}else
			{
				this.smoothScrollTo(0, 0);
				isOpen=true;
			}
			return true;
			}
		return super.onTouchEvent(ev);
	}
	public void open()
	{
		if(isOpen) return ;
		this.smoothScrollTo(0, 0);
		isOpen=true;
	}
	public void close()
	{
		if(!isOpen) return;
		this.smoothScrollTo(mMenuWidth, 0);
		isOpen=false;
	}
	/**
	 * �л��˵�
	 */
	public void toggle()
	{
		if(isOpen) 
			{
			close();
			}
		else{
			open();
		}
			
	}
}
