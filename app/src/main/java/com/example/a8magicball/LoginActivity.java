package com.example.a8magicball;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextemail, editTextpass;

    private ImageView imgvlogo;

    private Button buttonsignin, buttonsignup;

    private com.google.android.gms.common.SignInButton buttongoogle;

    private com.facebook.login.widget.LoginButton buttonfacebook;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog loaderbar;

    private String email, pass;

    private String TAGOOGLE = "simpletagggole";

    private String TAGFACEBOOK = "simpletagggole";

    private AlertDialog alert;

    private int GOOGLE_SIGN_IN = 101;

    private int FACEBBOK_SIGN_IN = 102;

    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseUser user;

    private GoogleSignInAccount account;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);


        loaderbar = new ProgressDialog(this);
        imgvlogo = findViewById(R.id.imgvlogo);
        buttonsignin = findViewById(R.id.btnsignin);
        buttonsignup = findViewById(R.id.btnsignup);
        buttongoogle = findViewById(R.id.btnsigningoogle);
        firebaseAuth = FirebaseAuth.getInstance();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();



        editTextemail = findViewById(R.id.etemail);
        editTextpass = findViewById(R.id.etpass);


        buttongoogle.setOnClickListener(this);

        editTextpass.setOnClickListener(this);
        editTextemail.setOnClickListener(this);
        buttonsignup.setOnClickListener(this);
        buttonsignin.setOnClickListener(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        account = GoogleSignIn.getLastSignedInAccount(this);

        firebaseAuth = FirebaseAuth.getInstance();

        user = firebaseAuth.getCurrentUser();


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnsignup:
                if (isNetDisponible()) {
                    createUser();
                }
                else if (isOnlineNet()) {
                    createUser();
                }
                else{
                    Toast.makeText(this, "No tienes Conexion", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btnsignin:
                if (isNetDisponible()) {
                    iniciarSesion();

                }
                else if (isOnlineNet()) {
                    iniciarSesion();

                }
                else{
                    Toast.makeText(this, "No tienes Conexion", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnsigningoogle:
                loaderbar.setMessage("Cargando");
                loaderbar.show();
                signIn(buttongoogle);
                break;


        }


    }

    public void createUser() {
        email = editTextemail.getText().toString().trim();
        pass = editTextpass.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "The email field must be filled", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "The password field must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        loaderbar.setMessage("Registering user...");
        loaderbar.show();


        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loaderbar.dismiss();
                Intent intent;

                if (task.isSuccessful()) {

                    Toast.makeText(LoginActivity.this, "Correo valido", Toast.LENGTH_SHORT).show();
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        {
                            Toast.makeText(LoginActivity.this, "Correo ya registrado", Toast.LENGTH_SHORT).show();
                        }

                    } else {


                        Toast.makeText(LoginActivity.this, validarlogin(((FirebaseAuthException) task.getException()).getErrorCode()), Toast.LENGTH_LONG).show();


                    }

                }

            }
        });

    }

    public void iniciarSesion() {
        email = editTextemail.getText().toString();
        pass = editTextpass.getText().toString();

        if (validartextview(editTextemail)) {
            Toast.makeText(this, "The email field must be filled", Toast.LENGTH_SHORT).show();
            return;
        }
        if (validartextview(editTextpass)) {
            Toast.makeText(this, "The password field must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        loaderbar.setMessage("Sign in user...");
        loaderbar.show();

        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Intent intent;
                loaderbar.dismiss();

                if (task.isSuccessful()) {

                    Toast.makeText(LoginActivity.this, firebaseAuth.getCurrentUser().getEmail() + " connected", Toast.LENGTH_SHORT).show();

                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);

                } else {


                    if (task.getException().getLocalizedMessage().contains("We have blocked all requests")) {
                        Toast.makeText(LoginActivity.this, "Haz realizado el máximo de intentos, vuelva a intentar más tarde", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, validarlogin(((FirebaseAuthException) task.getException()).getErrorCode()), Toast.LENGTH_LONG).show();
                    }


                }
            }


        });
    }


    public boolean validartextview(TextView v) {
        if (TextUtils.isEmpty(v.getText().toString()))
            return true;
        else return false;

    }


    public String validarlogin(String codigoerror) {

        switch (codigoerror) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                return "El formato de token personalizado es incorrecto. Por favor revise la documentación";

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                return "El token personalizado corresponde a una audiencia diferente.";

            case "ERROR_INVALID_CREDENTIAL":
                return "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.";

            case "ERROR_INVALID_EMAIL":
                return "La dirección de correo electrónico está mal formateada.";


            case "ERROR_WRONG_PASSWORD":
                return "La contraseña no es válida";


            case "ERROR_USER_MISMATCH":
                return "Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente.";


            case "ERROR_REQUIRES_RECENT_LOGIN":
                return "Esta operación es confidencial y requiere autenticación reciente. Inicie sesión nuevamente antes de volver a intentar esta solicitud.";


            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                return "Ya existe una cuenta con la misma dirección de correo electrónico pero con diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.";


            case "ERROR_EMAIL_ALREADY_IN_USE":
                return "La dirección de correo electrónico ya está en uso por otra cuenta.";


            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                return "Esta credencial ya está asociada con una cuenta de usuario diferente.";


            case "ERROR_USER_DISABLED":
                return "La cuenta de usuario ha sido desactivada por un administrador.";


            case "ERROR_USER_TOKEN_EXPIRED":
                return "La credencial del usuario ya no es válida. El usuario debe iniciar sesión de nuevo.";


            case "ERROR_USER_NOT_FOUND":
                return "No hay ningún registro de usuario correspondiente a este identificador. Es posible que el usuario haya sido eliminado.";


            case "ERROR_INVALID_USER_TOKEN":
                return "La credencial del usuario ya no es válida. El usuario debe iniciar sesión de nuevo.";

            case "ERROR_OPERATION_NOT_ALLOWED":
                return "Esta operación no está permitida. Debe habilitar este servicio en la consola.";

            case "ERROR_WEAK_PASSWORD":
                return "La contraseña proporcionada es debil.";

            default:
                return "Codigo excepcion no encontrado";


        }

    }


    private void signIn(View v) {

        switch (v.getId()) {

            case R.id.btnsigningoogle:
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();

                startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
                break;


        }
    }

    public void accesscredential(GoogleSignInAccount acct, AccessToken token){

        AuthCredential credential = null;


        if (acct != null) {
            credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            firebaseAuth(credential);

        }
        else if (token != null){
            credential = FacebookAuthProvider.getCredential(token.getToken());
            firebaseAuth(credential);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loaderbar.dismiss();
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {


            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                accesscredential(account,null);
                loaderbar.setMessage("Registrando usuario");
                loaderbar.show();

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAGOOGLE, "Google sign in failed", e);
                // ...
            }
        }

    }

    private void firebaseAuth(AuthCredential credential) {



        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAGOOGLE, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();


                            Toast.makeText(LoginActivity.this, user.getEmail() + " Connected", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAGOOGLE, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Fallo la conexión", Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });


    }


    @Override
    protected void onStart() {

        user = firebaseAuth.getCurrentUser();

        if (user != null) {
            startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(this, user.getEmail(), Toast.LENGTH_SHORT).show();
        }
        super.onStart();
    }

    public void signout(FirebaseAuth session) {

        user = session.getCurrentUser();

        if (user != null) {

            session.signOut();

        } else {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }


    }
    private boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }
    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
}
