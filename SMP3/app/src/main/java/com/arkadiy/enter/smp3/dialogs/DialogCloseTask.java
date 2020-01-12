package com.arkadiy.enter.smp3.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.arkadiy.enter.smp3.R;

@SuppressLint("ValidFragment")
public class DialogCloseTask extends AppCompatDialogFragment {
    private EditText commentTaskEditText;
    private DialogCloseTaskListener listener;
    private int position;
    public DialogCloseTask(int position){this.position = position;}
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dailog_close,null);
        builder.setView(view).setTitle("Task name: "+getTag())
                .setNegativeButton("CENCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("CLOSE TASK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String tmpStr = commentTaskEditText.getText().toString().trim();
                        listener.applyText(tmpStr , getTag(),position);

                    }
                });
        commentTaskEditText = view.findViewById(R.id.commentTask_EditText);

        return builder.create();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DialogCloseTaskListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+" must implement DialogCloseTaskListener");
        }
    }

    public interface DialogCloseTaskListener{
        void applyText(String reason , String tag ,int position);
    }
}

