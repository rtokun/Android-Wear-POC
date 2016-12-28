package com.artyom.androidwearpoc.ui;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.artyom.androidwearpoc.R;
import com.artyom.androidwearpoc.export.EmailSender;

import java.io.File;

/**
 * Created by tomerlev on 28/12/2016.
 */

public  class ExportFileDialog extends DialogFragment{

    private static Context mContext;
    private boolean mSuccess;
    private String mText;
    private String mPath;

    public static ExportFileDialog newInstance(Context context,
                                               boolean success,
                                               String text,
                                               String accSamplesFilePath){
        ExportFileDialog dialog = new ExportFileDialog();

        Bundle args = new Bundle();
        args.putString("text", text);
        args.putBoolean("success", success);
        args.putString("path", accSamplesFilePath);
        dialog.setArguments(args);

        mContext = context;
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSuccess = getArguments().getBoolean("success");
        mText = getArguments().getString("text");
        mPath = getArguments().getString("path");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.export_file_dialog,container,false);
        TextView text = (TextView)view.findViewById(R.id.textViewExportDialog);
        Button send = (Button)view.findViewById(R.id.buttonSend);
        Button close = (Button)view.findViewById(R.id.buttonClose);

        text.setText(mText);

        if(!mSuccess){
            send.setVisibility(View.GONE);
        }
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EmailSender.sendFileInEmail(mContext,new File(mPath));
                ExportFileDialog.this.dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ExportFileDialog.this.dismiss();
            }
        });
        return view;
    }
}
