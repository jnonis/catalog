package com.example.catalog.ui.widget;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Implementation of RecyclerView.Adapter which wrap a CursorAdapter.
 */
public abstract class RecyclerCursorAdapter extends
        RecyclerView.Adapter<RecyclerCursorAdapter.ViewHolder> {
    /** Context. */
    private Context mContext;
    /** Wrapped cursor adapter. */
    private CursorAdapter mCursorAdapter;

    /**
     * Constructor.
     *
     * @param context the context.
     */
    public RecyclerCursorAdapter(Context context) {
        mContext = context;
        mCursorAdapter = new CursorAdapter(context, null, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return RecyclerCursorAdapter.this.newView(context, cursor, parent);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                RecyclerCursorAdapter.this.bindView(view, context, cursor);
            }
        };
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursorAdapter.getCursor().moveToPosition(position);
        mCursorAdapter.bindView(holder.itemView, mContext, mCursorAdapter.getCursor());
    }

    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }

    /**
     * @see CursorAdapter#swapCursor(Cursor)
     */
    public Cursor swapCursor(Cursor newCursor) {
        Cursor oldCursor = mCursorAdapter.swapCursor(newCursor);
        notifyDataSetChanged();
        return oldCursor;
    }

    /**
     * @see CursorAdapter#newView(Context, Cursor, ViewGroup)
     */
    public abstract View newView(Context context, Cursor cursor, ViewGroup parent);

    /**
     * @see CursorAdapter#bindView(View, Context, Cursor)
     */
    public abstract void bindView(View view, Context context, Cursor cursor);

    /** View holder. */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
