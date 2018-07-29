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
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity /*Implements the Cursor Loader*/
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;

    private static final String TAG = CatalogActivity.class.getSimpleName();
    private PetCursorAdapter mPetCursorAdapter;
    private SQLiteDatabase db;
    private ListView mPetListView;

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

        mPetListView = (ListView) findViewById(R.id.pet_listview);
        mPetCursorAdapter = new PetCursorAdapter(this, null);
        mPetListView.setAdapter(mPetCursorAdapter);

        //set the empty view
        View emptyView = findViewById(R.id.empty_view);
        mPetListView.setEmptyView(emptyView);

        mPetListView.setOnItemClickListener(clickPetItem);

        getLoaderManager().initLoader(LOADER_ID, null, this);

    }

    /**
     * Setup a click listener on the items in the list view
     */
    private AdapterView.OnItemClickListener clickPetItem = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //setup the Uri
            Uri petUri = PetEntry.CONTENT_URI; //content://com.example.pets/pets
            // content://com.example.pets/pets/#
            petUri = petUri.buildUpon().appendPath(String.valueOf(id)).build();
            //send the intent
            Intent intent = EditorActivity.newIntent(CatalogActivity.this, petUri);
            startActivity(intent);
        }
    };


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
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertPet() {
        //setup content values to store a dummy row into Database
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);

        /**
         * Use the content resolver to insert a new entry into our database
         * The {@link android.content.ContentResolver} will send our insert operation to our
         * {@link android.content.ContentProvider} {@link com.example.android.pets.data.PetProvider}
         * and receives a Uri with the ID appended to it of the newly inserted entry
         */
        Uri uri = getContentResolver().insert(PetEntry.CONTENT_URI, values);
    }

    /**
     * Display the database info once this activity has been restarted
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //Perform a query operation that returns the entire pets table
        String projection[] = {
                PetEntry._ID, //we want the ID
                PetEntry.COLUMN_PET_NAME, //The pet name
                PetEntry.COLUMN_PET_BREED, //Pet breed
                PetEntry.COLUMN_PET_GENDER, //Pet gender
                PetEntry.COLUMN_PET_WEIGHT //the pet weight
        };

        //get the Uri to query the entire table pets
        Uri petsTableUri = PetEntry.CONTENT_URI;

        //return a cursor loader
        //The cursor loader will perform the query on the cursor and returns a Loader<Cursor> to
        //the onLoadFinished
        return new CursorLoader(this, //The activity context
                petsTableUri, //the Uri
                projection, //The table columns projection
                null, //The selection
                null, //the selection arguments
                null); //the sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        /**
         * When the {@link LoaderManager.LoaderCallbacks#onCreateLoader(int, Bundle)} is done loading
         * the cursor it will send them to the {@link LoaderManager.LoaderCallbacks#onLoadFinished(Loader, Object)}
         * method.
         * Once we receive the {@link Cursor} data we should swap the {@link CursorAdapter} {@link PetCursorAdapter}
         */
        mPetCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        /**
         * Once the {@link LoaderManager.LoaderCallbacks} has finished loading the cursor becomes
         * detached and invalid;
         * in order to reuse a new cursor we need to to replace the {@link PetCursorAdapter} Cursor
         * with a null.
         */
        mPetCursorAdapter.swapCursor(null);

    }
}
