package me.xuyuan.notegg;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dropbox.sync.android.DbxPath;

public class NotebookListActivity extends MainActivity {
    private static final String LOG_TAG = NotebookListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.mPath = DbxPath.ROOT;
        this.mListFolder = true;
        super.onCreate(savedInstanceState);
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
