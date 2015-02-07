package com.andrew.bergfeld.snackbar.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.andrew.bergfeld.snackbar.R;
import com.andrew.bergfeld.snackbar.activity.SnackbarProvider;
import com.andrew.bergfeld.snackbar.adaper.ExampleAdapter;

public class ExampleList extends ListView {

    private ActionMode mActionMode;

    public ExampleList(Context context) {
        super(context);

        initialize();
    }

    public ExampleList(Context context, AttributeSet attrs) {
        super(context, attrs);

        initialize();
    }

    public ExampleList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initialize();
    }

    private void initialize() {
        setupList();
    }

    private Snackbar getSnackbar() {
        return ((SnackbarProvider) getContext()).getSnackbar();
    }

    private void setupList() {

        setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode != null) {
                    //Mark an item as selected
                } else {
                    getSnackbar().withMessage("List Item " + position + " Clicked!")
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

        setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode == null) {
                    mActionMode = startActionMode(mActionCallback);
                }

                return false;
            }
        });

        setAdapter(new ExampleAdapter(getContext()));
    }

    private void deleteSelectedListItems() {
        //Delete items

        mActionMode.finish();
        mActionMode = null;

        getSnackbar().withMessage("Items deleted. This text is pretty long isn't it. It should only wrap to two lines though.")
                .withDuration(Snackbar.Duration.LONG)
                .withBackgroundColor(android.R.color.holo_green_light)
                .show();
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

}
