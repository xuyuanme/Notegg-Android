package me.xuyuan.notegg;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import com.dropbox.sync.android.DbxPath;

public class NoteDetailActivity extends FragmentActivity {

    @TargetApi(11)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        if (Build.VERSION.SDK_INT >= 11) {
            // Moving this call into a helper class avoids crashes on DalvikVM in
            // API4, which will crash if a class contains a call to an unknown method,
            // even if it is never called.
            Api11Helper.setDisplayHomeAsUpEnabled(this, true);
        }

        if (savedInstanceState == null) {
            String path = getIntent().getStringExtra(NoteDetailFragment.PATH_KEY);
            NoteDetailFragment fragment = NoteDetailFragment.getInstance(new DbxPath(path));
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.note_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
//            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class Api11Helper {
        @TargetApi(11)
        public static void setDisplayHomeAsUpEnabled(NoteDetailActivity activity, boolean value) {
            activity.getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
