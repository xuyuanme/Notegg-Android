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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

    private DbxPath mPath;
    private boolean mListFolder;

    private OnFragmentInteractionListener mListener;
    private View mLinkButton;
    private View mEmptyText;
    private View mLoadingSpinner;
    private DbxAccountManager mAccountManager;
    private DbxFileSystem mfileSystem;

    public static FolderListFragment newInstance(DbxPath path, boolean listFolder) {
        FolderListFragment fragment = new FolderListFragment();
        Bundle args = new Bundle();
        if (null != path) {
            args.putString(PATH_KEY, path.toString());
        }
        args.putBoolean(LIST_FOLDER_KEY, listFolder);
        fragment.setArguments(args);
        return fragment;
    }

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

        try {
            mfileSystem = DbxFileSystem.forAccount(mAccountManager.getLinkedAccount());
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
                    mPath = DbxPath.ROOT.getChild("Main");
                    mfileSystem.createFolder(mPath);
                }
            }
        } catch (DbxException.Unauthorized unauthorized) {
            unauthorized.printStackTrace();
        } catch (DbxException e) {
            e.printStackTrace();
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
                mAccountManager.startLinkFromSupportFragment(FolderListFragment.this, 0);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                // We are now linked.
                Log.d(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx link account successful, show linked view, and do load");
                showLinkedView();
                doLoad(mPath, mListFolder, true);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
    }

    @Override
    public void onDetach() {
        Log.d(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx on detach");
        super.onDetach();
        mListener = null;
        mAccountManager = null;
        mPath = null;
        mListFolder = false;
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
    }

    private void showLinkedView() {
        getListView().setVisibility(View.VISIBLE);
        mLinkButton.setVisibility(View.GONE);
    }

    public void doLoad(DbxPath path, boolean listFolder, boolean reset) {
        mPath = path;
        mListFolder = listFolder;
        mLoadingSpinner.setVisibility(View.VISIBLE);
        if (mAccountManager.hasLinkedAccount()) {
            Bundle args = new Bundle();
            args.putString(PATH_KEY, path.toString());
            args.putBoolean(LIST_FOLDER_KEY, listFolder);
            if (reset) {
                Log.d(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx restart loader");
                getLoaderManager().restartLoader(0, args, this);
            } else {
                Log.d(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx initial loader");
                getLoaderManager().initLoader(0, args, this);
            }
        }
    }

}
