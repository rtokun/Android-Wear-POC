package com.artyom.androidwearpoc.report;

import android.os.AsyncTask;
import android.os.Environment;

import com.artyom.androidwearpoc.shared.models.ChunkData;
import com.artyom.androidwearpoc.shared.DefaultConfiguration;
import com.artyom.androidwearpoc.shared.models.AccelerometerSampleData;
import com.artyom.androidwearpoc.shared.models.SamplesChunk;
import com.artyom.androidwearpoc.util.SharedPrefsController;
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

public class MyLogger {

    private SharedPrefsController mSharedPrefsController;

    public MyLogger(SharedPrefsController mSharedPrefsController) {
        this.mSharedPrefsController = mSharedPrefsController;
    }

    private void writeToLogFile(String string) {
        LogChunkDataTask logChunkDataTask = new LogChunkDataTask();
        logChunkDataTask.execute(string);
    }

    public boolean deleteLogs() {
        File chunksFile = createFile(CHUNKS_LOG_FILE_NAME);
        File samplesFile = createFile(SAMPLE_GAPS_LOG_FILE_NAME);
        return chunksFile.delete() && samplesFile.delete();
    }

    public void logChunkDataToFile(SamplesChunk samplesChunk) {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd:HH:mm:ss:SSS", Locale
                .getDefault());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("chunk received at: ")
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

    public void logSampleGaps(SamplesChunk samplesChunk) {
        LogSampleGapsTask gapsTask = new LogSampleGapsTask();
        gapsTask.execute(samplesChunk);
    }

    private class LogSampleGapsTask extends AsyncTask<SamplesChunk, Void, Void> {

        private File file;

        private SimpleDateFormat simpleDateFormat;

        public LogSampleGapsTask() {
            file = createFile(SAMPLE_GAPS_LOG_FILE_NAME);
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
                        ChunkData lastChunkData = mSharedPrefsController.getLastMessage();
                        if (lastChunkData != null){
                            boolean packagesValidTimesDiff = compareSampleTimes(lastChunkData
                                    .getLastSampleTimestamp(), next.getTimestamp());
                            if (!packagesValidTimesDiff) {
                                writeChunksGapToFile(lastChunkData, next, params[0].getBatteryPercentage());
                            }
                        }
                        prev = next;
                        continue;
                    }

                    // Here we iterating on each sample in the new arrived chunk and comparing to
                    // previous sample. If the difference in milliseconds above the maximum
                    // difference allowed (configured in Configuration file) we log the
                    // difference to file.
                    boolean validDiff = compareSampleTimes(prev.getTimestamp(), next.getTimestamp());
                    if (!validDiff) {
                        writeSamplesGapToFile(prev, next, params[0].getBatteryPercentage());
                    }
                    prev = next;
                }
            } catch (Exception e) {
                Timber.e(e);
            }
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
            return "Chunks time gap! last sample in previous chunk at: "
                    + DateUtils.millisecondsToString(lastChunkData.getLastSampleTimestamp(), simpleDateFormat)
                    + " , first sample in new chunk at: "
                    + DateUtils.millisecondsToString(next.getTimestamp(), simpleDateFormat)
                    + " , battery level: "
                    + batteryLevel
                    + " , gap: "
                    + (next.getTimestamp() - lastChunkData.getLastSampleTimestamp())
                    + " ms";
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
            return "previous sample at: "
                    + DateUtils.millisecondsToString(prev.getTimestamp(), simpleDateFormat)
                    + " , new sample at: "
                    + DateUtils.millisecondsToString(next.getTimestamp(), simpleDateFormat)
                    + " , battery level: "
                    + batteryLevel
                    + " , gap: "
                    + (next.getTimestamp() - prev.getTimestamp())
                    + " ms";
        }

        private boolean compareSampleTimes(long prevTime, long nextTime) {
            return Math.abs(prevTime - nextTime) <= DefaultConfiguration.MAX_ALLOWED_SAMPLES_DIFF_IN_MILLIS;
        }

    }

    private class LogChunkDataTask extends AsyncTask<String, Void, Void> {

        private File logsFile;

        public LogChunkDataTask() {
            logsFile = createFile(CHUNKS_LOG_FILE_NAME);
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

    private File createFile(String fileName) {
        String date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        String name = String.format("%s_%s.log", fileName, date);

        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File androidPoCDir = new File(externalStorageDirectory, "WearPoC");
        File logsDir = new File(androidPoCDir, "Logs");
        logsDir.mkdirs();

        return new File(logsDir, name);
    }


}
