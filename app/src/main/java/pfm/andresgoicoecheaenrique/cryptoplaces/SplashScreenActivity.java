package pfm.andresgoicoecheaenrique.cryptoplaces;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SplashScreenActivity extends AppCompatActivity {

    // Peticion inical para ir cargando datos api coinmap
    private RequestQueue mQueue;
    private ArrayList<Venue> venuesAL = new ArrayList<Venue>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //String url = "https://coinmap.org/api/v1/venues/?limit="+35000;//500 venues
        //jsonParse(url);

        waitSomeSecs();
    }




    private void waitSomeSecs(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, GoogleMapsActivity.class);//LoginActivity
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
    private void jsonParse(String url) {
        mQueue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("venues");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject venue = jsonArray.getJSONObject(i);

                                venuesAL.add(new Venue(
                                        venue.getLong("id"),
                                        venue.getDouble("lat"),
                                        venue.getDouble("lon"),
                                        venue.getString("category"),
                                        venue.getString("name"),
                                        venue.getLong("created_on"),
                                        venue.getString("geolocation_degrees")
                                ));
                            }
                            mostrarToast("tamaño = "+venuesAL.size());
                            //cargar a la bbdd
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mQueue.add(request);
    }
    private void mostrarToast(String msj){
        Toast.makeText(this, msj, Toast.LENGTH_LONG).show();
    }
}