package com.artyom.androidwearpoc.shared;

import static com.artyom.androidwearpoc.shared.enums.DataTransferType.ASSET;

/**
 * Created by Artyom on 28/12/2016.
 */

public class Configuration {

    public static final Enum DATA_TRANSFER_TYPE = ASSET;

    // 50 samples a second * 60 seconds * X minutes
//    public static final long SAMPLES_PER_PACKAGE_LIMIT = 50 * 60 * 5;

    // 50 samples a second * 30 seconds
    public static final long SAMPLES_PER_PACKAGE_LIMIT = 50 * 30;

    public static final boolean LOG_EACH_SAMPLE = false;
}
