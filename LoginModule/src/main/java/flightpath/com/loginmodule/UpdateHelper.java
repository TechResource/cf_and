package flightpath.com.loginmodule;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

import com.flightpathcore.objects.UpdateAppObject;
import com.flightpathcore.utilities.Utilities;

import java.io.File;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-21.
 */
public class UpdateHelper {

    private Context context;
    private UpdateAppObject updateAppObject;
    private ProgressDialog progressDialog;
    private DialogInterface.OnClickListener negativeListener;

    public UpdateHelper(Context context, UpdateAppObject updateAppObject, DialogInterface.OnClickListener negativeListener){
        this.context = context;
        this.negativeListener = negativeListener;
        this.updateAppObject = updateAppObject;
    }

    public void askUpdateApp(){
        Utilities.styleAlertDialog(new AlertDialog.Builder(context, R.style.BlueAlertDialog)
                .setTitle(updateAppObject.title)
                .setMessage("Do you want to upgrade app to that version?\nNew in this version:\n"+updateAppObject.whatsnew)
                .setPositiveButton(R.string.yes_label, (dialog1, which1) -> downloadAndInstall(updateAppObject.url, context))
                .setNegativeButton(R.string.no_label, negativeListener)
                .setCancelable(false)
                .show());
    }

    private void downloadAndInstall(String apkUrl, Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(R.string.update_label);
        progressDialog.setMessage(context.getString(R.string.update_app_message));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Downloader downloader = new Downloader(downloaderCallbacks);
        downloader.execute(apkUrl);
    }

    private Downloader.DownloaderCallbacks downloaderCallbacks = new Downloader.DownloaderCallbacks() {
        @Override
        public void onProgress(Integer downloadedPercent) {
            progressDialog.setProgress(downloadedPercent);
        }

        @Override
        public void onComplete() {
            progressDialog.dismiss();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(Utilities.getUpdateApkPath())), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        @Override
        public void onCancel() {
            progressDialog.dismiss();
        }
    };
}
