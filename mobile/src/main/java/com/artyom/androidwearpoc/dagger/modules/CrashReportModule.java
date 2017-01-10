package com.artyom.androidwearpoc.dagger.modules;

import com.artyom.androidwearpoc.dagger.scopes.ForApplication;
import com.artyom.androidwearpoc.report.CrashReportController;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Artyom-IDEO on 10-Jan-17.
 */
@Module
public class CrashReportModule {

    @ForApplication
    @Provides
    CrashReportController crashReportController(){
        return new CrashReportController();
    }

}
