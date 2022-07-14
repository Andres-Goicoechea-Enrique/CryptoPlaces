package pfm.andresgoicoecheaenrique.cryptoplaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import pfm.andresgoicoecheaenrique.cryptoplaces.Kraken.ExchangeAPI;


public class DialogFragmentNewExchangeAPI extends DialogFragment {

    private EditText apiName_ET;
    private EditText key_ET;
    private EditText secret_ET;

    private String nombre = "";
    private String key = "";
    private String secret = "";

    private Button boton_crear;
    private Button boton_cerrar;

    private GestorBD_API_Kraken gestorBD_api_kraken;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        gestorBD_api_kraken = new GestorBD_API_Kraken(getActivity(), CommonUtils.buildTableNameDB("test1@mail.es"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_fragment_new_exchange_api, container, false);

        initControls(view);

        boton_crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarCamposAPI()){
                    ExchangeAPI krakenAPIcode = new ExchangeAPI(-1, nombre, key, secret);
                    CommonUtils.operacionesBDAPIS(gestorBD_api_kraken, (short) 0, krakenAPIcode, getContext());
                    ((GoogleMapsActivity)getActivity()).initAdapterKrakenAPIs();
                    dismiss();
                }
                else{
                    CommonUtils.mostrarToast(getResources().getString(R.string.error_reading_data_from_form_new_API), getContext());
                }
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

    private void initControls(View view) {
        apiName_ET = view.findViewById(R.id.NombreAPI_ET_id);
        key_ET = view.findViewById(R.id.keyAPI_ET_id);
        secret_ET = view.findViewById(R.id.secretAPI_ET_id);

        boton_crear = view.findViewById(R.id.boton_crear_api_id);
        boton_cerrar = view.findViewById(R.id.boton_cerrar_id);
    }

    private boolean validarCamposAPI(){
        boolean resul = false;
        boolean checkNombre = validarNombre();
        boolean checkKey = validarKey();
        boolean checkSecret = validarSecret();

        if(checkNombre && checkKey && checkSecret){
            resul = true;
        }

        return resul;
    }

    private boolean validarNombre(){
        nombre = apiName_ET.getText().toString();
        boolean resul = false;

        if(nombre.isEmpty()){
            apiName_ET.setError(getResources().getString(R.string.Input_Error_field_empty));
        }
        else if(nombre.length() > 20){
            apiName_ET.setError(getResources().getString(R.string.Input_Error_name));
        }
        else{
            apiName_ET.setError(null);
            resul = true;
        }
        return resul;
    }

    private boolean validarKey(){
        key = key_ET.getText().toString();
        boolean resul = false;

        if(key.isEmpty()){
            key_ET.setError(getResources().getString(R.string.Input_Error_field_empty));
        }
        else if(key.length() < 56){
            key_ET.setError(getResources().getString(R.string.Input_Error_key1));
        }
        else if(key.length() > 56){
            key_ET.setError(getResources().getString(R.string.Input_Error_key2));
        }
        else{
            key_ET.setError(null);
            resul = true;
        }
        return resul;
    }

    private boolean validarSecret(){
        secret = secret_ET.getText().toString();
        boolean resul = false;

        if(secret.isEmpty()){
            secret_ET.setError(getResources().getString(R.string.Input_Error_field_empty));
        }
        else if(secret.length() < 88){
            secret_ET.setError(getResources().getString(R.string.Input_Error_secret1));
        }
        else if(secret.length() > 88){
            secret_ET.setError(getResources().getString(R.string.Input_Error_secret2));
        }
        else{
            secret_ET.setError(null);
            resul = true;
        }
        return resul;
    }


}