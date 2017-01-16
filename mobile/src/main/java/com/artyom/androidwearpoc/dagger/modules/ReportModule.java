package com.artyom.androidwearpoc.dagger.modules;

import android.os.Environment;

import com.artyom.androidwearpoc.dagger.scopes.ForApplication;
import com.artyom.androidwearpoc.report.MyLogger;
import com.artyom.androidwearpoc.report.ReportController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

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
