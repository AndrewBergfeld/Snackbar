package com.andrew.bergfeld.snackbar;

import android.view.View;

import com.andrew.bergfeld.snackbar.widget.Snackbar;

import java.util.LinkedList;

public final class SnackbarManager implements Snackbar.SnackbarManager {

    private Snackbar mSnackbar;
    private LinkedList<Snackbar.SnackbarMessageVo> mMessages;

    public SnackbarManager(Snackbar snackbar) {
        mSnackbar = snackbar;
        mMessages = new LinkedList<Snackbar.SnackbarMessageVo>();

        mSnackbar.setSnackbarManager(this);
    }

    public Builder withMessage(String message) {
        return new Builder(message);
    }

    public class Builder {

        private Snackbar.SnackbarMessageVo mSnack;

        public Builder(String message) {
            mSnack = new Snackbar.SnackbarMessageVo();
            mSnack.message = message;
            mSnack.duration = Snackbar.Duration.SHORT;
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

        public Builder withListener(Snackbar.SnackListener listener) {
            mSnack.listener = listener;

            return this;
        }

        public void show() {
            if (mSnackbar.isShowingMessage()) {
                mMessages.add(mSnack);
            } else {
                mSnackbar.show(mSnack);
            }
        }

    }

    @Override
    public void onMessageDone() {
        if (mMessages.size() > 0) {
            mSnackbar.show(mMessages.removeFirst());
        }
    }
}
