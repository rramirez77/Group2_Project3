package android.example.myapplication;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendActivity3 extends AppCompatActivity {

    /**
     * init
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        FriendActivity3 c = this;
        LinearLayout friendHolder = findViewById(R.id.friendHolder);
        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //top nav buttons
        Button friendActivityBtn = findViewById(R.id.friendMain);
        friendActivityBtn.setBackgroundColor(Color.YELLOW);
        friendActivityBtn.setOnClickListener(view -> {
            Intent i = new Intent(FriendActivity3.this, FriendActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            finish();
        });

        Button friendAddBtn = findViewById(R.id.friendAdd);
        friendAddBtn.setBackgroundColor(Color.YELLOW);
        friendAddBtn.setOnClickListener(view -> {
            Intent i = new Intent(FriendActivity3.this, FriendActivity2.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            finish();
        });

        findViewById(R.id.friendReq).setBackgroundColor(Color.CYAN);
        //end friend buttons


        /*REMOVED: addFriend MOVED TO OWN ACTIVITY
        //set height because addFriend needs room for add button
        ScrollView layout = findViewById(R.id.friendHolderHolder);
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        System.out.println(params.height + " " + params.width);
        params.height = 1260;
        layout.setLayoutParams(params);*/

        //build ui
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
                //get all on inc req list
                String[] s = pln.split(",");
                for(int i = 0; i < s.length; i++){
                    final int j = i;
                    FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String name = snapshot.child(s[j]).child("Name").getValue().toString();

                            //build name display / accept / reject incoming friend requests
                            LinearLayout buttonHolder = new LinearLayout(c);

                            Button b = new Button(c);
                            b.setWidth(700);
                            b.setText(name);

                            ImageButton accept = new ImageButton(c);
                            Drawable accImg = ResourcesCompat.getDrawable(c.getResources(), R.drawable.accepticon, null);
                            accept.setBackground(accImg);

                            ImageButton deny = new ImageButton(c);
                            Drawable denyImg = ResourcesCompat.getDrawable(c.getResources(), R.drawable.denyicon, null);
                            deny.setBackground(denyImg);

                            buttonHolder.addView(b);
                            buttonHolder.addView(accept);
                            buttonHolder.addView(deny);

                            //name button, when clicked, will alternately display their account name and friend code
                            b.setOnClickListener (view ->{
                                if(b.getText().equals(name)){
                                    b.setText(snapshot.child(s[j]).child("Friends").child("FriendID").getValue().toString());
                                }else{
                                    b.setText(name);
                                }

                            });

                            //accept friend request button
                            accept.setOnClickListener(view ->{
                                //removes request from my list
                                friendHolder.removeView(buttonHolder);
                                String newList = "";
                                for(int k = 0; k < s.length; k++){
                                    if(k != j){
                                        newList += s[k] + ",";
                                    }
                                }
                                mDatabase.child("Friends").child("InReq").setValue(newList);

                                //accept friend request. changes my friend list and friend's friend list.
                                mDatabase.child("Friends").child("IsFriend").setValue(s[j] + "," + snapshot.child(myId).child("Friends").child("IsFriend").getValue().toString());
                                FirebaseDatabase.getInstance().getReference().child("Users").child(s[j]).child("Friends").child("IsFriend").setValue(myId + "," + snapshot.child(s[j]).child("Friends").child("IsFriend").getValue().toString());


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

                            //decline incoming friend request button
                            deny.setOnClickListener(view -> {
                                //remove from my side
                                friendHolder.removeView(buttonHolder);
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
                            friendHolder.addView(buttonHolder);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
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
