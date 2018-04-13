package com.example.deepakkandpal.kunsh;

import android.content.Intent;
import android.icu.util.TimeUnit;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;


public class PhoneLogin extends AppCompatActivity {



    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;



    private static final String TAG = "PhoneLogin";
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    TextView t1,t2;
    ImageView i1;
    EditText e1,e2,e3;
    Button b1,b2;
    String input;
    Boolean exist =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        e3 =(EditText)findViewById(R.id.Phonenoedittext);

        input=e3.getText().toString();


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    for (DataSnapshot d: ds.getChildren()){

                        Map<String,String> map = (Map) d.getValue();

                        String number = (String) map.get("number");

                        if(input.equals(number)){
                            exist=true;

                            break;
                        }

                        Log.v("Edata",""+number);

                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        e1 = (EditText) findViewById(R.id.Phonenoedittext);
        b1 = (Button) findViewById(R.id.PhoneVerify);
        t1 = (TextView)findViewById(R.id.textView2Phone);
        i1 = (ImageView)findViewById(R.id.imageView2Phone);
        e2 = (EditText) findViewById(R.id.OTPeditText);
        b2 = (Button)findViewById(R.id.OTPVERIFY);
        t2 = (TextView)findViewById(R.id.textViewVerified);
        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
               // Log.d(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress = false;
                Toast.makeText(PhoneLogin.this,"Verification Complete",Toast.LENGTH_SHORT).show();
               signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
               // Log.w(TAG, "onVerificationFailed", e);
                Toast.makeText(PhoneLogin.this,"Verification Failed",Toast.LENGTH_SHORT).show();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(PhoneLogin.this,"InValid Phone Number",Toast.LENGTH_SHORT).show();
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                }

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
               // Log.d(TAG, "onCodeSent:" + verificationId);
                Toast.makeText(PhoneLogin.this,"Verification code has been send on your number",Toast.LENGTH_SHORT).show();
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                e1.setVisibility(View.GONE);
                b1.setVisibility(View.GONE);
                t1.setVisibility(View.GONE);
                i1.setVisibility(View.GONE);
                t2.setVisibility(View.VISIBLE);
                e2.setVisibility(View.VISIBLE);
                b2.setVisibility(View.VISIBLE);
                // ...
            }
        };

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {






                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        e1.getText().toString(),
                        60,
                        java.util.concurrent.TimeUnit.SECONDS,
                        PhoneLogin.this,
                        mCallbacks);

        }});

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, e2.getText().toString());
                // [END verify_with_code]
                signInWithPhoneAuthCredential(credential);
            }
        });


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                           // Log.d(TAG, "signInWithCredential:success");
                            startActivity(new Intent(PhoneLogin.this,Home.class));
                            Toast.makeText(PhoneLogin.this,"Verification Done",Toast.LENGTH_SHORT).show();
                            // ...
                        } else {
                           // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(PhoneLogin.this,"Invalid Verification",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }



}
