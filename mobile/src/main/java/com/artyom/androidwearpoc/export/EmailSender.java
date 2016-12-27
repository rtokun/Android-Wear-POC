package com.artyom.androidwearpoc.export;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * Created by tomerlev on 27/12/2016.
 */

public class EmailSender {

    public static void sendFileInEmail(Context context, File file){
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("*/*");

        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Android Wear PoC App");
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

}
