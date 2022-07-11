package pfm.andresgoicoecheaenrique.cryptoplaces.Kraken;

import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import pfm.andresgoicoecheaenrique.cryptoplaces.R;

public class MostrarBalanceActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {


    private static final Double MIN_AMMOUNT = 0.00001;

    // BUSCADOR
    private SearchView buscadorSV;
    private TextView cantidad_tv, error_carga_tv;

    private ArrayList<Criptomoneda> cryptosAL = new ArrayList<Criptomoneda>();
    private RecyclerView krakenCryptos_RV;
    private AdaptadorRecyclerViewBalance kraken_adaptadorCriptomonedasRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_balance);

        exeThreadKrakenCryptos();
        waitSomeSecs();
        initToolbar();
        initControls();
        initListener();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.mostrar_balance_main_toolbar);
        setSupportActionBar(toolbar);
    }

    private void initControls(){
        if (buscadorSV == null) {
            buscadorSV = (SearchView) findViewById(R.id.mostrarBalance_SV_id);
            buscadorSV.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        cantidad_tv = (TextView) findViewById(R.id.mostrarBalance_cantidad_types_cryptos_TV_id);
        error_carga_tv = (TextView) findViewById(R.id.mostrarBalance_no_cryptos_TV_id);//usar en el adapter
        krakenCryptos_RV = (RecyclerView) findViewById(R.id.mostrarBalance_krakenCryptos_RV_id);
    }

    private void exeThreadKrakenCryptos(){
        KrakenAPIcode api = new KrakenAPIcode();

        String key = "1RjDI4V5G0I4sOe80GkqLXN05Y7g2cMv+nBAVPGN557tm0Hg38Znp8hu";
        String secret = "RQvR9u6sX4riXcI0LBT4GwV69TwSGxB/UGO6p/W+aZzKvj7D9s23uoOEjrBP19w5gTx+df7t2v+T5UET6DGbwA==";

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    obtenerInfo(api.petBalance(key, secret));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
    private void obtenerInfo(String response){
        try {
            JSONObject JOResponse = new JSONObject(response);
            JSONArray JAError = JOResponse.getJSONArray("error");
            JSONObject JOResult = JOResponse.getJSONObject("result");

            Iterator<String> keys = JOResult.keys();

            while (keys.hasNext())
            {
                // Get the key
                String key = keys.next();

                // Get the value
                String value = JOResult.get(key).toString();
                if(Double.parseDouble(value) >= MIN_AMMOUNT){
                    cryptosAL.add(new Criptomoneda(key, value));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            cryptosAL.clear();
        }
    }

    //Inicializar el listener del searchView
    private void initListener() {
        buscadorSV.setOnQueryTextListener(this);
    }

    /**
     * Se implementara en un futuro.
     * {@link #onQueryTextSubmit(String)}
     * {@link #onQueryTextChange(String)}
     */
    //Se ejecuta cuando pulsamos en el boton buscar del teclado, lo usaremos para hacer busquedas de nombres de establecimientos, pero el metodo es requerido por el implement
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    //Se ejecuta cuando escribimos una letra en el searchview //FALTA
    @Override
    public boolean onQueryTextChange(String newText) {
        if (buscadorSV.getQueryHint().toString() == getResources().getString(R.string.hint_searchview_crypto_type)) {
            kraken_adaptadorCriptomonedasRV.filtrado(newText);
        } else {
            //adaptadorRV.filtrado(newText, 1);
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mostrar_balance_menu, menu);
        return true;
    }

    /**
     * Damos funcionalidad a las opciones del toolbar.
     * {@link #onOptionsItemSelected(MenuItem)}
     */
    //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.close_I_id) {
            finish();//cerramos la activity
        }
        return true;
    }

    private void checkDataCryptosAL(){
        if(cryptosAL.isEmpty()){
            cantidad_tv.setVisibility(View.GONE);
            error_carga_tv.setVisibility(View.VISIBLE);
        }
        else{
            cantidad_tv.setVisibility(View.VISIBLE);
            error_carga_tv.setVisibility(View.GONE);
        }
    }

    private void initAdapterKrakenCryptos() {
        checkDataCryptosAL();
        krakenCryptos_RV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        krakenCryptos_RV.setNestedScrollingEnabled(true);

        cantidad_tv.setText(getResources().getString(R.string.cantidad1_tv)+""+cryptosAL.size()+getResources().getString(R.string.cantidad2_cryptos_tv));

        kraken_adaptadorCriptomonedasRV = new AdaptadorRecyclerViewBalance(cryptosAL);
        krakenCryptos_RV.setAdapter(kraken_adaptadorCriptomonedasRV);
    }

    private void waitSomeSecs(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initAdapterKrakenCryptos();
            }
        }, 2000);
    }
}