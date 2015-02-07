package com.andrew.bergfeld.snackbar.widget;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.andrew.bergfeld.snackbar.R;

import java.util.LinkedList;

public class Snackbar extends FrameLayout {

    private static final float SWIPE_AWAY_THRESHOLD = 150;

    private View mContainer;
    private TextView mMessageText;
    private TextView mActionText;
    private Listener mListener;

    private Runnable mAnimateGoneRunnable;
    private LinkedList<Vo> mMessages;
    private Vo mCurrentSnack;

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
        mAnimateGoneRunnable = new Runnable() {
            @Override
            public void run() {
                animateGone();
            }
        };

        mContainer = findViewById(R.id.container);
        mActionText = (TextView) findViewById(R.id.action);
        mMessageText = (TextView) findViewById(R.id.message);

        mContainer.setVisibility(GONE);
        mContainer.setOnTouchListener(new HorizontalSwipeListener(getContext()));
    }

    private void showMessage(final Vo snackbarMessageVo) {
        mCurrentSnack = snackbarMessageVo;
        mMessageText.setText(snackbarMessageVo.message);

        mListener = snackbarMessageVo.listener;

        if (snackbarMessageVo.action != null) {
            mActionText.setVisibility(VISIBLE);
            mActionText.setText(snackbarMessageVo.action);

            if (snackbarMessageVo.actionListener == null) {
                throw new IllegalStateException("If a Snackbar has an action it must have an actionListener as well.");
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

                postDelayed(mAnimateGoneRunnable, duration.getValue());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mContainer.setVisibility(VISIBLE);
        mContainer.startAnimation(show);
    }

    private void animateGone() {
        cancelDelayedRunnable();

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

               handleNextMessage();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mContainer.startAnimation(hide);
    }

    private void swipeAway(float deltaX) {
        cancelDelayedRunnable();

        Animation swipeAnimation = AnimationUtils.loadAnimation(getContext(), deltaX > 0 ? R.anim.snackbar_swipe_right : R.anim.snackbar_swipe_left);

        swipeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mContainer.setTranslationX(0);
                mContainer.setVisibility(GONE);

                handleNextMessage();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mContainer.startAnimation(swipeAnimation);
    }

    private void animateCanceledSwipe() {
        mContainer.animate()
                .translationX(0)
                .setDuration(getResources().getInteger(R.integer.snackbar_animation_duration))
                .start();
    }

    private void cancelDelayedRunnable() {
        removeCallbacks(mAnimateGoneRunnable);
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

    @Override
    protected Parcelable onSaveInstanceState() {
        SaveState saveState = new SaveState(super.onSaveInstanceState());

        if (isShowingMessage()) {
            mMessages.addFirst(mCurrentSnack);
        }

        saveState.messages = mMessages;

        return saveState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SaveState saveState = (SaveState) state;

        mMessages = saveState.messages;

        handleNextMessage();

        super.onRestoreInstanceState(saveState.getSuperState());
    }

    private class Vo {
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

    private void handleNextMessage() {
        mCurrentSnack = null;

        if (mMessages.size() > 0) {
            showMessage(mMessages.removeFirst());
        }
    }

    private class HorizontalSwipeListener implements OnTouchListener {

        private float mDownPosition;
        private float mDraggedDistance;
        private boolean mIsDragging;
        private float mTouchSlop;

        public HorizontalSwipeListener(Context context) {
            mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mDownPosition = event.getRawX();

                    return true;

                case MotionEvent.ACTION_MOVE:
                    mDraggedDistance = event.getRawX() - mDownPosition;

                    if (!mIsDragging && Math.abs(mDraggedDistance) > mTouchSlop) {
                        mIsDragging = true;
                    }

                    if (mIsDragging) {
                        mContainer.setTranslationX(mDraggedDistance - mTouchSlop);
                    }

                    return true;

                case MotionEvent.ACTION_UP:
                    if (!mIsDragging) {
                        //Treat as click
                        animateGone();
                    }

                    float deltaX = event.getRawX() - mDownPosition;

                    if (Math.abs(deltaX) >= SWIPE_AWAY_THRESHOLD) {
                        swipeAway(deltaX);
                    } else {
                        animateCanceledSwipe();
                    }

                    mDownPosition = 0;
                    mDraggedDistance = 0;

                    mIsDragging = false;

                    return true;
            }

            return false;
        }
    }

    private static class SaveState extends BaseSavedState {

        public LinkedList<Vo> messages;

        public SaveState(Parcel source) {
            super(source);

            messages = (LinkedList<Vo>) source.readSerializable();
        }

        public SaveState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);

            dest.writeSerializable(messages);
        }

        public static final Creator<SaveState> CREATOR = new Creator<SaveState>() {
            @Override
            public SaveState createFromParcel(Parcel source) {
                return new SaveState(source);
            }

            @Override
            public SaveState[] newArray(int size) {
                return new SaveState[size];
            }
        };
    }

}
