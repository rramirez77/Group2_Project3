package android.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private EditText mEmail, mPass;
    private TextView mTextView;
    private Button signUpBtn;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        mEmail = findViewById(R.id.email_reg);
        mPass = findViewById(R.id.passreg);
        mTextView = findViewById(R.id.pass_signin);
        signUpBtn = findViewById(R.id.registration_btn);

        mAuth = FirebaseAuth.getInstance();
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }
    private void createUser() {
        String email = mEmail.getText().toString();
        String pass = mPass.getText().toString();

        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (!pass.isEmpty()) {
                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignUpActivity.this, "Registered Successful!", Toast.LENGTH_SHORT).show();

                                //CODE FOR TRANSITIONING PAGES
                                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                finish();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                                    public void onFailure(@NonNull Exception e){
                            Toast.makeText(SignUpActivity .this,"Registration Error",Toast.LENGTH_SHORT).show();
                }
                });


            } else {
                mPass.setError("Empty Fields are not allowed");
            }
        } else if (email.isEmpty()) {
            mEmail.setError("Empty fields are not allowed");
        } else {
            mEmail.setError("Please Enter correct email");

        }

    }
}