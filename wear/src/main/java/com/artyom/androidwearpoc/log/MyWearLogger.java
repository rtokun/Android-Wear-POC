package com.artyom.androidwearpoc.log;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.artyom.androidwearpoc.MyWearApplication;
import com.artyom.androidwearpoc.shared.models.MessagePackage;
import com.bytesizebit.androidutils.DateUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by Artyom-IDEO on 16-Jan-17.
 */

public class MyWearLogger {

    private Context appContext;

    public MyWearLogger(Context appContext) {
        this.appContext = appContext;
    }

    public void writeToLogFile(String string) {
        LogTask logTask = new LogTask();
        logTask.execute(string);
    }

    public boolean deleteLogs(){
        File logsFile = createFile();
        return logsFile.delete();
    }

    public void logChunkToFile(MessagePackage messagePackage) {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd:HH:mm:ss:SSS", Locale
                .getDefault());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("chunk received at: ")
                .append(DateUtils.millisecondsToString(currentTime, simpleDateFormat))

                .append(" first sample at: ")
                .append(DateUtils.millisecondsToString(messagePackage
                                .getAccelerometerSamples()
                                .get(0)
                                .getTimestamp()
                        , simpleDateFormat))

                .append(", last sample at: ")
                .append(DateUtils.millisecondsToString(messagePackage
                                .getAccelerometerSamples()
                                .get(messagePackage.getAccelerometerSamples().size() - 1)
                                .getTimestamp()
                        , simpleDateFormat))

                .append(", chunk index: ")
                .append(messagePackage.getIndex())

                .append(", samples: ")
                .append(messagePackage.getAccelerometerSamples().size())

                .append(", battery: ")
                .append(messagePackage.getBatteryPercentage());

        writeToLogFile(stringBuilder.toString());
    }


    private class LogTask extends AsyncTask<String, Void, Void> {

        private File logsFile;

        public LogTask() {
            logsFile = createFile();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                FileWriter filewriter = new FileWriter(logsFile, true);
                BufferedWriter bw = new BufferedWriter(filewriter);
                bw.write(params[0]);
                bw.newLine();
                bw.close();
            } catch (Exception e) {
                Timber.e(e);
            }
            return null;
        }

    }

    private File createFile() {
        String date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        String filename = String.format("%s_%s.log", "logs", date);

        File externalStorageDirectory = appContext.getExternalFilesDir(Environment.DIRECTORY_DCIM);
        File androidPoCDir = new File(externalStorageDirectory, "WearPoC");
        File logsDir = new File(androidPoCDir, "Logs");
        logsDir.mkdirs();

        return new File(logsDir, filename);
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


}
