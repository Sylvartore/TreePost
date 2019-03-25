package ca.bcit.planters.treepost;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.core.view.Change;

public class ChangeNicknameActivity extends AppCompatActivity {

    private static final String TAG = "ChangeNicknameActivity";
    private FirebaseAuth mAuth;

    EditText nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nickname);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Set Nick Name");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        nickname = findViewById(R.id.set_nickname_edit_text);
        nickname.setText(user.getDisplayName());

        findViewById(R.id.nickname_save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newNickname = nickname.getText().toString();
                if (!newNickname.matches("^[a-zA-Z0-9_]*$")) {
                    Toast.makeText(ChangeNicknameActivity.this, "Invalid Nick Name",
                            Toast.LENGTH_SHORT).show();
                } else {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(newNickname)
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User profile updated.");
                                        FirebaseUIActivity.currentUser.SetNickName(newNickname);
                                        onBackPressed();
                                    }
                                }
                            });
                }
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        onBackPressed();
        return true;
    }
}
