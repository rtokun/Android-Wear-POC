package com.artyom.androidwearpoc.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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

public class ExportFileDialog extends DialogFragment {

    private boolean mSuccess;

    private String mText;

    private String mPath;

    public static final String SUCCESS_KEY = "success";
    public static final String TEXT_KEY = "text";
    public static final String PATH_KEY = "path";

    private ExportFileDialogInteractionInterface mInteractionInterface;

    private DialogInterface.OnClickListener mNegativeOnClickListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            dismiss();
        }
    };

    public static ExportFileDialog newInstance(boolean success,
                                               String text,
                                               String accSamplesFilePath) {
        ExportFileDialog dialog = new ExportFileDialog();

        Bundle args = new Bundle();
        args.putString(TEXT_KEY, text);
        args.putBoolean(SUCCESS_KEY, success);
        args.putString(PATH_KEY, accSamplesFilePath);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSuccess = getArguments().getBoolean(SUCCESS_KEY);
        mText = getArguments().getString(TEXT_KEY);
        mPath = getArguments().getString(PATH_KEY);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mInteractionInterface = (ExportFileDialogInteractionInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ExportFileDialogInteractionListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (mSuccess){
            return new AlertDialog.Builder(getActivity())
                    .setTitle("Share Logs")
                    .setMessage(mText)
                    .setPositiveButton(R.string.alert_dialog_share,
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    mInteractionInterface.onShareClick(mPath);
                                    dismiss();
                                }
                            }
                    )
                    .setNegativeButton(R.string.alert_dialog_close, mNegativeOnClickListener)
                    .create();
        } else {
            return new AlertDialog.Builder(getActivity())
                    .setTitle("No records")
                    .setMessage(mText)
                    .setNegativeButton(R.string.alert_dialog_close, mNegativeOnClickListener)
                    .create();
        }
    }

    public interface ExportFileDialogInteractionInterface {
        void onShareClick(String pathToFile);
    }
}
