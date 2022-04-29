package android.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        Button friendActivityBtn = findViewById(R.id.friendMain);
        friendActivityBtn.setOnClickListener(view -> {
            Intent i = new Intent(FriendActivity3.this, FriendActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            finish();
        });
        Button friendAddBtn = findViewById(R.id.friendAdd);
        friendAddBtn.setOnClickListener(view -> {
            Intent i = new Intent(FriendActivity3.this, FriendActivity2.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            finish();
        });
        //end friend buttons

        FriendActivity3 c = this;
        LinearLayout friendHolder = findViewById(R.id.friendHolder);

        /*REMOVED: addFriend MOVED TO OWN ACTIVITY
        //set height because addFriend needs room for add button
        ScrollView layout = findViewById(R.id.friendHolderHolder);
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        System.out.println(params.height + " " + params.width);
        params.height = 1260;
        layout.setLayoutParams(params);*/

        findViewById(R.id.friendMain).setBackgroundColor(Color.YELLOW);
        findViewById(R.id.friendAdd).setBackgroundColor(Color.YELLOW);
        findViewById(R.id.friendReq).setBackgroundColor(Color.CYAN);

        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(myId);
        mDatabase.child("Friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendHolder.removeAllViews();
                String pln = snapshot.child("InReq").getValue().toString();
                if(pln.equals("")){
                    //System.out.println("No friends, return");
                    return;
                }
                String[] s = pln.split(",");
                for(int i = 0; i < s.length; i++){
                    final int j = i;
                    FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String name = snapshot.child(s[j]).child("Name").getValue().toString();
                            Button b = new Button(c);
                            Button accept = new Button(c);
                            Button deny = new Button(c);
                            accept.setText("Accept");
                            deny.setText("Reject");
                            b.setText(name);
                            accept.setOnClickListener(view ->{
                                //accept friend request. changes my friend list and friend's friend list.
                                mDatabase.child("Friends").child("IsFriend").setValue(s[j] + "," + snapshot.child(myId).child("Friends").child("IsFriend").getValue().toString());
                                FirebaseDatabase.getInstance().getReference().child("Users").child(s[j]).child("Friends").child("IsFriend").setValue(myId + "," + snapshot.child(s[j]).child("Friends").child("IsFriend").getValue().toString());

                                //removes request from my list
                                friendHolder.removeView(b);
                                friendHolder.removeView(accept);
                                friendHolder.removeView(deny);
                                String newList = "";
                                for(int k = 0; k < s.length; k++){
                                    if(k != j){
                                        newList += s[k] + ",";
                                    }
                                }
                                mDatabase.child("Friends").child("InReq").setValue(newList);

                                //remove request from other's outrequest area
                                String theirNew = snapshot.child(s[j]).child("Friends").child("OutReq").getValue().toString();
                                int rem = theirNew.indexOf(myId);
                                if(rem != -1){
                                    theirNew = theirNew.substring(0,rem) + theirNew.substring(rem + myId.length() + 1);
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(s[j]).child("Friends").child("OutReq").setValue(theirNew);
                                }else{
                                    //LOG ERROR: TO REACH HERE, WOULD HAVE HAD TO HAVE A ONE-SIDED RELATION.
                                }
                                s[j] = "";

                            });
                            deny.setOnClickListener(view -> {
                                //remove from my side
                                friendHolder.removeView(b);
                                friendHolder.removeView(accept);
                                friendHolder.removeView(deny);
                                String newList = "";
                                for(int k = 0; k < s.length; k++){
                                    if(k != j){
                                        newList += s[k] + ",";
                                    }
                                }
                                mDatabase.child("Friends").child("InReq").setValue(newList);

                                //remove request from other's outrequest area
                                String theirNew = snapshot.child(s[j]).child("Friends").child("OutReq").getValue().toString();
                                int rem = theirNew.indexOf(myId);
                                if(rem != -1){
                                    theirNew = theirNew.substring(0,rem) + theirNew.substring(rem + myId.length() + 1);
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(s[j]).child("Friends").child("OutReq").setValue(theirNew);
                                }else{
                                    //LOG ERROR: TO REACH HERE, WOULD HAVE HAD TO HAVE A ONE-SIDED RELATION.
                                }
                                s[j] = "";
                            });
                            friendHolder.addView(b);
                            friendHolder.addView(accept);
                            friendHolder.addView(deny);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //WRITE LOGFILE
                System.out.println("DATABASE ERROR");
            }
        });


        //FOOTER ACCESS STUFF
        Button mapBtn = findViewById(R.id.mapButton);
        mapBtn.setOnClickListener(view -> {
            Intent i = new Intent(FriendActivity3.this, MapsActivity.class);
            startActivity(i);
            finish();
        });

        Button friendBtn = findViewById(R.id.friendButton);
        friendBtn.setOnClickListener(view -> {
            Intent i = new Intent(FriendActivity3.this, FriendActivity.class);
            startActivity(i);
            finish();
        });

        Button homeBtn = findViewById(R.id.homeButton);
        homeBtn.setOnClickListener(view -> {
            Intent i = new Intent(FriendActivity3.this, HomeActivity.class);
            startActivity(i);
            finish();
        });
        //END GENERIC MENU
    }

}
