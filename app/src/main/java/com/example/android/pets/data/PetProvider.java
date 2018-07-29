package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.pets.data.PetContract.PetEntry;

/**
 * a {@link ContentProvider} pet Provider class to take care of the CRUD methods
 */
public class PetProvider extends ContentProvider {

    /**
     * Setup constants to be provided for the {@link android.content.UriMatcher}
     */
    private static final int PETS = 100;
    private static final int PET_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);
    }

    /**
     * Get a reference to our {@link android.database.sqlite.SQLiteOpenHelper} {@link PetDbHelper}
     * to interact with the Database {@link android.database.sqlite.SQLiteDatabase}
     */
    private PetDbHelper mPetDbHelper;

    /**
     * Instantiate our Pet {@link android.database.sqlite.SQLiteOpenHelper}
     *
     * @return true
     */
    @Override
    public boolean onCreate() {
        mPetDbHelper = new PetDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mPetDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(PetEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        //notify the Cursor Loader that data changed on the uri
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertPet(Uri uri, ContentValues values) {
        /**
         * Data validation
         * name: NOT NULL
         * breed: could be null
         * gender: an Integer between 0 and 3
         * weight: positive integer
         */
        String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
        if(name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        int gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
        if(!PetEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Pet can only be one of three genders");
        }

        int weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
        if(weight < 0) {
            throw new IllegalArgumentException("Pet can't have a negative weight");
        }

        //Get a writable database from our SQLiteOpenHelper class
        SQLiteDatabase database = mPetDbHelper.getWritableDatabase();

        /**
         * Insert a new row into the database with the provided {@link ContentValues}
         * this should return an ID of type LONG INTEGER
         */
        long id = database.insert(PetEntry.TABLE_NAME,
                null, values);

        //Notify the Cursor Loader that the Cursor has changed
        getContext().getContentResolver().notifyChange(uri, null);

        //We return the content Uri with the id of the newly inserted row appended using the
        //ConentUris helper utility class
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mPetDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:

                // Delete all rows that match the selection and selection args
                return deletePet(selection, selectionArgs, database, uri);
            case PET_ID:
                // Delete a single row given by the ID in the URI
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deletePet(selection, selectionArgs, database, uri);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    private int deletePet(@Nullable String selection, @Nullable String[] selectionArgs,
                          SQLiteDatabase database, Uri uri) {
        int rowsDeleted = database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
        if(rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, values, selection, selectionArgs);
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //if Content Values contains no elements skip update
        if(values.size() == 0) {
            return 0;
        }

        /**
         * Data validation
         * We use {@link ContentValues#containsKey(String)} to check whether an attribute exists
         */
        if(values.containsKey(PetEntry.COLUMN_PET_NAME)) {
            String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
            if(name == null) {
                throw new IllegalArgumentException("Pet needs a name");
            }
        }

        if(values.containsKey(PetEntry.COLUMN_PET_GENDER)) {
            Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
            if(gender == null || !PetEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Not a valid gender");
            }
        }

        if(values.containsKey(PetEntry.COLUMN_PET_WEIGHT)) {
            Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
            if(weight == null || weight < 0) {
                throw new IllegalArgumentException("Not a valid weight");
            }
        }


        //Get SQLiteDatabase
        SQLiteDatabase database = mPetDbHelper.getWritableDatabase();

        int rowsAffected = database.update(PetEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        //notify the Cursor Loader that the Cursor was updated
        getContext().getContentResolver().notifyChange(uri, null);
        //return the number of rows affected
        return rowsAffected;
    }
}
