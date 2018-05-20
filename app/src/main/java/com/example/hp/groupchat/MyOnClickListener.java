package com.example.hp.groupchat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.hp.groupchat.shared.KeyWordSystem;
import com.example.hp.groupchat.shared.PackData;

import java.util.ArrayList;

public class MyOnClickListener implements View.OnClickListener {
    RecyclerView recyclerView;
    ArrayList<PackData> data;
    Context context;
    public MyOnClickListener(Context applicationContext, RecyclerView messagesList, ArrayList<PackData> sentMessages) {
        context=applicationContext;
        recyclerView=messagesList;
        data=sentMessages;
    }


    @Override
    public void onClick(View v) {
        final int childLayoutPosition = recyclerView.getChildLayoutPosition(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackData packData = data.get(childLayoutPosition);
                if (packData.getType().equals(KeyWordSystem.File_Transfer)){
                    Intent intent=new Intent(context,DisplayActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("msg",packData);
                    context.startActivity(intent);
                }
            }
        });

    }
}
