package ca.bcit.planters.treepost;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AddFriendListener implements View.OnLongClickListener {
    User user;
    private Context context;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    public AddFriendListener(User user, Context context) {
        this.user = user;
        this.context = context;
    }

    @Override
    public boolean onLongClick(View v) {
        if (FirebaseUIActivity.currentUser.userId.equals(this.user.userId)) return false;
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final boolean isFriend = dataSnapshot.child("friends").child(FirebaseUIActivity.currentUser.userId).child(user.userId).getValue() != null;
                String action = isFriend ? "Remove Friend" : "Add Friend";
                final String[] types = {action, "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which != 1) {
                            if (isFriend) {
                                myRef.child("/friends/").child(FirebaseUIActivity.currentUser.userId).child(user.userId).removeValue();
                            } else {
                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put("/friends/" + FirebaseUIActivity.currentUser.userId + "/" + user.userId, user.email);
                                myRef.updateChildren(childUpdates);
                            }
                        }
                    }
                });
                builder.show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        myRef.addListenerForSingleValueEvent(postListener);
        return false;
    }
}
