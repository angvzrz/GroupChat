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
                    .inflate(R.layout.single_message_sent, parent, false);
            view.setOnClickListener(onClickListener);
            return new SentMessageHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_message_received, parent, false);
            view.setOnClickListener(onClickListener);

            return new ReceivedMessageHolder(view);
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

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {

        private TextView messageSentText;
        private ImageView messageSentImage;
        SentMessageHolder(View itemView) {
            super(itemView);
            messageSentText = itemView.findViewById(R.id.textViewEnvio);
            messageSentImage = itemView.findViewById(R.id.message_sent_img);

        }

        void bind(final PackData message) {
            messageSentText.setText(message.getText());
            if (message.getType().equals(KeyWordSystem.File_Transfer)) {
                InputStream is = new ByteArrayInputStream(message.getContent());
                Drawable drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeStream(is));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
                messageSentImage.setImageDrawable(drawable);
                messageSentImage.setLayoutParams(params);
                messageSentText.setVisibility(View.INVISIBLE);

            }
        }


    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        private TextView messageReceivedText;
        private ImageView messageReceivedImage;
        ReceivedMessageHolder(View view) {
            super(view);
            messageReceivedText = view.findViewById(R.id.textViewRecibido);
            messageReceivedImage = view.findViewById(R.id.message_recv_img);
        }

        void bind(final PackData message) {
            String text = message.getFrom() + ": " + message.getText();
            messageReceivedText.setText(text);
            if (message.getType().equals(KeyWordSystem.File_Transfer)) {
                InputStream is = new ByteArrayInputStream(message.getContent());
                Drawable drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeStream(is));
                // drawable.setBounds(0, 0, 150, 150);

                messageReceivedImage.setImageDrawable(drawable);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
                messageReceivedImage.setLayoutParams(params);
                messageReceivedText.setVisibility(View.INVISIBLE);

            }
        }

    }


}
