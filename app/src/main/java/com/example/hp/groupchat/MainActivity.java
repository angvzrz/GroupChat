package com.example.hp.groupchat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class MainActivity extends Activity {
    LinkedBlockingQueue<String> bq;
    ArrayList<Mensaje> men;
    Adaptador adaptador1;
    String nombre;
    ImageButton enviar;
    EditText mensaje;
    ClienteConexion conexion;
    MainActivity main;

    public static final int PICK_IMAGE = 1821312;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enviar = (ImageButton) findViewById(R.id.msg);
        mensaje = (EditText) findViewById(R.id.gentxt);
        men = new ArrayList<>();
        main = this;
        bq = new LinkedBlockingQueue();
        men.add(new Mensaje("Diego", "Este es un mensaje", 'E'));
        nombre = "Alejandro";
        men.add(new Mensaje("Diego", "Este es un mensaje", 'R'));


        adaptador1 = new Adaptador(this, men);
        final ListView opc = (ListView) findViewById(R.id.opc);
        opc.setAdapter(adaptador1);
        conexion = new ClienteConexion("192.168.0.21", 10001, main);
        conexion.execute(nombre);

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                men.add(new Mensaje(nombre, mensaje.getText().toString(), 'E'));
                adaptador1.notifyDataSetChanged();
                bq.add(nombre + ":" + mensaje.getText().toString());
                mensaje.setText("");

            }
        });
        verifyStoragePermissions(this);
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        }
    }



    public void chooseImg(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE) {
            if (data.getData() != null) {
                try {
                    conexion.onSendFile(data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //showing toast when unable to capture the image
                Toast.makeText(main, "Unable to upload Image Please Try again ...", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            try {
                conexion.onSendFile(imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static class Mensaje {
        String nombre,
                mensaje;
        char posicion;


        public Mensaje(String nombre, String mensaje, char posicion) {
            this.nombre = nombre;
            this.mensaje = mensaje;
            this.posicion = posicion;
        }

        public String getNombre() {
            return nombre;
        }

        public String getMensaje() {
            return mensaje;
        }

        public char getPosicion() {
            return posicion;
        }
    }

    class Adaptador extends ArrayAdapter<Mensaje> {
        ArrayList<Mensaje> datos;

        public Adaptador(Context context, ArrayList<Mensaje> datos) {
            super(context, R.layout.list, datos);
            this.datos = datos;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View item = inflater.inflate(R.layout.list, null);
            if (datos.get(position).getPosicion() == 'E') {
                LinearLayout recibido = (LinearLayout) item.findViewById(R.id.layoutRecibo);
                recibido.setVisibility(View.GONE);
                TextView env = (TextView) item.findViewById(R.id.textViewEnvio);
                env.setText(datos.get(position).getMensaje());
            } else {
                LinearLayout recibido = (LinearLayout) item.findViewById(R.id.layoutEnvio);
                recibido.setVisibility(View.GONE);
                TextView env = (TextView) item.findViewById(R.id.textViewRecibido);
                env.setText(datos.get(position).getMensaje());
            }
            return (item);

        }
    }
}