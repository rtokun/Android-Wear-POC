package com.artyom.androidwearpoc.shared;

import java.util.concurrent.TimeUnit;

import static com.artyom.androidwearpoc.shared.enums.DataTransferType.ASSET;

/**
 * Created by Artyom on 28/12/2016.
 */

public class DefaultConfiguration {

    public static final Enum DATA_TRANSFER_TYPE = ASSET;

    // 50 samples a second * 60 seconds * X minutes
//    public static final int DEFAULT_SAMPLES_PER_PACKAGE_LIMIT = 50 * 60 * 5;

    // 50 samples a second * 30 seconds
    public static final int DEFAULT_SAMPLES_PER_PACKAGE_LIMIT = 50 * 30;

    public static final boolean LOG_EACH_SAMPLE = false;

    public static final int DEFAULT_SAMPLING_RATE_IN_MICRO = 20000;

    public static final int DEFAULT_SAMPLING_RATE_IN_HZ = 1000000 / DEFAULT_SAMPLING_RATE_IN_MICRO;

    public static final int MAX_ALLOWED_SAMPLES_DIFF_IN_MILLIS = 25;

    public static final int MIN_ALLOWED_SAMPLES_DIFF_IN_MILLIS = 15;

    public static final long MAX_ALLOWED_DIFF_BETWEEN_PACKAGES_IN_MILLIS =
            TimeUnit.MICROSECONDS.toMillis(DEFAULT_SAMPLING_RATE_IN_MICRO) * 2;

}
