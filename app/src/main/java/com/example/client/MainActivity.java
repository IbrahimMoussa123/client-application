package com.example.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.client.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MutableLiveData<String> message = new MutableLiveData<String>();

    private Socket socket;
    private DataOutputStream outToServer;
    private BufferedReader inFromServer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(socket==null){
                            try{
                                socket = new Socket("192.168.43.222", 12345);
                                outToServer = new DataOutputStream(socket.getOutputStream());
                                inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            }
                            catch(IOException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();


            }
        });

        binding.button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String send = binding.text.getText().toString();

                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try{
                            outToServer.write((send + '\n').getBytes());
                            String modifiedSentence = inFromServer.readLine();
                            message.postValue(modifiedSentence);
                        }
                        catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        message.observe(this, new Observer() {
            @Override
            public void onChanged(Object s) {
                Toast.makeText(
                        getApplicationContext(),
                        s.toString(),
                        Toast.LENGTH_LONG
                ).show();

            }
        });
    }
}