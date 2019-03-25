package ca.bcit.planters.treepost;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class UserProfileActivity extends AppCompatActivity {

    ImageButton nick_name_edit_btn;
    TextView nick_name_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Account Info");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        nick_name_edit_btn = findViewById(R.id.profile_nickname_btn);
        nick_name_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfileActivity.this, ChangeNicknameActivity.class));
            }
        });
        nick_name_tv.setText(FirebaseUIActivity.currentUser.nickname);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        onBackPressed();
        return true;
    }
}
