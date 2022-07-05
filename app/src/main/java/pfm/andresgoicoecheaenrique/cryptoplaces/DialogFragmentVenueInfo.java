package pfm.andresgoicoecheaenrique.cryptoplaces;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DialogFragmentVenueInfo extends DialogFragment {

    private ToggleButton tb;

    private TextView nombreVenue;
    private TextView categoriaVenue;
    private TextView paisVenue;
    private TextView creacionVenue;
    private TextView actualizacionVenue;

    private Button boton_cerrar;

    private ImageButton boton_share;
    private ImageButton boton_call;

    private String name;
    private String category;
    private String country;
    private String created_on;
    private String updated_on;
    private String phone;
    private String share;
    private Boolean favChecked;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString("name", "");
            category = getArguments().getString("category","");
            country = getArguments().getString("country","");
            created_on = getArguments().getString("created_on", "");
            updated_on = getArguments().getString("updated_on","");
            phone = getArguments().getString("phone","");
            share = getArguments().getString("share","");
            if(updated_on == "null"){
                updated_on = created_on;
            }
            favChecked = Boolean.parseBoolean(getArguments().getString("favChecked","false"));
        }
        else{
            CommonUtils.mostrarToast("NO se pudo cargar la informacion del sitio.", getContext());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_fragment_venue_info, container, false);

        initControls(view);
        initGeneralValues();

        boton_cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        boton_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String body = share;//https://www.google.com/maps/search/-18.9137896,47.5401904
                String sub = getResources().getString(R.string.share_asunto);
                myIntent.putExtra(Intent.EXTRA_SUBJECT,sub);
                myIntent.putExtra(Intent.EXTRA_TEXT,body);
                startActivity(Intent.createChooser(myIntent, "Share Using"));
            }
        });

        boton_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numero = phone;
                if(!Patterns.PHONE.matcher(numero).matches()){
                    mostrarToast(getResources().getString(R.string.call_Error_number));
                }
                else{
                    Uri call = Uri.parse("tel:"+numero);
                    Intent intent = new Intent(Intent.ACTION_DIAL, call);
                    startActivity(intent);
                }
            }
        });

        //ToggleButton tb = (ToggleButton) findViewById(R.id.toggleBTN_id);
        /*tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tb.isChecked()) {
                    mostrarToast("chekeado");
                } else {
                    mostrarToast("no chekeado");
                }
            }
        });*/

        return view;
    }

    private void initControls(View view) {
        nombreVenue = view.findViewById(R.id.NombreVenue_TV);
        categoriaVenue = view.findViewById(R.id.id_categoriaVenueTV);
        paisVenue = view.findViewById(R.id.id_paisVenueTV);
        creacionVenue = view.findViewById(R.id.id_creacionTV);
        actualizacionVenue = view.findViewById(R.id.id_actualizacionTV);

        boton_cerrar = view.findViewById(R.id.boton_cerrar);
        boton_share = view.findViewById(R.id.boton_share);
        boton_call = view.findViewById(R.id.boton_call);

        tb = (ToggleButton) view.findViewById(R.id.toggleBTN_id);

        if(phone == "null"){
            boton_call.setVisibility(View.GONE);
        }
    }

    private void initGeneralValues(){
        nombreVenue.setText(name);
        categoriaVenue.setText(category);
        paisVenue.setText(country);
        creacionVenue.setText(convertLongToDate(created_on));
        actualizacionVenue.setText(convertLongToDate(updated_on));
        tb.setChecked(favChecked);
    }

    private String convertLongToDate(String timeStamp){
        timeStamp += "000";
        long l = Long.parseLong(timeStamp);
        Date date = new Date(l);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy\n hh:mm:ss");
        return formatter.format(date);
    }

    private void mostrarToast(String msj){
        Toast.makeText(getContext(), msj, Toast.LENGTH_SHORT).show();
    }
}
