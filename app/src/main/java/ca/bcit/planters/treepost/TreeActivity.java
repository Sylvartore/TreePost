package ca.bcit.planters.treepost;

import android.content.Context;
import android.service.autofill.Dataset;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
                Log.d("msgId",messageList.get(position).msgId);
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
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    DeletePublicMsg(messageList.get(position).msgId);
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
        treeId = (String) getIntent().getExtras().get("id");
        editNewPubMsg = findViewById(R.id.edit_new_pub_msg);
        editNewPubMsg.bringToFront();
        Button btnPostNewPubMsg = findViewById(R.id.btn_new_pub_msg);
        btnPostNewPubMsg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                String key = myRef.child("trees").child(treeId).child("publicMsg").push().getKey();
                Message newMsg = new Message(key, new Date(), FirebaseUIActivity.currentUser, editNewPubMsg.getText().toString());
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

    public void DeletePublicMsg(String msgId) {
        myRef.child("trees").child(treeId).child("publicMsg").child(msgId).removeValue();
    }

    public void ShowPublicMsg(DataSnapshot dataSnapshot) {
        messageList.clear();
        for (DataSnapshot ds : dataSnapshot.child("trees").child(treeId).child("publicMsg").getChildren()) {
            Message pubMsg = ds.getValue(Message.class);
            String key = ds.getKey();
            Log.d("Key: ", key);
            messageList.add(pubMsg);
        }
        adapter.notifyDataSetChanged();
    }
}
