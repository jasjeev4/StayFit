package science.logarithmic.stayfit;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth mAuth;
    private static final String TAG = "SignUpActivity";
    private String emaildomain = "@stayfit.logarithmic.science";

    private BottomNavigationView navigationView;


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_login:
                Intent myIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(myIntent);
                return true;
            case R.id.navigation_signup:
                return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        navigationView = findViewById(R.id.nav_view);
        //Attach the even listener for the navigation view
        navigationView.setOnNavigationItemSelectedListener(this);
        //Set the active tab in the Navigation view
        navigationView.setSelectedItemId(R.id.navigation_signup);

        mAuth = FirebaseAuth.getInstance();

        // Views to be accessed
        final Button signupButton = findViewById(R.id.signup);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        final EditText userEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);




        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Show the loading progress
                //loadingProgressBar.setVisibility(View.VISIBLE);

                //Get user submitted username and password.
                String username = userEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                //Convert username to a mock email for Firebase authentication
                String email = username + emaildomain;

                //Attempt to create account with Firebase
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(SignUpActivity.this, "Logged in",
                                            Toast.LENGTH_SHORT).show();

                                    //Set up SQL lite DB


                                    //Start pedometer service
                                    Intent service = new Intent(SignUpActivity.this, PedometerService.class);
                                    service.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                                    startService(service);

                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.d(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }

                                // ...=
                            }
                        });
            }

        });
    }
}
