package ca.bcit.planters.treepost;

import android.service.autofill.Dataset;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TreeActivity extends AppCompatActivity {

    private static final String TAG = "TreeActivity";

    private String treeId;
    private TextView publicMsg;
    private EditText editNewPubMsg;
    private Button btnPostNewPubMsg;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree);
        treeId = (String) getIntent().getExtras().get("id");
        publicMsg = findViewById(R.id.pub_msg);
        editNewPubMsg = findViewById(R.id.edit_new_pub_msg);
        btnPostNewPubMsg = findViewById(R.id.btn_new_pub_msg);
        btnPostNewPubMsg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Message newMsg = new Message(new Date(), FirebaseUIActivity.currentUser, editNewPubMsg.getText().toString());
                String key = myRef.child("trees").child(treeId).child("publicMsg").push().getKey();
                Map<String, Object> msgValues = newMsg.toMap();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/trees/" + treeId + "/publicMsg/" +  key, msgValues);
                myRef.updateChildren(childUpdates);
            }
        });
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                ShowPublicMsg(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void ShowPublicMsg(DataSnapshot dataSnapshot) {
        StringBuilder sb = new StringBuilder();
        for (DataSnapshot ds : dataSnapshot.child("trees").child(treeId).child("publicMsg").getChildren()) {
            Message pubMsg = ds.getValue(Message.class);
            sb.append(pubMsg.content);
            sb.append("\n");
            sb.append(pubMsg.owner.email);
            sb.append("\n");
            sb.append(pubMsg.timeStamp);
            sb.append("\n\n");
        }
        publicMsg.setText(sb.toString());
    }
}
