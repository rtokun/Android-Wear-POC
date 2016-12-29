package com.artyom.androidwearpoc.dagger.components;

import com.artyom.androidwearpoc.MyMobileApplication;
import com.artyom.androidwearpoc.dagger.modules.DBRepoModule;
import com.artyom.androidwearpoc.db.AccelerometerSamplesRepo;
import com.artyom.androidwearpoc.db.BatteryLevelSamplesRepo;
import com.artyom.androidwearpoc.ui.main.MainActivity;
import com.artyom.androidwearpoc.wear.data.DataReceiverService;

import dagger.Component;

/**
 * Created by tomerlev on 28/12/2016.
 */
@Component(modules = DBRepoModule.class)
public interface DBReposComponent {

    BatteryLevelSamplesRepo getBatteryLevelSamplesRepo();

    AccelerometerSamplesRepo getAccelerometerSamplesRepo();

    void inject(DataReceiverService dataReceiverService);
}
