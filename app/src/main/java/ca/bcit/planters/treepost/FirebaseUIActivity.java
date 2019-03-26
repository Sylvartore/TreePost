package ca.bcit.planters.treepost;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseUIActivity extends AppCompatActivity {

    public static User currentUser;

    private static final String TAG = "FirebaseUIActivity";

    private FirebaseAuth mAuth;

    private EditText mEmail, mPassword;
    private Button btnLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_ui);
        getSupportActionBar().hide();
        findViewById(R.id.login_layout).requestFocus();
        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.input_email);
        mPassword = findViewById(R.id.input_password);
        btnLogIn = findViewById(R.id.btn_login);
        btnLogIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                if (!email.equals("") && !password.equals("")) {
                    if (!email.matches("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)" +
                            "*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
                        Toast.makeText(FirebaseUIActivity.this,
                                "A valid email is required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    btnLogIn.setEnabled(false);
                    final ProgressDialog progressDialog = new ProgressDialog(FirebaseUIActivity.this,
                            R.style.Theme_AppCompat_Light);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Authenticating...");
                    progressDialog.show();
                    SignIn(email, password);
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                    btnLogIn.setEnabled(true);
                                }
                            }, 3000);
                } else {
                    Toast.makeText(FirebaseUIActivity.this,
                            "Please enter email and password", Toast.LENGTH_SHORT).show();
                }

            }
        });

        findViewById(R.id.link_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirebaseUIActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void SignIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            currentUser = new User(user.getUid(), user.getEmail(), user.getDisplayName());
                            Toast.makeText(FirebaseUIActivity.this, "Hello " + user.getDisplayName(),
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(FirebaseUIActivity.this, MainActivity.class);
                            intent.putExtra("email", user.getEmail());
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(FirebaseUIActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}