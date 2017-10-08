package com.jr.poliv.animeapp.global;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import com.jr.poliv.animeapp.R;

/**
 * Created by poliv on 9/30/2017.
 */

public class DisplayTextDialogue extends DialogFragment {

    String text;
    Context context;

    public static DisplayTextDialogue getInstance(Context context, String text){
        return new DisplayTextDialogue(context, text);
    }


    private DisplayTextDialogue(Context context, String text){
        this.context = context;
        this.text = text;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(context)
                .setView(R.layout.display_text)
                .create();
    }
}
