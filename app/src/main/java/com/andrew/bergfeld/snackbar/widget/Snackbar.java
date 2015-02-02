package com.andrew.bergfeld.snackbar.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.andrew.bergfeld.snackbar.R;

import java.util.LinkedList;

public class Snackbar extends FrameLayout {

    //Delta in pixels to count as a swipe away gesture
    private static final float SWIPE_AWAY_THRESHOLD = 50;

    private View mContainer;
    private TextView mMessageText;
    private TextView mActionText;
    private Listener mListener;

    private float mDragStartedX;
    private boolean mIsDragging;

    private LinkedList<Vo> mMessages;

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

        mMessages = new LinkedList<Vo>();

        mContainer = findViewById(R.id.container);
        mActionText = (TextView) findViewById(R.id.action);
        mMessageText = (TextView) findViewById(R.id.message);

        mContainer.setVisibility(GONE);
        mContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateGone();
            }
        });
    }

    //WIP swipe away
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        float deltaX;
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mIsDragging = true;
//                mDragStartedX = event.getX();
//                return true;
//
//            case MotionEvent.ACTION_MOVE:
//                if (mIsDragging) {
//                    deltaX = (mDragStartedX - event.getX()) * -1;
//
//                    setTranslationX(deltaX);
//                    return true;
//                }
//
//
//            case MotionEvent.ACTION_UP:
//                mIsDragging = false;
//                deltaX = (mDragStartedX - event.getX()) * -1;
//
//                if (deltaX > SWIPE_AWAY_THRESHOLD) {
//                    swipeAway(deltaX);
//                }
//                return true;
//        }
//
//        return super.onTouchEvent(event);
//    }

    private void showMessage(final Vo snackbarMessageVo) {
        mMessageText.setText(snackbarMessageVo.message);

        mListener = snackbarMessageVo.listener;

        if (snackbarMessageVo.action != null) {
            mActionText.setVisibility(VISIBLE);
            mActionText.setText(snackbarMessageVo.action);

            if (snackbarMessageVo.actionListener == null) {
                throw new IllegalStateException("If a snackbar has an action it must have an actionListener as well.");
            }

            mActionText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbarMessageVo.actionListener.onActionClicked();

                    animateGone();
                }
            });
        } else {
            mActionText.setVisibility(GONE);
        }

        Resources resources = getResources();

        mContainer.setBackgroundColor(resources.getColor(snackbarMessageVo.containerColor));
        mMessageText.setTextColor(resources.getColor(snackbarMessageVo.messageTextColor));
        mActionText.setTextColor(resources.getColor(snackbarMessageVo.actionTextColor));

        animateVisible(snackbarMessageVo.duration);
    }

    private boolean isShowingMessage() {
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

               onMessageDone();
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

    public static class Listener {

        public void onShowMessage() {
        }

        public void onMessageShown() {
        }

        public void onHideMessage() {
        }

        public void onMessageDone() {
        }
    }

    public static class Vo {
        public String message;
        public String action;
        public ActionListener actionListener;
        public Duration duration;
        public Listener listener;

        public int containerColor;
        public int messageTextColor;
        public int actionTextColor;
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

    public Builder withMessage(String message) {
        return new Builder(message);
    }

    public class Builder {

        private Vo mSnack;

        public Builder(String message) {
            mSnack = new Vo();
            mSnack.message = message;
            mSnack.duration = Snackbar.Duration.SHORT;
            mSnack.containerColor = R.color.snackbar_background;
            mSnack.messageTextColor = R.color.snackbar_message_text_color;
            mSnack.actionTextColor = R.color.snackbar_action_text_color;
        }

        public Builder withAction(String message, Snackbar.ActionListener actionClickListener) {
            mSnack.action = message;
            mSnack.actionListener = actionClickListener;

            return this;
        }

        public Builder withDuration(Snackbar.Duration duration) {
            mSnack.duration = duration;

            return this;
        }

        public Builder withListener(Listener listener) {
            mSnack.listener = listener;

            return this;
        }

        public Builder withBackgroundColor(int backgroundColorResId) {
            mSnack.containerColor = backgroundColorResId;

            return this;
        }

        public Builder withMessageTextColor(int messageTextColorResId) {
            mSnack.messageTextColor = messageTextColorResId;

            return this;
        }

        public Builder withActionTextColor(int actionTextColorResId) {
            mSnack.actionTextColor = actionTextColorResId;

            return this;
        }

        public void show() {
            if (isShowingMessage()) {
                mMessages.add(mSnack);
            } else {
                showMessage(mSnack);
            }
        }

    }

    private void onMessageDone() {
        if (mMessages.size() > 0) {
            showMessage(mMessages.removeFirst());
        }
    }
}
