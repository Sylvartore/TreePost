package ca.bcit.planters.treepost;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendSelect extends ListActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Map<String, String> friendMap = new HashMap<>();
        final List<String> friendList = new ArrayList<>();
        final String key = getIntent().getExtras().get("key").toString();
        final String msg = getIntent().getExtras().get("msg").toString();
        final String treeId = getIntent().getExtras().get("treeId").toString();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.child("friends").child(FirebaseUIActivity.currentUser.userId).getChildren()) {
                    if (ds.getValue() != null && ds.getKey() != null) {
                        String email = ds.getValue().toString();
                        String userId = ds.getKey();
                        friendMap.put(email, userId);
                        friendList.add(email);
                    }
                }
                if (friendList.isEmpty()) {
                    Toast.makeText(FriendSelect.this, "Add a friend first", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                Toast.makeText(FriendSelect.this, "Please select a friend to send", Toast.LENGTH_SHORT).show();
                getListView().setAdapter(new ArrayAdapter<>(
                        FriendSelect.this, android.R.layout.simple_list_item_1, friendList));

                getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String email = friendList.get(i);
                        String userId = friendMap.get(email);
                        Message newMsg = new Message(key, new Date(), FirebaseUIActivity.currentUser, msg);
                        newMsg.receiver = new User(userId, email);
                        Map<String, Object> msgValues = newMsg.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/trees/" + treeId + "/privateMsg/" + key, msgValues);
                        myRef.updateChildren(childUpdates);
                        finish();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        myRef.addListenerForSingleValueEvent(postListener);
    }
}