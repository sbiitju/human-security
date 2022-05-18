package com.asad.humansecurity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    EditText email,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        firebaseAuth=FirebaseAuth.getInstance();
        checkStatus();
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
    }
    private void checkStatus() {
        if(firebaseAuth.getCurrentUser()==null) return;
        else {
            startActivity(new Intent(this,MainTask.class));
            finish();
        }
    }

    public void SignUp(View view) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view1=getLayoutInflater().inflate(R.layout.profile,null);
        EditText name,number,priority1,priority2,priority3,spassword;
        name=view1.findViewById(R.id.name);
        number=view1.findViewById(R.id.number);
        priority1=view1.findViewById(R.id.priority1);
        priority2=view1.findViewById(R.id.priority2);
        priority3=view1.findViewById(R.id.priority3);
        spassword=view1.findViewById(R.id.sPassword);
        Button button=view1.findViewById(R.id.submit);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n,nu,p1,p2,p3,p;
                n=name.getText().toString();
                nu=number.getText().toString();
                p1=priority1.getText().toString();
                p2=priority2.getText().toString();
                p3=priority3.getText().toString();
                p=spassword.getText().toString();

                firebaseAuth.createUserWithEmailAndPassword(nu,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(firebaseAuth.getCurrentUser().getUid());
                        Profile profile=new Profile(n,nu,p1,p2,p3);
                        databaseReference.setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(MainActivity.this,MainTask.class));
                                finish();
                            }else {
                                Toast.makeText(MainActivity.this,"Failed",Toast.LENGTH_LONG).show();
                            }
                            }
                        });
                    }else {
                        Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                    }
                });
            }
        });

        builder.setView(view1).show();
    }

    public void SignIn(View view) {
        firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(MainActivity.this,MainTask.class));
                    finish();
                }else {
                    Toast.makeText(MainActivity.this,"Failed",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}