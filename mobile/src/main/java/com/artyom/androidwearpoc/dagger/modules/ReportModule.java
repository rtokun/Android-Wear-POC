package com.artyom.androidwearpoc.dagger.modules;

import com.artyom.androidwearpoc.dagger.scopes.ForApplication;
import com.artyom.androidwearpoc.report.ReportController;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Artyom-IDEO on 10-Jan-17.
 */
@Module
public class ReportModule {

    @ForApplication
    @Provides
    ReportController crashReportController(){
        return new ReportController();
    }

}
