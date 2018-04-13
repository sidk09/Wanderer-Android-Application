package com.example.deepakkandpal.kunsh;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    DatabaseReference data;
    FirebaseDatabase database = FirebaseDatabase.getInstance();


    EditText e1,e2,e3;
    Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        e1 = (EditText)findViewById(R.id.editText);

        e2 = (EditText)findViewById(R.id.editText3);
        e3 = (EditText)findViewById(R.id.editText4);

        b = (Button)findViewById(R.id.register);



        data = FirebaseDatabase.getInstance().getReference("users");





    }
    public void login(View view) {

        String name = e1.getText().toString();

        String email = e2.getText().toString();
        String number = e3.getText().toString();
        if(name.contains("1")||name.contains("2")||name.contains("3")||name.contains("4")||name.contains("5")||name.contains("6")||name.contains("7")||name.contains("8")||name.contains("9")||name.contains("@")||name.contains(".")||name.isEmpty()||number.isEmpty()||email.isEmpty()||(!email.contains("@")))

        {
            Toast.makeText(getApplicationContext(),"Invalid entry",Toast.LENGTH_SHORT).show();

        }
        else {


            String id = data.push().getKey();

            User user = new User(id, name, email, number);

            data.child(id).setValue(user);


            Intent intent = new Intent(this, PhoneLogin.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "Successfully Registered", Toast.LENGTH_SHORT).show();
        }

    }
}


