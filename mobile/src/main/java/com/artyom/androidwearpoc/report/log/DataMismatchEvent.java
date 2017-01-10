package com.artyom.androidwearpoc.report.log;

import com.crashlytics.android.answers.CustomEvent;

/**
 * Created by Artyom-IDEO on 10-Jan-17.
 */

public class DataMismatchEvent extends CustomEvent {

    private long requiredSamplesPerPackage;

    private long actualSamplesPerPackge;

    public DataMismatchEvent() {
        super("Data Mismatch");
    }

    public void updateSamplesAmountMismatch(int requiredAmount, int actualAmount) {
        this.putCustomAttribute("Samples Amount Mismatch",
                "required: " + requiredAmount +
                " found: " + actualAmount);
    }
}
