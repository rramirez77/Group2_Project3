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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {
    //Email
    private TextInputEditText mEmail;
    //Password
    private TextInputEditText mPwd;
    //Confirmed password
    private TextInputEditText mCfmPwd;
    //LogIn TextField
    private TextView logInTV;
    //Sign Up Button
    private Button signUpBtn;
    //Firebase Authentication
    private FirebaseAuth mAuth;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private TextInputEditText userID;
    private TextView usersName;

    private final String[] idGenerator = {"Apple", "Argue", "Away", "Banana", "Book", "Box", "Cat", "Cramp", "Cute",
            "Dog", "Door", "Dusk", "Egg", "Emu", "Exit", "Fire", "Flag", "Fern", "Get", "Gold", "Grape", "Hair", "Hello", "Horse",
            "Ibis", "Ice", "Itch", "Jelly", "Jump", "Just", "Keep", "Kick", "Kite", "Leap", "List", "Loud", "Map", "Moon", "Most",
            "Name", "Near", "Next", "Opal", "Open", "Orange", "Pink", "Plum", "Purple", "Quail", "Quest", "Quick", "Rest", "Roar", "Run",
            "Sit", "Stay", "Sun", "Table", "Task", "Top", "Under", "Undo", "Unit", "Value", "Vest", "Vista", "Water", "Where", "Wish",
            "Yard", "Yawn", "Yell", "Zero", "Zone", "Zoo"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Initialize Email
        mEmail = findViewById(R.id.EditEmail);
        //Initialize password
        mPwd = findViewById(R.id.EditPassword);
        //Initialize confirmed password
        mCfmPwd = findViewById(R.id.EditCfmPassword);
        //initialize LogIn textview
        logInTV = findViewById(R.id.pass_signIn);
        //Initialize signUp button
        signUpBtn = findViewById(R.id.registration_btn);
        //initialize user ID
        userID = findViewById(R.id.EditEmail);

        //initialize name
        usersName = findViewById(R.id.EditName);

        //get Instance of Firebase Authentication
        mAuth = FirebaseAuth.getInstance();


        //IF USER CLICKS ON "SIGN IN"
        logInTV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(i);
            }
        });


        //IF USER CLICKS ON SIGN UP BUTTON
        signUpBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //get email input
                String userName = mEmail.getText().toString();
                //get password input
                String pwd = mPwd.getText().toString();
                //get confirmed password input
                String cfmPwd = mCfmPwd.getText().toString();

                //user id
                String ID = userID.getText().toString();
                //name of user
                String nameOfUser = usersName.getText().toString();

                //VERIFY INPUT VALIDATION

                //IF PASSWORD != CONFIRMED PASSWORD
                if(!pwd.equals(cfmPwd)){
                    Toast.makeText(SignUpActivity.this, "Please enter a valid password", Toast.LENGTH_SHORT).show();
                //IF TEXT FIELDS ARE EMPTY
                }else if(TextUtils.isEmpty(userName) && TextUtils.isEmpty(pwd) && TextUtils.isEmpty(cfmPwd)) {
                    Toast.makeText(SignUpActivity.this, "Please add your credentials", Toast.LENGTH_SHORT).show();
               //CREATE NEW USER
                }else{
                    //CREATE IN FIREBASE AUTHENTICATION, PASS USERNAME AND PASSWORD
                    mAuth.createUserWithEmailAndPassword(userName,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                //Adds user to the database
                                //EMAIL CANNOT BE AN ID!!! B/C OF THE '@'
                              databaseReference = FirebaseDatabase.getInstance("https://journeys-4ee13-default-rtdb.firebaseio.com/").getReference("Users");
                              //get ID and set "Name" of user in DataBase
                              databaseReference.child(mAuth.getUid()).child("Name").setValue(nameOfUser).addOnFailureListener(new OnFailureListener() {
                                  @Override
                                  public void onFailure(@NonNull Exception e) {
                                      Toast.makeText(SignUpActivity.this, "Database error!", Toast.LENGTH_SHORT).show();
                                  }
                              }); //end of addOnFailureListener

                                //generate database fields for our new user
                              databaseReference.child(mAuth.getUid()).child("Steps").child("Sun").setValue(0);
                              databaseReference.child(mAuth.getUid()).child("Steps").child("Mon").setValue(0);
                              databaseReference.child(mAuth.getUid()).child("Steps").child("Tue").setValue(0);
                              databaseReference.child(mAuth.getUid()).child("Steps").child("Wed").setValue(0);
                              databaseReference.child(mAuth.getUid()).child("Steps").child("Thu").setValue(0);
                              databaseReference.child(mAuth.getUid()).child("Steps").child("Fri").setValue(0);
                              databaseReference.child(mAuth.getUid()).child("Steps").child("Sat").setValue(0);
                              databaseReference.child(mAuth.getUid()).child("Steps").child("Life").setValue(0);
                              databaseReference.child(mAuth.getUid()).child("Friends").child("IsFriend").setValue("");
                              databaseReference.child(mAuth.getUid()).child("Friends").child("InReq").setValue("");
                              databaseReference.child(mAuth.getUid()).child("Friends").child("OutReq").setValue("");

                              databaseReference.child(mAuth.getUid()).child("Friends").child("FriendID").setValue("");
                              //generate unique friendId
                                //starts as minimum 4 word sequence, adds another word if sequence already exists
                              final boolean[] uniqueId = {false};
                              String friendId = "";
                              int x = (int)(idGenerator.length* Math.random());
                              friendId += idGenerator[x];
                              x = (int)(idGenerator.length* Math.random());
                              friendId += idGenerator[x];
                              x = (int)(idGenerator.length* Math.random());
                              friendId += idGenerator[x];
                              while(!uniqueId[0]){
                                  uniqueId[0] = true;
                                  x = (int)(idGenerator.length* Math.random());
                                  friendId += idGenerator[x];
                                  final String[] finalFriendId = {friendId};
                                  databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                      @Override
                                      public void onDataChange(@NonNull DataSnapshot snapshot) {
                                          for (DataSnapshot childSnapshot : snapshot.getChildren()){
                                              if(finalFriendId[0].equals(childSnapshot.child("Friends").child("FriendID").getValue().toString())){
                                                  uniqueId[0] = false;
                                                  break;
                                              }
                                          }
                                      }
                                      @Override
                                      public void onCancelled(@NonNull DatabaseError error) {}
                                  });
                              }
                              databaseReference.child(mAuth.getUid()).child("Friends").child("FriendID").setValue(friendId);


                                //success msg if registration is successful
                                Toast.makeText(SignUpActivity.this, "Register Successful!", Toast.LENGTH_SHORT).show();

                                //AFTER REGISTERING SUCCESSFULLY, USER GOES TO SIGN IN PAGE
                                Intent i = new Intent(SignUpActivity.this, SignInActivity.class);
                                startActivity(i);
                                finish();

                            //if registration failed
                            }else{
                                Toast.makeText(SignUpActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                            }

                        }//end of OnComplete
                    });
                } //end else

            } //end of OnClick
        }); //end of SignUp button listener

    } //end of OnCreate
} //end of SignUpActivity












/******OLD CODE********/
        /*
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });*/




    /*
    private void createUser() {
        String email = mEmail.getText().toString();
        String pass = mPwd.getText().toString();

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

    }*/
