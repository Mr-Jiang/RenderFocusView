package com.example.renderfocusview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Property;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import static android.view.GestureDetector.SimpleOnGestureListener;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

@SuppressLint("NewApi")
public class RenderFocusView extends FrameLayout {

	/** Render color int */
	private int RenderColor;
	/** Render overlay boolean */
	private boolean RenderOverlay;
	/** Render hover boolean */
	private boolean RenderHover;
	/** Render diameter boolean */
	private int RenderDiameter;
	/** Render duration int */
	private int RenderDuration;
	/** Render alpha int */
	private int RenderAlpha;
	/** Render delay click boolean */
	private boolean RenderDelayClick;
	/** Render fade duration int */
	private int RenderFadeDuration;
	/** Render presistent boolean */
	private boolean RenderPersistent;
	/** Render background drawable */
	private Drawable RenderBackground;
	/** Render in adapter boolean */
	private boolean RenderInAdapter;
	/** Render rounded corners float */
	private float RenderRoundedCorners;

	/** Render radius */
	private float radius;

	@SuppressWarnings("rawtypes")
	/** view adapterview*/
	private AdapterView parentAdapter;
	/** view childview */
	private View childView;

	/** Render animator */
	private AnimatorSet RenderAnimator;
	/** Render ObjectAnimator hoverAnimator */
	private ObjectAnimator hoverAnimator;

	/** Point currentCoords */
	private Point currentCoords = new Point();
	/** Point previousCoords */
	private Point previousCoords = new Point();
	/** Render layerType */
	private int layerType;

	/** Render eventCancelled */
	private boolean eventCancelled;
	/** Render prepressed */
	private boolean prepressed;
	/** Render positionInAdapter */
	private int positionInAdapter;

	/** Render GestureDetector gestureDetector */
	private GestureDetector gestureDetector;
	/** Render PerformClickEvent pendingClickEvent */
	private PerformClickEvent pendingClickEvent;
	/** Render PressedEvent pendingPressEvent */
	private PressedEvent pendingPressEvent;

	/** Render DEFAULT_DURATION 360 */
	private static final int DEFAULT_DURATION = 360;
	/** Render DEFAULT_FADE_DURATION 80 */
	private static final int DEFAULT_FADE_DURATION = 80;
	/** Render DEFAULT_DIAMETER_DP 40 */
	private static final float DEFAULT_DIAMETER_DP = 40;
	/** Render DEFAULT_ALPHA 0.2 */
	private static final float DEFAULT_ALPHA = 0.2f;
	/** Render DEFAULT_COLOR black */
	private static final int DEFAULT_COLOR = Color.BLACK;
	/** Render DEFAULT_BACKGROUND transparent */
	private static final int DEFAULT_BACKGROUND = Color.TRANSPARENT;
	/** Render DEFAULT_HOVER true */
	private static final boolean DEFAULT_HOVER = true;
	/** Render DEFAULT_DELAY_CLICK true */
	private static final boolean DEFAULT_DELAY_CLICK = true;
	/** Render DEFAULT_PERSISTENT false */
	private static final boolean DEFAULT_PERSISTENT = false;
	/** Render EFAULT_SEARCH_ADAPTER false */
	private static final boolean DEFAULT_SEARCH_ADAPTER = false;
	/** Render DEFAULT_Render_OVERLAY false */
	private static final boolean DEFAULT_Render_OVERLAY = false;
	/** Render DEFAULT_ROUNDED_CORNERS 0 */
	private static final int DEFAULT_ROUNDED_CORNERS = 0;

	/** Render Paint */
	private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	/** Render Rect */
	private final Rect bounds = new Rect();

	/** Render FADE_EXTRA_DELAY 50 */
	private static final int FADE_EXTRA_DELAY = 50;
	/** Render HOVER_DURATION 2500 */
	private static final long HOVER_DURATION = 2500;

	public static RenderBuilder on(View view) {
		return new RenderBuilder(view);
	}

	public RenderFocusView(Context context) {
		this(context, null, 0);
	}

	public RenderFocusView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RenderFocusView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		setWillNotDraw(false);
		gestureDetector = new GestureDetector(context, longClickListener);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.RenderFocusView);
		RenderColor = a.getColor(R.styleable.RenderFocusView_RenderColor,
				DEFAULT_COLOR);
		RenderDiameter = a.getDimensionPixelSize(
				R.styleable.RenderFocusView_RenderDimension,
				(int) dpToPx(getResources(), DEFAULT_DIAMETER_DP));
		RenderOverlay = a.getBoolean(R.styleable.RenderFocusView_RenderOverlay,
				DEFAULT_Render_OVERLAY);
		RenderHover = a.getBoolean(R.styleable.RenderFocusView_RenderHover,
				DEFAULT_HOVER);
		RenderDuration = a.getInt(R.styleable.RenderFocusView_RenderDuration,
				DEFAULT_DURATION);
		RenderAlpha = (int) (255 * a.getFloat(
				R.styleable.RenderFocusView_RenderAlpha, DEFAULT_ALPHA));
		RenderDelayClick = a.getBoolean(
				R.styleable.RenderFocusView_RenderDelayClick,
				DEFAULT_DELAY_CLICK);
		RenderFadeDuration = a.getInteger(
				R.styleable.RenderFocusView_RenderFadeDuration,
				DEFAULT_FADE_DURATION);
		RenderBackground = new ColorDrawable(a.getColor(
				R.styleable.RenderFocusView_RenderBackground,
				DEFAULT_BACKGROUND));
		RenderPersistent = a.getBoolean(
				R.styleable.RenderFocusView_RenderPersistent,
				DEFAULT_PERSISTENT);
		RenderInAdapter = a.getBoolean(
				R.styleable.RenderFocusView_RenderInAdapter,
				DEFAULT_SEARCH_ADAPTER);
		RenderRoundedCorners = a.getDimensionPixelSize(
				R.styleable.RenderFocusView_RenderRoundedCorners,
				DEFAULT_ROUNDED_CORNERS);

		a.recycle();

		paint.setColor(RenderColor);
		paint.setAlpha(RenderAlpha);

		enableClipPathSupportIfNecessary();
	}

	@SuppressWarnings("unchecked")
	public <T extends View> T getChildView() {
		return (T) childView;
	}

	@Override
	public final void addView(View child, int index,
			ViewGroup.LayoutParams params) {
		if (getChildCount() > 0) {
			throw new IllegalStateException(
					"RenderFocusView can host only one child");
		}
		childView = child;
		super.addView(child, index, params);
	}

	@Override
	public void setOnClickListener(OnClickListener onClickListener) {
		if (childView == null) {
			throw new IllegalStateException(
					"RenderFocusView must have a child view to handle clicks");
		}
		childView.setOnClickListener(onClickListener);
	}

	@Override
	public void setOnLongClickListener(OnLongClickListener onClickListener) {
		if (childView == null) {
			throw new IllegalStateException(
					"RenderFocusView must have a child view to handle clicks");
		}
		childView.setOnLongClickListener(onClickListener);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		return !findClickableViewInChild(childView, (int) event.getX(),
				(int) event.getY());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean superOnTouchEvent = super.onTouchEvent(event);

		if (!isEnabled() || !childView.isEnabled())
			return superOnTouchEvent;

		boolean isEventInBounds = bounds.contains((int) event.getX(),
				(int) event.getY());

		if (isEventInBounds) {
			previousCoords.set(currentCoords.x, currentCoords.y);
			currentCoords.set((int) event.getX(), (int) event.getY());
		}

		boolean gestureResult = gestureDetector.onTouchEvent(event);
		if (gestureResult || hasPerformedLongPress) {
			return true;
		} else {
			int action = event.getActionMasked();
			switch (action) {
			case MotionEvent.ACTION_UP:
				pendingClickEvent = new PerformClickEvent();

				if (prepressed) {
					childView.setPressed(true);
					postDelayed(new Runnable() {
						@Override
						public void run() {
							childView.setPressed(false);
						}
					}, ViewConfiguration.getPressedStateDuration());
				}

				if (isEventInBounds) {
					startRender(pendingClickEvent);
				} else if (!RenderHover) {
					setRadius(0);
				}
				if (!RenderDelayClick && isEventInBounds) {
					pendingClickEvent.run();
				}
				cancelPressedEvent();
				break;
			case MotionEvent.ACTION_DOWN:
				setPositionInAdapter();
				eventCancelled = false;
				pendingPressEvent = new PressedEvent(event);
				if (isInScrollingContainer()) {
					cancelPressedEvent();
					prepressed = true;
					postDelayed(pendingPressEvent,
							ViewConfiguration.getTapTimeout());
				} else {
					pendingPressEvent.run();
				}
				break;
			case MotionEvent.ACTION_CANCEL:
				if (RenderInAdapter) {
					// dont use current coords in adapter since they tend to
					// jump drastically on scroll
					currentCoords.set(previousCoords.x, previousCoords.y);
					previousCoords = new Point();
				}
				childView.onTouchEvent(event);
				if (RenderHover) {
					if (!prepressed) {
						startRender(null);
					}
				} else {
					childView.setPressed(false);
				}
				cancelPressedEvent();
				break;
			case MotionEvent.ACTION_MOVE:
				if (RenderHover) {
					if (isEventInBounds && !eventCancelled) {
						invalidate();
					} else if (!isEventInBounds) {
						startRender(null);
					}
				}

				if (!isEventInBounds) {
					cancelPressedEvent();
					if (hoverAnimator != null) {
						hoverAnimator.cancel();
					}
					childView.onTouchEvent(event);
					eventCancelled = true;
				}
				break;
			}
			return true;
		}
	}

	private void cancelPressedEvent() {
		if (pendingPressEvent != null) {
			removeCallbacks(pendingPressEvent);
			prepressed = false;
		}
	}

	private boolean hasPerformedLongPress;
	private SimpleOnGestureListener longClickListener = new GestureDetector.SimpleOnGestureListener() {
		public void onLongPress(MotionEvent e) {
			hasPerformedLongPress = childView.performLongClick();
			if (hasPerformedLongPress) {
				if (RenderHover) {
					startRender(null);
				}
				cancelPressedEvent();
			}
		}

		@Override
		public boolean onDown(MotionEvent e) {
			hasPerformedLongPress = false;
			return super.onDown(e);
		}
	};

	private void startHover() {
		if (eventCancelled)
			return;

		if (hoverAnimator != null) {
			hoverAnimator.cancel();
		}
		final float radius = (float) (Math.sqrt(Math.pow(getWidth(), 2)
				+ Math.pow(getHeight(), 2)) * 1.2f);
		hoverAnimator = ObjectAnimator.ofFloat(this, radiusProperty,
				RenderDiameter, radius).setDuration(HOVER_DURATION);
		hoverAnimator.setInterpolator(new LinearInterpolator());
		hoverAnimator.start();
	}

	private void startRender(final Runnable animationEndRunnable) {
		if (eventCancelled)
			return;

		float endRadius = getEndRadius();

		cancelAnimations();

		RenderAnimator = new AnimatorSet();
		RenderAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				if (!RenderPersistent) {
					setRadius(0);
					setRenderAlpha(RenderAlpha);
				}
				if (animationEndRunnable != null && RenderDelayClick) {
					animationEndRunnable.run();
				}
				childView.setPressed(false);
			}
		});

		ObjectAnimator Render = ObjectAnimator.ofFloat(this, radiusProperty,
				radius, endRadius);
		Render.setDuration(RenderDuration);
		Render.setInterpolator(new DecelerateInterpolator());
		ObjectAnimator fade = ObjectAnimator.ofInt(this, circleAlphaProperty,
				RenderAlpha, 0);
		fade.setDuration(RenderFadeDuration);
		fade.setInterpolator(new AccelerateInterpolator());
		fade.setStartDelay(RenderDuration - RenderFadeDuration
				- FADE_EXTRA_DELAY);

		if (RenderPersistent) {
			RenderAnimator.play(Render);
		} else if (getRadius() > endRadius) {
			fade.setStartDelay(0);
			RenderAnimator.play(fade);
		} else {
			RenderAnimator.playTogether(Render, fade);
		}
		RenderAnimator.start();
	}

	private void cancelAnimations() {
		if (RenderAnimator != null) {
			RenderAnimator.cancel();
			RenderAnimator.removeAllListeners();
		}

		if (hoverAnimator != null) {
			hoverAnimator.cancel();
		}
	}

	private float getEndRadius() {
		final int width = getWidth();
		final int height = getHeight();

		final int halfWidth = width / 2;
		final int halfHeight = height / 2;

		final float radiusX = halfWidth > currentCoords.x ? width
				- currentCoords.x : currentCoords.x;
		final float radiusY = halfHeight > currentCoords.y ? height
				- currentCoords.y : currentCoords.y;

		return (float) Math.sqrt(Math.pow(radiusX, 2) + Math.pow(radiusY, 2)) * 1.2f;
	}

	private boolean isInScrollingContainer() {
		ViewParent p = getParent();
		while (p != null && p instanceof ViewGroup) {
			if (((ViewGroup) p).shouldDelayChildPressedState()) {
				return true;
			}
			p = p.getParent();
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	private AdapterView findParentAdapterView() {
		if (parentAdapter != null) {
			return parentAdapter;
		}
		ViewParent current = getParent();
		while (true) {
			if (current instanceof AdapterView) {
				parentAdapter = (AdapterView) current;
				return parentAdapter;
			} else {
				try {
					current = current.getParent();
				} catch (NullPointerException npe) {
					throw new RuntimeException(
							"Could not find a parent AdapterView");
				}
			}
		}
	}

	private void setPositionInAdapter() {
		if (RenderInAdapter) {
			positionInAdapter = findParentAdapterView().getPositionForView(
					RenderFocusView.this);
		}
	}

	private boolean adapterPositionChanged() {
		if (RenderInAdapter) {
			int newPosition = findParentAdapterView().getPositionForView(
					RenderFocusView.this);
			final boolean changed = newPosition != positionInAdapter;
			positionInAdapter = newPosition;
			if (changed) {
				cancelPressedEvent();
				cancelAnimations();
				childView.setPressed(false);
				setRadius(0);
			}
			return changed;
		}
		return false;
	}

	private boolean findClickableViewInChild(View view, int x, int y) {
		if (view instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup) view;
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				View child = viewGroup.getChildAt(i);
				final Rect rect = new Rect();
				child.getHitRect(rect);

				final boolean contains = rect.contains(x, y);
				if (contains) {
					return findClickableViewInChild(child, x - rect.left, y
							- rect.top);
				}
			}
		} else if (view != childView) {
			return (view.isEnabled() && (view.isClickable()
					|| view.isLongClickable() || view.isFocusableInTouchMode()));
		}

		return view.isFocusableInTouchMode();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		bounds.set(0, 0, w, h);
		RenderBackground.setBounds(bounds);
	}

	@Override
	public boolean isInEditMode() {
		return true;
	}

	/*
	 * Drawing
	 */
	@Override
	public void draw(Canvas canvas) {
		final boolean positionChanged = adapterPositionChanged();
		if (RenderOverlay) {
			if (!positionChanged) {
				RenderBackground.draw(canvas);
			}
			super.draw(canvas);
			if (!positionChanged) {
				if (RenderRoundedCorners != 0) {
					Path clipPath = new Path();
					RectF rect = new RectF(0, 0, canvas.getWidth(),
							canvas.getHeight());
					clipPath.addRoundRect(rect, RenderRoundedCorners,
							RenderRoundedCorners, Path.Direction.CW);
					canvas.clipPath(clipPath);
				}
				canvas.drawCircle(currentCoords.x, currentCoords.y, radius,
						paint);
			}
		} else {
			if (!positionChanged) {
				RenderBackground.draw(canvas);
				canvas.drawCircle(currentCoords.x, currentCoords.y, radius,
						paint);
			}
			super.draw(canvas);
		}
	}

	/*
	 * Animations
	 */
	private Property<RenderFocusView, Float> radiusProperty = new Property<RenderFocusView, Float>(
			Float.class, "radius") {
		@Override
		public Float get(RenderFocusView object) {
			return object.getRadius();
		}

		@Override
		public void set(RenderFocusView object, Float value) {
			object.setRadius(value);
		}
	};

	private float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
		invalidate();
	}

	private Property<RenderFocusView, Integer> circleAlphaProperty = new Property<RenderFocusView, Integer>(
			Integer.class, "RenderAlpha") {
		@Override
		public Integer get(RenderFocusView object) {
			return object.getRenderAlpha();
		}

		@Override
		public void set(RenderFocusView object, Integer value) {
			object.setRenderAlpha(value);
		}
	};

	public int getRenderAlpha() {
		return paint.getAlpha();
	}

	public void setRenderAlpha(Integer RenderAlpha) {
		paint.setAlpha(RenderAlpha);
		invalidate();
	}

	/*
	 * Accessor
	 */
	public void setRenderColor(int RenderColor) {
		this.RenderColor = RenderColor;
		paint.setColor(RenderColor);
		paint.setAlpha(RenderAlpha);
		invalidate();
	}

	public void setRenderOverlay(boolean RenderOverlay) {
		this.RenderOverlay = RenderOverlay;
	}

	public void setRenderDiameter(int RenderDiameter) {
		this.RenderDiameter = RenderDiameter;
	}

	public void setRenderDuration(int RenderDuration) {
		this.RenderDuration = RenderDuration;
	}

	public void setRenderBackground(int color) {
		RenderBackground = new ColorDrawable(color);
		RenderBackground.setBounds(bounds);
		invalidate();
	}

	public void setRenderHover(boolean RenderHover) {
		this.RenderHover = RenderHover;
	}

	public void setRenderDelayClick(boolean RenderDelayClick) {
		this.RenderDelayClick = RenderDelayClick;
	}

	public void setRenderFadeDuration(int RenderFadeDuration) {
		this.RenderFadeDuration = RenderFadeDuration;
	}

	public void setRenderPersistent(boolean RenderPersistent) {
		this.RenderPersistent = RenderPersistent;
	}

	public void setRenderInAdapter(boolean RenderInAdapter) {
		this.RenderInAdapter = RenderInAdapter;
	}

	public void setRenderRoundedCorners(int RenderRoundedCorner) {
		this.RenderRoundedCorners = RenderRoundedCorner;
		enableClipPathSupportIfNecessary();
	}

	public void setDefaultRenderAlpha(float alpha) {
		this.RenderAlpha = (int) (255 * alpha);
		paint.setAlpha(RenderAlpha);
		invalidate();
	}

	public void performRender() {
		currentCoords = new Point(getWidth() / 2, getHeight() / 2);
		startRender(null);
	}

	public void performRender(Point anchor) {
		currentCoords = new Point(anchor.x, anchor.y);
		startRender(null);
	}

	/**
	 * {@link Canvas#clipPath(Path)} is not supported in hardware accelerated
	 * layers before API 18. Use software layer instead
	 * <p/>
	 * https://developer.android.com/guide/topics/graphics/hardware-accel.html#
	 * unsupported
	 */
	private void enableClipPathSupportIfNecessary() {
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			if (RenderRoundedCorners != 0) {
				layerType = getLayerType();
				setLayerType(LAYER_TYPE_SOFTWARE, null);
			} else {
				setLayerType(layerType, null);
			}
		}
	}

	/*
	 * Helper
	 */
	private class PerformClickEvent implements Runnable {

		@SuppressWarnings("rawtypes")
		@Override
		public void run() {
			if (hasPerformedLongPress)
				return;

			// if parent is an AdapterView, try to call its ItemClickListener
			if (getParent() instanceof AdapterView) {
				// try clicking direct child first
				if (!childView.performClick())
					// if it did not handle it dispatch to adapterView
					clickAdapterView((AdapterView) getParent());
			} else if (RenderInAdapter) {
				// find adapter view
				clickAdapterView(findParentAdapterView());
			} else {
				// otherwise, just perform click on child
				childView.performClick();
			}
		}

		@SuppressWarnings("rawtypes")
		private void clickAdapterView(AdapterView parent) {
			final int position = parent
					.getPositionForView(RenderFocusView.this);
			final long itemId = parent.getAdapter() != null ? parent
					.getAdapter().getItemId(position) : 0;
			if (position != AdapterView.INVALID_POSITION) {
				parent.performItemClick(RenderFocusView.this, position, itemId);
			}
		}
	}

	private final class PressedEvent implements Runnable {

		private final MotionEvent event;

		public PressedEvent(MotionEvent event) {
			this.event = event;
		}

		@Override
		public void run() {
			prepressed = false;
			childView.setLongClickable(false);// prevent the child's long
												// click,let's the Render layout
												// call it's performLongClick
			childView.onTouchEvent(event);
			childView.setPressed(true);
			if (RenderHover) {
				startHover();
			}
		}
	}

	static float dpToPx(Resources resources, float dp) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				resources.getDisplayMetrics());
	}

	/*
	 * Builder
	 */

	public static class RenderBuilder {

		private final Context context;
		private final View child;

		private int RenderColor = DEFAULT_COLOR;
		private boolean RenderOverlay = DEFAULT_Render_OVERLAY;
		private boolean RenderHover = DEFAULT_HOVER;
		private float RenderDiameter = DEFAULT_DIAMETER_DP;
		private int RenderDuration = DEFAULT_DURATION;
		private float RenderAlpha = DEFAULT_ALPHA;
		private boolean RenderDelayClick = DEFAULT_DELAY_CLICK;
		private int RenderFadeDuration = DEFAULT_FADE_DURATION;
		private boolean RenderPersistent = DEFAULT_PERSISTENT;
		private int RenderBackground = DEFAULT_BACKGROUND;
		private boolean RenderSearchAdapter = DEFAULT_SEARCH_ADAPTER;
		private float RenderRoundedCorner = DEFAULT_ROUNDED_CORNERS;

		public RenderBuilder(View child) {
			this.child = child;
			this.context = child.getContext();
		}

		public RenderBuilder RenderColor(int color) {
			this.RenderColor = color;
			return this;
		}

		public RenderBuilder RenderOverlay(boolean overlay) {
			this.RenderOverlay = overlay;
			return this;
		}

		public RenderBuilder RenderHover(boolean hover) {
			this.RenderHover = hover;
			return this;
		}

		public RenderBuilder RenderDiameterDp(int diameterDp) {
			this.RenderDiameter = diameterDp;
			return this;
		}

		public RenderBuilder RenderDuration(int duration) {
			this.RenderDuration = duration;
			return this;
		}

		public RenderBuilder RenderAlpha(float alpha) {
			this.RenderAlpha = alpha;
			return this;
		}

		public RenderBuilder RenderDelayClick(boolean delayClick) {
			this.RenderDelayClick = delayClick;
			return this;
		}

		public RenderBuilder RenderFadeDuration(int fadeDuration) {
			this.RenderFadeDuration = fadeDuration;
			return this;
		}

		public RenderBuilder RenderPersistent(boolean persistent) {
			this.RenderPersistent = persistent;
			return this;
		}

		public RenderBuilder RenderBackground(int color) {
			this.RenderBackground = color;
			return this;
		}

		public RenderBuilder RenderInAdapter(boolean inAdapter) {
			this.RenderSearchAdapter = inAdapter;
			return this;
		}

		public RenderBuilder RenderRoundedCorners(int radiusDp) {
			this.RenderRoundedCorner = radiusDp;
			return this;
		}

		public RenderFocusView create() {
			RenderFocusView layout = new RenderFocusView(context);
			layout.setRenderColor(RenderColor);
			layout.setDefaultRenderAlpha(RenderAlpha);
			layout.setRenderDelayClick(RenderDelayClick);
			layout.setRenderDiameter((int) dpToPx(context.getResources(),
					RenderDiameter));
			layout.setRenderDuration(RenderDuration);
			layout.setRenderFadeDuration(RenderFadeDuration);
			layout.setRenderHover(RenderHover);
			layout.setRenderPersistent(RenderPersistent);
			layout.setRenderOverlay(RenderOverlay);
			layout.setRenderBackground(RenderBackground);
			layout.setRenderInAdapter(RenderSearchAdapter);
			layout.setRenderRoundedCorners((int) dpToPx(context.getResources(),
					RenderRoundedCorner));

			ViewGroup.LayoutParams params = child.getLayoutParams();
			ViewGroup parent = (ViewGroup) child.getParent();
			int index = 0;

			if (parent != null && parent instanceof RenderFocusView) {
				throw new IllegalStateException(
						"RenderFocusView could not be created: parent of the view already is a RenderFocusView");
			}

			if (parent != null) {
				index = parent.indexOfChild(child);
				parent.removeView(child);
			}

			layout.addView(child, new ViewGroup.LayoutParams(MATCH_PARENT,
					MATCH_PARENT));

			if (parent != null) {
				parent.addView(layout, index, params);
			}

			return layout;
		}
	}
}
