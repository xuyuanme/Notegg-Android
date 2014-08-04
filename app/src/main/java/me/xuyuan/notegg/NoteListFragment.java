package me.xuyuan.notegg;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.dropbox.sync.android.DbxPath;

/**
 * Created by Yuan on 14-8-4.
 */
public class NoteListFragment extends FolderListFragment {
    public NoteListFragment() {
    }

    public static NoteListFragment newInstance(DbxPath path, boolean listFolder) {
        NoteListFragment fragment = new NoteListFragment();
        Bundle args = new Bundle();
        if (null != path) {
            args.putString(PATH_KEY, path.toString());
        }
        args.putBoolean(LIST_FOLDER_KEY, listFolder);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.note_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}
