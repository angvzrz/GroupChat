package com.example.hp.groupchat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.hp.groupchat.shared.PackData;

import java.util.List;

/**
 * GroupChat.
 * Created by redrover on 18/05/18.
 * With IntelliJ IDEA.
 **/
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<PackData> messagesList;

    public MessageAdapter(List<PackData> messagesList) {
        this.messagesList = messagesList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageSentText;
        public TextView messageReceivedText;
        public ImageView messageSentImage;
        public ImageView messageReceivedImage;

        public MessageViewHolder(View view) {
            super(view);

            messageSentText = (TextView) view.findViewById(R.id.textViewEnvio);
            messageReceivedText = (TextView) view.findViewById(R.id.textViewRecibido);
            messageSentImage = (ImageView) view.findViewById(R.id.message_sent_img);
            messageReceivedImage = (ImageView) view.findViewById(R.id.message_recv_img);
        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_message_sent, parent, false);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {

        PackData message = messagesList.get(position);

        String fromUser = message.getFrom();
        String messageType = message.getType();
        char messagePosition = message.getPosition();

        if (messageType.equals("text") && messagePosition == 'E') {
            holder.messageSentText.setText(message.getText());
            holder.messageSentImage.setVisibility(View.INVISIBLE);
            
        }
        else if (messageType.equals("text") && messagePosition == 'R') {
            holder.messageReceivedText.setText(message.getText());
            holder.messageReceivedImage.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
