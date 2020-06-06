package app.vit.smartgardener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MobilePhoneActivity extends AppCompatActivity {

    private EditText number;
    private Button requestOTP;
    String No;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference dbRegister,dbUser;
    ProgressBar requestPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_phone);

        ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Smart Gardener");

        number = findViewById(R.id.mobileNo);
        requestOTP = findViewById(R.id.requestOTP);
        requestPB = findViewById(R.id.RequestProgressBar);

        requestOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(number.getText().toString())){
                    number.setError("Number is required");
                    number.requestFocus();
                    Toast.makeText(MobilePhoneActivity.this, "Enter No ....", Toast.LENGTH_SHORT).show();
                    return;

                }
                else if(number.getText().toString().replace(" ","").length()!=10){
                    number.setError("Enter Correct No");
                    number.requestFocus();
                    Toast.makeText(MobilePhoneActivity.this, "Enter Correct No ...", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    requestPB.setVisibility(View.VISIBLE);
                    No = number.getText().toString();
                   Intent intent = new Intent(MobilePhoneActivity.this,OtpVerification.class);
                    intent.putExtra("number","+91"+No);
                    startActivity(intent);
                    finish();

                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(MobilePhoneActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed(){
        finishAffinity();
    }



}
