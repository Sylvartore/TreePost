package ca.bcit.planters.treepost;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;

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

public class MessageActivity extends AppCompatActivity {

    private static final String TAG = "MessageActivity";

    private TextView msgContent;
    private ImageView msgUserAvatar;
    private TextView msgDate;
    private TextView msgUserName;
    private EditText replyMsg;
    private String msgId;
    private String treeId;
    private Message message;
    private String msgType;

    private RecyclerView replyRecyclerView;
    private ReplyMessageAdapter adapter;
    private List<Message> replyMessageList;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        getSupportActionBar().hide();
        msgId = (String) getIntent().getExtras().get("msgId");
        treeId = (String) getIntent().getExtras().get("treeId");
        msgType = (String) getIntent().getExtras().get("msgType");
        msgContent = findViewById(R.id.msg_content);
        msgContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    message.content = msgContent.getText().toString();
                    Map<String, Object> msgValues = message.toMap();
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/trees/" + treeId + "/" + msgType + "/" + msgId, msgValues);
                    myRef.updateChildren(childUpdates);
                }
            }
        });
        msgUserAvatar = findViewById(R.id.msg_user_avatar);
        msgDate = findViewById(R.id.msg_date);
        msgUserName = findViewById(R.id.msg_user_name);
        replyMsg = findViewById(R.id.edit_new_reply_msg);
        replyMsg.bringToFront();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                message = dataSnapshot.child("trees").child(treeId).child(msgType).child(msgId).getValue(Message.class);
                msgContent.setText(message.content);
                msgContent.setFocusable(FirebaseUIActivity.currentUser.userId.equals(message.owner.userId));
                msgUserName.setText(message.owner.email);
                msgUserName.requestFocus();
                msgUserName.setOnLongClickListener(new AddFriendListener(message.owner, MessageActivity.this));
                msgUserAvatar.setOnLongClickListener(new AddFriendListener(message.owner, MessageActivity.this));
                replyMessageList.clear();
                for (DataSnapshot ds : dataSnapshot.child("trees").child(treeId).child(msgType).child(msgId).child("replies").getChildren()) {
                    Message reply = ds.getValue(Message.class);
                    replyMessageList.add(reply);
                }
                Log.d(TAG, "onDataChange: " + (replyMessageList.size() == 0 ? 0 : replyMessageList.get(replyMessageList.size() - 1).content));
                msgDate.setText(message.timeStamp.toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        replyRecyclerView = findViewById(R.id.reply_recycler_view);
        replyMessageList = new ArrayList<>();
        adapter = new ReplyMessageAdapter(this, replyMessageList);
        replyRecyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        replyRecyclerView.setLayoutManager(mLayoutManager);
        replyRecyclerView.setItemAnimator(new DefaultItemAnimator());


        Button btnReplyMsg = findViewById(R.id.btn_new_reply_msg);
        btnReplyMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                String key = myRef.child("trees").child(treeId).child(msgType).child(msgId).child("replies").push().getKey();
                if (message != null) {
                    if (message.replies == null)
                        message.replies = new HashMap<>();
                    message.replies.put(key, new Message(key, new Date(), FirebaseUIActivity.currentUser, replyMsg.getText().toString()));
                    Map<String, Object> msgValues = message.toMap();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/trees/" + treeId + "/" + msgType + "/" + msgId, msgValues);
                    myRef.updateChildren(childUpdates);
                    replyMsg.setText("");
                }
            }
        });
    }
}
