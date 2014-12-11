package random.frank.segmenttab;

import random.frank.segmenttab.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 
 * @author frank.random
 *
 */
public class SegmentTab extends View {

	private Context mContext;
	
	private int mSegCount = 2;  
	private int mColor = Color.RED;
	private int mPressedColor = Color.RED;
	private int mTextSize = 30;
	
	private int mStrokeWidth = 2;
	private int mRoundArc = 8;
	
	private String mTabTexts[];
	
	private RectF mMainRect, mHeadRect, mTailRect;
	
	private RectF[] mRectArr;
	
	private Paint mLinePaint, mPressingPaint, mPressedPaint;
	private Paint mTextPaint, mTextPaintReverse;

	private int mSelected = 0;
	private int mLastSelected = 0;
	
	private boolean mIsPressing = false;
	
	private TabClickListener mListener;
	
	public SegmentTab(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initAttrs(attrs);
		initValues();
		initPaints();
		initText(attrs);
	}

	public SegmentTab(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		initAttrs(attrs);
		initValues();
		initPaints();
		initText(attrs);
	}
	
	public void setTabClickListener(TabClickListener listener) {
		mListener = listener;
	}
	
	public boolean setText(String text, int index) {
		if(index < 0) {
			return false;
		}
		if(index > mSegCount - 1) {
			return false;
		}
		mTabTexts[index] = text;
		return true;
	}
	
	public void select(int index) {
		if(mSelected==index) {
			return;
		}
		
		mLastSelected = mSelected;
		mSelected = index;
		
		invalidate();
		return;
	}
	
	private void initAttrs(AttributeSet attrs) {
		TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.seg);
		
		mSegCount = typedArray.getInt(R.styleable.seg_count, 2);
		mColor = typedArray.getColor(R.styleable.seg_color, Color.RED);
		mPressedColor = Color.argb(255, 
							 (int)(Color.red(mColor)*1.2) > 255 ? 255 : (int)(Color.red(mColor)*1.2), 
							 (int)(Color.green(mColor)*1.2) > 255 ? 255 : (int)(Color.green(mColor)*1.2), 
							 (int)(Color.blue(mColor)*1.2) > 255 ? 255 : (int)(Color.blue(mColor)*1.2));
		mTextSize = typedArray.getInt(R.styleable.seg_textSize, 30);
		typedArray.recycle();
	}

	private void initValues() {
		mMainRect = new RectF();
		mHeadRect = new RectF();
		mTailRect = new RectF();
		mRectArr = new RectF[mSegCount];
		
		for(int i=0; i<mSegCount; i++) {
			mRectArr[i] = new RectF();
		}
		
		mTabTexts = new String[mSegCount];
		
		mLinePaint = new Paint();  
		mPressingPaint = new Paint();  
		mPressedPaint = new Paint();  
		
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaintReverse = new Paint(Paint.ANTI_ALIAS_FLAG);
	}
	
	private void initPaints() {
		mLinePaint.setColor(mColor);   
		mLinePaint.setStrokeJoin(Paint.Join.ROUND);   
		mLinePaint.setStrokeCap(Paint.Cap.ROUND);   
		mLinePaint.setStrokeWidth(mStrokeWidth); 
		mLinePaint.setStyle(Style.STROKE);
		
		mPressingPaint.setColor(mPressedColor);   
		mPressingPaint.setStrokeJoin(Paint.Join.ROUND);   
		mPressingPaint.setStrokeCap(Paint.Cap.ROUND);   
		mPressingPaint.setStrokeWidth(mStrokeWidth); 
		mPressingPaint.setStyle(Style.FILL);
		
		mPressedPaint.setColor(mColor);
		mPressedPaint.setStrokeJoin(Paint.Join.ROUND);   
		mPressedPaint.setStrokeCap(Paint.Cap.ROUND);   
		mPressedPaint.setStrokeWidth(mStrokeWidth); 
		mPressedPaint.setStyle(Style.FILL);
		
		mTextPaint.setTextSize(mTextSize);
		mTextPaint.setColor(mColor);
		mTextPaint.setTextAlign(Paint.Align.CENTER);  
		
		mTextPaintReverse.setTextSize(mTextSize);
		mTextPaintReverse.setColor(Color.WHITE);
		mTextPaintReverse.setTextAlign(Paint.Align.CENTER); 
	}
	
	private void initRects() {
		mMainRect.left = mStrokeWidth/2;
		mMainRect.right = getMeasuredWidth() - mStrokeWidth/2;
		mMainRect.top = mStrokeWidth/2;
		mMainRect.bottom = getMeasuredHeight() - mStrokeWidth/2;
		
		for(int i=0; i<mRectArr.length; i++) {
			mRectArr[i].top = mStrokeWidth/2;
			mRectArr[i].bottom = getMeasuredHeight() - mStrokeWidth/2;
			mRectArr[i].left = mStrokeWidth/2 + i * getMeasuredWidth()/mSegCount;
			mRectArr[i].right = (i+1) * getMeasuredWidth()/mSegCount - mStrokeWidth/2;
		}
		
		mHeadRect.top = mStrokeWidth/2;
		mHeadRect.bottom = getMeasuredHeight() - mStrokeWidth/2;
		mHeadRect.left = getMeasuredWidth()/mSegCount/2;
		mHeadRect.right = getMeasuredWidth()/mSegCount - mStrokeWidth/2;
		
		mTailRect.top = mStrokeWidth;
		mTailRect.bottom = getMeasuredHeight() - mStrokeWidth;
		mTailRect.left = getMeasuredWidth()/mSegCount*(mSegCount-1);
		mTailRect.right = getMeasuredWidth() - mStrokeWidth - getMeasuredWidth()/mSegCount/2;
	}
	
	private void initText(AttributeSet attrs) {
		TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.seg);
		
		for(int i=0; i<mSegCount; i++) {
			switch(i) {
			case 0: 
				mTabTexts[0] = typedArray.getString(R.styleable.seg_tab1);
				break;
			case 1: 
				mTabTexts[1] = typedArray.getString(R.styleable.seg_tab2);
				break;
			case 2: 
				mTabTexts[2] = typedArray.getString(R.styleable.seg_tab3);
				break;
			case 3: 
				mTabTexts[3] = typedArray.getString(R.styleable.seg_tab4);
				break;
			case 4: 
				mTabTexts[4] = typedArray.getString(R.styleable.seg_tab5);
				break;
			case 5: 
				mTabTexts[5] = typedArray.getString(R.styleable.seg_tab6);
				break;
			case 6: 
				mTabTexts[6] = typedArray.getString(R.styleable.seg_tab7);
				break;
			case 7: 
				mTabTexts[7] = typedArray.getString(R.styleable.seg_tab8);
				break;
			case 8: 
				mTabTexts[8] = typedArray.getString(R.styleable.seg_tab9);
				break;
			}
		}
		
		typedArray.recycle();
	}
	
	private void drawText(Canvas canvas, Paint paint, RectF rect, String text){

		FontMetricsInt fontMetrics = paint.getFontMetricsInt();  
		int baseline = (int) (rect.top + (rect.bottom - rect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top);  	
	    canvas.drawText(text, rect.centerX(), baseline, paint);  
	}
	
	private int calEventPosition(MotionEvent event) {
		for(int i=0; i<mSegCount; i++) {
			if(mRectArr[i].contains(event.getX(), event.getY())) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		int eventAction = event.getAction();

		
		switch (eventAction) {

		case MotionEvent.ACTION_DOWN:
			mIsPressing = true;
			int index = calEventPosition(event);
			if(index==mSelected) {
				return false;
			}
			mSelected = index==-1 ? mSelected : index;
			invalidate();
			return true;
		case MotionEvent.ACTION_UP:
			this.performClick();
			mIsPressing = false;
			
			int upIndex = calEventPosition(event);
			if(upIndex==mSelected) {
				if(mListener!=null) {
					mListener.onTabClick(mSelected);
				}
			} else {
				mSelected = mLastSelected;
			}
			mLastSelected = mSelected;
			invalidate();
			return true;
		default:
			return true;
		}
	}

	@Override
	public boolean performClick() {
		return super.performClick();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		initRects();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawRoundRect(mMainRect, mRoundArc, mRoundArc, mLinePaint);
		
		Paint currentPaint = mIsPressing ? mPressingPaint : mPressedPaint;
		
		if(mSelected==0) {
			canvas.drawRect(mHeadRect, currentPaint);
			canvas.drawRoundRect(mRectArr[mSelected], mRoundArc, mRoundArc, currentPaint);
		} else if(mSelected==mSegCount-1) {
			canvas.drawRect(mTailRect, currentPaint);
			canvas.drawRoundRect(mRectArr[mSelected], mRoundArc, mRoundArc, currentPaint);
		} else {
			canvas.drawRect(mRectArr[mSelected], currentPaint);
		}
		
		for(int i=0; i<mSegCount-1; i++) {
			canvas.drawLine(getMeasuredWidth()/mSegCount*(i+1), 0, getMeasuredWidth()/mSegCount*(i+1), getMeasuredHeight(), mPressedPaint);
		}
		
		for(int i=0; i<mSegCount; i++) {
			
			String text = (mTabTexts[i]==null||mTabTexts[i].equals("")) ? "" : mTabTexts[i];
			
			if(i==mSelected) {
				drawText(canvas, mTextPaintReverse, mRectArr[i], text);
			}
			else {
				drawText(canvas, mTextPaint, mRectArr[i], text);
			}
			
		}
	}
	
	public interface TabClickListener {
		abstract void onTabClick(int index);
	}
}
