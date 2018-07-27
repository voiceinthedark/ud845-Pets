package com.example.android.pets.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * A contract table to map the SQLite Database schema on a 1:1 basis
 */
public final class PetContract {

    private PetContract(){}

    /**
     * This is the authority that we will use to construct our Content {@link android.net.Uri}
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";

    /**
     * The base content Uri is the scheme in concat with our {@link PetContract#CONTENT_AUTHORITY}
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);

    /**
     * This is our table name, the table we would like to query
     */
    public static final String PATH_PETS = "pets";



    public static final class PetEntry implements BaseColumns {

        /**
         * The Content Uri of our pets table inside of our {@link android.database.sqlite.SQLiteDatabase}
         * our complete Content Uri is the scheme in this case {@code content://} followed by
         * content authority {@code com.example.android.pets} followed by the path to our database
         * table {@code pets}
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);

        //The table name in the sqlite schema
        public static final String TABLE_NAME = "pets";

        /**
         * The Id is inherited from BaseColumns
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;
        /**
         * The pet name column in our database {@code name}
         * Type: TEXT
         */
        public static final String COLUMN_PET_NAME = "name";
        /**
         * The breed column name {@code breed}
         * Type: TEXT
         */
        public static final String COLUMN_PET_BREED = "breed";
        /**
         * The gender column name {@code gender}
         * Type: INTEGER
         */
        public static final String COLUMN_PET_GENDER = "gender";
        /**
         * The weight column name {@code weight}
         * Type: INTEGER
         */
        public static final String COLUMN_PET_WEIGHT = "weight";

        /**
         * Add Constants to map to the Gender column of our pets table; these are 1 of 3: Unknown,
         * Male or Female
         */

        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;


    }
}
