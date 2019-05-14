package net.robinx.rippledrawable;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.LinearInterpolator;

/**
 * Created by Robin on 2018/4/17.
 * Email: robinxdroid@gmail.com
 * Blog: http://robinx.net/
 */
public class RippleDrawableCompat extends Drawable implements OnTouchListener {
    private static final Property<RippleDrawableCompat, Float> CREATE_TOUCH_RIPPLE = new FloatProperty<RippleDrawableCompat>("createTouchRipple") {

        @Override
        public Float get(RippleDrawableCompat object) {
            return object.getAnimationState();
        }

        @Override
        public void setValue(RippleDrawableCompat object, float value) {
            object.createTouchRipple(value, true);
        }
    };
    private static final int DEFAULT_ANIM_DURATION = 450;
    private static final float END_SCALE = 1.3F;
    private static final int RIPPLE_TOUCH_MAX_ALPHA = 70;
    private static final int RIPPLE_TOUCH_MIN_ALPHA = 20;
    private static final int START_ANIM_DURATION = 3000;
    private boolean eminateFromCenter;
    private float mAnimationValue;
    private ObjectAnimator mCurrentAnimator;
    private Drawable mOriginalBackground;
    private Paint mRippleBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mRipplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Circle mTouchRipple;
    private int mViewSize = 0;

    private RippleDrawableCompat() {
        this.initRippleElements();
    }

    /**
     * 使用{@link RippleDrawableCompat}，从中心位置扩散波纹
     *
     * @param v
     * @param primaryColor
     */
    public static void createCenterRipple(View v, int primaryColor) {
        RippleDrawableCompat rippleDrawableCompat = new RippleDrawableCompat();
        rippleDrawableCompat.setDrawable(v.getBackground());
        rippleDrawableCompat.setColor(primaryColor);
        rippleDrawableCompat.setEminateFromCenter(true);
        rippleDrawableCompat.setBounds(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
        v.setOnTouchListener(rippleDrawableCompat);
        if (VERSION.SDK_INT >= 16) {
            v.setBackground(rippleDrawableCompat);
        } else {
            v.setBackgroundDrawable(rippleDrawableCompat);
        }

    }

    /**
     * 使用{@link RippleDrawableCompat}，从点击位置扩散波纹
     *
     * @param v
     * @param primaryColor
     */
    public static void createRippleCompat(View v, int primaryColor) {
        if (!(v.getBackground() instanceof RippleDrawableCompat)) {
            RippleDrawableCompat rippleDrawableCompat = new RippleDrawableCompat();
            rippleDrawableCompat.setDrawable(v.getBackground());
            rippleDrawableCompat.setColor(primaryColor);
            rippleDrawableCompat.setBounds(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
            v.setOnTouchListener(rippleDrawableCompat);
            if (VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                v.setBackground(rippleDrawableCompat);
            } else {
                v.setBackgroundDrawable(rippleDrawableCompat);
            }
        }
    }

    /**
     * {@code Build.VERSION_CODES.LOLLIPOP} 以上版本使用{@link android.graphics.drawable.RippleDrawable},此时需要为View设置一个背景，{@link android.graphics.drawable.RippleDrawable}计算边界使用<br>
     * {@code Build.VERSION_CODES.LOLLIPOP} 以上版本使用{@link RippleDrawableCompat}
     *
     * @param v
     * @param primaryColor
     */
    public static void createRipple(View v, int primaryColor) {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createStockRipple(v, primaryColor);
        } else {
            RippleDrawableCompat rippleDrawableCompat = new RippleDrawableCompat();
            rippleDrawableCompat.setDrawable(v.getBackground());
            rippleDrawableCompat.setColor(primaryColor);
            rippleDrawableCompat.setBounds(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
            v.setOnTouchListener(rippleDrawableCompat);
            if (VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                v.setBackground(rippleDrawableCompat);
            } else {
                v.setBackgroundDrawable(rippleDrawableCompat);
            }
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void createStockRipple(View v, int primaryColor) {
        int[][] states = new int[1][];
        int[] state = new int[]{android.R.attr.state_enabled};
        states[0] = state;
        int[] colors = new int[]{primaryColor};
        v.setBackground(new RippleDrawable(new ColorStateList(states, colors), v.getBackground(), null));
    }

    private void setTouchRippleCoords(float value, float setAlpha) {
        this.mTouchRipple.cx = value;
        this.mTouchRipple.cy = setAlpha;
    }

    private void createTouchRipple(float value, boolean setAlpha) {
        this.mAnimationValue = value;
        this.mTouchRipple.radius = this.mAnimationValue * END_SCALE * (float) this.mViewSize;
        int alpha = RIPPLE_TOUCH_MIN_ALPHA + (int) (this.mAnimationValue * 50.0f);
        if (setAlpha) {
            this.mRipplePaint.setAlpha(90 - alpha);
        }

        this.invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        if (this.mOriginalBackground != null) {
            this.mOriginalBackground.setBounds(this.getBounds());
            this.mOriginalBackground.draw(canvas);
        }

        this.mTouchRipple.draw(canvas, this.mRipplePaint);
    }

    private float getAnimationState() {
        return this.mAnimationValue;
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    @Override
    public int[] getState() {
        int[] state;
        if (this.mOriginalBackground != null) {
            state = this.mOriginalBackground.getState();
            this.mOriginalBackground.invalidateSelf();
        } else {
            state = super.getState();
        }

        return state;
    }

    private void initRippleElements() {
        this.mTouchRipple = new Circle();
        this.mRipplePaint.setStyle(Style.FILL);
        this.mRippleBackgroundPaint.setStyle(Style.FILL);
    }

    public boolean isEminateFromCenter() {
        return this.eminateFromCenter;
    }

    private void onFingerDown(View v, float x, float y) {
        this.onFingerMove(v, x, y);
        this.mTouchRipple.radius = 0.0F;
        if (this.eminateFromCenter) {
            this.mViewSize = Math.max(v.getWidth() / 2, v.getHeight() / 2);
        } else {
            this.mViewSize = Math.max(v.getWidth(), v.getHeight());
        }

        if (this.mCurrentAnimator == null) {
            this.mCurrentAnimator = ObjectAnimator.ofFloat(this, CREATE_TOUCH_RIPPLE, 0.0F, 1.0F);
            this.mCurrentAnimator.setDuration(START_ANIM_DURATION);
            this.mCurrentAnimator.setInterpolator(new LinearInterpolator());
        }

        if (!this.mCurrentAnimator.isRunning()) {
            this.mCurrentAnimator.start();
        }

    }

    private void onFingerMove(View v, float x, float y) {
        if (this.eminateFromCenter) {
            this.setTouchRippleCoords((float) (v.getWidth() / 2), (float) (v.getHeight() / 2));
        } else {
            this.setTouchRippleCoords(x, y);
        }

        this.invalidateSelf();
    }

    private void onFingerUp() {
        long currentPlayTime;
        if (this.mCurrentAnimator == null) {
            currentPlayTime = 0L;
        } else {
            currentPlayTime = this.mCurrentAnimator.getCurrentPlayTime();
        }

        if (this.mCurrentAnimator != null) {
            this.mCurrentAnimator.cancel();
            this.mCurrentAnimator = null;
        }

        this.mCurrentAnimator = ObjectAnimator.ofFloat(this, CREATE_TOUCH_RIPPLE, 0.0F, 1.0F);
        this.mCurrentAnimator.setDuration(DEFAULT_ANIM_DURATION);
        this.mCurrentAnimator.start();
        this.mCurrentAnimator.setInterpolator(new LinearInterpolator());
        this.mCurrentAnimator.setCurrentPlayTime((long) (DEFAULT_ANIM_DURATION * ((float) currentPlayTime / START_ANIM_DURATION)));
        this.mCurrentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                RippleDrawableCompat.this.mCurrentAnimator = null;
            }
        });
        ObjectAnimator rippleAlphaAnimator = ObjectAnimator.ofInt(this, "RippleAlpha", this.mRipplePaint.getAlpha(), 0).setDuration(DEFAULT_ANIM_DURATION);
        rippleAlphaAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                RippleDrawableCompat.this.invalidateSelf();
            }
        });
        rippleAlphaAnimator.start();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.onFingerDown(v, event.getX(), event.getY());
                v.onTouchEvent(event);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                this.onFingerUp();
                break;
            case MotionEvent.ACTION_MOVE:
                this.onFingerMove(v, event.getX(), event.getY());
        }

        return false;
    }

    @Override
    public void setAlpha(int alpha) {
    }

    public void setColor(int primaryColor) {
        this.mRipplePaint.setColor(primaryColor);
        this.invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter var1) {
    }

    public void setDrawable(Drawable cf) {
        this.mOriginalBackground = cf;
        this.invalidateSelf();
    }

    public void setEminateFromCenter(boolean eminateFromCenter) {
        this.eminateFromCenter = eminateFromCenter;
    }

    public void setRippleAlpha(int alpha) {
        this.mRipplePaint.setAlpha(alpha);
    }

    @Override
    public boolean setState(int[] stateSet) {
        boolean returnValue;
        if (this.mOriginalBackground != null) {
            returnValue = this.mOriginalBackground.setState(stateSet);
            this.mOriginalBackground.invalidateSelf();
        } else {
            returnValue = super.setState(stateSet);
        }

        return returnValue;
    }

    static final class Circle {
        float cx;
        float cy;
        float radius;

        Circle() {
        }

        public void draw(Canvas canvas, Paint paint) {
            canvas.drawCircle(this.cx, this.cy, this.radius, paint);
        }
    }


    public static abstract class FloatProperty<T> extends Property<T, Float> {
        public FloatProperty(String name) {
            super(Float.class, name);
        }

        @Override
        public final void set(T object, Float value) {
            this.setValue(object, value.floatValue());
        }

        public abstract void setValue(T object, float value);
    }

}
