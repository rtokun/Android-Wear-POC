package com.artyom.androidwearpoc.dagger.modules;

import android.os.Environment;

import com.artyom.androidwearpoc.dagger.scopes.ForApplication;
import com.artyom.androidwearpoc.report.MyLogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Artyom-IDEO on 16-Jan-17.
 */
@Module
public class LogModule {

    @ForApplication
    @Provides
    MyLogger myLogger(){
        return new MyLogger();
    }
}
