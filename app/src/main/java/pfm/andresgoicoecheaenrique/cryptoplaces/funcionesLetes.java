package pfm.andresgoicoecheaenrique.cryptoplaces;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class funcionesLetes {

    private DialogFragmentVenueInfo dialogFragmentVenueInfo = new DialogFragmentVenueInfo();
    private RequestQueue mQueue;
    private JSONObject jsonObjectVenueDetails;
    private Context context;
    private androidx.fragment.app.FragmentManager FragManager;

    public funcionesLetes(Context c, androidx.fragment.app.FragmentManager FragManager) {
        this.context = c;
        this.FragManager = FragManager;
    }

    // Comprobar si hay conexion a internet
    private boolean isInternetEnabled() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    void jsonParse(String url, Integer code) {
        if (!isInternetEnabled()) {// No internet
            //crearAlert(2);
        } else {// Si internet
            mQueue = Volley.newRequestQueue(context);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Peticion API Coinmap venue info
                            try {
                                jsonObjectVenueDetails = response.getJSONObject("venue");
                                String lat = jsonObjectVenueDetails.getString("lat");
                                String lon = jsonObjectVenueDetails.getString("lon");

                                //coordenadasDestino = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));

                                //DialogFragmentVenueInfo dialogFragmentVenueInfo = new DialogFragmentVenueInfo();
                                Bundle bundle = new Bundle();
                                bundle.putString("name", jsonObjectVenueDetails.getString("name"));
                                bundle.putString("category", jsonObjectVenueDetails.getString("category"));
                                bundle.putString("country", jsonObjectVenueDetails.getString("country"));
                                bundle.putString("created_on", jsonObjectVenueDetails.getString("created_on"));
                                bundle.putString("updated_on", jsonObjectVenueDetails.getString("updated_on"));
                                bundle.putString("phone", jsonObjectVenueDetails.getString("phone"));
                                bundle.putString("share", "https://www.google.com/maps/search/" + lat + "," + lon);
                                dialogFragmentVenueInfo.setArguments(bundle);
                                dialogFragmentVenueInfo.show(FragManager, "GoogleMapsDialogFragmentVenueInfo");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //mostrarToast(getResources().getString(R.string.toast_error_peticion_coinmap) + error);
                    error.printStackTrace();
                }
            });

            mQueue.add(request);
        }
    }
}
