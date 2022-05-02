package android.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Iterator;

public class ChatActivity extends AppCompatActivity {

    /**
     * user id as in database
     */
    static String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    /**
     * chatroom id
     */
    static String chatLog = "";

    /**
     * init
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ChatActivity c = this;
        TextView chatName = findViewById(R.id.chatName);
        LinearLayout chatHolder = findViewById(R.id.chatHolder);

        //identify other user's name
        chatName.setText(getIntent().getStringExtra("otherName"));
        chatLog = myId;
        String other = getIntent().getStringExtra("otherId");
        if(other.compareTo(chatLog) > 0){
            chatLog = chatLog + other;
        }else{
            chatLog = other + chatLog;
        }

        DatabaseReference chatData = FirebaseDatabase.getInstance().getReference().child("Chat").child(chatLog);
        Query recentPostsQuery = chatData.limitToLast(50);//last X messages will be displayed
        recentPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() != 0){//avoid null chat errors when no chat history exists
                    Iterator<DataSnapshot> iter = snapshot.getChildren().iterator();
                    for(int i = 0; i < snapshot.getChildrenCount() - 1; i++){
                        DataSnapshot x = iter.next();
                    //}
                    //for(DataSnapshot x: snapshot.getChildren()){
                        //displays last x from query line above messages
                        Instant instant = Instant.parse(x.child("Time").getValue().toString());
                        String datetime = instant.atZone(ZoneId.of(ZoneId.systemDefault().getId())).toString();

                        //formats into human friendly string
                        String printTime = "";

                        if(datetime.charAt(5) == '0'){
                            printTime += datetime.charAt(6) + "/";
                        }else{
                            printTime += datetime.substring(5,7) + "/";
                        }
                        if(datetime.charAt(8) == '0'){
                            printTime += datetime.charAt(9) + " ";
                        }else{
                            printTime += datetime.substring(8,10) + " ";
                        }
                        int hour = Integer.parseInt(datetime.substring(11,13));
                        if(hour < 1){
                            printTime = "12:" + datetime.substring(14,16) + " AM";
                        }else if(hour < 12){
                            printTime = hour + ":" + datetime.substring(14,16) + " AM";
                        }else if(hour < 13){
                            printTime = "12:" + datetime.substring(14,16) + " PM";
                        }else{
                            printTime = (hour-12) + ":" + datetime.substring(14,16) + " PM";
                        }

                        //determine who sent message and build proper entry
                        if(x.child("Sender").getValue().toString().equals(myId)){
                            //I sent this message
                            LinearLayout txtHold = new LinearLayout(c);

                            //spacer
                            ImageView pushBar = new ImageView(c);
                            pushBar.setBackground(ResourcesCompat.getDrawable(c.getResources(), R.drawable.spacerbar, null));
                            chatHolder.addView(pushBar);

                            //indent
                            ImageView pusher = new ImageView(c);
                            Drawable pushImg = ResourcesCompat.getDrawable(c.getResources(), R.drawable.blank, null);
                            pusher.setBackground(pushImg);
                            txtHold.addView(pusher);

                            //actual message
                            TextView txt = new TextView(c);
                            txt.setBackgroundColor(Color.CYAN);
                            txt.setTextSize(22);
                            txt.setWidth(800);
                            String s = printTime + "\n" + x.child("Msg").getValue().toString();
                            SpannableString ss1 = new SpannableString(s);
                            ss1.setSpan(new RelativeSizeSpan(.75f), 0, printTime.length(), 0); // set size
                            ss1.setSpan(new StyleSpan(Typeface.ITALIC), 0, 5, 0);// set color
                            txt.setText(ss1);
                            txtHold.addView(txt);

                            chatHolder.addView(txtHold);
                        }else{
                            //I received this message
                            LinearLayout txtHold = new LinearLayout(c);

                            //spacer
                            ImageView pushBar = new ImageView(c);
                            pushBar.setBackground(ResourcesCompat.getDrawable(c.getResources(), R.drawable.spacerbar, null));
                            chatHolder.addView(pushBar);

                            //actual message
                            TextView txt = new TextView(c);
                            txt.setBackgroundColor(Color.YELLOW);
                            txt.setTextSize(22);
                            txt.setWidth(800);
                            String s = printTime + "\n" + x.child("Msg").getValue().toString();
                            SpannableString ss1 = new SpannableString(s);
                            ss1.setSpan(new RelativeSizeSpan(.75f), 0, printTime.length(), 0); // set size
                            ss1.setSpan(new StyleSpan(Typeface.ITALIC), 0, 5, 0);// set color
                            txt.setText(ss1);
                            txtHold.addView(txt);

                            chatHolder.addView(txtHold);
                        }
                    }
                    //after building chat history, scroll to bottom
                    ScrollView chatHolderHolder = findViewById(R.id.chatHolderHolder);
                    chatHolderHolder.post(new Runnable() {
                        @Override
                        public void run() {
                            chatHolderHolder.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                    chatHolderHolder.setVerticalScrollBarEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //end init build

        //every time database recieves new message, will populate new message in to app
        recentPostsQuery = chatData.limitToLast(1);//last X messages will be displayed
        recentPostsQuery.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() != 0){//avoid null chat errors when no chat history exists
                    for(DataSnapshot x: snapshot.getChildren()){
                        //displays last x from query line above messages
                        Instant instant = Instant.parse(x.child("Time").getValue().toString());
                        String datetime = instant.atZone(ZoneId.of(ZoneId.systemDefault().getId())).toString();

                        //formats into human friendly string
                        String printTime = "";

                        if(datetime.charAt(5) == '0'){
                            printTime += datetime.charAt(6) + "/";
                        }else{
                            printTime += datetime.substring(5,7) + "/";
                        }
                        if(datetime.charAt(8) == '0'){
                            printTime += datetime.charAt(9) + " ";
                        }else{
                            printTime += datetime.substring(8,10) + " ";
                        }
                        int hour = Integer.parseInt(datetime.substring(11,13));
                        if(hour < 1){
                            printTime = "12:" + datetime.substring(14,16) + " AM";
                        }else if(hour < 12){
                            printTime = hour + ":" + datetime.substring(14,16) + " AM";
                        }else if(hour < 13){
                            printTime = "12:" + datetime.substring(14,16) + " PM";
                        }else{
                            printTime = (hour-12) + ":" + datetime.substring(14,16) + " PM";
                        }

                        //determine who sent message and build proper entry
                        if(x.child("Sender").getValue().toString().equals(myId)){
                            //I sent this message
                            LinearLayout txtHold = new LinearLayout(c);

                            //spacer
                            ImageView pushBar = new ImageView(c);
                            pushBar.setBackground(ResourcesCompat.getDrawable(c.getResources(), R.drawable.spacerbar, null));
                            chatHolder.addView(pushBar);

                            //indent
                            ImageView pusher = new ImageView(c);
                            Drawable pushImg = ResourcesCompat.getDrawable(c.getResources(), R.drawable.blank, null);
                            pusher.setBackground(pushImg);
                            txtHold.addView(pusher);

                            //actual message
                            TextView txt = new TextView(c);
                            txt.setBackgroundColor(Color.CYAN);
                            txt.setTextSize(22);
                            txt.setWidth(800);
                            String s = printTime + "\n" + x.child("Msg").getValue().toString();
                            SpannableString ss1 = new SpannableString(s);
                            ss1.setSpan(new RelativeSizeSpan(.75f), 0, printTime.length(), 0); // set size
                            ss1.setSpan(new StyleSpan(Typeface.ITALIC), 0, 5, 0);// set color
                            txt.setText(ss1);
                            txtHold.addView(txt);

                            chatHolder.addView(txtHold);
                        }else{
                            //I received this message
                            LinearLayout txtHold = new LinearLayout(c);

                            //spacer
                            ImageView pushBar = new ImageView(c);
                            pushBar.setBackground(ResourcesCompat.getDrawable(c.getResources(), R.drawable.spacerbar, null));
                            chatHolder.addView(pushBar);

                            //actual message
                            TextView txt = new TextView(c);
                            txt.setBackgroundColor(Color.YELLOW);
                            txt.setTextSize(22);
                            txt.setWidth(800);
                            String s = printTime + "\n" + x.child("Msg").getValue().toString();
                            SpannableString ss1 = new SpannableString(s);
                            ss1.setSpan(new RelativeSizeSpan(.75f), 0, printTime.length(), 0); // set size
                            ss1.setSpan(new StyleSpan(Typeface.ITALIC), 0, 5, 0);// set color
                            txt.setText(ss1);
                            txtHold.addView(txt);

                            chatHolder.addView(txtHold);
                        }
                    }
                    //after building chat history, scroll to bottom
                    ScrollView chatHolderHolder = findViewById(R.id.chatHolderHolder);
                    chatHolderHolder.post(new Runnable() {
                        @Override
                        public void run() {
                            chatHolderHolder.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                    chatHolderHolder.setVerticalScrollBarEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //FOOTER ACCESS STUFF
        Button mapBtn = findViewById(R.id.mapButton);
        mapBtn.setOnClickListener(view -> {
            Intent i = new Intent(ChatActivity.this, MapsActivity.class);
            startActivity(i);
            finish();
        });

        Button friendBtn = findViewById(R.id.friendButton);
        friendBtn.setOnClickListener(view -> {
            Intent i = new Intent(ChatActivity.this, FriendActivity.class);
            startActivity(i);
            finish();
        });

        Button homeBtn = findViewById(R.id.homeButton);
        homeBtn.setOnClickListener(view -> {
            Intent i = new Intent(ChatActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        });
        //END GENERIC MENU

    }

    /**
     *
     *
     *
     * @param v
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendMsg(View v){
        EditText sendM = findViewById(R.id.editTextReturn);
        String s = sendM.getText().toString();
        if(s.isEmpty()){
            return;
        }
        DatabaseReference chatData = FirebaseDatabase.getInstance().getReference().child("Chat").child(chatLog);
        String sendTime = Instant.now().toString();
        String dbTime = sendTime.replaceAll("\\.",":");
        chatData.child(dbTime).child("Sender").setValue(myId);
        chatData.child(dbTime).child("Time").setValue(sendTime);
        chatData.child(dbTime).child("Msg").setValue(s);
        sendM.setText("");
    }

    public void closeChat(View v){
        finish();
    }

}
