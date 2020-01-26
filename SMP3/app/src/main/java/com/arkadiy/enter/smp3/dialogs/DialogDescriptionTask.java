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
import android.widget.TextView;

import com.arkadiy.enter.smp3.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("ValidFragment")
public class DialogDescriptionTask extends AppCompatDialogFragment {

    private TextView dialogDescriptionEditText;
    private TextView taskNameDSCTextView;
    private TextView startTimeDSCTextView;
    private TextView endTimeDSCTextView;
    private DialogCloseTask.DialogCloseTaskListener listener;
    private String description;
    private String nameTask;
    private String dateStart;
    private String dateEnd;
//itemsList.get(position).getTimeDateStart(),itemsList.get(position).getTimeDateEnd()
    public DialogDescriptionTask(String description,String dateStart,String dateEnd) {

        this.description  = description;
        this.dateStart  = formatDatetime(dateStart);
        this.dateEnd = formatDatetime(dateEnd);


    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

//        +"\n"+"Start Time: "+"\n"+this.dateStart+"\n"+"End Time Task: "+this.dateEnd
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dailog_description,null);
        builder.setView(view)
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        taskNameDSCTextView= view.findViewById(R.id.taskName_DSC_TextView);
        startTimeDSCTextView = view.findViewById(R.id.startTimeDSC_TextView);
        endTimeDSCTextView = view.findViewById(R.id.endTimeDSC_TextView);
        dialogDescriptionEditText = view.findViewById(R.id.descriptionTask_EditText);

        taskNameDSCTextView.setText(getTag());
        startTimeDSCTextView.setText(this.dateStart);
        endTimeDSCTextView.setText(this.dateEnd);
        dialogDescriptionEditText.setText(description);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DialogCloseTask.DialogCloseTaskListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+" must implement DialogDescriptionTaskListener");
        }
    }

    public interface DialogCloseTaskListener{
        void applyText(String reason , String tag );
    }

    public static String formatDatetime(String dateTime){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date newDate;
            newDate = format.parse(dateTime);
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String date = format.format(newDate);
            return date;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}