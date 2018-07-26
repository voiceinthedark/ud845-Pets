package com.example.android.pets.data;

import android.provider.BaseColumns;

/**
 * A contract table to map the SQLite Database schema on a 1:1 basis
 */
public final class PetContract {

    private PetContract(){}

    public static final class PetEntry implements BaseColumns {

        //The table name in the sqlite schema
        public static final String TABLE_NAME = "pets";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PET_NAME = "name";
        public static final String COLUMN_PET_BREED = "breed";
        public static final String COLUMN_PET_GENDER = "gender";
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
