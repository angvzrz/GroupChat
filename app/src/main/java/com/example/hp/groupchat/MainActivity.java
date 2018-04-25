package com.example.hp.groupchat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.groupchat.Connection.ClientConnection;
import com.example.hp.groupchat.shared.KeyWordSystem;
import com.example.hp.groupchat.shared.PackData;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class MainActivity extends Activity {
    private LinkedBlockingQueue<PackData> bq;
    private ArrayList<PackData> men;
    private PackAdapter packAdapter;
    private String nombre;
    private ImageButton enviar;
    private EditText mensaje;
    private ClientConnection clientConnection;
    private MainActivity main;

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

        nombre = "User-" + System.currentTimeMillis();
        PackData packData = new PackData(nombre, KeyWordSystem.Only_Text, "Este es un mensaje");
        packData.setPos('E');
        men.add(packData);

        men.add(new PackData(nombre, KeyWordSystem.Only_Text, "Este es un mensaje"));


        packAdapter = new PackAdapter(this, men);
        final ListView opc = (ListView) findViewById(R.id.opc);
        opc.setAdapter(packAdapter);
        clientConnection = new ClientConnection("192.168.0.21", 3074, this);
        clientConnection.execute(new PackData(nombre, KeyWordSystem.Connected, nombre));

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackData packData = new PackData(nombre, KeyWordSystem.Only_Text, mensaje.getText().toString());
                packData.setPos('E');
                men.add(packData);
                packAdapter.notifyDataSetChanged();
                bq.add(packData);
                mensaje.getText().clear();

            }
        });
        verifyStoragePermissions(this);
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.contains("image/png")) {
                handleSendImage(intent); // Handle single image being sent
            }
        }
    }


    public void chooseImg(View view) {
        Intent intent = new Intent();
        intent.setType("image/png");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE) {


            if (data.getData() != null) {
                Uri imageUri = data.getData();// = conexion.getRealPathFromURI(data.getData());

                PackData msj = new PackData(nombre, KeyWordSystem.File_Transfer, imageUri.getPath());

                Uri selectedImage = data.getData();
               /* try {
                    Bitmap bitmapImage = decodeBitmap(selectedImage);
                    msj.setBitmap(bitmapImage);
                    msj.setUriImg(imageUri);
                    msj.setHasImg(true);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                men.add(msj);
                adaptador1.notifyDataSetChanged();
                bq.add(KeyWordSystem.File_Transfer + " " + (men.size() - 1));*/
            } else {
                //showing toast when unable to capture the image
                Toast.makeText(main, "Unable to upload Image Please Try again ...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //bq.add(KeyWordSystem.Close_Connection);
    }

    public void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            PackData msj = new PackData(nombre, KeyWordSystem.File_Transfer, imageUri.getPath());
            msj.setPos('E');
            //msj.setImageUri(imageUri);
            men.add(msj);
            packAdapter.notifyDataSetChanged();

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

    public Bitmap decodeBitmap(Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 100;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
    }


    class PackAdapter extends ArrayAdapter<PackData> {
        ArrayList<PackData> data;

        public PackAdapter(Context context, ArrayList<PackData> data) {
            super(context, R.layout.list, data);
            this.data = data;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View item = inflater.inflate(R.layout.list, null);
            if (data.get(position).getPos() == 'E') {
                LinearLayout recibido = (LinearLayout) item.findViewById(R.id.layoutRecibo);
                recibido.setVisibility(View.GONE);
                TextView env = (TextView) item.findViewById(R.id.textViewEnvio);
                PackData msg=data.get(position);
                String txt = msg.getTime() + " - " + msg.getFrom() + ": " + msg.getText();
                env.setText(txt);

            } else {
                LinearLayout recibido = (LinearLayout) item.findViewById(R.id.layoutEnvio);
                recibido.setVisibility(View.GONE);
                TextView env = (TextView) item.findViewById(R.id.textViewRecibido);
                PackData msg=data.get(position);
                String txt = msg.getTime() + " - " + msg.getFrom() + ": " + msg.getText();
                env.setText(txt);

            }
            return (item);

        }

    }

    public PackAdapter getPackAdapter() {
        return packAdapter;
    }

    public void addNewMsg(PackData mensaje) {
        men.add(mensaje);
        packAdapter.notifyDataSetChanged();
    }

    public LinkedBlockingQueue<PackData> getBq() {
        return bq;
    }

    public MainActivity getMain() {
        return main;
    }

    public String getNombre() {
        return nombre;
    }

    public ArrayList<PackData> getMen() {
        return men;
    }
}