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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TreeActivity extends AppCompatActivity {

    private static final String TAG = "TreeActivity";

    private String treeId;
    private EditText editNewPubMsg;

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList;
    private Set<String> privateMsg;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree);
        getSupportActionBar().hide();
        recyclerView = findViewById(R.id.recycler_view);
        messageList = new ArrayList<>();
        privateMsg = new HashSet<>();
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
                String msgId = messageList.get(position).msgId;
                intent.putExtra("msgId", msgId);
                intent.putExtra("treeId", treeId);
                String msgType = privateMsg.contains(msgId) ? "privateMsg" : "publicMsg";
                intent.putExtra("msgType", msgType);
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

                String msg = editNewPubMsg.getText().toString();

                Intent intent = new Intent(TreeActivity.this, FriendSelect.class);
                intent.putExtra("msg", msg);
                intent.putExtra("treeId", treeId);
                editNewPubMsg.setText("");
                startActivity(intent);
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
        if (privateMsg.contains(msgId)) {
            myRef.child("trees").child(treeId).child("privateMsg").child(msgId).removeValue();
        } else {
            myRef.child("trees").child(treeId).child("publicMsg").child(msgId).removeValue();
        }
    }

    private void ShowMsg(DataSnapshot dataSnapshot) {
        messageList.clear();
        privateMsg.clear();
        for (DataSnapshot ds : dataSnapshot.child("trees").child(treeId).child("privateMsg").getChildren()) {
            Message msg = ds.getValue(Message.class);
            String curId = FirebaseUIActivity.currentUser.userId;
            if (msg == null) continue;
            if (!msg.owner.userId.equals(curId) && !msg.receiver.userId.equals(curId)) continue;
            messageList.add(msg);
            privateMsg.add(msg.msgId);
        }
        for (DataSnapshot ds : dataSnapshot.child("trees").child(treeId).child("publicMsg").getChildren()) {
            Message msg = ds.getValue(Message.class);
            messageList.add(msg);
        }
        adapter.notifyDataSetChanged();
    }
}
