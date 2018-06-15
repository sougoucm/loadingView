package com.xiaodao.libloading;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * FloatingCirclesDrawable
 */
@SuppressLint("NewApi")
public class FloatingCirclesDrawable extends Drawable implements Drawable.Callback {

    // constants
    private int MAX_LEVEL = 10000;
    private int CENT_LEVEL = MAX_LEVEL / 2;
    private int MID_LEVEL = CENT_LEVEL / 2;
    private int ALPHA_OPAQUE = 255;
    private int ACCELERATION_LEVEL = 2;

    // default
    private int mAlpha = ALPHA_OPAQUE;
    private ColorFilter mColorFilter;

    // points and paints
    private Point[] mArrowPoints;
    private Paint mPaint1;
    private Paint mPaint2;
    private Paint mPaint3;
    private Paint mPaint4;
    private double unit;
    private int width, x_beg, y_beg,  offset;

    // speed related
    private int acceleration = ACCELERATION_LEVEL;
    private double distance = 0.5 * ACCELERATION_LEVEL * MID_LEVEL * MID_LEVEL;
    private double max_speed; // set in setAcceleration(...);
    private double offsetPercentage;

    // top color var
    private int colorSign;
    private ProgressStates currentProgressStates = ProgressStates.GREEN_TOP;

    //rect or circle
    private boolean flag = false;

    private enum ProgressStates {
        GREEN_TOP,
        YELLOW_TOP,
        RED_TOP,
        BLUE_TOP
    }

    public FloatingCirclesDrawable(int[] colors, boolean flag) {
    	if(flag) {
    		MAX_LEVEL = 20000;
    		CENT_LEVEL = MAX_LEVEL / 2;
    	    MID_LEVEL = CENT_LEVEL / 2;
    	}
        initCirclesProgress(colors);
    }

    private void initCirclesProgress(int[] colors) {
        //init Paint colors
        initColors(colors);

        // init alpha and color filter
        setAlpha(mAlpha);
        setColorFilter(mColorFilter);

        // offset percentage
        setAcceleration(ACCELERATION_LEVEL);
        offsetPercentage = 0;

        // init colorSign
        colorSign = 1; // |= 1, |= 2, |= 4, |= 8 --> 0xF
    }

    private void initColors(int[] colors) {
        // red circle, left up
        mPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint1.setColor(colors[0]);
        mPaint1.setAntiAlias(true);

        // blue circle, right down
        mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint2.setColor(colors[1]);
        mPaint2.setAntiAlias(true);

        // yellow circle, left down
        mPaint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint3.setColor(colors[2]);
        mPaint3.setAntiAlias(true);

        // green circle, right up
        mPaint4 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint4.setColor(colors[3]);
        mPaint4.setAntiAlias(true);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        measureCircleProgress(bounds.width(), bounds.height());
    }

    int ef_width;
    @Override
    protected boolean onLevelChange(int level) {
        // calc one offset data is enough
        // 0.5 * a * t^2 / mCenterPoint.x = level / sideLevel
        // t from 0 to 10,000, so divided into 4 parts.
        // the ACCELERATION_LEVEL defines how many divisions in 10000 levels
        level %= MAX_LEVEL / acceleration;
        final int temp_level = level % (MID_LEVEL / acceleration);

        ef_width = (int)(unit * 3.0); // effective width
        if(level < CENT_LEVEL / acceleration) { // go
            if(level < MID_LEVEL / acceleration) {
                // set colorSign
                if(colorSign == 0xF) {
                    changeTopColor();
                    colorSign = 1;
                }
                // from beg to mid
                offsetPercentage = 0.5 * acceleration * temp_level * temp_level / distance;
                offset = (int)(offsetPercentage * ef_width / 2); // x and y direction offset
            }
            else {
                // set colorSign
                colorSign |= 2;
                // from mid to end
                offsetPercentage = (max_speed * temp_level
                        - 0.5 * acceleration * temp_level * temp_level) / distance
                        + 1.0;
                offset = (int)(offsetPercentage * ef_width / 2); // x and y direction offset
            }
        }
        else { // back
            if(level < (CENT_LEVEL + MID_LEVEL) / acceleration) {
                // set colorSign
                if(colorSign == 0x3) {
                    changeTopColor();
                    colorSign |= 4;
                }
                // from end to mid
                offsetPercentage = 0.5 * acceleration * temp_level * temp_level  / distance;
                offset = (int)(ef_width - offsetPercentage * ef_width / 2); // x and y direction offset
            }
            else {
                // set colorSign
                colorSign |= 8;
                // from mid to beg
                offsetPercentage = (max_speed * temp_level
                        - 0.5 * acceleration * temp_level * temp_level) / distance
                        + 1.0;
                offsetPercentage = offsetPercentage == 1.0 ? 2.0 : offsetPercentage;
                offset = (int)(ef_width - offsetPercentage * ef_width / 2); // x and y direction offset
            }
        }


        mArrowPoints[0].set((int)unit+x_beg+offset, (int)unit+y_beg+offset); // mPaint1, left up
        mArrowPoints[1].set((int)(unit*4.0)+x_beg-offset, (int)(unit*4.0)+y_beg-offset); // mPaint2, right down
        mArrowPoints[2].set((int)unit+x_beg+offset, (int)(unit*4.0)+y_beg-offset); // mPaint3, left down
        mArrowPoints[3].set((int)(unit*4.0)+x_beg-offset, (int)unit+y_beg+offset); // mPaint4, right up

        return true;
    }

    int count = 0;
    private void changeTopColor() {
    	count++;
    	if(count%2==0) flag = !flag;
        switch(currentProgressStates){
            case GREEN_TOP:
                currentProgressStates = ProgressStates.YELLOW_TOP;
                break;
            case YELLOW_TOP:
                currentProgressStates = ProgressStates.RED_TOP;
                break;
            case RED_TOP:
                currentProgressStates = ProgressStates.BLUE_TOP;
                break;
            case BLUE_TOP:
                currentProgressStates = ProgressStates.GREEN_TOP;
                break;
        }
    }



    @Override
    public void draw(Canvas canvas) {

    	double tmp = unit;
    	unit = tmp/2;
//    	if(unit/2*offset/ef_width>tmp/3){
//    		unit=unit/2;//*offset/ef_width;
//    	}else {
//    		unit = tmp/2;
//		}
    	//flag = true;
        // draw circles
        if(currentProgressStates != ProgressStates.RED_TOP)
            if(flag) canvas.drawCircle(mArrowPoints[0].x, mArrowPoints[0].y, (float)unit, mPaint1);
        	else canvas.drawRect(mArrowPoints[0].x- (float)unit, mArrowPoints[0].y- (float)unit, mArrowPoints[0].x + (float)unit, mArrowPoints[0].y + (float)unit, mPaint1);
        if(currentProgressStates != ProgressStates.BLUE_TOP)
        	if(flag) canvas.drawCircle(mArrowPoints[1].x, mArrowPoints[1].y, (float)unit, mPaint2);
        	else canvas.drawRect(mArrowPoints[1].x- (float)unit, mArrowPoints[1].y- (float)unit, mArrowPoints[1].x + (float)unit, mArrowPoints[1].y + (float)unit, mPaint2);
        if(currentProgressStates != ProgressStates.YELLOW_TOP)
        	if(flag) canvas.drawCircle(mArrowPoints[2].x, mArrowPoints[2].y, (float)unit, mPaint3);
        	else canvas.drawRect(mArrowPoints[2].x- (float)unit, mArrowPoints[2].y- (float)unit, mArrowPoints[2].x + (float)unit, mArrowPoints[2].y + (float)unit, mPaint3);
        if(currentProgressStates != ProgressStates.GREEN_TOP)
        	if(flag) canvas.drawCircle(mArrowPoints[3].x, mArrowPoints[3].y, (float)unit, mPaint4);
        	else canvas.drawRect(mArrowPoints[3].x- (float)unit, mArrowPoints[3].y- (float)unit, mArrowPoints[3].x + (float)unit, mArrowPoints[3].y + (float)unit, mPaint4);

        // draw the top one
        switch(currentProgressStates){
            case GREEN_TOP:
            	if(flag) canvas.drawCircle(mArrowPoints[3].x, mArrowPoints[3].y, (float)unit, mPaint4);
            	else canvas.drawRect(mArrowPoints[3].x- (float)unit, mArrowPoints[3].y- (float)unit, mArrowPoints[3].x + (float)unit, mArrowPoints[3].y + (float)unit, mPaint4);
                break;
            case YELLOW_TOP:
            	if(flag) canvas.drawCircle(mArrowPoints[2].x, mArrowPoints[2].y, (float)unit, mPaint3);
            	else canvas.drawRect(mArrowPoints[2].x- (float)unit, mArrowPoints[2].y- (float)unit, mArrowPoints[2].x + (float)unit, mArrowPoints[2].y + (float)unit, mPaint3);
                break;
            case RED_TOP:
            	if(flag) canvas.drawCircle(mArrowPoints[0].x, mArrowPoints[0].y, (float)unit, mPaint1);
            	else canvas.drawRect(mArrowPoints[0].x- (float)unit, mArrowPoints[0].y- (float)unit, mArrowPoints[0].x + (float)unit, mArrowPoints[0].y + (float)unit, mPaint1);
                break;
            case BLUE_TOP:
            	if(flag) canvas.drawCircle(mArrowPoints[1].x, mArrowPoints[1].y, (float)unit, mPaint2);
            	else canvas.drawRect(mArrowPoints[1].x- (float)unit, mArrowPoints[1].y- (float)unit, mArrowPoints[1].x + (float)unit, mArrowPoints[1].y + (float)unit, mPaint2);
                break;
        }

        unit = tmp;
    }

    private void measureCircleProgress(int width, int height) {
        // get min edge as width
        if(width > height) {
            // use height
            this.width = height - 1; // minus 1 to avoid "3/2=1"
            x_beg = (width - height) / 2 + 1;
            y_beg = 1;
        }
        else {
            //use width
            this.width = width - 1;
            x_beg = 1;
            y_beg = (height - width) / 2 + 1;
        }
        unit = (double)this.width / 5.0;

        // init the original position, and then set position by offsets
        mArrowPoints = new Point[4];
        mArrowPoints[0] = new Point((int)unit+x_beg, (int)unit+y_beg); // mPaint1, left up
        mArrowPoints[1] = new Point((int)(unit*4.0)+x_beg, (int)(unit*4.0)+y_beg); // mPaint2, right down
        mArrowPoints[2] = new Point((int)unit+x_beg, (int)(unit*4.0)+y_beg); // mPaint3, left down
        mArrowPoints[3] = new Point((int)(unit*4.0)+x_beg, (int)unit+y_beg); // mPaint4, right up
    }

    public void setAcceleration(int acceleration) {
        this.acceleration = acceleration;
        distance = 0.5 * acceleration * (MID_LEVEL / acceleration) * (MID_LEVEL / acceleration);
        max_speed = acceleration * (MID_LEVEL / acceleration);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint1.setAlpha(alpha);
        mPaint2.setAlpha(alpha);
        mPaint3.setAlpha(alpha);
        mPaint4.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mColorFilter = cf;
        mPaint1.setColorFilter(cf);
        mPaint2.setColorFilter(cf);
        mPaint3.setColorFilter(cf);
        mPaint4.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void invalidateDrawable(Drawable who) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.invalidateDrawable(this);
        }
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.scheduleDrawable(this, what, when);
        }
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.unscheduleDrawable(this, what);
        }
    }

    public static class Builder {
        private int[] mColors;
        private boolean slowFlag;

        public Builder(Context context){
            initDefaults(context);
        }

        private void initDefaults(Context context) {
            //Default values
        	mColors = new int[]{Color.parseColor("#ff33b5e5"),Color.parseColor("#ff99cc00"),Color.parseColor("#ffff4444"),Color.parseColor("#ffffbb33")};
        	slowFlag = false;
        }

        public Builder colors(int[] colors) {
            if (colors == null || colors.length == 0) {
                throw new IllegalArgumentException("Your color array must contains at least 4 values");
            }

            mColors = colors;
            return this;
        }

        public Builder setVelocitySlow() {
            slowFlag = true;
            return this;
        }

        public Drawable build() {
            return new FloatingCirclesDrawable(mColors, slowFlag);
        }
    }
}