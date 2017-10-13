package com.jr.poliv.animeapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.jr.poliv.animeapp.global.Global;
import com.jr.poliv.animeapp.global.Season;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class SettingsActivity extends AppCompatActivity { //cut off year at 1917 to year after current
    boolean changesMade = false;
    private final int MINIMUM_YEAR = 1917;
    private int defaultYear;
    private Season defaultSeason;
    Spinner year, season;
    Button delete, defaultButton;
    ArrayAdapter<Integer> yearAdapter;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        defaultYear = Global.getDefaultYear(this);
        defaultSeason = Global.getDefaultSeason(this);
        yearAdapter = new ArrayAdapter<Integer>(this, R.layout.support_simple_spinner_dropdown_item, getYearArrayList());

        year = (Spinner) findViewById(R.id.spYear);
        year.setAdapter(yearAdapter);
        year.setSelection(yearAdapter.getPosition(Global.getUserDefinedYear()));
        year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkChangesMade();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        season = (Spinner) findViewById(R.id.spSeason);
        season.setSelection(getSpinnerItemPosition(Global.getUserDefinedSeason()));
        season.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkChangesMade();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        defaultButton = (Button) findViewById(R.id.btDefault);
        defaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToDefault();
            }
        });
        delete = (Button) findViewById(R.id.btDelete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogue();
            }
        });

       fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(changesMade){
                    save();
                    Snackbar.make(view, "Saved", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    setResult(MainActivity.REFRESH);
                    finish();
                }
            }
        });
        setFABState();
        assert(getSupportActionBar() != null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setToDefault() {
        year.setSelection(yearAdapter.getPosition(defaultYear));
        season.setSelection(getSpinnerItemPosition(defaultSeason));
    }

    private void delete() {// used to delete local data
        Log.d("Paul", "About to delete");
        try {
            FileUtils.deleteDirectory(new File(Global.getSeasonFolder(this, (Integer) year.getSelectedItem(), Season.valueOf(season.getSelectedItem().toString()))));
            Log.d("Paul", "SUCCESS, folder deleted");
            Global.unFavouriteEntireSeason(this, (Integer) year.getSelectedItem(), Season.valueOf(season.getSelectedItem().toString()));
            Log.d("Paul", "SUCCESS, Season unfavourited");
        } catch (IOException e) {
            Log.d("Paul", "Folder not deleted "+e.toString());
            e.printStackTrace();
        }finally {
            setDeleteButtonState();
        }
    }

    private void save(){
        Global.setUserDefinedYear(this, (Integer) year.getSelectedItem());
        Global.setUserDefinedSeason(this, Season.valueOf(season.getSelectedItem().toString()));
    }

    private void checkChangesMade(){
        setDeleteButtonState();

        if(changesMade != ( ( ((Integer) year.getSelectedItem()) == Global.getUserDefinedYear()) && ( Season.valueOf(season.getSelectedItem().toString()) == Global.getUserDefinedSeason()))) //if changesMade is set to false when the year and season haven't been changed
            return;

        changesMade = !changesMade;
        setFABState();
    }

    private void setFABState(){
        if(changesMade){// if changes have been made
            fab.setAlpha((float) 1);
        }else{// if changes haven't been made
            fab.setAlpha((float) 0.2);
        }
    }

    private void setDeleteButtonState(){
        if(Global.checkForLocalData(this, (Integer) year.getSelectedItem(), Season.valueOf(season.getSelectedItem().toString()))){
            delete.setClickable(true);
            delete.setVisibility(View.VISIBLE);
        }else{
            delete.setClickable(false);
            delete.setVisibility(View.INVISIBLE);
        }
    }


    private Integer[] getYearArrayList(){
        Integer[] list = new Integer[defaultYear - MINIMUM_YEAR + 2];

        for(int i = 0; i < list.length; i++){
            list[i] = MINIMUM_YEAR + i;
        }


        return list;
    }

    private int getSpinnerItemPosition(Season season){
        switch(season){
            case Winter:
                return 0;
            case Spring:
                return 1;
            case Summer:
                return 2;
            case Fall:
                return 3;
            default: return 0;
        }
    }

    private void showDialogue(){
        DialogFragment newFragment = new ConfirmationDialog();
        newFragment.show(getFragmentManager(), "Confirmation");
    }


    private class ConfirmationDialog extends DialogFragment {

        public ConfirmationDialog(){

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle("Are you sure you want to delete "+ season.getSelectedItem().toString()+ " " + year.getSelectedItem().toString() + " local data?")
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int boss) {
                                    delete();
                                }
                            }
                    )
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            }
                    )
                    .create();
        }
    }

}
