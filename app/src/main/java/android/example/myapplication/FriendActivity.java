package android.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        Button friendActivity2Btn = findViewById(R.id.friendAdd);
        friendActivity2Btn.setOnClickListener(view -> {
            Intent i = new Intent(FriendActivity.this, FriendActivity2.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            finish();
        });
        Button friendMakeBtn = findViewById(R.id.friendReq);
        friendMakeBtn.setOnClickListener(view -> {
            Intent i = new Intent(FriendActivity.this, FriendActivity3.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            finish();
        });
        //end friend buttons

        FriendActivity c = this;
        LinearLayout friendHolder = findViewById(R.id.friendHolder);

        findViewById(R.id.friendMain).setBackgroundColor(Color.CYAN);
        findViewById(R.id.friendAdd).setBackgroundColor(Color.YELLOW);
        findViewById(R.id.friendReq).setBackgroundColor(Color.YELLOW);

        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(myId);
        mDatabase.child("Friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendHolder.removeAllViews();
                String pln = snapshot.child("IsFriend").getValue().toString();
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
                            b.setText(name);
                            b.setOnClickListener(view -> {
                                Intent i = new Intent(FriendActivity.this, ChatActivity.class);
                                i.putExtra("otherUser",s[j]);
                                startActivity(i);
                                finish();

                            });
                            b.setOnLongClickListener(view -> {
                                AlertDialog.Builder alert = new AlertDialog.Builder(c);
                                alert.setTitle("Confirm remove friend?");
                                //alert.setMessage("This will delete all chat history as well.");
                                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String newFriends = "";
                                        for(int k = 0; k < s.length; k++){
                                            if(k != j){
                                                newFriends += s[k] + ",";
                                            }
                                        }
                                        mDatabase.child("Friends").child("IsFriend").setValue(newFriends);
                                        String theirNew = snapshot.child(s[j]).child("Friends").child("IsFriend").getValue().toString();
                                        int rem = theirNew.indexOf(myId);
                                        if(rem != -1){
                                            theirNew = theirNew.substring(0,rem) + theirNew.substring(rem + myId.length() + 1);
                                            FirebaseDatabase.getInstance().getReference().child("Users").child(s[j]).child("Friends").child("IsFriend").setValue(theirNew);
                                        }else{
                                            //LOG ERROR: TO REACH HERE, WOULD HAVE HAD TO HAVE A ONE-SIDED FRIEND RELATION.
                                        }
                                        dialog.dismiss();
                                    }
                                });

                                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                alert.show();

                                return false;
                            });
                            friendHolder.addView(b);
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
            Intent i = new Intent(FriendActivity.this, MapsActivity.class);
            startActivity(i);
            finish();
        });

        Button friendBtn = findViewById(R.id.friendButton);
        friendBtn.setOnClickListener(view -> {
            Intent i = new Intent(FriendActivity.this, FriendActivity.class);
            startActivity(i);
            finish();
        });

        Button homeBtn = findViewById(R.id.homeButton);
        homeBtn.setOnClickListener(view -> {
            Intent i = new Intent(FriendActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        });
        //END GENERIC MENU
    }

}
