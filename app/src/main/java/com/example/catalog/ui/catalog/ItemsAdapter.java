package com.example.catalog.ui.catalog;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.catalog.R;
import com.example.catalog.provider.AppContract;
import com.example.catalog.ui.widget.RecyclerCursorAdapter;
import com.example.catalog.utils.ImageLoaderUtils;

/**
 * Adapter for catalog items.
 */
public class ItemsAdapter extends RecyclerCursorAdapter {

    /**
     * Constructor.
     *
     * @param context the context.
     */
    public ItemsAdapter(Context context) {
        super(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.catalog_item, parent, false);
        ViewHolder holder = new ViewHolder();
        holder.name = (TextView) view.findViewById(R.id.catalog_item_name);
        holder.image = (ImageView) view.findViewById(R.id.catalog_item_image);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndex(AppContract.Items.NAME));
        String imageUrl = cursor.getString(cursor.getColumnIndex(AppContract.Items.IMAGE_URL));

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(name);
        ImageLoaderUtils.displayImage(imageUrl, holder.image);
    }

    /** View holder. */
    private static class ViewHolder {
        TextView name;
        ImageView image;
    }
}
