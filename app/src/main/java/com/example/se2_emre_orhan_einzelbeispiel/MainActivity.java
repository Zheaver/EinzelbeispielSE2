package com.example.se2_emre_orhan_einzelbeispiel;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {


    private TextView TextViewReceivedData;
    private EditText Studentnumber;
    private Button button;
    private final String serverName = "se2-submission.aau.at";
    private final int serverPort = 20080;
    private Button ButtonPrimeNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextViewReceivedData = findViewById(R.id.textviewRectxt);
        Studentnumber = findViewById(R.id.etMatrikelnummer);
        button = findViewById(R.id.buttonClientConnect);
        ButtonPrimeNumbers = findViewById(R.id.button2);


    }

    public void onClick(View view) {
        String matriculationNumber = Studentnumber.getText().toString();
        sendToServer(matriculationNumber);
    }

    public void onClickPrimeNumbers(View view) {
        String matriculationNumber = Studentnumber.getText().toString();


    }



    private void sendToServer(final String matriculationNumber) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("se2-submission.aau.at", 20080);

                    // Send matriculation number to server
                    OutputStream outputStream = socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(outputStream, true);
                    writer.println(matriculationNumber);

                    // Receive response from server
                    InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    final String response = reader.readLine();

                    // Close connections
                    writer.close();
                    reader.close();
                    socket.close();

                    // Update UI on the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextViewReceivedData.setText(response);
                        }
                    });
                } catch (IOException e) {
                    final String errorMessage = "Error: " + e.getMessage();

                    // Update UI on the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextViewReceivedData.setText(errorMessage);
                        }
                    });
                }
            }
        }).start();
    }
}