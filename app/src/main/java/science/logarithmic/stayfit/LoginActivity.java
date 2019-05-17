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

public class LoginActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";
    private String emaildomain = "@stayfit.logarithmic.science";

    private BottomNavigationView navigationView;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_login:
                return true;
            case R.id.navigation_signup:
                Toast.makeText(LoginActivity.this, "Clicked signup",
                        Toast.LENGTH_SHORT).show();
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

        navigationView = findViewById(R.id.nav_view);
        navigationView.setOnNavigationItemSelectedListener(this);

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

                //Attempt to login with firebase. Might only accept emails.
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(LoginActivity.this, "Logged in",
                                            Toast.LENGTH_SHORT).show();

                                    // Start pedometer service
                                    Intent service = new Intent(LoginActivity.this, PedometerService.class);
                                    service.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                                    startService(service);

                                    // Change to stats page
                                    Intent myIntent = new Intent(LoginActivity.this, StatsActivity.class);
                                    startActivity(myIntent);

                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.d(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }

                                // ...
                            }
                        });
            }
        });
    }
}
