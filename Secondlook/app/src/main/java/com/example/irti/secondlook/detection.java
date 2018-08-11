package com.example.irti.secondlook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;

public class detection extends AppCompatActivity {
    // Globals
    private static final int RESULT_PICK_CONTACT = 85500;
    EditText contact = null;
    Button analyze = null;
    Button select_contact = null;
    TextView result = null;
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    // Change it with the IP of your server
    final String upLoadServerUri = "http://192.168.8.100:5000/file";
    String uploadFileName = "myfile";
    String serverMessage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);

        contact = findViewById(R.id.contact);
        analyze = findViewById(R.id.uploadFile);
        result = findViewById(R.id.result);
        select_contact = findViewById(R.id.select_contact);

        select_contact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 7);
            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                analyze.setVisibility(View.VISIBLE);
            }
        });

        analyze.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
//                startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
                // Message Reading Code
                // reading messages
                String contact_num = contact.getText().toString();
                String msgData = "";
                // if contact field is not empty
                if (!contact_num.isEmpty()) {

                    Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
                    if (cursor.moveToFirst()) { // must check the result to prevent exception
                        do {

                            if (cursor.getString(2).equals(contact_num)) {
                                //msgData += cursor.getString(2);
                                msgData += " " + cursor.getString(12) + " ";

                            }
                        } while (cursor.moveToNext());
                        System.out.println(msgData);
                        // file reading complete
                    } else { // No sms found in the inbox
                        Toast.makeText(getApplicationContext(), "No SMS found", Toast.LENGTH_SHORT).show();
                    }
                } else { // contact field is empty
                    Toast.makeText(getApplicationContext(), "Contact Field is empty", Toast.LENGTH_SHORT).show();
                }
                // Message Reading Code End

                // Writing Message in the text file Code START
                try {
                    // Creates a file in the primary external storage space of the
                    // current application.
                    // If the file does not exists, it is created.
                    File testFile = new File(getApplicationContext().getExternalFilesDir(null), "TestFile.txt");
                    if (!testFile.exists())
                        testFile.createNewFile();

                    // Adds a line to the file
                    BufferedWriter writer = new BufferedWriter(new FileWriter(testFile /*append*/));
                    writer.write(msgData.toString());
                    writer.close();
                    // Refresh the data so it can seen when the device is plugged in a
                    // computer. You may have to unplug and replug the device to see the
                    // latest changes. This is not necessary if the user should not modify
                    // the files.
                    if(testFile.exists()){
                        uploadFileName = testFile.getPath().toString();
                    }

                } catch (IOException e) {
                    Log.e("ReadWriteFile", "Unable to write to the TestFile.txt file.");
                }

                // Writing Message in the text file Code END

                // Sending file on the server START
                new Thread(new Runnable()
                {
                    public void run()
                    {
                        runOnUiThread(new Runnable()
                        {
                            public void run()
                            {
                                Toast.makeText(getApplicationContext(), "Upload Start", Toast.LENGTH_SHORT).show();
                            }
                        });

                        uploadFile(uploadFileName);
                    }
                }).start();
                // Sending file on the server END
            }
        });
    }
    ////////////////////////
    @Override
    public void onActivityResult(int RequestCode, int ResultCode, Intent ResultIntent) {

        super.onActivityResult(RequestCode, ResultCode, ResultIntent);

        switch (RequestCode) {

            case (7):
                if (ResultCode == Activity.RESULT_OK) {

                    Uri uri;
                    Cursor cursor1, cursor2;
                    String TempNameHolder, TempNumberHolder, TempContactID, IDresult = "" ;
                    String Number = "";
                    int IDresultHolder ;

                    uri = ResultIntent.getData();

                    cursor1 = getContentResolver().query(uri, null, null, null, null);

                    if (cursor1.moveToFirst()) {

                        TempNameHolder = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        TempContactID = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID));
//                        Number = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.));
                        IDresult = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        IDresultHolder = Integer.valueOf(IDresult) ;

                        if (IDresultHolder == 1) {

                            cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + TempContactID, null, null);

                            while (cursor2.moveToNext()) {

                                TempNumberHolder = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                if (TempNumberHolder.substring(0,2).contains("+")) {
                                    System.out.println(TempNumberHolder+ "asdasdasdasdasdadaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                                    contact.setText(TempNumberHolder.toString());
                                    break;
                                }
                                else{
                                    System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                                }

                                //contact.setText(TempNumberHolder.toString());
//                                Toast.makeText(getApplicationContext(), TempNumberHolder.toString(), Toast.LENGTH_SHORT ).show();
                            }
                        }

                    }
                }
                break;
        }
    }


    /////////////////////////
    ////////////   Code To Upload File On server START ////////////////////////////
    public int uploadFile(String sourceFileUri)
    {
        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile())
        {
            Log.e("uploadFile", "Source File not exist :"
                     + uploadFileName);
            runOnUiThread(new Runnable()
            {
                public void run()
                {
                    Toast.makeText(getApplicationContext(), "Source file doesn't exist", Toast.LENGTH_SHORT).show();
                }
            });
            return 0;
        }
        else
        {
            try
            {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("messageFile", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"messageFile\";filename=\""
                        + "messages.txt" + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0)
                {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                // Converting the Input Stream to string
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }
                serverMessage = sb.toString();
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();
                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200)
                {
                    runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            Toast.makeText(getApplicationContext(), "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
                            result.setText(serverMessage);
                        }
                    });
                }
                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            }
            catch (MalformedURLException ex)
            {
                ex.printStackTrace();

                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        Toast.makeText(getApplicationContext(), "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            }
            catch (Exception e)
            {
                e.printStackTrace();

                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        Toast.makeText(getApplicationContext(), "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return serverResponseCode;
        }
    }
    ////////////   Code To Upload File On server END ////////////////////////////

}


