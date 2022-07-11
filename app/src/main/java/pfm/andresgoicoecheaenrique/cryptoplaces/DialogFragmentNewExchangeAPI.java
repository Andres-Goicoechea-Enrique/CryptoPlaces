package pfm.andresgoicoecheaenrique.cryptoplaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class DialogFragmentNewExchangeAPI extends DialogFragment {

    private EditText apiName;
    private EditText key;
    private EditText secret;

    private Button boton_crear;
    private Button boton_cerrar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_fragment_new_exchange_api, container, false);

        initControls(view);

        boton_cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.mostrarToast(apiName.getText().toString(), getContext());
                dismiss();
            }
        });

        return view;
    }

    private void initControls(View view) {
        apiName = view.findViewById(R.id.NombreAPI_ET_id);
        key = view.findViewById(R.id.keyAPI_ET_id);
        secret = view.findViewById(R.id.secretAPI_ET_id);

        //boton_crear = view.findViewById(R.id.boton_crear_id);
        boton_cerrar = view.findViewById(R.id.boton_cerrar_id);
    }


}