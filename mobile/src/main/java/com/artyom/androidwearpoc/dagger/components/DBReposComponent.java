package com.artyom.androidwearpoc.dagger.components;

import com.artyom.androidwearpoc.MyMobileApplication;
import com.artyom.androidwearpoc.dagger.modules.DBRepoModule;
import com.artyom.androidwearpoc.wear.data.DataReceiverService;

import dagger.Component;

/**
 * Created by tomerlev on 28/12/2016.
 */
@Component(modules = DBRepoModule.class)
public interface DBReposComponent {

    void inject(DataReceiverService dataReceiverService);

    void inject(MyMobileApplication myMobileApplication);
}
