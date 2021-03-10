package com.alethesuren.womensafetyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static android.preference.PreferenceManager.*;

public class EditActivity extends AppCompatActivity {

    private EditText first_name, last_name, phone_number;

    CheckBox message_Check_Box;

    Spinner spinner;
    private String phoneNumFromIntent;

    private Button save_Button;
    public static final String CONTACT_BOOK_PREFERENCE = "ContactBook" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_page);

        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        phone_number = findViewById(R.id.phone_number);
        message_Check_Box = findViewById(R.id.checkbox_message);
        save_Button = findViewById(R.id.saveButton);
        spinner = findViewById(R.id.spinner);
        //create a list of items for the spinner.
        String[] items = new String[]{"1", "2", "3"};

//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

//set the spinners adapter to the previously created one.
        spinner.setAdapter(adapter);


//        message_Check_Box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//
//
//            }
//        });

        phoneNumFromIntent = getIntent().getStringExtra("PHONE_NUM");
        if (phoneNumFromIntent.equals("")) {
            Toast.makeText(this, "on Create Mode", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "on Update Mode, Phone: " + phoneNumFromIntent, Toast.LENGTH_SHORT).show();
            /*
            Fetch user data from shared preferences
            - get data by phone_number in sharedpeferences
            - set first, last, phone, messssage flag, priority in the edit text
             */
            SharedPreferences sharedpreferences = getSharedPreferences(CONTACT_BOOK_PREFERENCE, Context.MODE_PRIVATE);
            String data = sharedpreferences.getString(phoneNumFromIntent, "no-value-found");
            if (data.equals("no-value-found")) {
                Toast.makeText(this, "Data for phone: " + phoneNumFromIntent + " doesn't exist", Toast.LENGTH_SHORT).show();
            } else {
                // data = Balman,Rawat,9840052692
                // parts = {"Balman", "Rawat", "9840052692"}
                String[] parts = data.split(",");
                String f_name = parts[0];
                String l_name = parts[1];
                String p_num = parts[2];
                String smFlag = parts[3];
                String priority = parts[4];


                first_name.setText(f_name);
                last_name.setText(l_name);
                phone_number.setText(p_num);
                message_Check_Box.setChecked(Boolean.parseBoolean(smFlag));
                spinner.setSelection(Integer.parseInt(priority)-1);

            }

        }


        save_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //when save button is clicked put data on shared preferences..
                //validate
                /*
                - get
                    - first_name
                    - last_name
                    - phone_number
                    - message flag
                    - priority

                */
                String f_name = first_name.getText().toString();
                f_name = f_name.trim();
                String l_name = last_name.getText().toString();
                l_name = l_name.trim();
                String p_num = phone_number.getText().toString();
                p_num = p_num.trim();
                boolean status = message_Check_Box.isChecked();
                String priority = spinner.getSelectedItem().toString();



                //String priority = s_priority.getText().toString();


                /*
                - validate all of them
                    - check if the first_name is empty or not
                        - if empty, show alert in a toast and do nothing
                    - check if the last_name is empty or not
                        - if empty, show alert in a toast and do nothing
                    - check if phone number is empty or not
                        - if empty or less than 10 or greator than 10, show phone number error alert in toast
                */
                if (f_name.equals("")){
                    Toast.makeText(EditActivity.this, "First Name Can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (l_name.equals("")){
                    Toast.makeText(EditActivity.this, "Last Name Can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (p_num.equals("")){
                    Toast.makeText(EditActivity.this, "Phone number can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (p_num.length() != 10){
                    Toast.makeText(EditActivity.this, "There should be exactly 10 digits in phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                /*
                - After all the successful validation
                - save the data in shared preferences
                    - use key as phone_number
                    - append all the data in CSV(comma separated value). data = first_name + "," + last_name + "," + phone_number + "," + messageFlag + "," + priority
                 */

                String data = f_name + "," + l_name + "," + p_num + ","+status + "," +priority;
                SharedPreferences sharedpreferences = getSharedPreferences(CONTACT_BOOK_PREFERENCE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(p_num, data);


                String keys = sharedpreferences.getString("keys","no-value-found");
                //if it is create mode and the number already exits in sharedpreference then do not save the data and show alert
                // for already existing record
                if(phoneNumFromIntent.equals("") && keys.contains(p_num)){
                    Toast.makeText(EditActivity.this, "Record with the given phone number already exists!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check if the mode is update mode, and the number is alson being updated
                // if the mode is update mode and the number is changed then delete the old record and update with new one
                if(!phoneNumFromIntent.equals("") && !phoneNumFromIntent.equals(p_num)){
                    if (!keys.equals("no-value-found")){
                        keys = keys.replace(phoneNumFromIntent+",", "");
                        keys = keys.replace(","+phoneNumFromIntent, "");
                        keys = keys.replace(phoneNumFromIntent,"");
                    }
                    editor.putString("keys", keys);
                    editor.remove(phoneNumFromIntent); //remove the data
                }



                // Get the keys first
                // append the existing number to the keys
                if (keys.equals("no-value-found")) {
                    keys = p_num;
                }else{
                    // Append the phone number in the keys only if the phone_number doesn't exist in the keys
                    if (!keys.contains(p_num)){
                        keys = keys + p_num + ",";
                    }
                }
                editor.putString("keys", keys);
               // editor.putBoolean("keys",status);
                editor.apply();

                Toast.makeText(EditActivity.this, "Successfully saved!!", Toast.LENGTH_SHORT).show();
                finish();

                /*
                Flush all the fields
                */
//                first_name.setText("");
//                last_name.setText("");
//                phone_number.setText("");


            }


        });

    }

}