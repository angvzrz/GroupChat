package com.example.hp.groupchat.Connection;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.example.hp.groupchat.shared.KeyWordSystem;
import com.example.hp.groupchat.MainActivity;
import com.example.hp.groupchat.shared.PackData;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;

public class ClientSender extends AsyncTask<PackData, PackData, PackData> {

    private MainActivity main;
    private ObjectOutputStream outputStream;
    private boolean statusConnection;

    public ClientSender(ObjectOutputStream outputStream, MainActivity main) {
        this.outputStream = outputStream;
        this.main = main;

    }

    @Override
    protected void onPostExecute(PackData s) {

        super.onPostExecute(s);

    }

    @Override
    protected void onPreExecute() {
        try {

            //outputStream.writeUTF(main.getNombre()+" "+KeyWordSystem.UserConnected);
            outputStream.writeObject(new PackData(main.getNombre(), KeyWordSystem.UserConnected, KeyWordSystem.UserConnected));
            this.statusConnection = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected PackData doInBackground(PackData... params) {

        Log.e("doInBackground", params[0].toString());
        Log.e("Status connection", "TRUE");
        while (statusConnection) {
            try {
                PackData message = main.getBq().take();
                //main.bq.peek();

               /* if (!message.getType().equals(KeyWordSystem.File_Transfer)) {
                    outputStream.writeUTF(message.getFrom()+" " +message.getText());
                } else {
                    Log.e(KeyWordSystem.File_Transfer, "Prepared");
                    sendBytes(message.getContent());
                    Log.e(KeyWordSystem.File_Transfer, message.toString());
                }*/
                outputStream.writeObject(message);

            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                statusConnection = false;
            }
        }
        return null;
    }

    private void onSendFile(PackData msg) throws IOException, URISyntaxException {
        /*System.out.println("Connecting...");
        Log.e(KeyWordSystem.File_Transfer, msg);
        int pos = Integer.parseInt(msg.split(" ")[1]);
        MainActivity.Mensaje mensaje = main.getMen().get(pos);
        Bitmap bitmap = mensaje.getBitmap();
        Log.e("myFile", "on course");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        sendBytes(main.getNombre() + "_img_" + System.currentTimeMillis() + ".png", byteArray);
        stream.close();*/
      /*  byte[] mybytearray = new byte[ myFile.length()];




        DataOutputStream dos = new DataOutputStream(os);
        dos.writeUTF(KeyWordSystem.File_Transfer + " " + myFile.getName());
        dos.writeLong(myFile.length());
        dos.write(mybytearray, 0, mybytearray.length);
        dos.flush();

        bis.close();*/
       /* byte[] buffer = new byte[8192]; // or 4096, or more
        FileInputStream fis = new FileInputStream(bitmap.getf);

        BufferedInputStream bis = new BufferedInputStream(fis);

        OutputStream os = clientSocket.getOutputStream();
        int count;
        DataOutputStream outputStream=new DataOutputStream(os);
        outputStream.writeUTF(msg);
        outputStream.writeLong(myFile.length());

        while ((count = bis.read(buffer)) > 0)
        {
            outputStream.write(buffer, 0, count);
        }
        bis.close();*/


    }

    public void sendBytes(byte[] myByteArray) throws IOException {
        sendBytes(myByteArray, 0, myByteArray.length);
    }

    public void sendBytes(byte[] myByteArray, int start, int len) throws IOException {
        if (len < 0)
            throw new IllegalArgumentException("Negative length not allowed");
        if (start < 0 || start >= myByteArray.length)
            throw new IndexOutOfBoundsException("Out of bounds: " + start);
        // Other checks if needed.

        // May be better to save the streams in the support class;
        // just like the socket variable.
        OutputStream out = outputStream;
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeUTF(KeyWordSystem.File_Transfer + " " + main.getNombre());
        dos.writeInt(len);
        if (len > 0) {
            dos.write(myByteArray, start, len);
        }
    }

    public void setStatusConnection(boolean statusConnection) {
        this.statusConnection = statusConnection;
    }
}