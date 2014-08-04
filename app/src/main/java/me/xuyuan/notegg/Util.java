package me.xuyuan.notegg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

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

    public static AlertDialog getPromptAlert(Context context, View promptView, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }
                );

        // return alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        return alertDialog;
    }

    static String stripExtension(String extension, String filename) {
        extension = "." + extension;
        if (filename.endsWith(extension)) {
            return filename.substring(0, filename.length() - extension.length());
        }
        return filename;
    }
}
