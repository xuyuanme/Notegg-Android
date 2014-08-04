package me.xuyuan.notegg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.dropbox.sync.android.DbxPath;


public class MainActivity extends ActionBarActivity implements FolderListFragment.OnFragmentInteractionListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx on create activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            String path = getIntent().getStringExtra(FolderListFragment.PATH_KEY);
            boolean listFolder = getIntent().getBooleanExtra(FolderListFragment.LIST_FOLDER_KEY, false);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.folder_list_container, NoteListFragment.newInstance(path != null ? new DbxPath(path) : null, listFolder))
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG, "on activity result " + requestCode + " " + requestCode);
        // Must call super method if you want to override it. otherwise Fragment's onActivityResult() won't be called.
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFragmentInteraction(DbxPath path) {
        Intent detailIntent = new Intent(this, NoteDetailActivity.class);
        detailIntent.putExtra(NoteDetailFragment.PATH_KEY, path.toString());
        startActivity(detailIntent);
    }

}
