package pfm.andresgoicoecheaenrique.cryptoplaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;
import java.util.concurrent.Executor;

public class DialogFragmentResetPassword extends DialogFragment {

    private EditText email_ET;
    private Button resetPassword_BTN, boton_cerrar;

    private String correoIntroducido = "";

    private FirebaseAuth mAuth;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_fragment_reset_password, container, false);

        initControls(view);
        mAuth = FirebaseAuth.getInstance();

        resetPassword_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        boton_cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private void resetPassword() {
        if(validarCorreo()){
            mAuth.setLanguageCode(Locale.getDefault().getLanguage());
            mAuth.sendPasswordResetEmail(correoIntroducido)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                CommonUtils.mostrarToast(getResources().getString(R.string.Send_Password_Reset_Email), getContext());
                                dismiss();
                            } else {
                                CommonUtils.mostrarToast(getResources().getString(R.string.Error_Password_Reset_Email), getContext());
                            }
                        }
                    });
        }
        else{
            CommonUtils.mostrarToast(getResources().getString(R.string.Input_Error_fields), getContext());
        }


    }
    private boolean validarCorreo(){
        correoIntroducido = email_ET.getText().toString();
        boolean resul = false;

        if(correoIntroducido.isEmpty()){
            email_ET.setError(getResources().getString(R.string.Input_Error_field_empty));
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(correoIntroducido).matches()){
            email_ET.setError(getResources().getString(R.string.Input_Error_email1));
        }
        else{
            email_ET.setError(null);
            resul = true;
        }
        return resul;
    }


    private void initControls(View view) {
        email_ET = view.findViewById(R.id.email_ET_id);

        resetPassword_BTN = view.findViewById(R.id.boton_reset_df_id);
        boton_cerrar = view.findViewById(R.id.boton_cerrar_df_id);
    }

}