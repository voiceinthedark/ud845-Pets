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

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    private static final String TAG = CatalogActivity.class.getSimpleName();
    private PetDbHelper mDbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new PetDbHelper(this);
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {

        // Create and/or open a database to read from it
        db = mDbHelper.getReadableDatabase();

        //Perform a query operation that returns the entire pets table
        String projection[] = {
                PetEntry._ID, //we want the ID
                PetEntry.COLUMN_PET_NAME, //The pet name
                PetEntry.COLUMN_PET_BREED, //Pet breed
                PetEntry.COLUMN_PET_GENDER, //Pet gender
                PetEntry.COLUMN_PET_WEIGHT //the pet weight
        };
        Cursor cursor = db.query(PetEntry.TABLE_NAME,
                projection, //The columns to return
                null, //columns for the where clause
                null, //values for where clause
                null, //column to group by
                null, //condition of grouping
                null //the order
        );
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_view_pet);
            displayView.setText("Number of rows in pets database table: " + cursor.getCount());

            //get the column indices
            int idColumnIndex = cursor.getColumnIndex(PetEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
            int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
            int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

            //set the names of columns
            displayView.append("\n" + cursor.getColumnName(idColumnIndex) +
                    " - " + cursor.getColumnName(nameColumnIndex) +
                    " - " + cursor.getColumnName(breedColumnIndex) +
                    " - " + cursor.getColumnName(genderColumnIndex) +
                    " - " + cursor.getColumnName(weightColumnIndex) + "\n");

            //iterate through the cursor to capture the rows
            while (cursor.moveToNext()) {
                int idPet = cursor.getInt(idColumnIndex);
                String namePet = cursor.getString(nameColumnIndex);
                String breedPet = cursor.getString(breedColumnIndex);
                int genderPet = cursor.getInt(genderColumnIndex);
                int weightPet = cursor.getInt(weightColumnIndex);

                displayView.append("\n" +
                        idPet + " - " +
                        namePet + " - " +
                        breedPet + " - " +
                        genderPet + " - " +
                        weightPet);

            }

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Display the database info once this activity has been restarted
     */
    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void insertPet() {
        db = mDbHelper.getWritableDatabase();
        //setup content values to store into Database
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);

        long index = db.insert(PetEntry.TABLE_NAME,
                null,
                values);
        Log.i(TAG, "index " + index);

    }
}
