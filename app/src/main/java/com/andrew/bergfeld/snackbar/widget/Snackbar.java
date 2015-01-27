package com.andrew.bergfeld.snackbar.widget;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.andrew.bergfeld.snackbar.R;

public class Snackbar extends FrameLayout {

    //Delta in pixels to count as a swipe away gesture
    private static final float SWIPE_AWAY_THRESHOLD = 50;

    private View mContainer;
    private TextView mMessage;
    private TextView mAction;
    private SnackListener mListener;
    private SnackbarManager mManager;

    private float mDragStartedX;
    private boolean mIsDragging;

    public Snackbar(Context context) {
        super(context);

        initialize(context);
    }

    public Snackbar(Context context, AttributeSet attrs) {
        super(context, attrs);

        initialize(context);
    }

    public Snackbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_snackbar, this, true);

        mContainer = findViewById(R.id.container);
        mAction = (TextView) findViewById(R.id.action);
        mMessage = (TextView) findViewById(R.id.message);

        mContainer.setVisibility(GONE);
        mContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateGone();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float deltaX;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsDragging = true;
                mDragStartedX = event.getX();
                return true;

            case MotionEvent.ACTION_MOVE:
                if (mIsDragging) {
                    deltaX = (mDragStartedX - event.getX()) * -1;

                    setTranslationX(deltaX);
                    return true;
                }


            case MotionEvent.ACTION_UP:
                mIsDragging = false;
                deltaX = (mDragStartedX - event.getX()) * -1;

                if (deltaX > SWIPE_AWAY_THRESHOLD) {
                    swipeAway(deltaX);
                }
                return true;
        }

        return super.onTouchEvent(event);
    }

    public void show(final SnackbarMessageVo snackbarMessageVo) {
        mMessage.setText(snackbarMessageVo.message);

        mListener = snackbarMessageVo.listener;

        if (snackbarMessageVo.action != null) {
            mAction.setVisibility(VISIBLE);
            mAction.setText(snackbarMessageVo.action);
            mAction.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbarMessageVo.actionListener.onActionClicked();

                    animateGone();
                }
            });
        } else {
            mAction.setVisibility(GONE);
        }

        if (mManager == null) {
            throw new IllegalStateException("Snackbar.setSnackbarManager() must be used prior to showing messages");
        }

        animateVisible(snackbarMessageVo.duration);
    }

    public boolean isShowingMessage() {
        return mContainer.getVisibility() == VISIBLE;
    }

    private void animateVisible(final Duration duration) {
        Animation show = AnimationUtils.loadAnimation(getContext(), R.anim.snackbar_show);

        show.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (mListener != null) {
                    mListener.onShowMessage();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mListener != null) {
                    mListener.onMessageShown();
                }

                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animateGone();
                        }
                    }, duration.getValue());

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mContainer.setVisibility(VISIBLE);
        mContainer.startAnimation(show);
    }

    private void animateGone() {
        Animation hide = AnimationUtils.loadAnimation(getContext(), R.anim.snackbar_hide);

        hide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (mListener != null) {
                    mListener.onHideMessage();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mContainer.setVisibility(GONE);

                if (mListener != null) {
                    mListener.onMessageDone();
                }

                mManager.onMessageDone();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mContainer.startAnimation(hide);
    }

    private void swipeAway(float deltaX) {
        Animation swipeAnimation = AnimationUtils.loadAnimation(getContext(), deltaX > 0 ? R.anim.snackbar_swipe_right : R.anim.snackbar_swipe_left);

        swipeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mContainer.setTranslationX(0);
                mContainer.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mContainer.startAnimation(swipeAnimation);
    }

    public static class SnackListener {

        public void onShowMessage() {
        }

        public void onMessageShown() {
        }

        public void onHideMessage() {
        }

        public void onMessageDone() {
        }
    }

    public void setSnackbarManager(SnackbarManager snackbarManager) {
        mManager = snackbarManager;
    }

    public interface SnackbarManager {
        void onMessageDone();
    }

    public static class SnackbarMessageVo {
        public String message;
        public String action;
        public ActionListener actionListener;
        public Duration duration;
        public SnackListener listener;
    }

    public interface ActionListener {
        void onActionClicked();
    }

    public static enum Duration {
        SHORT(3000),
        LONG(5000);

        private int value;

        Duration(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
