package com.andrew.bergfeld.snackbar.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.andrew.bergfeld.snackbar.R;
import com.andrew.bergfeld.snackbar.adaper.DummyAdapter;
import com.andrew.bergfeld.snackbar.widget.Snackbar;

public class MainActivity extends Activity {

    private ActionMode mActionMode;
    private ListView mList;
    private Snackbar mSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mSnackbar = (Snackbar) findViewById(R.id.snackbar);

        setupList();
    }

    public Snackbar getSnackbar() {
        return mSnackbar;
    }

    private void setupList() {
        mList = (ListView) findViewById(R.id.list);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode != null) {
                    mList.setSelection(position);
                } else {
                    getSnackbar().withMessage("List Item Clicked!")
                            .withAction("Ok!", new Snackbar.ActionListener() {
                                @Override
                                public void onActionClicked() {
                                    //Do Stuff here
                                }
                            })
                            .withDuration(Snackbar.Duration.LONG)
                            .show();
                }
            }
        });

        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode == null) {
                    mActionMode = startActionMode(mActionCallback);
                }

                return false;
            }
        });

        mList.setAdapter(new DummyAdapter(this));
    }

    private ActionMode.Callback mActionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.cab_main_activity, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.delete) {
                deleteSelectedListItems();
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    private void deleteSelectedListItems() {
        //Fake Deletion
        mActionMode.finish();
        mActionMode = null;

        getSnackbar().withMessage("Items deleted home skillet. This message is longer than normal to wrap to two lines. Hopefully.")
                .withDuration(Snackbar.Duration.LONG)
                .withBackgroundColor(android.R.color.holo_green_light)
                .show();
    }
}
