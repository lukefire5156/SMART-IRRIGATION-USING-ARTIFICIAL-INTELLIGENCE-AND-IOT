package app.vit.smartgardener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class OtpVerification extends AppCompatActivity {

    private EditText otp;
    private Button verify;
    private String number,verificationId;
    private FirebaseAuth mAuth;
    ProgressBar verifyProgress;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("OTP Verification");

        otp = findViewById(R.id.OTP);
        verify = findViewById(R.id.Verify);
        number = getIntent().getStringExtra("number");
        mAuth = FirebaseAuth.getInstance();
        verifyProgress = findViewById(R.id.verifyProgressBar);

        Log.e("number",number);

        /*sendVerificationCode(number);*/
        Log.e("sendverify no:",number);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        String code = phoneAuthCredential.getSmsCode();
                        if (code != null) {
                            //Code is Detected Automatically
                            otp.setText(code);
                            verifyCode(code);
                        }
                    }

                    @Override
                    public void onCodeSent(@NonNull String s,@NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationId = s;
                        Log.d("verificationID:",verificationId);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        verifyProgress.setVisibility(View.GONE);
                        Toast.makeText(OtpVerification.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code=otp.getText().toString().trim();
                if(code.isEmpty()||code.length()<6){
                    otp.setError("Enter the Code ...");
                    otp.requestFocus();
                    return;
                }
                verifyCode(code);
            }
        });
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }



    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser users = FirebaseAuth.getInstance().getCurrentUser();

                            startActivity(new Intent(OtpVerification.this,MainActivity.class));
                            Toast.makeText(OtpVerification.this, "LOGIN SUCCESSFUL", Toast.LENGTH_LONG).show();


                        } else {
                            verifyProgress.setVisibility(View.GONE);
                            Toast.makeText(OtpVerification.this, "Verification Failed", Toast.LENGTH_SHORT).show();
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

