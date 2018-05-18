package com.example.hp.groupchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private EditText user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user =  findViewById(R.id.namelbl);
    }
    public void onClick(View v) {

        if (TextUtils.isEmpty(user.getText())) {
            Toast.makeText(this, "No ha ingresado nombre de usuario", Toast.LENGTH_LONG).show();
        }
        else
        {
            //Creamos el Intent
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            //Creamos la información a pasar entre actividades
            Bundle b = new Bundle();
            b.putString("name", user.getText().toString().trim());
            //Añadimos la información al intent
            intent.putExtras(b);
            //Iniciamos la nueva actividad
            startActivity(intent);
        }
    }
}
