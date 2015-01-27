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
import com.andrew.bergfeld.snackbar.SnackbarManager;
import com.andrew.bergfeld.snackbar.adaper.DummyAdapter;
import com.andrew.bergfeld.snackbar.widget.Snackbar;

public class MainActivity extends Activity {

    private ActionMode mActionMode;
    private ListView mList;
    private SnackbarManager mSnackBarManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mSnackBarManager = new SnackbarManager((Snackbar) findViewById(R.id.snackbar));

        setupList();
    }

    public SnackbarManager getSnackBarManager() {
        //I'm still up in the air on how to handle accessing the manager.
        //Having the manager class hold onto a view statically for SnackBarManager.withMessage()
        //isn't that appealing because of holding onto a reference of that view
        //I think the answer is either figuring how to inject one and if that falls through
        //then just this implementation would probably be better.
        return mSnackBarManager;
    }

    private void setupList() {
        mList = (ListView) findViewById(R.id.list);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode != null) {
                    mList.setSelection(position);
                } else {
                    getSnackBarManager().withMessage("List Item Clicked!")
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

        getSnackBarManager().withMessage("Items Deleted Homie")
                            .withDuration(Snackbar.Duration.LONG)
                            .withListener(new Snackbar.SnackListener() {
                                @Override
                                public void onShowMessage() {
                                    //Begin animating things so that snackbar doesn't overlap Floating Action Buttons, etc
                                }

                                @Override
                                public void onMessageShown() {
                                    //Message is visible
                                }

                                @Override
                                public void onHideMessage() {
                                    //Begin animating things back down because snackbar is going away
                                }

                                @Override
                                public void onMessageDone() {
                                    //Message is completely gone
                                }
                            })
                .show();
    }
}
