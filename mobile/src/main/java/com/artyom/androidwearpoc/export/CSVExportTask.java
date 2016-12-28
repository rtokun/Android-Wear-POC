package com.artyom.androidwearpoc.export;

import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;

import com.artyom.androidwearpoc.model.AccelerometerSample;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

/**
 * Created by tomerlev on 27/12/2016.
 */
public class CSVExportTask extends AsyncTask<Void,Integer,Void> {

    private File mExportFile;
    private ProgressBar mProgressBar;
    private Callback mCallback;

    public  interface Callback{
        void onSuccess(String exportFilePath);

        void onFailure(String message);

        void onNoData();
    }

    public CSVExportTask(ProgressBar progressBar, Callback callback) {
        this.mProgressBar = progressBar;
        this.mCallback = callback;
    }

    @Override
    protected void onPreExecute() {
        final String date = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(new Date());
        final String filename = String.format("%s_%s.csv", "export", date);

        final String directory = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/Android-Wear-POC";
        mExportFile = new File(directory, filename);
        final File logPath = mExportFile.getParentFile();

        if (!logPath.isDirectory() && !logPath.mkdirs()) {
            Timber.i("Could not create directory for export files");
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        BufferedWriter bw = null;
        try {

            FileWriter filewriter = new FileWriter(mExportFile);
            bw = new BufferedWriter(filewriter);

            // Read data
            // TODO: Extract to method
            Realm realm = Realm.getDefaultInstance();
            RealmResults<AccelerometerSample> result = realm.where(AccelerometerSample.class).findAll();
            final int numSamples = result.size();
            publishProgress(0,numSamples);
            if(numSamples == 0){
                mCallback.onNoData();
                return null;
            }

            // Write the string to the file
            for (int i = 1; i < numSamples; i++) {
                publishProgress(i);

                AccelerometerSample sample = result.get(i);
                StringBuffer sb = new StringBuffer();
                sb.append(sample.getTs());
                sb.append(" ,");
                sb.append(String.valueOf(sample.getX()));
                sb.append(" ,");
                sb.append(String.valueOf(sample.getY()));
                sb.append(" ,");
                sb.append(String.valueOf(sample.getZ()));
                sb.append("\n");
                bw.write(sb.toString());
            }
            bw.flush();
            bw.close();
            Timber.i("CSV file saved to: %s", mExportFile.getAbsolutePath());
            deleteDataFromDB(realm,result);
        } catch (IOException e) {
            Timber.e("Unable to write export file, error: %s", e.getMessage());
            mCallback.onFailure(e.getMessage());
        }
        return null;
    }

    private void deleteDataFromDB(Realm realm, RealmResults<AccelerometerSample> result){
        Timber.i("Deleting data from DB after export...");
        realm.beginTransaction();
        result.deleteAllFromRealm();
        realm.commitTransaction();
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        mProgressBar.setVisibility(View.GONE);
        mCallback.onSuccess(mExportFile.getAbsolutePath());
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if(values[0] == 0) {
            mProgressBar.setMax(values[1]);
            mProgressBar.setVisibility(View.VISIBLE);
        }
        mProgressBar.setProgress(values[0]);
    }
}
