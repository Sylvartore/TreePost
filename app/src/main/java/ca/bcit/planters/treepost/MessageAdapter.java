package ca.bcit.planters.treepost;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private Context mContext;
    private List<Message> messageList;
    private OnItemClickListener mListener;
    private static final String YELLOW = "#fdeca6";
    private static final String PINK = "#ffaec8";

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "MyViewHolder";

        public TextView content;
        public Message currentMsg;

        public MyViewHolder(View view, final OnItemClickListener listener) {
            super(view);
            content = view.findViewById(R.id.card_content);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public MessageAdapter(Context mContext, List<Message> messageList) {
        this.mContext = mContext;
        this.messageList = messageList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_card, parent, false);
        return new MyViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.currentMsg = message;
        holder.content.setText(message.content);
        if (message.receiver == null) holder.content.setBackgroundColor(Color.parseColor(YELLOW));
        else holder.content.setBackgroundColor(Color.parseColor(PINK));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
