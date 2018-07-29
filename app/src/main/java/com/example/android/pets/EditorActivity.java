/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.PetContract.PetEntry;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = EditorActivity.class.getSimpleName();
    private static final int LOADER_EDIT = 1;

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = PetEntry.GENDER_UNKNOWN;

    private Uri mPetUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mPetUri= (Uri) getIntent().getData();
        if(mPetUri != null){
            setTitle("Edit Pet");
            //initialize the cursor loader
            getLoaderManager().initLoader(LOADER_EDIT, null, this);
            Log.i(TAG, "Uri: " + mPetUri);
        }else {
            setTitle(R.string.editor_activity_title_new_pet);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        setupSpinner();


    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = PetEntry.GENDER_UNKNOWN; // Unknown
            }
        });
    }

    public static Intent newIntent(Context context, Uri petUri){
        Intent intent = new Intent(context, EditorActivity.class);
        intent.setData(petUri);
        return intent;
    }

    /**
     * Method to insert or update a new pet into the database, with the fields' values received from
     * the layout views
     */
    private void savePet(){
        ContentValues values = new ContentValues();

        String petName = mNameEditText.getText().toString().trim();
        String petBreed = mBreedEditText.getText().toString().trim();
        int petGender = mGender;
        int petWeight = Integer.parseInt(mWeightEditText.getText().toString());

        values.put(PetEntry.COLUMN_PET_NAME, petName);
        values.put(PetEntry.COLUMN_PET_BREED, petBreed);
        values.put(PetEntry.COLUMN_PET_GENDER, petGender);
        values.put(PetEntry.COLUMN_PET_WEIGHT, petWeight);


        //if the Uri is null it means we are inserting a new pet
        if(mPetUri == null) {
            Uri newPet = getContentResolver()
                    .insert(PetEntry.CONTENT_URI, values);

            //get the id from the Uri by extracting the Last Path Segment method
            long insertResult = Long.parseLong(newPet.getLastPathSegment());
            //Show a toast message after insertion (or failed insertion)
            showToast(insertResult);
        }
        else { //The uri contain data so we need to update this pet data
            long result = getContentResolver()
                    .update(mPetUri, values, null, null);
            showToast(result);

        }
    }

    /**
     * show toast message after performing the insert operation
     * @param insertResult -1 for error, positive integer in case of success
     */
    private void showToast(long insertResult) {
        if(insertResult == -1){
            Toast.makeText(this,
                    R.string.toast_error,
                    Toast.LENGTH_LONG)
                    .show();
        }
        else if(mPetUri != null){
            Toast.makeText(this,
                    "pet updated",
                    Toast.LENGTH_LONG)
                    .show();
        }
        else{
            Toast.makeText(this,
                    getString(R.string.toast_success),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //save a pet into the database
                savePet();
                //close the current activity and return to the parent activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                mPetUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //move cursor to first position
        data.moveToFirst();

        //get the data from the cursor and bind them to our views
        int nameIndex = data.getColumnIndex(PetEntry.COLUMN_PET_NAME);
        int breedIndex = data.getColumnIndex(PetEntry.COLUMN_PET_BREED);
        int genderIndex = data.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
        int weightIndex = data.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

        mNameEditText.setText(data.getString(nameIndex));
        mBreedEditText.setText(data.getString(breedIndex));
        mGenderSpinner.setSelection(data.getInt(genderIndex));
        mWeightEditText.setText(data.getString(weightIndex));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //reset the views
        mNameEditText.setText("");
        mBreedEditText.setText("");
        mGenderSpinner.setSelection(0);
        mWeightEditText.setText("");
    }
}