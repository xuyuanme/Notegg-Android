package me.xuyuan.notegg;

import android.content.Context;

import com.dropbox.sync.android.DbxAccountManager;

/**
 * Created by Yuan on 14-7-29.
 */
public class Util {
    private static final String appKey = "w7hk0g1c2pnqs8g";
    private static final String appSecret = "otz05jdtj42mp83";

    public static DbxAccountManager getAccountManager(Context context) {
        return DbxAccountManager.getInstance(context.getApplicationContext(), appKey, appSecret);
    }

    static String stripExtension(String extension, String filename) {
        extension = "." + extension;
        if (filename.endsWith(extension)) {
            return filename.substring(0, filename.length() - extension.length());
        }
        return filename;
    }
}
