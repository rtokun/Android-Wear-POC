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
    private boolean mSendViaMail;
    private ProgressBar mProgressBar;

    public CSVExportTask(boolean sendViaMail, ProgressBar progressBar) {
        this.mSendViaMail = sendViaMail;
        this.mProgressBar = progressBar;
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
            publishProgress(0);
            FileWriter filewriter = new FileWriter(mExportFile);
            bw = new BufferedWriter(filewriter);

            // Read data
            // TODO: Extract to method
            Realm realm = Realm.getDefaultInstance();
            RealmResults<AccelerometerSample> result = realm.where(AccelerometerSample.class).findAll();
            final int numSamples = result.size();

            // Write the string to the file
            for (int i = 1; i < numSamples; i++) {
                final int progress = i;
                publishProgress((progress - i) /100);

                AccelerometerSample sample = result.get(i);
                StringBuffer sb = new StringBuffer();
                sb.append(sample.getTs());
                sb.append(" ,");
                sb.append(String.valueOf(sample.getX()));
                sb.append(" ,");
                sb.append(String.valueOf(sample.getY()));
                sb.append(" ,");
                sb.append(String.valueOf(sample.getZ()));
                sb.append(" ,");
                sb.append("\n");
                bw.write(sb.toString());
            }
            bw.flush();
            bw.close();
            Timber.e("CSV file saved to: %s", mExportFile.getAbsolutePath());
        } catch (IOException e) {
            Timber.e("Unable to write export file, error: %s", e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mProgressBar.setVisibility(View.GONE);
        if(mSendViaMail){
            //EmailSender.sendFileInEmail(mExportFile);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if(values[0] == 0) {
            mProgressBar.setMax(100);
            mProgressBar.setVisibility(View.VISIBLE);
        }
        mProgressBar.setProgress(values[0]);
    }
}
