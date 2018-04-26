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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.groupchat.Connection.ClientConnection;
import com.example.hp.groupchat.shared.KeyWordSystem;
import com.example.hp.groupchat.shared.PackData;
import com.example.hp.groupchat.shared.ServerUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;
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
        PackData packData = new PackData(nombre, KeyWordSystem.Only_Text, "Este es un mensaje  mio");
        packData.setPos('E');
        men.add(packData);

        men.add(new PackData(nombre, KeyWordSystem.Only_Text, "Este es un mensaje de otro"));


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

                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap bmp = BitmapFactory.decodeStream(imageStream);

                    int width = bmp.getWidth();
                    int height = bmp.getHeight();

                    int size = bmp.getRowBytes() * bmp.getHeight();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                    bmp.copyPixelsToBuffer(byteBuffer);
                    byte[] byteArray = byteBuffer.array();

                    PackData msj = new PackData(nombre, KeyWordSystem.File_Transfer, bmp.getConfig().name());
                    msj.setPos('E');
                    msj.setFile_name(bmp.getConfig().name());
                    msj.setContent(byteArray);
                    msj.setSize(size);
                    msj.setWidth(width);
                    msj.setHeight(height);
                    men.add(msj);
                    packAdapter.notifyDataSetChanged();
                    bq.add(msj);
                    mensaje.getText().clear();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(main, "Something went wrong", Toast.LENGTH_LONG).show();
                }
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


    class PackAdapter extends ArrayAdapter<PackData> {
        ArrayList<PackData> data;

        public PackAdapter(Context context, ArrayList<PackData> data) {
            super(context, R.layout.list, data);
            this.data = data;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View item = inflater.inflate(R.layout.list, null);
            PackData packData = data.get(position);
            if (packData.getPos() == 'E') {
                LinearLayout send =  item.findViewById(R.id.layoutRecibo);
                send.setVisibility(View.GONE);
                TextView env =  item.findViewById(R.id.textViewEnvio);
                String txt = packData.getTime() + " - " + packData.getFrom() + ": " + packData.getText();
                env.setText(txt);
                if (packData.getType().equals(KeyWordSystem.File_Transfer)) {
                    Bitmap.Config configBmp = Bitmap.Config.valueOf(packData.getFile_name());
                    Bitmap bitmap_tmp = Bitmap.createBitmap(packData.getWidth(), packData.getHeight(), configBmp);
                    ByteBuffer buffer = ByteBuffer.wrap(packData.getContent());
                    bitmap_tmp.copyPixelsFromBuffer(buffer);
                    ImageView imageView=item.findViewById(R.id.imageMe);
                    imageView.setImageBitmap(bitmap_tmp);
                }

            } else {
                LinearLayout receiver =  item.findViewById(R.id.layoutEnvio);
                receiver.setVisibility(View.GONE);
                TextView env =  item.findViewById(R.id.textViewRecibido);

                String txt = packData.getTime() + " - " + packData.getFrom() + ": " + packData.getText();
                env.setText(txt);
                if (packData.getType().equals(KeyWordSystem.File_Transfer)) {
                    Bitmap.Config configBmp = Bitmap.Config.valueOf(packData.getFile_name());
                    Bitmap bitmap_tmp = Bitmap.createBitmap(packData.getWidth(), packData.getHeight(), configBmp);
                    ByteBuffer buffer = ByteBuffer.wrap(packData.getContent());
                    bitmap_tmp.copyPixelsFromBuffer(buffer);
                    ImageView imageView=item.findViewById(R.id.imageOther);
                    imageView.setImageBitmap(bitmap_tmp);
                }


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