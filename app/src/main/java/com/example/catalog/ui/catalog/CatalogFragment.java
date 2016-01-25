package com.example.catalog.ui.catalog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.catalog.R;
import com.example.catalog.provider.AppContract;
import com.example.catalog.service.ApiIntentService;
import com.example.catalog.ui.widget.ErrorDialogFragment;

/**
 * Fragment which shows the list of catalog items.
 */
public class CatalogFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    /** Filter state. */
    private static final String STATE_FILTER = "STATE_FILTER";
    /** Error dialog tag. */
    private static final String ERROR_DIALOG_FRAGMENT = "ERROR_DIALOG_FRAGMENT";
    /** Filter edit text. */
    private EditText mFilterView;
    /** Items list view. */
    private RecyclerView mItemsView;
    /** Items adapter. */
    private ItemsAdapter mAdapter;
    /** Service result receiver. */
    private BroadcastReceiver mResultReceiver;

    private String mFilter;

    /** Creates a new instance of this fragment. */
    public static CatalogFragment newInstance() {
        return new CatalogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mResultReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int result = intent.getExtras().getInt(ApiIntentService.EXTRA_RESULT);
                handleServiceResult(result);
            }
        };

        if (savedInstanceState != null) {
            mFilter = savedInstanceState.getString(STATE_FILTER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catalog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mItemsView = (RecyclerView) view.findViewById(R.id.catalog_items);
        mItemsView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
        mFilterView = (EditText) view.findViewById(R.id.catalog_filter);

        mFilterView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String filter = s.toString();
                mFilter = filter;
                startLoader();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mAdapter == null) {
            mAdapter = new ItemsAdapter(getContext().getApplicationContext());
        }
        mItemsView.setAdapter(mAdapter);

        startLoader();

        if (savedInstanceState == null) {
            startRequest();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mResultReceiver,
                new IntentFilter(ApiIntentService.ACTION_SERVICE_FINISHED));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mResultReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_FILTER, mFilter);
    }

    private void startRequest() {
        Intent intent = new Intent(getActivity(), ApiIntentService.class);
        getActivity().startService(intent);
    }

    /** Handles service results. */
    private void handleServiceResult(int result) {
        switch (result) {
            case ApiIntentService.RESULT_OK:
                break;
            case ApiIntentService.RESULT_NETWORK_FAIL:
                showErrorDialog(R.string.error_connection);
                break;
            case ApiIntentService.RESULT_APP_FAIL:
                showErrorDialog(R.string.error_app);
                break;
            case ApiIntentService.RESULT_SERVICE_FAIL:
                showErrorDialog(R.string.error_service);
                break;
        }
    }

    /**
     * Shows an error dialog.
     * It will remove any other previous error dialog.
     * @param resErrorMessage the resrouce of error messages.
     */
    private void showErrorDialog(int resErrorMessage) {
        ErrorDialogFragment errorDialog = ErrorDialogFragment.newInstance(
                resErrorMessage, false);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Remove old fragment.
        Fragment dialog = fragmentManager.findFragmentByTag(
                ERROR_DIALOG_FRAGMENT);
        if (dialog != null) {
            transaction.remove(dialog);
        }

        // Add new fragment.
        transaction.add(errorDialog, ERROR_DIALOG_FRAGMENT);
        transaction.commit();
    }

    private void startLoader() {
        LoaderManager loaderManager = getActivity().getSupportLoaderManager();
        if (loaderManager.getLoader(0) != null) {
            loaderManager.restartLoader(0, null, this);
        } else {
            loaderManager.initLoader(0, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = null;
        String[] selectionArgs = null;
        if (!TextUtils.isEmpty(mFilter)) {
            selection = AppContract.Items.NAME + " like ?";
            selectionArgs = new String[] { "%" + mFilter + "%" };
        }
        return new CursorLoader(getContext().getApplicationContext(),
                AppContract.Items.CONTENT_URI,
                AppContract.Items.DEFAULT_PROJECTION,
                selection, selectionArgs,
                AppContract.Items.DEFAULT_SORT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
