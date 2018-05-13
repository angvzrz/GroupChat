package com.example.hp.groupchat;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.hp.groupchat.shared.PackData;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class DisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        ImageView imageView=findViewById(R.id.imageViewDisplay);
        PackData packData= (PackData) this.getIntent().getSerializableExtra("msg");
        InputStream is = new ByteArrayInputStream(packData.getContent());
        imageView.setImageBitmap(BitmapFactory.decodeStream(is));
    }
}
