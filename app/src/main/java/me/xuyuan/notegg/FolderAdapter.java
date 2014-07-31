package me.xuyuan.notegg;

/**
 * Created by Yuan on 14-7-29.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dropbox.sync.android.DbxFileInfo;

import java.util.List;

/**
 * Adapts a {@code List<DbxFileInfo>} to be displayed in a listview.
 * Displays folders as disabled.
 */
class FolderAdapter extends BaseAdapter {
    private static final String LOG_TAG = FolderAdapter.class.getSimpleName();

    private final List<DbxFileInfo> mEntries;
    private final LayoutInflater mInflater;
    private final Context mContext;

    public FolderAdapter(Context context, List<DbxFileInfo> entries) {
        mEntries = entries;
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mEntries.size();
    }

    @Override
    public Object getItem(int position) {
        return mEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    @SuppressLint("InlinedApi")
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            int list_item_resid = (Build.VERSION.SDK_INT >= 11)
                    ? android.R.layout.simple_list_item_activated_1
                    : android.R.layout.simple_list_item_1;
            convertView = mInflater.inflate(list_item_resid, parent, false);
        }
        DbxFileInfo info = mEntries.get(position);
        TextView text = (TextView) convertView.findViewById(android.R.id.text1);
        text.setText(getName(info));

        return convertView;
    }

    private String getName(DbxFileInfo info) {
        return Util.stripExtension("txt", info.path.getName());
    }

}
