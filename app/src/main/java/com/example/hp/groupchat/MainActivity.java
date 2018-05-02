package com.example.hp.groupchat;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

public class MainActivity extends AppCompatActivity {
    private LinkedBlockingQueue<PackData> bq;
    private ArrayList<PackData> men;
    private PackAdapter packAdapter;
    private String nombre;
    private ImageButton enviar;
    private EditText mensaje;
    private ClientConnection clientConnection;
    private MainActivity main;

    public static final int PICK_IMAGE = 182;
    private static final int REQ_CODE_SPEECH_INPUT = 101;
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

        enviar = findViewById(R.id.msg);
        mensaje = findViewById(R.id.gentxt);
        men = new ArrayList<>();
        main = this;
        bq = new LinkedBlockingQueue();

        mensaje.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(isEmpty(mensaje)){
                    enviar.setImageResource(android.R.drawable.ic_btn_speak_now);
                }else{
                    enviar.setImageResource(android.R.drawable.ic_menu_send);
                }


            }
        });

        nombre = "User-" + System.currentTimeMillis();
        PackData packData = new PackData(nombre, KeyWordSystem.Only_Text, "Este es un mensaje  mio");
        packData.setPos('E');
        men.add(packData);

        men.add(new PackData(nombre, KeyWordSystem.Only_Text, "Este es un mensaje de otro"));


        packAdapter = new PackAdapter(this, men);
        final ListView opc = findViewById(R.id.opc);
        opc.setAdapter(packAdapter);
        clientConnection = new ClientConnection("192.168.0.8", 10001, this);
        clientConnection.execute(new PackData(nombre, KeyWordSystem.Connected, nombre));

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEmpty(mensaje)){
                    PackData packData = new PackData(nombre, KeyWordSystem.Only_Text, mensaje.getText().toString());
                    packData.setPos('E');
                    men.add(packData);
                    packAdapter.notifyDataSetChanged();
                    bq.add(packData);
                    mensaje.getText().clear();
                }else{
                    promptSpeechInput();
                }
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

        opc.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        opc.setStackFromBottom(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                chooseImg();
                return (true);
            case R.id.reset:
                Toast.makeText(this, "Reset menu", Toast.LENGTH_LONG).show();

                return (true);
            case R.id.about:
                Toast.makeText(this, "About menu", Toast.LENGTH_LONG).show();
                return (true);
            case R.id.exit:
                finish();
                return (true);

        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_SPEECH_INPUT:
                    if (data != null) {
                        ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        Iterator i = result.iterator();
                        String s = "";
                        while (i.hasNext()) {
                            s = (String) i.next();
                        }
                        mensaje.setText(s);
                    }
                    break;
                case PICK_IMAGE:
                    if (data.getData() != null) {

                        try {
                            final Uri imageUri = data.getData();
                            final InputStream imageStream = getContentResolver().openInputStream(imageUri);

                            byte[] byteArray = readBytes(imageStream);
                            PackData msj = new PackData(nombre, KeyWordSystem.File_Transfer, "");
                            msj.setPos('E');
                            msj.setContent(byteArray);

                            men.add(msj);
                            packAdapter.notifyDataSetChanged();
                            bq.add(msj);
                            mensaje.getText().clear();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Toast.makeText(main, "Something went wrong", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //showing toast when unable to capture the image
                        Toast.makeText(main, "Unable to upload Image Please Try again ...", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    public void chooseImg() {
        Intent intent = new Intent();
        intent.setType("image/png");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    private void promptSpeechInput() {
        Intent intentvoice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentvoice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intentvoice.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es");
        intentvoice.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        try {
            startActivityForResult(intentvoice, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    public void handleSendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            PackData msj = new PackData(nombre, KeyWordSystem.File_Transfer, imageUri.getPath());
            msj.setPos('E');
            men.add(msj);
            packAdapter.notifyDataSetChanged();

        }

    }

    public byte[] readBytes(InputStream stream) throws IOException {
        if (stream == null) return new byte[]{};
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        boolean error = false;
        try {
            int numRead = 0;
            while ((numRead = stream.read(buffer)) > -1) {
                output.write(buffer, 0, numRead);
            }
        } catch (IOException e) {
            error = true; // this error should be thrown, even if there is an error closing stream
            throw e;
        } catch (RuntimeException e) {
            error = true; // this error should be thrown, even if there is an error closing stream
            throw e;
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                if (!error) throw e;
            }
        }
        output.flush();
        return output.toByteArray();

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
                LinearLayout send = item.findViewById(R.id.layoutRecibo);
                send.setVisibility(View.GONE);
                TextView env = item.findViewById(R.id.textViewEnvio);
                String txt = packData.getTime() + " - " + packData.getFrom() + ": " + packData.getText();
                env.setText(txt);
                if (packData.getType().equals(KeyWordSystem.File_Transfer)) {
                    ImageView imageView = item.findViewById(R.id.imageMe);
                    InputStream is = new ByteArrayInputStream(packData.getContent());
                    Bitmap bmp = BitmapFactory.decodeStream(is);
                    imageView.setImageBitmap(bmp);
                }

            } else {
                LinearLayout receiver = item.findViewById(R.id.layoutEnvio);
                receiver.setVisibility(View.GONE);
                TextView env = item.findViewById(R.id.textViewRecibido);

                String txt = packData.getTime() + " - " + packData.getFrom() + ": " + packData.getText();
                env.setText(txt);
                if (packData.getType().equals(KeyWordSystem.File_Transfer)) {

                    InputStream is = new ByteArrayInputStream(packData.getContent());
                    Bitmap bmp = BitmapFactory.decodeStream(is);
                    ImageView imageView = item.findViewById(R.id.imageOther);
                    imageView.setImageBitmap(bmp);
                }


            }
            return (item);

        }

    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
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