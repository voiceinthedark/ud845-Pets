package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.pets.data.PetContract;

public class PetCursorAdapter extends CursorAdapter {

    private static final String TAG = PetCursorAdapter.class.getSimpleName();

    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Bind the list_item xml layout to our Cursor view as a new view; this will be inflated every
     * time the {@link ListView} requires a item view to display data from the {@link Cursor}
     * @param context The activity context
     * @param cursor The curso to the database view
     * @param parent the ViewGroup parent LinearLayout
     * @return a new view of the inflated list_item.xml
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        if(cursor == null){
            Log.e(TAG, "cursor is null ");
            return;
        }

        //get the name textview
        TextView name = (TextView) view.findViewById(R.id.name);
        //get the breed textview
        TextView breed = (TextView) view.findViewById(R.id.summary);

        String namePet = cursor.getString(
                cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME));
        String breedPet = cursor.getString(
                cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED));

        //bind the textviews with the data
        name.setText(namePet);
        breed.setText(breedPet);

    }
}
