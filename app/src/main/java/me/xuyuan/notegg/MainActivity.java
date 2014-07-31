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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, FolderListFragment.newInstance(mPath, mListFolder))
//                    .commit();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        getMenuInflater().inflate(R.menu.notebooklist, menu);
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
            startActivityForResult(intent, 1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG, "selected folder " + data.getStringExtra(FolderListFragment.PATH_KEY));
        if (resultCode == RESULT_OK) {
            FolderListFragment folderListFragment = (FolderListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_folder_list);
            folderListFragment.doLoad(new DbxPath(data.getStringExtra(FolderListFragment.PATH_KEY)), false, true);
        }
    }

    @Override
    public void onFragmentInteraction(DbxPath path) {
        Log.d(LOG_TAG, path.toString());
    }

}
