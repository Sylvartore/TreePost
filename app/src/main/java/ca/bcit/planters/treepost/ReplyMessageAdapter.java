package ca.bcit.planters.treepost;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class ReplyMessageAdapter extends RecyclerView.Adapter<ReplyMessageAdapter.MyReplyViewHolder> {
    private Context mContext;
    private List<Message> messageList;
    public static class MyReplyViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "MyViewHolder";
        public TextView content;
        public TextView date;
        public ImageView userAvatar;
        public TextView userName;
        public Message currentMsg;
        public MyReplyViewHolder(View view) {
            super(view);
            content = view.findViewById(R.id.reply_card_content);
            date = view.findViewById(R.id.reply_card_date);
            userAvatar = view.findViewById(R.id.reply_msg_user_avatar);
            userName = view.findViewById(R.id.reply_msg_user_name);
        }
    }

    public ReplyMessageAdapter(Context mContext, List<Message> messageList) {
        this.mContext = mContext;
        this.messageList = messageList;
    }

    @Override
    public ReplyMessageAdapter.MyReplyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reply_message_card, parent, false);
        return new ReplyMessageAdapter.MyReplyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ReplyMessageAdapter.MyReplyViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.currentMsg = message;
        holder.content.setText(message.content);
        holder.date.setText(message.timeStamp.toString());
        holder.userName.setText(message.owner.email);
        holder.userName.setOnLongClickListener(new AddFriendListener(message.owner,mContext));
        holder.userAvatar.setOnLongClickListener(new AddFriendListener(message.owner,mContext));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
