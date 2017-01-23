package com.artyom.androidwearpoc.log;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.artyom.androidwearpoc.shared.DefaultConfiguration;
import com.artyom.androidwearpoc.shared.models.AccelerometerSampleData;
import com.artyom.androidwearpoc.shared.models.ChunkData;
import com.artyom.androidwearpoc.shared.models.SamplesChunk;
import com.artyom.androidwearpoc.util.WearSharedPrefsController;
import com.bytesizebit.androidutils.DateUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

import static com.artyom.androidwearpoc.shared.CommonConstants.CHUNKS_LOG_FILE_NAME;
import static com.artyom.androidwearpoc.shared.CommonConstants.SAMPLE_GAPS_LOG_FILE_NAME;

/**
 * Created by Artyom-IDEO on 16-Jan-17.
 */

public class MyWearLogger {

    private static final String DELIMETER = ",";

    private static final String CSV_EXTENSION = "csv";

    private static final String LOG_EXTENSION = "log";

    private Context appContext;

    private WearSharedPrefsController mSharedPrefsController;

    public MyWearLogger(Context appContext, WearSharedPrefsController sharedPrefsController) {
        this.appContext = appContext;
        this.mSharedPrefsController = sharedPrefsController;
    }

    public void writeToLogFile(String string) {
        LogChunkDataTask logChunkDataTask = new LogChunkDataTask();
        logChunkDataTask.execute(string);
    }

    public void logChunkDataToFile(SamplesChunk samplesChunk) {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd:HH:mm:ss:SSS", Locale
                .getDefault());

        int index = mSharedPrefsController.getChunkIndex();
        samplesChunk.setIndex(index);
        index++;
        mSharedPrefsController.setChunkIndex(index);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("chunk log time: ")
                .append(DateUtils.millisecondsToString(currentTime, simpleDateFormat))

                .append(" first sample at: ")
                .append(DateUtils.millisecondsToString(samplesChunk
                                .getAccelerometerSamples()
                                .get(0)
                                .getTimestamp()
                        , simpleDateFormat))

                .append(", last sample at: ")
                .append(DateUtils.millisecondsToString(samplesChunk
                                .getAccelerometerSamples()
                                .get(samplesChunk.getAccelerometerSamples().size() - 1)
                                .getTimestamp()
                        , simpleDateFormat))

                .append(", chunk index: ")
                .append(samplesChunk.getIndex())

                .append(", samples: ")
                .append(samplesChunk.getAccelerometerSamples().size())

                .append(", battery: ")
                .append(samplesChunk.getBatteryPercentage());

        writeToLogFile(stringBuilder.toString());
    }

    public void logSamples(SamplesChunk samplesChunk) {
        LogSamplesTask gapsTask = new LogSamplesTask();
        gapsTask.execute(samplesChunk);
    }

    private class LogChunkDataTask extends AsyncTask<String, Void, Void> {

        private File logsFile;

        public LogChunkDataTask() {
            logsFile = createFile(CHUNKS_LOG_FILE_NAME, LOG_EXTENSION);
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

    private class LogSamplesTask extends AsyncTask<SamplesChunk, Void, Void> {

        private File file;

        private SimpleDateFormat simpleDateFormat;

        public LogSamplesTask() {
            file = createFile(SAMPLE_GAPS_LOG_FILE_NAME, CSV_EXTENSION);
            simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault());
        }

        @Override
        protected Void doInBackground(SamplesChunk... params) {
            try {
                List<AccelerometerSampleData> samples = params[0].getAccelerometerSamples();

                AccelerometerSampleData prev = null;
                for (AccelerometerSampleData next : samples) {

                    // Here we are checking the timestamp difference between last sample in the
                    // previous chunk and first sample in the new arrived chunk. After the
                    // comparison we starting iterate on the samples inside new chunk.
                    if (prev == null) {
                        ChunkData lastChunkData = mSharedPrefsController.getLastChunk();
                        if (lastChunkData != null) {
                            writeChunksGapToFile(lastChunkData, next, params[0].getBatteryPercentage());
                        }
                        prev = next;
                        continue;
                    }

                    // Here we iterating on each sample in the new arrived chunk and comparing to
                    // previous sample. If the difference in milliseconds above the maximum
                    // difference allowed (configured in Configuration file) we log the
                    // difference to file.
                    writeSamplesGapToFile(prev, next, params[0].getBatteryPercentage());
                    prev = next;
                }
            } catch (Exception e) {
                Timber.e(e);
            }
            mSharedPrefsController.saveMessage(params[0]);
            return null;
        }

        private void writeChunksGapToFile(ChunkData lastChunkData,
                                          AccelerometerSampleData next,
                                          float batteryLevel) throws IOException {
            FileWriter filewriter = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(filewriter);
            String gapString = createChunksGapString(lastChunkData, next, batteryLevel);
            bw.write(gapString);
            bw.newLine();
            bw.close();
        }

        private String createChunksGapString(ChunkData lastChunkData,
                                             AccelerometerSampleData next,
                                             float batteryLevel) {
            return
                    //New sample time
                    DateUtils.millisecondsToString(next.getTimestamp(), simpleDateFormat) + DELIMETER
                            + next.getX() + DELIMETER
                            + next.getY() + DELIMETER
                            + next.getZ() + DELIMETER
                            + batteryLevel + DELIMETER
                            + (next.getTimestamp() - lastChunkData.getLastSampleTimestamp()) + DELIMETER
                            + "Yes";
        }

        private void writeSamplesGapToFile(AccelerometerSampleData prev,
                                           AccelerometerSampleData next,
                                           float batteryLevel)
                throws IOException {
            FileWriter filewriter = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(filewriter);
            String gapString = createGapString(prev, next, batteryLevel);
            bw.write(gapString);
            bw.newLine();
            bw.close();
        }

        private String createGapString(AccelerometerSampleData prev,
                                       AccelerometerSampleData next,
                                       float batteryLevel) {
            return
                    //New sample time

                    DateUtils.millisecondsToString(next.getTimestamp(), simpleDateFormat) + DELIMETER
                            + next.getX() + DELIMETER
                            + next.getY() + DELIMETER
                            + next.getZ() + DELIMETER
                            + batteryLevel + DELIMETER
                            + (next.getTimestamp() - prev.getTimestamp()) + DELIMETER;
        }

        private boolean compareSampleTimes(long prevTime, long nextTime) {
            return Math.abs(prevTime - nextTime) <= DefaultConfiguration.MAX_ALLOWED_SAMPLES_DIFF_IN_MILLIS;
        }

    }

    private File createFile(String name, String extension) {
        String date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        String fileName = name + "_" + date + "." + extension;

        File externalStorageDirectory = appContext.getExternalFilesDir(Environment.DIRECTORY_DCIM);
        File androidPoCDir = new File(externalStorageDirectory, "WearPoC");
        File logsDir = new File(androidPoCDir, "Logs");
        logsDir.mkdirs();

        File file = new File(logsDir, fileName);
        if (extension.equals(CSV_EXTENSION) && !file.exists()) {
            createTitles(file);
        }
        return file;
    }

    private void createTitles(File file) {
        String titles = "Sample time, X, Y, Z, Battery level, Gap in ms, Is chunk start";
        FileWriter filewriter = null;
        try {
            filewriter = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(filewriter);
            bw.write(titles);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            Timber.e(e);
        }
    }
}
