package pfm.andresgoicoecheaenrique.cryptoplaces;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    private ArrayList<Venue> venuesAL = new ArrayList<Venue>();


    private static final String ACCION_REGISTRAR = "reg";
    private static final String ACCION_ACCEDER = "acc";

    private SharedPreferences sharedPrefs;
    private static final String MY_PREFERENCE = "CryptoPlaces";

    private EditText correoUsuario, contraseñaUsuario;
    private Button registrarBTN, accederBTN, resetBTN;

    private DialogFragmentResetPassword dialogFragmentResetPassword = new DialogFragmentResetPassword();

    private FirebaseAuth mAuth;

    //DialogCarga dialogCarga = new DialogCarga(LoginActivity.this);
    //private Handler mainHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        initForm();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Si ha iniciado sesion y vuelve a esta pantalla se cierra la sesion
        //Borrar datos introducidos inicialmente
        if(mAuth.getCurrentUser() != null){
            mAuth.signOut();
            crearAlert(1);
        }
        correoUsuario.setText("");
        contraseñaUsuario.setText("");
    }



    private void initForm(){
        correoUsuario = (EditText) findViewById(R.id.campo_correo);
        contraseñaUsuario = (EditText) findViewById(R.id.campo_contraseña);
        registrarBTN = (Button) findViewById(R.id.boton_registrar);
        accederBTN = (Button) findViewById(R.id.boton_acceder);
        resetBTN = (Button) findViewById(R.id.boton_reset_login_id);

        registrarBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCondiciones(ACCION_REGISTRAR);
            }
        });
        accederBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCondiciones(ACCION_ACCEDER);
            }
        });
        resetBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragmentResetPassword.show(getSupportFragmentManager(), "LoginDialogFragmentResetPassword");
            }
        });
    }

    private void checkCondiciones(String btn){
        if(CommonUtils.isInternetEnabled(this)){
            if(validarCampos()){
                if(btn.equals(ACCION_REGISTRAR)){// registrarBTN
                    mAuth.createUserWithEmailAndPassword(correoUsuario.getText().toString(), contraseñaUsuario.getText().toString())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        moverMainActivity();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        crearAlert(0);
                                    }
                                }
                            });
                }
                else{// accederBTN
                    mAuth.signInWithEmailAndPassword(correoUsuario.getText().toString(), contraseñaUsuario.getText().toString())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        moverMainActivity();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        crearAlert(0);
                                    }
                                }
                            });
                }
            }
            else{// campos invalidos
                CommonUtils.mostrarToast(getResources().getString(R.string.Input_Error_fields), this);
            }
        }
        else{// No internet
            crearAlert(2);
        }
    }

    private boolean validarCampos() {
        boolean resul = false;
        boolean correo = validarCorreo();
        boolean contra = validarContraseña();

        if(correo && contra){
            resul = true;
        }

        return resul;
    }

    private boolean validarCorreo(){
        String correoIntroducido = correoUsuario.getText().toString();
        boolean resul = false;

        if(correoIntroducido.isEmpty()){
            correoUsuario.setError(getResources().getString(R.string.Input_Error_field_empty));
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(correoIntroducido).matches()){
            correoUsuario.setError(getResources().getString(R.string.Input_Error_email1));
        }
        else{
            correoUsuario.setError(null);
            resul = true;
        }
        return resul;
    }

    private boolean validarContraseña(){
        String contraseñaIntroducido = contraseñaUsuario.getText().toString();
        boolean resul = false;
        // Al menos 1 cifra, minus, mayus y entre 8 y 20 char
        Pattern patron = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$");

        if(contraseñaIntroducido.isEmpty()){
            contraseñaUsuario.setError(getResources().getString(R.string.Input_Error_field_empty));
        }
        else if(contraseñaIntroducido.length() < 8){
            contraseñaUsuario.setError(getResources().getString(R.string.Input_Error_contraseña1));
        }
        else if(contraseñaIntroducido.length() > 20){
            contraseñaUsuario.setError(getResources().getString(R.string.Input_Error_contraseña2));
        }
        else if(!patron.matcher(contraseñaIntroducido).matches()){
            contraseñaUsuario.setError(getResources().getString(R.string.Input_Error_contraseña3));
        }
        else{
            correoUsuario.setError(null);
            resul = true;
        }

        return resul;
    }

    private void crearAlert(int code) {
        AlertDialog.Builder constructor = new AlertDialog.Builder(LoginActivity.this);
        constructor.setCancelable(true);
        if(code == 0){
            constructor.setTitle(getResources().getString(R.string.title_alertdialog1));
            constructor.setMessage(getResources().getString(R.string.msg_alertdialog1));
            constructor.setNeutralButton(getResources().getString(R.string.neutral_btn_alertdialog), null);
        }
        else if(code == 1){
            constructor.setTitle(getResources().getString(R.string.title_alertdialog2));
            constructor.setMessage(getResources().getString(R.string.msg_alertdialog2));
            constructor.setNeutralButton(getResources().getString(R.string.neutral_btn_alertdialog), null);
        }
        else{//COMMON UTILS
            constructor.setTitle(getResources().getString(R.string.title_alertdialog3));
            constructor.setMessage(getResources().getString(R.string.msg_alertdialog3));
            constructor.setNeutralButton(getResources().getString(R.string.neutral_btn_alertdialog), null);
        }

        constructor.create().show();
    }

    private void moverMainActivity(){
        //Nos vamos a la siguiente Activity
        if(!isGooglePlayServicesAvailable()){
            finish();
        }
        else{
            sharedPrefs = getSharedPreferences(MY_PREFERENCE, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString("email", correoUsuario.getText().toString());
            editor.putString("passw", contraseñaUsuario.getText().toString());
            editor.commit();

            Intent intent = new Intent(LoginActivity.this, GoogleMapsActivity.class);
            startActivity(intent);
        }
    }

    private boolean isGooglePlayServicesAvailable(){
        boolean resul = true;
        GoogleApiAvailability gApi = GoogleApiAvailability.getInstance();
        int resultCode = gApi.isGooglePlayServicesAvailable(this);
        CommonUtils.mostrarToast(""+resultCode,this);//quitar
        if (resultCode != ConnectionResult.SUCCESS) {
            CommonUtils.mostrarToast("toast_playservices_unrecoverable",this);
            resul = false;
        }
        return resul;
    }
}