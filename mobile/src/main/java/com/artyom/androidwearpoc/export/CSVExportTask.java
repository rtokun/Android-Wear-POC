package com.artyom.androidwearpoc.export;

import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;

import com.artyom.androidwearpoc.model.AccelerometerSampleTEMPORAL;

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
public class CSVExportTask extends AsyncTask<Void, Integer, Boolean> {

    private File mExportFile;

    private ProgressBar mProgressBar;

    private Callback mCallback;

    public interface Callback {

        void onSuccess(File exportFile);

        void onFailure(String message);

        void onNoData();
    }

    public CSVExportTask(ProgressBar progressBar, Callback callback) {
        this.mProgressBar = progressBar;
        this.mCallback = callback;
    }


    protected void createFile() {
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
    protected Boolean doInBackground(Void... voids) {
        BufferedWriter bw = null;
        try {

            // Read data
            // TODO: Extract to method
            Realm realm = Realm.getDefaultInstance();
            //TODO revert back to - RealmResults<AccelerometerSample> result = realm.where
            // (AccelerometerSample.class).findAll();
            RealmResults<AccelerometerSampleTEMPORAL> result = realm.where(AccelerometerSampleTEMPORAL.class)
                    .findAll();

            final int numSamples = result.size();

            if (numSamples == 0) {
                mCallback.onNoData();
                realm.close();
                return false;
            }

            createFile();

            FileWriter filewriter = new FileWriter(mExportFile);
            bw = new BufferedWriter(filewriter);

            publishProgress(0, numSamples);

            AccelerometerSampleTEMPORAL previousSample = null;

            StringBuilder sb = new StringBuilder();
            sb.append("date, timestamp, diff, x, y, z, package index\n");

            // Write the string to the file
            for (int i = 1; i < numSamples; i++) {
                publishProgress(i);

                //TODO: revert back to - AccelerometerSample sample = result.get(i);
                AccelerometerSampleTEMPORAL sample = result.get(i);
                String timeStampDifference = calculateDiff(previousSample, sample);

                sb.append(sample.getDs())
                        .append(" ,")

                        .append(sample.getTs())
                        .append(" ,")

                        .append(timeStampDifference)
                        .append(" ,")

                        .append(String.valueOf(sample.getX()))
                        .append(" ,")

                        .append(String.valueOf(sample.getY()))
                        .append(" ,")

                        .append(String.valueOf(sample.getZ()))
                        //TODO: remove 2 lines below
                        .append(" ,")
                        .append(String.valueOf(sample.getMessageIndex()))
                        //end
                        .append("\n");

                previousSample = sample;
            }
            bw.write(sb.toString());
            bw.flush();
            bw.close();
            realm.close();
            Timber.i("CSV file saved to: %s", mExportFile.getAbsolutePath());
        } catch (IOException e) {
            Timber.e("Unable to write export file, error: %s", e.getMessage());
            mCallback.onFailure(e.getMessage());
        }

        return true;
    }

    private String calculateDiff(AccelerometerSampleTEMPORAL previousSample, AccelerometerSampleTEMPORAL newSample) {
        String diff = "null";
        if (previousSample != null && newSample != null) {
            diff = String.valueOf(newSample.getTs() - previousSample.getTs());
        }
        return diff;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        mProgressBar.setVisibility(View.GONE);
        if (success && mExportFile != null) {
            mCallback.onSuccess(mExportFile);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (values[0] == 0) {
            mProgressBar.setMax(values[1]);
            mProgressBar.setVisibility(View.VISIBLE);
        }
        mProgressBar.setProgress(values[0]);
    }
}
