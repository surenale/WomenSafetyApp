package com.alethesuren.womensafetyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactBookActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    ArrayList<String> names,pnums,smFlags,priorities;
    TestAdapter myAdapter;
    TextView createContactBtn;
    public static final int CONTACT_COUNT_LIMIT = 3;

    ImageView createContactImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_book);
        recyclerView = findViewById(R.id.recyclerView);

        // Intent for the Edit activity

        createContactImage = (ImageView)findViewById(R.id.addContactImage);
        createContactBtn = (TextView) findViewById(R.id.addcontact);
        createContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ContactBookActivity.this,EditActivity.class);
                i.putExtra("PHONE_NUM", "");
                startActivity(i);
            }
        });



    }


    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedpreferences = getSharedPreferences(EditActivity.CONTACT_BOOK_PREFERENCE, Context.MODE_PRIVATE);
        String keys = sharedpreferences.getString("keys","no-value-found");

        String[] p_nums;
        if(keys.equals("no-value-found")){
            p_nums = new String[0];
        }else{
            p_nums = keys.split(",");
        }

        names = new ArrayList<String>();
        pnums = new ArrayList<String>();
        smFlags = new ArrayList<String>();
        priorities = new ArrayList<String >();

        String data;
        for (String num: p_nums) {
            data = sharedpreferences.getString(num,"no-value-found");
            if (data.equals("no-value-found")){
                continue;
            }
            String[] parts = data.split(","); // parts Balman, Rawat, 9830004838
            names.add(parts[0] + " " + parts[1]);
            pnums.add(parts[2]);
            smFlags.add(parts[3]);
            priorities.add(parts[4]);
        }

        myAdapter = new TestAdapter(names, pnums,smFlags,priorities,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);

        //Check Contact Count to show/hide the contact button
        if( pnums.size() < CONTACT_COUNT_LIMIT ){
            createContactBtn.setVisibility(View.VISIBLE);
            createContactImage.setVisibility(View.VISIBLE);
        }else{
            createContactBtn.setVisibility(View.GONE);
            createContactImage.setVisibility(View.GONE);
        }

    }
}