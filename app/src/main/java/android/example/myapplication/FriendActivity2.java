package android.example.myapplication;

import android.content.Intent;
import android.example.myapplication.R;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class FriendActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends2);

        Button friendActivityBtn = findViewById(R.id.friendMain);
        friendActivityBtn.setOnClickListener(view -> {
            Intent i = new Intent(FriendActivity2.this, FriendActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            finish();
        });
        Button friendMakeBtn = findViewById(R.id.friendReq);
        friendMakeBtn.setOnClickListener(view -> {
            Intent i = new Intent(FriendActivity2.this, FriendActivity3.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            finish();
        });

        //end friend buttons

        FriendActivity2 c = this;
        LinearLayout friendHolder = findViewById(R.id.friendHolder);

        TextView myFriendId = findViewById(R.id.myFriendCode);

        //create top nav buttons
        findViewById(R.id.friendMain).setBackgroundColor(Color.YELLOW);
        findViewById(R.id.friendAdd).setBackgroundColor(Color.CYAN);
        findViewById(R.id.friendReq).setBackgroundColor(Color.YELLOW);

        //pull data, generate ui
        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(myId);

        mDatabase.child("Friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendHolder.removeAllViews();
                myFriendId.setText(snapshot.child("FriendID").getValue().toString());

                //check if need to build list
                String pln = snapshot.child("OutReq").getValue().toString();
                if(pln.equals("")){
                    //System.out.println("No friends, return");
                    return;
                }
                String[] s = pln.split(",");
                //start ui build
                for(int i = 0; i < s.length; i++){
                    final int j = i;
                    FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String name = snapshot.child(s[j]).child("Name").getValue().toString();
                            Button b = new Button(c);
                            b.setText(name);
                            b.setOnClickListener(view -> {
                                //cancels outgoing friend request.
                                friendHolder.removeView(b);
                                String newList = "";
                                for(int k = 0; k < s.length; k++){
                                    if(k != j){
                                        newList += s[k] + ",";
                                    }
                                }
                                mDatabase.child("Friends").child("OutReq").setValue(newList);

                                //remove request from other's inrequest area
                                String theirNew = snapshot.child(s[j]).child("Friends").child("InReq").getValue().toString();
                                int rem = theirNew.indexOf(myId);
                                if(rem != -1){
                                    theirNew = theirNew.substring(0,rem) + theirNew.substring(rem + myId.length() + 1);
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(s[j]).child("Friends").child("InReq").setValue(theirNew);
                                }else{
                                    //LOG ERROR: TO REACH HERE, WOULD HAVE HAD TO HAVE A ONE-SIDED RELATION.
                                }
                                s[j] = "";
                            });
                            friendHolder.addView(b);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                //end ui build

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //WRITE LOGFILE
                //System.out.println("DATABASE ERROR");
            }
        });

        //FOOTER ACCESS STUFF
        Button mapBtn = findViewById(R.id.mapButton);
        mapBtn.setOnClickListener(view -> {
            Intent i = new Intent(FriendActivity2.this, MapsActivity.class);
            startActivity(i);
            finish();
        });

        Button friendBtn = findViewById(R.id.friendButton);
        friendBtn.setOnClickListener(view -> {
            Intent i = new Intent(FriendActivity2.this, FriendActivity.class);
            startActivity(i);
            finish();
        });

        Button homeBtn = findViewById(R.id.homeButton);
        homeBtn.setOnClickListener(view -> {
            Intent i = new Intent(FriendActivity2.this, HomeActivity.class);
            startActivity(i);
            finish();
        });
        //END GENERIC MENU
    }

    public void findFriend(View v){
        //add friends
        TextView inString = findViewById(R.id.findFriendField);
        String inStr = inString.getText().toString();

        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference().child("Users");
        mData.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                    if(inStr.toLowerCase().equals(childSnapshot.child("Friends").child("FriendID").getValue().toString().toLowerCase())
                            && !snapshot.child(myId).child("Friends").child("OutReq").getValue().toString().contains(childSnapshot.getKey())){
                        //adds to our out list
                        String newOut = snapshot.child(myId).child("Friends").child("OutReq").getValue() + childSnapshot.getKey() + ",";
                        mData.child(myId).child("Friends").child("OutReq").setValue(newOut);

                        //add to others inList
                        mData.child(childSnapshot.getKey().toString()).child("Friends").child("InReq").setValue(
                                childSnapshot.child("Friends").child("InReq").getValue().toString() + myId + ",");

                        found = true;
                        break;
                    }
                }
                if(!found){
                    //prompt not found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}