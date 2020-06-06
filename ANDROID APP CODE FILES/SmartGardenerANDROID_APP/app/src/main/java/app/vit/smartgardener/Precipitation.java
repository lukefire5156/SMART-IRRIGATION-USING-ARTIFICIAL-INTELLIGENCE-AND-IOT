package app.vit.smartgardener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Precipitation extends AppCompatActivity {

    TextView precip;
    DatabaseReference db;
    ProgressBar precipPB;

    TextView humTV;
    ProgressBar humPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_precipitation);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Precipitation and Humidity");

        precip = findViewById(R.id.PrecipAmount);
        precipPB = findViewById(R.id.PrecipPB);

        humTV = findViewById(R.id.humidityPercentage);
        humPB = findViewById(R.id.humidityPB);

        db = FirebaseDatabase.getInstance().getReference();


        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String temperature = dataSnapshot.child("PrecipProbability").getValue().toString();
                precip.setText(temperature.substring(0,3)+"mm");

                String hum = dataSnapshot.child("DisplayHumidity").getValue().toString();
                humTV.setText(hum.substring(0,2)+"%");

                precipPB.setVisibility(View.GONE);
                precip.setVisibility(View.VISIBLE);

                humPB.setVisibility(View.GONE);
                humTV.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                precipPB.setVisibility(View.GONE);
                humPB.setVisibility(View.GONE);
                new AlertDialog.Builder(Precipitation.this)
                        .setTitle("Oops!")
                        .setMessage("Something went wrong!")
                        .setNegativeButton("Cancel", null)
                        .setIcon(R.drawable.ic_alert)
                        .show();
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            // back button
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
