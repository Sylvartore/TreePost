package ca.bcit.planters.treepost;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditMessageActivity extends AppCompatActivity {

    private static final String TAG = "EditMessageActivity";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private String msgId;
    private String treeId;
    private String msgType;
    private Message message;

    private EditText msgContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Message");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        msgId = (String) getIntent().getExtras().get("msgId");
        treeId = (String) getIntent().getExtras().get("treeId");
        msgType = (String) getIntent().getExtras().get("msgType");
        msgContent = findViewById(R.id.edit_msg);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                message = dataSnapshot.child("trees").child(treeId).child(msgType).child(msgId).getValue(Message.class);
                msgContent.setText(message.content);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        findViewById(R.id.edit_msg_save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message.content = msgContent.getText().toString();
                Map<String, Object> msgValues = message.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/trees/" + treeId + "/" + msgType + "/" + msgId, msgValues);
                myRef.updateChildren(childUpdates);
                onBackPressed();
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        onBackPressed();
        return true;
    }

}
