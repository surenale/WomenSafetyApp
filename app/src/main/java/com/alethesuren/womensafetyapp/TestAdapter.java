package com.alethesuren.womensafetyapp;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

import static com.alethesuren.womensafetyapp.EditActivity.CONTACT_BOOK_PREFERENCE;


public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder>{

    // Data holdings variables
    private final ArrayList<String> names, pnums,smFlags,priorities;
    private ContactBookActivity activity;

    // RecyclerView recyclerView;
    public TestAdapter(ArrayList<String> names, ArrayList<String> pnums,ArrayList<String> smFlags,ArrayList<String>priorities, ContactBookActivity activity) {

        this.names = names;
        this.pnums = pnums;
        this.smFlags = smFlags;
        this.priorities = priorities;
        this.activity=activity;
    }




    //Understand Later
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.my_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;


    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(names.get(position));
        holder.pnum.setText(pnums.get(position));
        holder.smFlags.setChecked(Boolean.parseBoolean(smFlags.get(position)));
        holder.smFlags.setEnabled(false);
        holder.priority.setText(priorities.get(position));
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity,EditActivity.class);
                i.putExtra("PHONE_NUM", pnums.get(position));
                activity.startActivity(i);
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(activity,"Contact Deleted", Toast.LENGTH_SHORT).show();
                String number = pnums.get(position);
                SharedPreferences sharedpreferences = activity.getSharedPreferences(CONTACT_BOOK_PREFERENCE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();

                //Update the Keys shared preferences
                String keys = sharedpreferences.getString("keys","no-value-found");
                if (!keys.equals("no-value-found")){
                    keys = keys.replace(number+",", "");
                    keys = keys.replace(","+number, "");
                    keys = keys.replace(number,"");
                }
                editor.putString("keys", keys);
                editor.remove(number); //remove the data
                editor.apply();

                // Remove from adapter as well
                names.remove(position);
                pnums.remove(position);
                smFlags.remove(position);
                priorities.remove(position);

                //Notify Adapter that data has been removed
                activity.myAdapter.notifyDataSetChanged();

                //Check if to hide the create contact button
                if(pnums.size()<ContactBookActivity.CONTACT_COUNT_LIMIT){
                    activity.createContactBtn.setVisibility(View.VISIBLE);
                    activity.createContactImage.setVisibility(View.VISIBLE);
                }

            }
        });
    }


    //Find out the length of the recycler view or the list view
    @Override
    public int getItemCount() {
        return names.size();
    }

    // Determines which xml file to set value to
    //Right now it will take my_row.xml
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView editButton;
        public TextView name, pnum;
        public CheckBox smFlags;
        public TextView priority;
        public  ImageView deleteBtn;
        public ViewHolder(View itemView) {
            super(itemView);
            this.editButton = itemView.findViewById(R.id.edit_btn);
            this.smFlags = itemView.findViewById(R.id.checkbox_message);
            this.name = itemView.findViewById(R.id.name);
            this.pnum = itemView.findViewById(R.id.pnum);
            this.priority = itemView.findViewById(R.id.priorityText);
            this.deleteBtn = itemView.findViewById(R.id.deleteBtn);
//            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }
}