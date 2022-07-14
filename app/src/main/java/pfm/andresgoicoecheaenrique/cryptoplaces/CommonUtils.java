package pfm.andresgoicoecheaenrique.cryptoplaces;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import pfm.andresgoicoecheaenrique.cryptoplaces.Kraken.ExchangeAPI;


public abstract class CommonUtils {

    private static RequestQueue mQueue;


    /**
     * GESTIONAR BD
     */
    protected static void operacionesBD(GestorBD_Venue gBD, short codeOperacion, Venue venue, Context context){
        if(codeOperacion == 0){// Insertar Venue
            long id1 = gBD.insertarNewVenue(venue);
            mostrarToast("ID: "+id1, context);
        }
        else if(codeOperacion == 1){// Eliminar Venue
            gBD.borrarVenue(venue.getId());
        }
        else{
            mostrarToast("Error", context);
        }
    }
    /**
     * Leer BD
     */
    protected static ArrayList<Venue> leerBBDDSQLite(GestorBD_Venue gBD){//ordenar
        return gBD.getAllVenues("NAME", "ASC");
    }

    /**
     * GESTIONAR BD APIS
     */
    protected static void operacionesBDAPIS(GestorBD_API_Kraken gBD, short codeOperacion, ExchangeAPI api, Context context){
        if(codeOperacion == 0){// Insertar API
            long id1 = gBD.insertarNewAPI(api);
            mostrarToast("ID: "+id1, context);
        }
        else if(codeOperacion == 1){// Eliminar API
            gBD.borrarAPI(api.getId());
        }
        else{
            mostrarToast("Error", context);
        }
    }

    /**
     * Nombre de la tabla de la BD en base al correo del usuario.
     */
    protected static String buildTableNameDB(String s){
        String resul = s.replace("@", "");
        return resul.replace(".","");
    }

    /**
     * Crear Alertas
     * @param code
     */
    protected static void crearAlert(short code, Context context) {

        AlertDialog.Builder constructor = new AlertDialog.Builder(context);
        constructor.setCancelable(false);
        if (code == 0) {// Muestra la informacion de Acerca de
            constructor.setTitle(context.getResources().getString(R.string.title_alertdialog_aboutus) + context.getResources().getString(R.string.app_name))
                    .setMessage(context.getResources().getString(R.string.msg_alertdialog_aboutus))
                    .setPositiveButton(context.getResources().getString(R.string.btn_alertdialog_aboutus), null);
        } else if (code == 1) {// ACTIVAR LOCALIZACION
            constructor.setTitle(context.getResources().getString(R.string.title_alertdialog_activate_location))
                    .setMessage(context.getResources().getString(R.string.msg_alertdialog_activate_location))
                    .setPositiveButton(context.getResources().getString(R.string.btn_positive_alertdialog_activate_location), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            context.startActivity(myIntent);
                        }
                    })
                    .setNegativeButton(context.getResources().getString(R.string.btn_negative_alertdialog_activate_location), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            mostrarToast(context.getResources().getString(R.string.toast_negative_alertdialog_activate_location), context);
                        }
                    });
        } else { // NO HAY INTERNET
            constructor.setTitle(context.getResources().getString(R.string.title_alertdialog3));
            constructor.setMessage(context.getResources().getString(R.string.msg_alertdialog3));
            constructor.setNeutralButton(context.getResources().getString(R.string.neutral_btn_alertdialog), null);
        }
        constructor.create().show();
    }

    /**
     * Comprobar si hay conexion a internet.
     */
    protected static boolean isInternetEnabled(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
    /**
     * Muestra un Toast corto.
     */
    protected static void mostrarToast(String msj, Context context) {
        Toast.makeText(context, msj, Toast.LENGTH_SHORT).show();
    }
    /**
     * Convierte el primer char de un string en MAYUS y el resto en minus.
     */
    protected static String setCategoryName(String string) {
        return string.substring(0, 1).toUpperCase(Locale.ROOT) + string.toLowerCase(Locale.ROOT).substring(1);

    }
    /**
     * Asignar un color a cada marcador dependiendo de su categoria
     */
    protected static BitmapDescriptor asignarColor(String category) {
        float colorIcono = (float)90.0;//sin categoria == default 3

        for(todasCategoriasEnum categorias : todasCategoriasEnum.values()){
            if(categorias.getCategoria().equals(category)){
                colorIcono = categorias.getColorIcono();
                break;
            }
        }
        BitmapDescriptor bitmap = BitmapDescriptorFactory.defaultMarker(colorIcono);
        return bitmap;
    }
    /**
     * Asignar un color INT a cada marcador dependiendo de su categoria
     */
    protected static short colorIndex(String category){
        short resul = (short) 3;//sin categoria == default 3
        for(todasCategoriasEnum categorias : todasCategoriasEnum.values()){
            if(categorias.getCategoria().equals(category)){
                resul = categorias.getIndex();
                break;
            }
        }
        return resul;
    }
    /**
     * Traduce las categorias al idioma correspondiente
     */
    protected static String traducirCategory(String category, Context context){
        String resul = context.getResources().getString(R.string.CATEGORIA4);//sin categoria == default 3
        for(todasCategoriasEnum categorias : todasCategoriasEnum.values()){
            if(categorias.getCategoria().equals(category)){
                resul = categorias.getLabel(context);
                break;
            }
        }
        return CommonUtils.setCategoryName(resul);
    }

    /**
     * Datos venue
     */
    protected static void jsonParse(String url, ArrayList<Venue> favsVenuesAL, Context context, androidx.fragment.app.FragmentManager FragManager){// short code,
        if(!isInternetEnabled(context)){
            crearAlert((short) 2, context);
        }
        else{
            mQueue = Volley.newRequestQueue(context);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Peticion API Coinmap venue info
                            try {
                                JSONObject jsonObjectVenueDetails = response.getJSONObject("venue");

                                // Guardar todos los datos, pero no da tiempo
                                Venue venueDFFB = convertDataForVenue(
                                        jsonObjectVenueDetails.getString("id"),
                                        jsonObjectVenueDetails.getString("lat"),
                                        jsonObjectVenueDetails.getString("lon"),
                                        jsonObjectVenueDetails.getString("category"),
                                        jsonObjectVenueDetails.getString("name"),
                                        jsonObjectVenueDetails.getString("created_on"),
                                        jsonObjectVenueDetails.getString("geolocation_degrees")
                                );

                                Bundle bundle = new Bundle();
                                bundle.putSerializable("venue", venueDFFB);
                                bundle.putString("updated_on", jsonObjectVenueDetails.getString("updated_on"));
                                bundle.putString("phone", jsonObjectVenueDetails.getString("phone"));
                                bundle.putBoolean("favChecked", isVenueInFavsAL(favsVenuesAL, venueDFFB));


                                DialogFragmentVenueInfo dialogFragmentVenueInfo = new DialogFragmentVenueInfo();
                                dialogFragmentVenueInfo.setArguments(bundle);
                                dialogFragmentVenueInfo.setCancelable(false);
                                dialogFragmentVenueInfo.show(FragManager, "GoogleMapsDialogFragmentVenueInfo");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mostrarToast(context.getResources().getString(R.string.toast_error_peticion_coinmap), context);
                    error.printStackTrace();
                }
            });

            mQueue.add(request);
        }
    }

    /**
     * Convertir data a un objeto Venue
     */
    private static Venue convertDataForVenue(String s1, String s2, String s3, String s4, String s5, String s6, String s7) {
        return new Venue(Long.parseLong(s1), Double.parseDouble(s2), Double.parseDouble(s3), s4, s5, Long.parseLong(s6), s7);
    }
    /**
     * Comprobar si el venue esta en la lista de favoritos
     */
    private static boolean isVenueInFavsAL(ArrayList<Venue> favsVenuesAL, Venue venue) {
        Boolean resul = false;
        for (Venue v : favsVenuesAL) {
            if (v.getId() == venue.getId()) {
                resul = true;
                break;
            }
        }
        return resul;
    }

}
