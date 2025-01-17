package science.logarithmic.stayfit;

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

// Logging in is supported by Firebase

public class LoginActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{


    // Use Firebase to login a user using a username which is made into a mock email

    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";
    private String emaildomain = "@stayfit.logarithmic.science";

    private BottomNavigationView navigationView;


    // Handle the bottom navigation's clicks
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_login:
                return true;
            case R.id.navigation_signup:
                // Switch to signup page
                Intent myIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(myIntent);
                return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the listener for the navigation view
        navigationView = findViewById(R.id.nav_view);
        //Attach the even listener for the navigation view
        navigationView.setOnNavigationItemSelectedListener(this);
        //Set the active tab in the Navigation view
        navigationView.setSelectedItemId(R.id.navigation_login);

        // Setup firebase authentication
        mAuth = FirebaseAuth.getInstance();

        // Views to be accessed
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        final EditText userEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);




        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Show the loading progress
                //loadingProgressBar.setVisibility(View.VISIBLE);

                //Get user submitted username and password.
                String username = userEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                //Convert username to a mock email for Firebase authentication
                String email = username + emaildomain;

                //Attempt to login with Firebase. Might only accept emails.
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(LoginActivity.this, "Logged in",
                                            Toast.LENGTH_SHORT).show();

                                    // Change to stats page
                                    Intent myIntent = new Intent(LoginActivity.this, StatsActivity.class);
                                    startActivity(myIntent);

                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.d(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });
    }
}
