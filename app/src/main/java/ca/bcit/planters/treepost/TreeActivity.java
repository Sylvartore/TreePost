package ca.bcit.planters.treepost;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import java.util.List;
import java.util.Map;

public class TreeActivity extends AppCompatActivity {

    private static final String TAG = "TreeActivity";

    private boolean isPrivateMsg;
    private String msgType;
    private String treeId;
    private EditText editNewPubMsg;

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree);
        getSupportActionBar().hide();
        recyclerView = findViewById(R.id.recycler_view);
        messageList = new ArrayList<>();
        adapter = new MessageAdapter(this, messageList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MessageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(TreeActivity.this, MessageActivity.class);
                intent.putExtra("msgId", messageList.get(position).msgId);
                intent.putExtra("treeId", treeId);
                intent.putExtra("msgType",msgType);
                startActivity(intent);
            }
        });
        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(recyclerView,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipeRight(int position) {
                                return false;
                            }

                            @Override
                            public boolean canSwipeLeft(int position) {
                                return messageList.get(position).owner.userId == FirebaseUIActivity.currentUser.userId;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    DeleteMsg(messageList.get(position).msgId);
                                    messageList.remove(position);
                                    adapter.notifyDataSetChanged();
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {

                            }
                        });

        recyclerView.addOnItemTouchListener(swipeTouchListener);
        treeId = getIntent().getExtras().get("id").toString();
        String type = getIntent().getExtras().get("type").toString();
        ((TextView) findViewById(R.id.label_msg_type)).setText(type);
        isPrivateMsg = "Private Message".equals(type);
        msgType = isPrivateMsg ? "privateMsg" : "publicMsg";

        editNewPubMsg = findViewById(R.id.edit_new_pub_msg);
        editNewPubMsg.bringToFront();
        Button btnPostNewPubMsg = findViewById(R.id.btn_new_pub_msg);
        btnPostNewPubMsg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editNewPubMsg.getText().toString().isEmpty()) {
                    Toast.makeText(TreeActivity.this, "Empty content", Toast.LENGTH_LONG).show();
                    return;
                }
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                String key = myRef.child("trees").child(treeId).child(msgType).push().getKey();
                String msg = editNewPubMsg.getText().toString();

                if (isPrivateMsg) {
                    Intent intent = new Intent(TreeActivity.this, FriendSelect.class);
                    intent.putExtra("key", key);
                    intent.putExtra("msg", msg);
                    intent.putExtra("treeId", treeId);
                    startActivity(intent);
                    return;
                }

                Message newMsg = new Message(key, new Date(), FirebaseUIActivity.currentUser, msg);
                Map<String, Object> msgValues = newMsg.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/trees/" + treeId + "/publicMsg/" + key, msgValues);
                myRef.updateChildren(childUpdates);
                editNewPubMsg.setText("");
            }
        });
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                ShowMsg(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void DeleteMsg(String msgId) {
        myRef.child("trees").child(treeId).child(msgType).child(msgId).removeValue();
    }

    private void ShowMsg(DataSnapshot dataSnapshot) {
        messageList.clear();
        for (DataSnapshot ds : dataSnapshot.child("trees").child(treeId).child(msgType).getChildren()) {
            Message msg = ds.getValue(Message.class);
            String curId = FirebaseUIActivity.currentUser.userId;
            if (isPrivateMsg && msg != null) {
                if (!msg.owner.userId.equals(curId) && !msg.receiver.userId.equals(curId))
                    continue;
            }
            messageList.add(msg);
        }
        adapter.notifyDataSetChanged();
    }
}
