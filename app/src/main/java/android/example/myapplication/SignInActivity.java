package android.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

   //Email
   private TextInputEditText mEmail;
   //Password
   private TextInputEditText mPwd;
   //LogIn Button
   private Button signInBtn;
   //SignUp TextField
   private TextView signUpTV;
   //Firebase Authentication
   private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Initialize email
        mEmail = findViewById(R.id.SignInEditEmail);
        //initialize password
        mPwd = findViewById(R.id.SignInEditPwd);
        //initialize SignUp textview
        signUpTV = findViewById(R.id.pass_signUp);
        //initialize sign in button
        signInBtn = findViewById(R.id.signIn_btn);
        //Get instance of firebase authentication
        mAuth = FirebaseAuth.getInstance();


        //User clicks on "Sign Up" TextView
        signUpTV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //send user to sign up page
                Intent i = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });// end of signUpTV listener

        //User clicks on Sign In Button
        signInBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Get email input
                String userName = mEmail.getText().toString();

                //Get password input
                String pwd = mPwd.getText().toString();

                //Input Validation
                if(TextUtils.isEmpty(userName) && TextUtils.isEmpty(pwd)){
                    Toast.makeText(SignInActivity.this, "Please enter your credentials", Toast.LENGTH_SHORT).show();
                    return; //why?

                }else{
                    //Firebase authentication sign in
                    mAuth.signInWithEmailAndPassword(userName, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //if successful
                            if(task.isSuccessful()){
                                Toast.makeText(SignInActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                                //Go to Home Page
                                Intent i = new Intent(SignInActivity.this, HomeActivity.class);
                                startActivity(i);
                                finish();

                                //if login is unsuccessful
                            }else{
                                //Display a login error msg
                                Toast.makeText(SignInActivity.this, "Login Failed! Sign in again!",Toast.LENGTH_SHORT ).show();
                            }

                        }
                    }); //end of firebase authentication
                }

            }

        });//end of signInBtn listener

    }//end of OnCreate
}//end of SignInActivity




/*********OLD CODE**********/
/*
signInBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            loginUser();
        }
    });
}


    private void loginUser() {
        String email = mEmail.getText().toString();
        String pass = mPass.getText().toString();

        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (!pass.isEmpty()) {
                mAuth.signInWithEmailAndPassword(email,pass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(SignInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                //CODE FOR TRANSITIONING PAGES
                                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignInActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
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

    }*/