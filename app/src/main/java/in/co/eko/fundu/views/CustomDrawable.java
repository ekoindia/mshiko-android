package in.co.eko.fundu.views;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import java.util.List;

/**
 * seekbar background with text on it.
 * 
 * @author sazonov-adm
 * 
 */
public class CustomDrawable extends Drawable {
	private final ComboSeekBar mySlider;
	private final Drawable myBase;
	private final Paint textUnselected;
	private float mThumbRadius;
	/**
	 * paints.
	 */
	private final Paint unselectLinePaint;
	private List<ComboSeekBar.Dot> mDots;
	private Paint selectLinePaint;
	private Paint circleLinePaint;
	private final Paint unSelectedCircleLinePaint;
	private float mDotRadius;
	private float mSmallDotRadius;
	private Paint textSelected;
	private int mTextSize;
	private float mTextMargin;
	private int mTextHeight;
	private boolean mIsMultiline;

	public CustomDrawable(Drawable base, ComboSeekBar slider, float thumbRadius, List<ComboSeekBar.Dot> dots, int color,int unSelectedLineColor, int textSize, boolean isMultiline) {
		mIsMultiline = isMultiline;
		mySlider = slider;
		myBase = base;
		mDots = dots;
		mTextSize = textSize;
		textUnselected = new Paint(Paint.ANTI_ALIAS_FLAG);
		textUnselected.setColor(unSelectedLineColor);
		textUnselected.setAlpha(255);

		textSelected = new Paint(Paint.ANTI_ALIAS_FLAG);
		textSelected.setTypeface(Typeface.DEFAULT_BOLD);
		textSelected.setColor(unSelectedLineColor);
		textSelected.setAlpha(255);

		mThumbRadius = thumbRadius;

		unselectLinePaint = new Paint();
		unselectLinePaint.setColor(unSelectedLineColor);
		unselectLinePaint.setStrokeWidth(toPix(3));

		selectLinePaint = new Paint();
		selectLinePaint.setColor(color);
		selectLinePaint.setStrokeWidth(toPix(3));

		circleLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circleLinePaint.setColor(color);

		unSelectedCircleLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		unSelectedCircleLinePaint.setColor(unSelectedLineColor);

		Rect textBounds = new Rect();
		textSelected.setTextSize(mTextSize * 2);
		textSelected.getTextBounds("M", 0, 1, textBounds);

		textUnselected.setTextSize(mTextSize);
		textSelected.setTextSize(mTextSize);

		mTextHeight = textBounds.height();
		mDotRadius = toPix(8);
		mSmallDotRadius = toPix(5);
		mTextMargin = toPix(50);
	}

	private float toPix(int size) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, mySlider.getContext().getResources().getDisplayMetrics());
	}

	@Override
	protected final void onBoundsChange(Rect bounds) {
		myBase.setBounds(bounds);
	}

	@Override
	protected final boolean onStateChange(int[] state) {
		invalidateSelf();
		return false;
	}

	@Override
	public final boolean isStateful() {
		return true;
	}
	@Override
	public final void draw(Canvas canvas) {
		// FLog.d("--- draw:" + (getBounds().right - getBounds().left));
		int height = this.getIntrinsicHeight() / 2;
		if (mDots.size() == 0) {
			canvas.drawLine(0, height, getBounds().right, height, unselectLinePaint);
			return;
		}
		for (ComboSeekBar.Dot dot : mDots) {
			if (dot.isSelected) {
				canvas.drawLine(mDots.get(0).mX, height, dot.mX, height, selectLinePaint);
				canvas.drawLine(dot.mX, height, mDots.get(mDots.size() - 1).mX, height, unselectLinePaint);
			}
			if (dot.isShouldSelected) {
				if(dot.isSmallCircle && !dot.isSelected){
					canvas.drawCircle(dot.mX, height, mSmallDotRadius, circleLinePaint);
				}else {
					canvas.drawCircle(dot.mX, height, mDotRadius, circleLinePaint);
				}
			} else {
				if(dot.isSmallCircle){
					canvas.drawCircle(dot.mX, height, mSmallDotRadius, unSelectedCircleLinePaint);
				}else {
					canvas.drawCircle(dot.mX, height, mDotRadius, unSelectedCircleLinePaint);
				}
			}

			drawText(canvas, dot, dot.mX, height);
		}
	}

	/**
	 * @param canvas
	 *            canvas.
	 * @param dot
	 *            current dot.
	 * @param x
	 *            x cor.
	 * @param y
	 *            y cor.
	 */
	private void drawText(Canvas canvas, ComboSeekBar.Dot dot, float x, float y) {
		final Rect textBounds = new Rect();
		textSelected.getTextBounds(dot.text, 0, dot.text.length(), textBounds);
		float xres;
		if (dot.id == (mDots.size() - 1)) {
			xres = getBounds().width() - textBounds.width();
		} else if (dot.id == 0) {
			xres = 0;
		} else {
			xres = x - (textBounds.width() / 2);
		}

		float yres;
		// Если многострочный текст
		if (mIsMultiline) {
			// Если четная точка, то сверху
			if ((dot.id % 2) == 0) {
				yres = y - mTextMargin - mDotRadius;
			} else {
				yres = y + mTextHeight;
			}
		} else {
			yres = y - (mDotRadius * 2) + mTextMargin;
		}

		//if (dot.isSelected) {
		//	canvas.drawText(dot.text, xres, yres, textSelected);
		//} else {
		if (dot.id%2==0)
			canvas.drawText(dot.text, xres, yres, textUnselected);
		else{
			canvas.drawText(dot.text, xres , yres- 100, textUnselected);

		}
		//}
	}

	@Override
	public final int getIntrinsicHeight() {
		if (mIsMultiline) {
			return (int) (selectLinePaint.getStrokeWidth() + mDotRadius + (mTextHeight) * 2  + mTextMargin);
		} else {
			return (int) (mThumbRadius + mTextMargin + mTextHeight + mDotRadius);
		}
	}

	@Override
	public final int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	public void setAlpha(int alpha) {
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
	}
}