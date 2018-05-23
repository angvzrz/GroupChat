package com.example.hp.groupchat;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

    private LinkedBlockingQueue<PackData> blockingQueue;
    private ArrayList<PackData> sentMessages;

    private PackAdapter packAdapter;
    private String userName;
    private ImageButton enviar;
    private EditText mensaje;
    private ClientConnection clientConnection;
    private MainActivity main;

    private RecyclerView messagesList;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

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

        Bundle bundle = this.getIntent().getExtras();
        //Construimos el mensaje a mostrar
        userName = bundle.getString("name");

        enviar = findViewById(R.id.msg);
        mensaje = findViewById(R.id.gentxt);
        sentMessages = new ArrayList<>();
        main = this;
        blockingQueue = new LinkedBlockingQueue<>();

        messagesList =  findViewById(R.id.my_recycler_view);
        messagesList.setHasFixedSize(true);
        //refreshLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
//

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        messagesList.setLayoutManager(mLayoutManager);


        // specify an adapter (see also next example)
        mAdapter = new MessageAdapter(getApplicationContext(),sentMessages,new MyOnClickListener(getApplicationContext(),messagesList,sentMessages));
        messagesList.setAdapter(mAdapter);

        mensaje.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (isEmpty(mensaje)) {
                    enviar.setImageResource(android.R.drawable.ic_btn_speak_now);
                }
                else {
                    enviar.setImageResource(android.R.drawable.ic_menu_send);
                }
            }
        });

        PackData packData = new PackData("Other", KeyWordSystem.Text_Only, "Este es un mensaje de alguien mas");
        packData.setPosition('R');
        sentMessages.add(packData);
        sentMessages.add(new PackData(userName, KeyWordSystem.Text_Only, "Este es un mensaje mio"));

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /*packAdapter = new PackAdapter(this, sentMessages);
        final ListView listViewChatMessages = findViewById(R.id.lv_chat_messages_main);

        listViewChatMessages.setAdapter(packAdapter);*/

//        clientConnection = new ClientConnection("187.213.202.80", 10001, this);
        clientConnection = new ClientConnection("192.168.0.5", 10001, this);


        clientConnection.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new PackData(userName, KeyWordSystem.Connected, userName));

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

       /* listViewChatMessages.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listViewChatMessages.setStackFromBottom(true);

        listViewChatMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listViewChatMessages.smoothScrollToPosition(listViewChatMessages.getCount() - 1);

            }
        });*/
        Log.e("Speech","is available: "+ SpeechRecognizer.isRecognitionAvailable(this));
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

                        while (i.hasNext())
                            s = (String) i.next();

                        PackData packData = new PackData(userName, KeyWordSystem.Text_Only,  s);
                        packData.setPosition('E');
                        sentMessages.add(packData);
                        mAdapter.notifyDataSetChanged();
                        blockingQueue.add(packData);
                        //mensaje.setText(s);
                    }
                    break;
                case PICK_IMAGE:
                    if (data.getData() != null) {

                        try {
                            final Uri imageUri = data.getData();
                            final InputStream imageStream = getContentResolver().openInputStream(imageUri);

                            byte[] byteArray = readBytes(imageStream);
                            PackData msj = new PackData(userName, KeyWordSystem.File_Transfer, "");
                            msj.setPosition('E');
                            msj.setContent(byteArray);

                            sentMessages.add(msj);
                            mAdapter.notifyDataSetChanged();
                            blockingQueue.add(msj);
                            mensaje.getText().clear();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Toast.makeText(main, "Something went wrong", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        //showing toast when unable to capture the image
                        Toast.makeText(main, "Unable to upload Image Please Try again ...", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("name", userName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (clientConnection!=null)
            blockingQueue.add(new PackData(userName, KeyWordSystem.Close_Connection, KeyWordSystem.Close_Connection));

        finish();
    }

    public void handleAction(View view) {
        if (!isEmpty(mensaje)) {
            PackData packData = new PackData(userName, KeyWordSystem.Text_Only,  mensaje.getText().toString());
            packData.setPosition('E');
            sentMessages.add(packData);
            mAdapter.notifyDataSetChanged();
            blockingQueue.add(packData);
            mensaje.getText().clear();
        }
        else {
            promptSpeechInput();
        }
    }

    public void chooseImg() {
        Intent intent = new Intent();
        intent.setType("image/png");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        //intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-MX");

       /* Intent intentvoice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentvoice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intentvoice.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es");
        intentvoice.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));*/
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    public void handleSendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            PackData msj = new PackData(userName, KeyWordSystem.File_Transfer, ":"+imageUri.getPath());
            msj.setPosition('E');
            sentMessages.add(msj);
            packAdapter.notifyDataSetChanged();

        }
    }

    public byte[] readBytes(InputStream stream) throws IOException {

        if (stream == null)
            return new byte[]{};

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
                if (!error)
                    throw e;
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
            final PackData packData = data.get(position);

            if (packData.getPosition() == 'E') {
                LinearLayout send = item.findViewById(R.id.msg_layout);
                send.setVisibility(View.GONE);
                TextView env = item.findViewById(R.id.msg_text);
                String txt = packData.getTime() + " - " + packData.getFrom() + ": " + packData.getText();
                env.setText(txt);

                if (packData.getType().equals(KeyWordSystem.File_Transfer)) {

                    InputStream is = new ByteArrayInputStream(packData.getContent());

                    Drawable drawable=new BitmapDrawable(this.getContext().getResources(), BitmapFactory.decodeStream(is));
                    drawable.setBounds(0,0,60,60);
                    env.setCompoundDrawables( drawable, null, null, null );
                }

            }
            else {
                LinearLayout receiver = item.findViewById(R.id.msg_layout);
                receiver.setVisibility(View.GONE);
                TextView env = item.findViewById(R.id.msg_text);

                String txt = packData.getTime() + " - " + packData.getFrom() + ": " + packData.getText();
                env.setText(txt);
                if (packData.getType().equals(KeyWordSystem.File_Transfer)) {

                    InputStream is = new ByteArrayInputStream(packData.getContent());

                    Drawable drawable=new BitmapDrawable(this.getContext().getResources(), BitmapFactory.decodeStream(is));
                    drawable.setBounds(0,0,60,60);
                    env.setCompoundDrawables(  null, drawable, null, null );

                }
            }
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (packData.getType().equals(KeyWordSystem.File_Transfer)) {
                        Intent intent=new Intent(MainActivity.this,DisplayActivity.class);
                        intent.putExtra("msg",packData);
                        startActivity(intent);
                    }
                }
            });
            return (item);
        }
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public void addNewMsg(PackData mensaje) {
        sentMessages.add(mensaje);
        //packAdapter.notifyDataSetChanged();
        mAdapter.notifyDataSetChanged();
    }

    public LinkedBlockingQueue<PackData> getBlockingQueue() {
        return blockingQueue;
    }

    public String getUserName() {
        return userName;
    }


}