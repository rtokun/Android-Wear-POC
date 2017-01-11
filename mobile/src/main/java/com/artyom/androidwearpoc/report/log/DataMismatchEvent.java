package com.artyom.androidwearpoc.report.log;

import com.crashlytics.android.answers.CustomEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Created by Artyom-IDEO on 10-Jan-17.
 */

public class DataMismatchEvent extends CustomEvent {

    public DataMismatchEvent() {
        super("Data Mismatch");
    }

    public void updateSamplesAmountMismatch(int requiredAmount, int actualAmount) {
        Timber.d("found mismatch in samples amount, required: %s, but found: %s", requiredAmount,
                actualAmount);
        this.putCustomAttribute("Samples Amount Mismatch",
                "required: " + requiredAmount +
                " found: " + actualAmount);
    }

    public void updatePackagesTimesMismatch(long lastSampleOldMessageTimestamp,
                                            long firstSampleNewMessageTimestamp) {
        String timeDiffString = createTimeDiffString(lastSampleOldMessageTimestamp,
                firstSampleNewMessageTimestamp);
        Timber.d("found mismatch in packages order: " + timeDiffString);
        this.putCustomAttribute("Packages Time Gap", timeDiffString);
    }

    private String createTimeDiffString(long lastSampleTime,
                                        long firstSampleTime) {

        long timeDiffInMillis = (firstSampleTime -
                lastSampleTime);

        DateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault());
        Date lastSampleDate = new Date(lastSampleTime);
        Date newSampleDate = new Date(firstSampleTime);

        if (timeDiffInMillis > 1000) {
            return "last sample at: " + format.format(lastSampleDate) +
                    " new sample at: " + format.format(newSampleDate) +
                    " => gap: " + TimeUnit.MILLISECONDS.toSeconds(timeDiffInMillis) + " seconds";
        } else {
            return "last sample at: " + format.format(lastSampleDate) +
                    " new sample at: " + format.format(newSampleDate) +
                    " => gap: " + timeDiffInMillis + " milliseconds";
        }
    }
}
