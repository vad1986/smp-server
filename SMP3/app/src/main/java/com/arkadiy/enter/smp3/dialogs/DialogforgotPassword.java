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
import android.widget.Button;
import android.widget.EditText;

import com.arkadiy.enter.smp3.R;

@SuppressLint("ValidFragment")
public class DialogforgotPassword extends AppCompatDialogFragment  {
    private EditText forgotPasswordEmailEditText;
    private EditText forgotPasswordUserNameEditText;
    private Button cancelButton;
    private Button enterButton;
    private DialogforgotPassword.DialogforgotPasswordListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_forgot_password,null);
        forgotPasswordEmailEditText = view.findViewById(R.id.forgotPasswordEmail_EditText);
        forgotPasswordUserNameEditText = view.findViewById(R.id.forgotPasswordUserName_EditText);
        enterButton = view.findViewById(R.id.enterRestore);
        cancelButton = view.findViewById(R.id.cancelRestore);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = forgotPasswordUserNameEditText.getText().toString();
                String email = forgotPasswordEmailEditText.getText().toString();
                listener.applyText(userName,email);
                dismiss();
            }
        });

//        builder.setView(view).setTitle("Forgot Your Password??")
//                .setNegativeButton("CENCEL", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                })
//                .setPositiveButton("SEND", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//
//                    }
//                });

        forgotPasswordEmailEditText = view.findViewById(R.id.forgotPasswordEmail_EditText);
        forgotPasswordUserNameEditText = view.findViewById(R.id.forgotPasswordUserName_EditText);


        builder.setView(view);

        return builder.create();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DialogforgotPasswordListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+" must implement DialogCloseTaskListener");
        }
    }

    public interface DialogforgotPasswordListener{
        void applyText(String userName , String email );
    }

}
