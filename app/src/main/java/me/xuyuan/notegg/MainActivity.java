package me.xuyuan.notegg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.dropbox.sync.android.DbxPath;


public class MainActivity extends ActionBarActivity implements FolderListFragment.OnFragmentInteractionListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int NOTEBOOK_LIST_INTENT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx on create activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            String path = getIntent().getStringExtra(FolderListFragment.PATH_KEY);
            boolean listFolder = getIntent().getBooleanExtra(FolderListFragment.LIST_FOLDER_KEY, false);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.folder_list_container, FolderListFragment.newInstance(path != null ? new DbxPath(path) : null, listFolder))
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_notebooks_list) {
            Intent intent = new Intent(this, NotebookListActivity.class);
            intent.putExtra(FolderListFragment.PATH_KEY, "/");
            intent.putExtra(FolderListFragment.LIST_FOLDER_KEY, true);
            startActivityForResult(intent, NOTEBOOK_LIST_INTENT);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NOTEBOOK_LIST_INTENT) {
            if (resultCode == RESULT_OK) {
                Log.d(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx selected folder " + data.getStringExtra(FolderListFragment.PATH_KEY));
                FolderListFragment folderListFragment = (FolderListFragment) getSupportFragmentManager().findFragmentById(R.id.folder_list_container);
                folderListFragment.doLoad(new DbxPath(data.getStringExtra(FolderListFragment.PATH_KEY)), false, true);
            }
        }
    }

    @Override
    public void onFragmentInteraction(DbxPath path) {
        Intent detailIntent = new Intent(this, NoteDetailActivity.class);
        detailIntent.putExtra(NoteDetailFragment.PATH_KEY, path.toString());
        startActivity(detailIntent);
    }

}
