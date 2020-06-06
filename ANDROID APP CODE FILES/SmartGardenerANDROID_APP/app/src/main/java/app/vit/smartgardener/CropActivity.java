package app.vit.smartgardener;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

public class CropActivity extends AppCompatActivity {

    Spinner crop,growth;
    Button save;
    ArrayList<String> cropList = new ArrayList<String>(Arrays.asList("Select an Option","Potato","Onion","Tomato","Cauliflower"));
    ArrayList<String> cropStage = new ArrayList<String>(Arrays.asList("Select an Option","Initial Stage","Mid Stage","End Stage"));
    String CropType,CropStage;
    DatabaseReference db;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Crop Details");

        crop = findViewById(R.id.CropSpinner);
        growth = findViewById(R.id.GrowthSpinner);
        save = findViewById(R.id.SaveBtn);
        progressBar = findViewById(R.id.progressBar);

        crop.setAdapter(new ArrayAdapter<String>(CropActivity.this, android.R.layout.simple_spinner_dropdown_item, cropList));
        growth.setAdapter(new ArrayAdapter<String>(CropActivity.this, android.R.layout.simple_spinner_dropdown_item, cropStage));

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(crop.getSelectedItem().equals("Select an Option")) {
                    new AlertDialog.Builder(CropActivity.this)
                            .setTitle("Oops!")
                            .setMessage("Crop name is required")
                            .setNegativeButton("OK", null)
                            .setIcon(R.drawable.ic_alert)
                            .show();
                }else if(growth.getSelectedItem().equals("Select an Option")) {
                    new AlertDialog.Builder(CropActivity.this)
                            .setTitle("Oops!")
                            .setMessage("Growth stage is required")
                            .setNegativeButton("OK", null)
                            .setIcon(R.drawable.ic_alert)
                            .show();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    CropType = crop.getSelectedItem().toString();
                    CropStage = growth.getSelectedItem().toString();
                    db = FirebaseDatabase.getInstance().getReference().child("Kc");

                    if (CropType.equals("Potato")){
                        if (CropStage.equals("Initial Stage")){
                            double x = 0.5;
                            db.setValue(x);
                        }else if(CropStage.equals("Mid Stage")){
                            double x = 1.15;
                            db.setValue(x);
                        }else if(CropStage.equals("End Stage")){
                            double x = 0.3164;
                            db.setValue(x);
                        }
                    }else if (CropType.equals("Onion")){
                        if (CropStage.equals("Initial Stage")){
                            double x = 0.7;
                            db.setValue(x);
                        }else if(CropStage.equals("Mid Stage")){
                            double x = 1.05;
                            db.setValue(x);
                        }else if(CropStage.equals("End Stage")){
                            double x = 0.85;
                            db.setValue(x);
                        }
                    }else if (CropType.equals("Tomato")){
                        if (CropStage.equals("Initial Stage")){
                            double x = 0.6;
                            db.setValue(x);
                        }else if(CropStage.equals("Mid Stage")){
                            double x = 1.322;
                            db.setValue(x);
                        }else if(CropStage.equals("End Stage")){
                            double x = 0.80;
                            db.setValue(x);
                        }
                    }else if (CropType.equals("Cauliflower")){
                        if (CropStage.equals("Initial Stage")){
                            double x = 0.7;
                            db.setValue(x);
                        }else if(CropStage.equals("Mid Stage")){
                            double x = 1.05;
                            db.setValue(x);
                        }else if(CropStage.equals("End Stage")){
                            double x = .95;
                            db.setValue(x);
                        }
                    }

                    progressBar.setVisibility(View.GONE);
                    new AlertDialog.Builder(CropActivity.this)
                            .setTitle("Congratulations!")
                            .setMessage("Crop details saved successfully.")
                            .setNegativeButton("OK", null)
                            .show();

                }
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
