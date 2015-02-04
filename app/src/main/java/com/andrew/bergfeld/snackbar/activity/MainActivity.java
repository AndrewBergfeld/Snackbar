package com.andrew.bergfeld.snackbar.activity;

import android.app.Activity;
import android.os.Bundle;

import com.andrew.bergfeld.snackbar.R;
import com.andrew.bergfeld.snackbar.widget.Snackbar;

public class MainActivity extends Activity implements SnackbarProvider {

    private Snackbar mSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mSnackbar = (Snackbar) findViewById(R.id.snackbar);
    }

    @Override
    public Snackbar getSnackbar() {
        return mSnackbar;
    }

}
