package pfm.andresgoicoecheaenrique.cryptoplaces;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
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

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private ImageButton boton_path;

    public Venue venueDFFB;

    private String updated_on;
    private String phone;
    private String share;
    private Boolean favChecked;

    private Boolean wasChecked;
    private Boolean isFAVS;

    private GestorBD_Venue gBD;
    private String correoUsuario;
    private Geocoder gcd;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            venueDFFB = (Venue) getArguments().getSerializable("venue");
            updated_on = getArguments().getString("updated_on", "null");
            phone = getArguments().getString("phone", "null");
            share = "https://www.google.com/maps/search/" + venueDFFB.getLat() + "," + venueDFFB.getLon();
            if (updated_on == "null") {
                updated_on = String.valueOf(venueDFFB.getCreatedOn());
            }
            favChecked = getArguments().getBoolean("favChecked", false);
            wasChecked = favChecked;
            isFAVS = getArguments().getBoolean("isFavs", false);
        } else {
            CommonUtils.mostrarToast(getResources().getString(R.string.error_reading_data_from_arguments), getContext());
        }
        correoUsuario = CommonUtils.readCorreoUsuario(getContext());
        if(correoUsuario == "null"){
            CommonUtils.mostrarToast("Error", getActivity());
        }
        else{
            gBD = new GestorBD_Venue(getActivity(), CommonUtils.buildTableNameDB(correoUsuario));
        }
        gcd = new Geocoder(getActivity(), Locale.getDefault());

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
                if(wasChecked != favChecked){
                    if (wasChecked) {
                        CommonUtils.operacionesBD(gBD, (short) 0, venueDFFB, getActivity());
                    } else if(!wasChecked) {
                        CommonUtils.operacionesBD(gBD, (short) 1, venueDFFB, getActivity());
                    }
                }
                if(isFAVS){
                    //Llamo a un metodo publico de una Activity desde un DialogFragment
                    ((GoogleMapsActivity)getActivity()).initAdapterFavs();
                }
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
                myIntent.putExtra(Intent.EXTRA_SUBJECT, sub);
                myIntent.putExtra(Intent.EXTRA_TEXT, body);
                startActivity(Intent.createChooser(myIntent, "Share Using"));
            }
        });

        boton_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numero = phone;
                if (!Patterns.PHONE.matcher(numero).matches()) {
                    mostrarToast(getResources().getString(R.string.call_Error_number));
                } else {
                    Uri call = Uri.parse("tel:" + numero);
                    Intent intent = new Intent(Intent.ACTION_DIAL, call);
                    startActivity(intent);
                }
            }
        });

        boton_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GoogleMapsActivity)getActivity()).peticionGoogle(new LatLng(venueDFFB.getLat(), venueDFFB.getLon()));
                dismiss();
            }
        });

        tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tb.isChecked()) {
                    wasChecked = true;
                } else {
                    wasChecked = false;
                }
            }
        });

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
        boton_path = view.findViewById(R.id.boton_route);

        tb = view.findViewById(R.id.toggleBTN_id);

        if (phone == "null") {
            boton_call.setVisibility(View.GONE);
        }
    }

    private void initGeneralValues() {
        nombreVenue.setText(venueDFFB.getName());
        categoriaVenue.setText(CommonUtils.traducirCategory(venueDFFB.getCategory(), getContext()));
        paisVenue.setText(countryBasedOncoordenates(venueDFFB.getLat(), venueDFFB.getLon()));
        creacionVenue.setText(convertLongToDate(String.valueOf(venueDFFB.getCreatedOn())));
        actualizacionVenue.setText(convertLongToDate(String.valueOf(updated_on)));
        tb.setChecked(favChecked);
    }

    private String convertLongToDate(String timeStamp) {
        timeStamp += "000";
        long l = Long.parseLong(timeStamp);
        Date date = new Date(l);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy\n hh:mm:ss");
        return formatter.format(date);
    }

    /**
     * ***** *****   CODE MOSTRAR INFO DF   ***** *****
     */
    private String countryBasedOncoordenates(Double lat, Double lon) {
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(lat, lon, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        String countryName = null;
        if (addresses.size() > 0) {
            countryName = addresses.get(0).getCountryName();
        }
        return countryName;
    }

    private void mostrarToast(String msj) {
        Toast.makeText(getContext(), msj, Toast.LENGTH_SHORT).show();
    }
}
