package flightpath.com.loginmodule;

import android.os.AsyncTask;
import android.util.Log;

import com.flightpathcore.utilities.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-04-20.
 */
public class Downloader extends AsyncTask<String, Integer, Void> {

    private DownloaderCallbacks callbacks;

    public Downloader(DownloaderCallbacks callbacks) {
        this.callbacks = callbacks;
        if(callbacks == null){
            throw new IllegalArgumentException("DownloaderCallbacks needed");
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            long total = 0;
            File file = new File(Utilities.getUpdateApkPath());
            output = new FileOutputStream(file, false);


            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK /*&& connection.getResponseCode() != HttpURLConnection.HTTP_PARTIAL*/) {
                Log.e("DOWNLOADER","Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
//                throw new Exception("Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
            }
            int fileLength = connection.getContentLength();
            if(total > 0){
                fileLength+=total;
                callbacks.onProgress((int) (total * 100 / fileLength));
            }

            input = connection.getInputStream();

            byte data[] = new byte[1024];
            int count;
            long currentTimestamp = System.currentTimeMillis();
            while ((count = input.read(data)) > 0) {
                if (isCancelled()) {
                    input.close();
                    return null;
                }

                total += count;
                if (fileLength > 0 && (currentTimestamp + 500) < System.currentTimeMillis() ) {
                    publishProgress((int) (total * 100 / fileLength));
                    currentTimestamp = System.currentTimeMillis();
                }
                output.write(data, 0, count);
            }

        } catch (Exception e) {
            if(e != null && e.getMessage() != null && !e.getMessage().isEmpty())
                Log.e("DOWNLOADER", e.getMessage());
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        callbacks.onCancel();
    }

    @Override
    protected void onCancelled(Void s) {
        super.onCancelled(s);
        callbacks.onCancel();
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        callbacks.onComplete();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        callbacks.onProgress(values[0]);
    }

    public interface DownloaderCallbacks{
        void onProgress(Integer downloadedPercent);
        void onComplete();
        void onCancel();
    }
}
