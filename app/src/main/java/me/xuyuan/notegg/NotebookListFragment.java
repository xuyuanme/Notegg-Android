package me.xuyuan.notegg;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.dropbox.sync.android.DbxPath;

/**
 * Created by Yuan on 14-8-4.
 */
public class NotebookListFragment extends FolderListFragment {
    public NotebookListFragment() {
    }

    public static NotebookListFragment newInstance() {
        NotebookListFragment fragment = new NotebookListFragment();
        Bundle args = new Bundle();
        args.putString(PATH_KEY, DbxPath.ROOT.toString());
        args.putBoolean(LIST_FOLDER_KEY, true);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.notebook_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}
