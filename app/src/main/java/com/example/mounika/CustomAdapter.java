package com.example.mounika;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<ContactModel> {

    Context context;
    List<ContactModel> contacts;
    DbHelper db;

    public CustomAdapter(@NonNull Context context, List<ContactModel> contacts) {
        super(context, 0, contacts);
        this.context = context;
        this.contacts = contacts;
        db = new DbHelper(context); // create a database helper object
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        ContactModel c = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        LinearLayout linearLayout = convertView.findViewById(R.id.linear);

        // Lookup view for data population
        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvPhone = convertView.findViewById(R.id.tvPhone);

        // Populate the data into the template view using the data object
        tvName.setText(c.getName());
        tvPhone.setText(c.getPhoneNo());

        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Generate a MaterialAlertDialog Box
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Remove Contact")
                        .setMessage("Are you sure you want to remove this contact?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Delete the specified contact from the database
                                db.deleteContact(c.getId());
                                // Remove the item from the list
                                contacts.remove(c);
                                // Notify the ListView that the dataset has been changed
                                notifyDataSetChanged();
                                Toast.makeText(context, "Contact removed!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                return false;
            }
        });
        // Return the completed view to render on the screen
        return convertView;
    }

    // This method will update the ListView
    public void refresh(List<ContactModel> list) {
        contacts.clear();
        contacts.addAll(list);
        notifyDataSetChanged();
    }
}
