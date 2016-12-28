package com.artyom.androidwearpoc.shared;

import static com.artyom.androidwearpoc.shared.enums.DataTransferType.ASSET;
import static com.artyom.androidwearpoc.shared.enums.DataTransferType.DEFAULT;

/**
 * Created by Artyom on 28/12/2016.
 */

public class Configuration {

    public static final Enum DATA_TRANSFER_TYPE = ASSET;

    // 5 minutes of 50 Hz data = 15000
    public static final long SAMPLES_PER_PACKAGE_LIMIT = 15000;

    public static final boolean LOG_EACH_SAMPLE = false;


}
