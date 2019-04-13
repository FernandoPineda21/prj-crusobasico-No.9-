package com.example.a8magicball;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Delayed;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String mensaje= "Presione la Bola 8";

    private String salir = "¿salir?";

    private Button buttonsignout;

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount googleSignInAccount;


    private ImageView rotacionblanco;
    private ObjectAnimator animacion;

    private ImageView rotacionnegra;
    private ObjectAnimator animacionreverse;

    private TextView texto8;
    private ObjectAnimator animacionreversetexto;

    private TextView respuesta;
    private int longitudpreunta = 69;
    private String respuestareconocimiento;
    private SpeechRecognizer speechRecognizer;

    private TextView txtwordreconizer;

    private int timeanimation = 1000;
    private TTSManager ttsManager = null;

    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;
    private long lastupdate;
    private float last_x, last_y, last_z;
    private float shakeball = 30;

    private String ultimarespuesta = "";
    private String primerarespuesta = "";
    private String respuestarepetida = "";

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private GoogleSignInAccount account;

    String[] Elementos = {
            "En mi Opinión, sí",
            "Es cierto",
            "Es decididamente sí",
            "Probablemente",
            "Todo apunta a que sí",
            "Sin duda",
            "Sí",
            "Definitivamente sí",
            "Vuelve a intentarlo",
            "Pregunta en otro momento",
            "No puedo predecirlo ahora",
            "Concéntrate y vuelve a preguntar",
            "Puede Ser",
            "Quizás",
            "Talvez",
            "Puede ser",
            "No confies en ello",
            "Mi Respuesta es no",
            "Mis Fuentes me dicen que no",
            "Por supuesto que no",
            "No",};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        firebaseAuth = FirebaseAuth.getInstance();

        user = firebaseAuth.getCurrentUser();



        rotacionblanco = findViewById(R.id.btn8bal1inside);
        rotacionblanco.setOnClickListener(this);

        rotacionnegra = findViewById(R.id.btn8balloutside);
        rotacionnegra.setOnClickListener(this);

        texto8 = findViewById(R.id.txt8);
        texto8.setOnClickListener(this);


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(sensor.TYPE_ACCELEROMETER);

        txtwordreconizer = findViewById(R.id.txtreconizer);

        ttsManager = new TTSManager();
        ttsManager.init(this);







        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {


                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                long curtime = System.currentTimeMillis();

                long diftime = (curtime - lastupdate);

                if (diftime > 100) {

                    lastupdate = curtime;

                    float speed = Math.abs(x + y + z - last_x - last_y - last_z);


                    if (speed > shakeball) {
                        correrAnimacion();
                    }
                    last_x = x;
                    last_y = y;
                    last_z = z;
                    lastupdate = curtime;

                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

    }


    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.btn8balloutside:
                onClick(rotacionblanco);
                break;
            case R.id.txt8:
                onClick(rotacionblanco);
                break;


            case R.id.btn8bal1inside:
                reconocerVoz();




                break;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    primerarespuesta = result.get(0).toString();
                    if (!ultimarespuesta.equals(primerarespuesta)) {
                        if (primerarespuesta.length() < longitudpreunta) {
                            txtwordreconizer.setText("¿" + result.get(0) + "?");
                            ultimarespuesta = primerarespuesta;
                        } else {
                            respuestareconocimiento = "La pregunta es extensa, Vuelve a intentar";
                            ttsManager.addQueue(respuestareconocimiento);
                        }
                    } else {
                        txtwordreconizer.setText("Esta pregunta es igual a la anterior, hazme otra");
                        ttsManager.addQueue("Respuesta repetida");
                    }
                }
                break;


        }

    }


    public String generarRandom(String[] elementos) {

        Random random = new Random(System.currentTimeMillis());

        int aleatorio = random.nextInt(21);

        String respuesta = elementos[aleatorio];

        return respuesta;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ttsManager.shutDown();
    }

    public void correrAnimacion() {

            animacion = ObjectAnimator.ofFloat(rotacionblanco, "rotation", 0f, 540f);
            animacion.setDuration(timeanimation);

            animacionreverse = ObjectAnimator.ofFloat(rotacionnegra, "rotation", 540f, 0f);
            animacionreverse.setDuration(timeanimation);

            animacionreversetexto = ObjectAnimator.ofFloat(texto8, "rotation", 0, 360f);
            animacionreversetexto.setDuration(timeanimation);

            AnimatorSet animatorSetRotation = new AnimatorSet();
            animatorSetRotation.playTogether(animacion, animacionreverse, animacionreversetexto);
            animatorSetRotation.start();


            animacion.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {


                    if (animacion.isRunning() == false) {
                        if (!txtwordreconizer.getText().equals(salir)) {
                            if (!txtwordreconizer.getText().equals(mensaje)) {
                                if (!respuestarepetida.equals(primerarespuesta)) {
                                    respuestareconocimiento = generarRandom(Elementos);
                                    Toast.makeText(getApplicationContext(), respuestareconocimiento, Toast.LENGTH_SHORT).show();
                                    ttsManager.addQueue(respuestareconocimiento);
                                    respuestarepetida = primerarespuesta;
                                } else {
                                    ttsManager.addQueue("Respuesta repetida, Favor intentar con otra");

                                }
                            } else {
                                ttsManager.addQueue("HAZME UNA PREGUNTA");
                                return;
                            }
                        }
                        else {
                            LoginActivity login = new LoginActivity();
                            login.signout(firebaseAuth);
                            mGoogleSignInClient.signOut();
                            startActivity(new Intent(MainActivity.this,LoginActivity.class));

                            finish();
                        }
                    }

                }
            });



    }

    public void reconocerVoz() {
        Intent intent;

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Device is not soported", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onPause() {
        sensorManager.unregisterListener(sensorEventListener);
        super.onPause();
    }

    @Override
    protected void onResume() {
        sensorManager.registerListener(sensorEventListener, sensor, sensorManager.SENSOR_DELAY_FASTEST);
        super.onResume();
    }


}
