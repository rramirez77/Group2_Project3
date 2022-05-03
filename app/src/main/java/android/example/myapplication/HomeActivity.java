package android.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button TOSTEP = findViewById(R.id.TOSTEPCOUNTER);
        TOSTEP.setOnClickListener(view -> {
            Intent i = new Intent(HomeActivity.this, StepActivity.class);
            startActivity(i);
        });

        Calendar calendar = Calendar.getInstance();
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

        mDatabase.child("Steps").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int day = calendar.DAY_OF_WEEK;
                String steps = "";
                switch(day){
                    case 1:
                        steps = snapshot.child("Mon").getValue().toString();
                        break;
                    case 2:
                        steps = snapshot.child("Tue").getValue().toString();
                        break;
                    case 3:
                        steps = snapshot.child("Wed").getValue().toString();
                        break;
                    case 4:
                        steps = snapshot.child("Thu").getValue().toString();
                        break;
                    case 5:
                        steps = snapshot.child("Fri").getValue().toString();
                        break;
                    case 6:
                        steps = snapshot.child("Sat").getValue().toString();
                        break;
                    case 7:
                        steps = snapshot.child("Sun").getValue().toString();
                        break;
                }

                /*int steps = Integer.parseInt(snapshot.child("Sun").getValue().toString()) +
                        Integer.parseInt(snapshot.child("Mon").getValue().toString()) +
                        Integer.parseInt(snapshot.child("Tue").getValue().toString()) +
                        Integer.parseInt(snapshot.child("Wed").getValue().toString()) +
                        Integer.parseInt(snapshot.child("Thu").getValue().toString()) +
                        Integer.parseInt(snapshot.child("Fri").getValue().toString()) +
                        Integer.parseInt(snapshot.child("Sat").getValue().toString());*/

                wstepText.setText("Today, you've taken\n" + steps + " steps!");

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

        //FOOTER ACCESS STUFF
        Button mapBtn = findViewById(R.id.mapButton);
        mapBtn.setOnClickListener(view -> {
            Intent i = new Intent(HomeActivity.this, MapsActivity.class);
            startActivity(i);
        });

        Button friendBtn = findViewById(R.id.friendButton);
        friendBtn.setOnClickListener(view -> {
            Intent i = new Intent(HomeActivity.this, FriendActivity.class);
            startActivity(i);

        });

        Button homeBtn = findViewById(R.id.homeButton);
        homeBtn.setOnClickListener(view -> {
            Intent i = new Intent(HomeActivity.this, HomeActivity.class);
            startActivity(i);

        });
        //END GENERIC MENU
    }

}