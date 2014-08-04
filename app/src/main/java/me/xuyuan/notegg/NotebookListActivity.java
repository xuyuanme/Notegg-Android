package me.xuyuan.notegg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;

import com.dropbox.sync.android.DbxPath;

public class NotebookListActivity extends ActionBarActivity implements FolderListFragment.OnFragmentInteractionListener {
    private static final String LOG_TAG = NotebookListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx on create activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.folder_list_container, NotebookListFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Updated 8/4/14: handle Home/Up button manually to keep background activity unchanged.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(DbxPath path) {
        Intent intent = new Intent();
        intent.putExtra(FolderListFragment.PATH_KEY, path.toString());
        setResult(RESULT_OK, intent);
        finish();
    }

}
