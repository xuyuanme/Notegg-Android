package me.xuyuan.notegg;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import me.xuyuan.notegg.data.FolderListComparator;
import me.xuyuan.notegg.data.FolderLoader;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class FolderListFragment extends ListFragment implements LoaderCallbacks<List<DbxFileInfo>> {
    private static final String LOG_TAG = FolderListFragment.class.getSimpleName();

    public static final String PATH_KEY = "path_key";
    public static final String LIST_FOLDER_KEY = "list_folder_key";

    private static final int REQUEST_LINK_TO_DBX_INTENT = 100;
    private static final int NOTEBOOK_LIST_INTENT = 101;

    private DbxPath mPath;
    private boolean mListFolder;

    private OnFragmentInteractionListener mListener;
    private View mLinkButton;
    private View mEmptyText;
    private View mLoadingSpinner;
    private MenuItem mAddNoteMenuItem;
    private MenuItem mSwitchNotebookMenuItem;
    private MenuItem mAddNotebookMenuItem;
    private boolean mShowMenuItems;
    private DbxAccountManager mAccountManager;
    private DbxFileSystem mfileSystem;
    private List<DbxFileInfo> mFileInfoList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FolderListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx on create");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            if (null != getArguments().getString(PATH_KEY)) {
                mPath = new DbxPath(getArguments().getString(PATH_KEY));
            }
            mListFolder = getArguments().getBoolean(LIST_FOLDER_KEY);
        }
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx on fragment start and visible, do load");
        super.onStart();
        doLoad(mPath, mListFolder, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx on create view");
        View view = inflater.inflate(R.layout.fragment_folder_list, container, false);

        mLinkButton = view.findViewById(R.id.link_button);
        mLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAccountManager.startLinkFromSupportFragment(FolderListFragment.this, REQUEST_LINK_TO_DBX_INTENT);
            }
        });
        mEmptyText = view.findViewById(R.id.empty_text);
        mLoadingSpinner = view.findViewById(R.id.list_loading);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setEmptyView(view.findViewById(android.R.id.empty));
        if (mAccountManager.hasLinkedAccount()) {
            Log.d(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx on view created, show linked view");
            showLinkedView();
        } else {
            showUnlinkedView();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx on activity created");
        super.onActivityCreated(savedInstanceState);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                removeItemFromList(position);
                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG, "on activity result " + requestCode + " " + requestCode);
        if (requestCode == REQUEST_LINK_TO_DBX_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                // We are now linked.
                Log.d(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx link account successful, show linked view, and do load");
                try {
                    mfileSystem = DbxFileSystem.forAccount(mAccountManager.getLinkedAccount());
                } catch (DbxException.Unauthorized unauthorized) {
                    unauthorized.printStackTrace();
                }
                showLinkedView();
                if (null != mAddNoteMenuItem) mAddNoteMenuItem.setVisible(mShowMenuItems);
                if (null != mAddNotebookMenuItem) mAddNotebookMenuItem.setVisible(mShowMenuItems);
                if (null != mSwitchNotebookMenuItem)
                    mSwitchNotebookMenuItem.setVisible(mShowMenuItems);
                doLoad(mPath, mListFolder, true);
            }
        }
        if (requestCode == NOTEBOOK_LIST_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx selected folder " + data.getStringExtra(FolderListFragment.PATH_KEY));
                doLoad(new DbxPath(data.getStringExtra(FolderListFragment.PATH_KEY)), false, true);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mAddNoteMenuItem = menu.findItem(R.id.action_add_note);
        mAddNotebookMenuItem = menu.findItem(R.id.action_add_notebook);
        mSwitchNotebookMenuItem = menu.findItem(R.id.action_switch_notebook);
        if (null != mAddNoteMenuItem) mAddNoteMenuItem.setVisible(mShowMenuItems);
        if (null != mAddNotebookMenuItem) mAddNotebookMenuItem.setVisible(mShowMenuItems);
        if (null != mSwitchNotebookMenuItem) mSwitchNotebookMenuItem.setVisible(mShowMenuItems);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptView = li.inflate(R.layout.prompt_alert, null);
        TextView hint = (TextView) promptView.findViewById(R.id.editTextDialogHint);
        final EditText userInput = (EditText) promptView.findViewById(R.id.editTextDialogUserInput);
        if (id == R.id.action_add_note) {
            hint.setText(R.string.message_alert_add_note);
            AlertDialog alertDialog = Util.getPromptAlert(getActivity(), promptView, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        DbxFile file = mfileSystem.create(mPath.getChild(userInput.getText().toString() + ".txt"));
                        file.close();
                        file = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            alertDialog.show();
        }
        if (id == R.id.action_add_notebook) {
            hint.setText(R.string.message_alert_add_notebook);
            AlertDialog alertDialog = Util.getPromptAlert(getActivity(), promptView, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        mfileSystem.createFolder(DbxPath.ROOT.getChild(userInput.getText().toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            alertDialog.show();
        }
        if (id == R.id.action_switch_notebook) {
            Intent intent = new Intent(getActivity(), NotebookListActivity.class);
            startActivityForResult(intent, NOTEBOOK_LIST_INTENT);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx on attach");
        super.onAttach(activity);

        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        mAccountManager = Util.getAccountManager(activity);
        if (mAccountManager.hasLinkedAccount()) {
            try {
                mfileSystem = DbxFileSystem.forAccount(mAccountManager.getLinkedAccount());
            } catch (DbxException.Unauthorized unauthorized) {
                unauthorized.printStackTrace();
            }
        }
    }

    @Override
    public void onDetach() {
        Log.d(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx on detach");
        super.onDetach();
        mListener = null;
        mAccountManager = null;
        mPath = null;
        mListFolder = false;
        mfileSystem = null;
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx on resume");
        super.onResume();
        if (mPath != null && !mPath.getName().equalsIgnoreCase("")) {
            getActivity().setTitle(mPath.getName());
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(((DbxFileInfo) getListAdapter().getItem(position)).path);
        }
    }

    /**
     * Implement LoaderCallbacks<List<DbxFileInfo>> methods
     */

    @Override
    public Loader<List<DbxFileInfo>> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx on create loader " + id + ", return loader for path " + args.getString(PATH_KEY));
        return new FolderLoader(getActivity(), mAccountManager, new DbxPath(args.getString(PATH_KEY)), args.getBoolean(LIST_FOLDER_KEY));
    }

    @Override
    public void onLoadFinished(Loader<List<DbxFileInfo>> loader, List<DbxFileInfo> data) {
        Log.d(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx on load finished, set list adapter to " + FolderAdapter.class.getSimpleName());
        mEmptyText.setVisibility(View.VISIBLE);
        mLoadingSpinner.setVisibility(View.GONE);
        mFileInfoList = data;
        setListAdapter(new FolderAdapter(getActivity(), data));
    }

    @Override
    public void onLoaderReset(Loader<List<DbxFileInfo>> loader) {
        Log.d(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx on loader reset");
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(DbxPath path);
    }


    private void showUnlinkedView() {
        getListView().setVisibility(View.GONE);
        mLinkButton.setVisibility(View.VISIBLE);
        mEmptyText.setVisibility(View.GONE);
        mLoadingSpinner.setVisibility(View.GONE);
        mShowMenuItems = false;
    }

    private void showLinkedView() {
        getListView().setVisibility(View.VISIBLE);
        mLinkButton.setVisibility(View.GONE);
        mShowMenuItems = true;
    }

    public void doLoad(DbxPath path, boolean listFolder, boolean reset) {
        if (!mAccountManager.hasLinkedAccount()) return;
        mPath = path;
        mListFolder = listFolder;
        mLoadingSpinner.setVisibility(View.VISIBLE);

        // Verify mPath, if it's invalid, assign one or create one.
        try {
            if (null == mPath || !mfileSystem.isFolder(mPath)) {
                Log.d(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx invalid path " + mPath + ", use the 1st folder in Root instead");
                List<DbxFileInfo> entries = mfileSystem.listFolder(DbxPath.ROOT);
                Iterator<DbxFileInfo> iterator = entries.iterator();
                while (iterator.hasNext()) {
                    DbxFileInfo fileInfo = iterator.next();
                    if (mListFolder) {
                        if (!fileInfo.isFolder) {
                            iterator.remove();
                        }
                    }
                }
                Collections.sort(entries, FolderListComparator.getNameFirst(true));
                if (entries.size() > 0) {
                    mPath = entries.get(0).path;
                } else {
                    Log.d(LOG_TAG, "create the /Main folder");
                    mPath = DbxPath.ROOT.getChild("Main");
                    mfileSystem.createFolder(mPath);
                }
            }
        } catch (DbxException e) {
            e.printStackTrace();
        }

        Bundle args = new Bundle();
        args.putString(PATH_KEY, mPath.toString());
        args.putBoolean(LIST_FOLDER_KEY, mListFolder);
        if (reset) {
            Log.d(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx restart loader");
            getLoaderManager().restartLoader(0, args, this);
        } else {
            Log.d(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx initial loader");
            getLoaderManager().initLoader(0, args, this);
        }
    }

    private void removeItemFromList(final int position) {
        final DbxPath path = ((DbxFileInfo) mFileInfoList.get(position)).path;

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle("WARNING");
        alert.setMessage("Delete " + Util.stripExtension("txt", path.getName()) + "?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Log.d(LOG_TAG, "Deleting " + path.toString());
                    mfileSystem.delete(path);
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

}
