package com.example.hp.groupchat;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hp.groupchat.shared.KeyWordSystem;
import com.example.hp.groupchat.shared.PackData;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * GroupChat.
 * Created by redrover on 18/05/18.
 * With IntelliJ IDEA.
 **/
public class MessageAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private final View.OnClickListener onClickListener;
    private List<PackData> messagesList;
    private Context context;

    public MessageAdapter(Context context, List<PackData> messagesList, MyOnClickListener myOnClickListener) {
        this.context = context;
        this.messagesList = messagesList;
        this.onClickListener = myOnClickListener;
    }
    
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_message_layout, parent, false);
            view.setBackground(view.getResources().getDrawable(R.drawable.rounded_corner1, null));
            view.setOnClickListener(onClickListener);

            return new MessagesViewHolder(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_message_layout, parent, false);
            view.setBackground(view.getResources().getDrawable(R.drawable.rounded_corner, null));
            view.setOnClickListener(onClickListener);

            return new MessagesViewHolder(view);
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        PackData data = messagesList.get(position);
        return (data.getPosition() == 'E') ? VIEW_TYPE_MESSAGE_SENT : VIEW_TYPE_MESSAGE_RECEIVED;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PackData message = messagesList.get(position);
        ((MessagesViewHolder) holder).bind(message);
    }

    private class MessagesViewHolder extends  RecyclerView.ViewHolder {

        private TextView messageText;
        private ImageView messageImage;

        MessagesViewHolder(View itemView) {
            super(itemView);
            this.messageText = itemView.findViewById(R.id.msg_text);
            this.messageImage = itemView.findViewById(R.id.msg_img);
        }

        void bind(final PackData message) {

            if (this.getItemViewType() == VIEW_TYPE_MESSAGE_RECEIVED)
                messageText.setText(message.getFrom() + ": " + message.getText());
            else if (this.getItemViewType() == VIEW_TYPE_MESSAGE_SENT)
                messageText.setText(message.getText());

            if (message.getType().equals(KeyWordSystem.File_Transfer)) {
                InputStream inputStream = new ByteArrayInputStream(message.getContent());
                Drawable drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeStream(inputStream));

                LinearLayout.LayoutParams params = new LinearLayout
                        .LayoutParams(250, 400);

                messageImage.setImageDrawable(drawable);
                messageImage.setLayoutParams(params);
                messageText.setVisibility(View.INVISIBLE);
            }
        }
    }
}
