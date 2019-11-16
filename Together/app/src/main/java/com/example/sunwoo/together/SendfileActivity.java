package com.example.sunwoo.together;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static android.content.ContentValues.TAG;

public class SendfileActivity extends Activity implements TextToSpeech.OnInitListener {
    /** Called when the activity is first created. */

    private static final int SELECT_PICTURE = 1;

    private String selectedImagePath;
    private ImageView img;
    private Socket sock;
    private TextToSpeech myTTS;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendfile);

        myTTS = new TextToSpeech(this, this);

        System.out.println("34");
        img = (ImageView) findViewById(R.id.ivPic);
        System.out.println("36");


        ((Button) findViewById(R.id.bBrowse))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        System.out.println("40");
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(
                                Intent.createChooser(intent, "Select Picture"),
                                SELECT_PICTURE);
                        System.out.println("47");
                        Log.d(TAG, "send~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    }
                });

        System.out.println("51");
        Button send = (Button) findViewById(R.id.bSend);
        // final TextView status = (TextView) findViewById(R.id.tvStatus);

        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Socket sock;


                Thread send = new Thread() {
                    public void run() {
                        Socket sock = null;
                        try {
                            sock = new Socket("192.168.0.162" ,9999);
                            // sendfile
                            //File myFile = new File("/sdcard/BF/bf.jpg");
                            File myFile = new File("/storage/emulated/0/BF/IMG_01.jpg");
                            byte[] mybytearray = new byte[(int) myFile.length()];
                            FileInputStream fis = new FileInputStream(myFile);
                            BufferedInputStream bis = new BufferedInputStream(fis);
                            bis.read(mybytearray, 0, mybytearray.length);
                            OutputStream os = sock.getOutputStream();
                            System.out.println("Sending...");
                            os.write(mybytearray, 0, mybytearray.length);
                            os.flush();

                            sock.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };

                send.start();

                Intent next = new Intent(SendfileActivity.this,Roading.class);
                startActivity(next);
            } /*catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }*/



        });

        Button fsend = (Button) findViewById(R.id.bbSend);
        // final TextView status = (TextView) findViewById(R.id.tvStatus);

        fsend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Socket sock;


                Thread fsend = new Thread() {
                    public void run() {
                        Socket sock = null;
                        try {
                            sock = new Socket("192.168.0.162" ,7770);
                            // sendfile
                            File myFile = new File (selectedImagePath);
                            byte[] mybytearray = new byte[(int) myFile.length()];
                            FileInputStream fis = new FileInputStream(myFile);
                            BufferedInputStream bis = new BufferedInputStream(fis);
                            bis.read(mybytearray, 0, mybytearray.length);
                            OutputStream os = sock.getOutputStream();
                            System.out.println("Sending...");
                            os.write(mybytearray, 0, mybytearray.length);
                            os.flush();

                            sock.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                fsend.start();

                // Intent next = new Intent(SendfileActivity.this,Translating.class);
                // startActivity(next);
            } /*catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }*/


        });


    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                TextView path = (TextView) findViewById(R.id.tvPath);
                path.setText("Image Path : " + selectedImagePath);
                img.setImageURI(selectedImageUri);
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void onInit(int status){
        String myText1 = "점자 번역을 위해";
        String myText2 = "화면을 한 번 터치해주세요.";

        myTTS.speak(myText1, TextToSpeech.QUEUE_FLUSH, null);
        myTTS.speak(myText2, TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        myTTS.shutdown();
    }
}