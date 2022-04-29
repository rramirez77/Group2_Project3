package android.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HomeActivity extends AppCompatActivity {

    private Button startBtn;
    private Button stopBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        startBtn = findViewById(R.id.Start_btn);
        stopBtn = findViewById(R.id.Stop_btn);

        startBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, MapsActivity.class);
                startActivity(i);
                finish();
            }
        }); //end of start button listener

        TextView welcome = findViewById(R.id.welcome);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mDatabase.child("Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                welcome.setText("Hello, " + snapshot.getValue() + "!");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //WRITE LOGFILE
                System.out.println("DATABASE ERROR");
            }
        });

        TextView wstepText = findViewById(R.id.weekCounterText);
        TextView stepText = findViewById(R.id.counterText);
        /*
        mDatabase.child("Steps").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int steps = Integer.parseInt(snapshot.child("Sun").getValue().toString()) +
                        Integer.parseInt(snapshot.child("Mon").getValue().toString()) +
                        Integer.parseInt(snapshot.child("Tue").getValue().toString()) +
                        Integer.parseInt(snapshot.child("Wed").getValue().toString()) +
                        Integer.parseInt(snapshot.child("Thu").getValue().toString()) +
                        Integer.parseInt(snapshot.child("Fri").getValue().toString()) +
                        Integer.parseInt(snapshot.child("Sat").getValue().toString());

                wstepText.setText("You've taken " + steps + " steps\nso far this week!");
                stepText.setText(snapshot.child("Sun").getValue() +
                        "\n" + snapshot.child("Mon").getValue() +
                        "\n" + snapshot.child("Tue").getValue() +
                        "\n" + snapshot.child("Wed").getValue() +
                        "\n" + snapshot.child("Thu").getValue() +
                        "\n" + snapshot.child("Fri").getValue() +
                        "\n" + snapshot.child("Sat").getValue() +
                        "\n\n" + snapshot.child("Life").getValue());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //WRITE LOGFILE
                System.out.println("DATABASE ERROR");
            }
        });
        */
        //FOOTER ACCESS STUFF
        Button mapBtn = findViewById(R.id.mapButton);
        mapBtn.setOnClickListener(view -> {
            Intent i = new Intent(HomeActivity.this, MapsActivity.class);
            startActivity(i);
            finish();
        });

        Button friendBtn = findViewById(R.id.friendButton);
        friendBtn.setOnClickListener(view -> {
            Intent i = new Intent(HomeActivity.this, FriendActivity.class);
            startActivity(i);
            finish();
        });

        Button homeBtn = findViewById(R.id.homeButton);
        homeBtn.setOnClickListener(view -> {
            Intent i = new Intent(HomeActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        });
        //END GENERIC MENU
    }


}