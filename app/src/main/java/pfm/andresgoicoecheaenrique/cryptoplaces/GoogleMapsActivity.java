package pfm.andresgoicoecheaenrique.cryptoplaces;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import pfm.andresgoicoecheaenrique.cryptoplaces.Kraken.AdaptadorRecyclerViewAPIs;
import pfm.andresgoicoecheaenrique.cryptoplaces.Kraken.AdaptadorRecyclerViewBalance;
import pfm.andresgoicoecheaenrique.cryptoplaces.Kraken.Criptomoneda;
import pfm.andresgoicoecheaenrique.cryptoplaces.Kraken.ExchangeAPI;


public class GoogleMapsActivity extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        SearchView.OnQueryTextListener {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean permissionDenied = false;


    private GoogleMap map;

    //mi code
    private SharedPreferences sharedPrefs;
    private static final String mypreference = "CryptoPlaces";
    private String correoUsuario;
    private String contraseñaUsuario;

    private ActionMenuItemView cambiar_mapa = null;
    private Button btn_mapa = null;
    private Button btn_cerca = null;
    private Button btn_favs = null;
    private Button btn_kraken = null;
    private Button btn_all = null;
    // MIRAR DONDE PONER
    private TextView distancia = null;
    private TextView duracion = null;
    // MAPA DE GOOGLE
    private LinearLayout map_LL;
    private LinearLayout search_and_num_id_LL;
    private LinearLayout kraken_LL;
    //Floating btn
    private FloatingActionButton AddKrakenAPI_FAB = null;
    // BUSCADOR
    private SearchView buscadorSV;
    private TextView cantidad_tv;
    //RecyclerView parainfo de los BTNs menu
    private RecyclerView cercaVenues_RV;
    private RecyclerView favsVenues_RV;
    private RecyclerView allVenues_RV;
    //KRAKEN TV
    private TextView sin_APIs_tv = null;
    //KRAKEN RVs
    private RecyclerView krakenAPIs_RV;
    private RecyclerView krakenCryptos_RV;
    private JSONObject jsonObjectCryptos;
    private ArrayList<Criptomoneda> cryptosAL = new ArrayList<Criptomoneda>();
    private ArrayList<ExchangeAPI> apisAL = new ArrayList<ExchangeAPI>();

    //Adaptadores RV
    private AdaptadorRecyclerViewVenue cerca_adaptadorVenuesRV;
    private AdaptadorRecyclerViewVenue favs_adaptadorVenuesRV;
    private AdaptadorRecyclerViewVenue all_adaptadorVenuesRV;

    private AdaptadorRecyclerViewAPIs kraken_adaptadorAPIsRV;
    private AdaptadorRecyclerViewBalance kraken_adaptadorCriptomonedasRV;

    //AL para la info de los RV
    private ArrayList<Venue> venuesAL = new ArrayList<Venue>();// mapa y all
    private ArrayList<Venue> nearRadiusAL = new ArrayList<Venue>();
    //----------------------------------------- HACER CARGA DESDE LOGIN EN EL ONCREATE DE ESTA ACTIVIDAD
    private ArrayList<Venue> favsVenuesAL = new ArrayList<Venue>();
    //FIREBASE DB
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference DocRef;
    private Venue venueDFFB;

    //Para las Peticiones API
    private RequestQueue mQueue;
    private JSONObject jsonObjectVenueDetails;
    // Coordenadas
    private LatLng coordenadasOrigen;
    private LatLng coordenadasDestino;
    //Controlar los btns del menu
    private Boolean[] view_btn_selected = {true, false, false, false, false};
    private short btn_selected = 0;
    private Button[] botones;
    //Dialog Fragment Venue Info
    private DialogFragmentVenueInfo dialogFragmentVenueInfo = new DialogFragmentVenueInfo();
    private DialogFragmentNewExchangeAPI dialogFragmentNewAPI = new DialogFragmentNewExchangeAPI();
    //Cambiar tipo mapa
    private final short[] tiposMapas = {GoogleMap.MAP_TYPE_NORMAL, GoogleMap.MAP_TYPE_SATELLITE, GoogleMap.MAP_TYPE_HYBRID, GoogleMap.MAP_TYPE_TERRAIN};
    private int[] msjToastMapType = {R.string.map_type_1, R.string.map_type_2, R.string.map_type_3, R.string.map_type_4};


    private LocationManager locationManager;
    private static final String[] todasTiposEstablecimientos = {"atm", "cafe", "grocery", "default", "shopping", "lodging", "nightlife", "attraction", "food", "transport", "sports", "trezor retailer", "Travel Agency", "ATM", "Grocery", "Educational Business", "services", "retail", "Categoria: padrão", "Category: default"};
    private final float[] iconosColores = {BitmapDescriptorFactory.HUE_AZURE, BitmapDescriptorFactory.HUE_BLUE, BitmapDescriptorFactory.HUE_CYAN, BitmapDescriptorFactory.HUE_GREEN, BitmapDescriptorFactory.HUE_MAGENTA, BitmapDescriptorFactory.HUE_ORANGE, BitmapDescriptorFactory.HUE_RED, BitmapDescriptorFactory.HUE_ROSE, BitmapDescriptorFactory.HUE_VIOLET, BitmapDescriptorFactory.HUE_YELLOW, BitmapDescriptorFactory.HUE_AZURE, BitmapDescriptorFactory.HUE_BLUE, BitmapDescriptorFactory.HUE_CYAN, BitmapDescriptorFactory.HUE_GREEN, BitmapDescriptorFactory.HUE_MAGENTA, BitmapDescriptorFactory.HUE_ORANGE, BitmapDescriptorFactory.HUE_RED};

    // No usar o poner la de todos los venues
    private final String urlCoinmap200Establecimientos = "https://coinmap.org/api/v1/venues/?lat1=27.045583057045775&lat2=44.136101471190756&lon1=-19.78248272986727&lon2=6.318913375760843&limit=100"; //200

    private int[] colors = new int[]{R.color.tipo0, R.color.tipo1, R.color.tipo2, R.color.tipo3, R.color.tipo4};

    private static final String[] routeModes = {"transit", "driving", "walking", "cycling"};

    //PINTAR RUTA
    private Polyline rutaActualPolyline;
    private PolylineOptions rutaActualpolyOptions;//
    private ArrayList<LatLng> rutaActualAL = new ArrayList<LatLng>();

    private ClusterManager<MarkerClusterItem> clusterManager;//

    private Geocoder gcd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sharedPrefs = getSharedPreferences(mypreference, MODE_PRIVATE);
        correoUsuario = sharedPrefs.getString("email", null);
        contraseñaUsuario = sharedPrefs.getString("passw", null);
        //Doc ref DB Firestore
        DocRef = db.document("users/" + correoUsuario);

        gcd = new Geocoder(this, Locale.getDefault());

        //Inits
        initToolbar();
        initControls();
        initButtons();
        //initAdapters();
        initListener();
        //requestWithSomeHttpHeaders();
    }


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
        System.out.println("PAIS: " + countryName);
        return countryName;
    }

    private void initControls() {
        if (buscadorSV == null) {
            buscadorSV = (SearchView) findViewById(R.id.searchView_id);
            buscadorSV.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        if (distancia == null) {
            distancia = (TextView) findViewById(R.id.textViewDistancia);
        }
        if (duracion == null) {
            duracion = (TextView) findViewById(R.id.textView2Duracion);
        }
        if (btn_mapa == null) {
            btn_mapa = (Button) findViewById(R.id.btn_mapa);
            cambiarFondoBTNMenu(btn_mapa, Color.RED, Color.BLUE);
        }
        if (btn_cerca == null) {
            btn_cerca = (Button) findViewById(R.id.btn_cerca);
        }
        if (btn_favs == null) {
            btn_favs = (Button) findViewById(R.id.btn_favs);
        }
        if (btn_kraken == null) {
            btn_kraken = (Button) findViewById(R.id.btn_kraken);
        }
        if (btn_all == null) {
            btn_all = (Button) findViewById(R.id.btn_all);
        }
        if (cambiar_mapa == null) {
            cambiar_mapa = (ActionMenuItemView) findViewById(R.id.cambiar_mapa);
        }
        if (sin_APIs_tv == null) {
            sin_APIs_tv = (TextView) findViewById(R.id.sin_APIs_tv);
        }
        if (AddKrakenAPI_FAB == null) {
            AddKrakenAPI_FAB = (FloatingActionButton) findViewById(R.id.floatingBTNAddKrakenAPI_id);
        }

        map_LL = (LinearLayout) findViewById(R.id.linearLayoutGoogleMap);
        search_and_num_id_LL = (LinearLayout) findViewById(R.id.search_and_num_id_LL);
        cantidad_tv = (TextView) findViewById(R.id.cantidad_tv);

        cercaVenues_RV = (RecyclerView) findViewById(R.id.cercaVenuesRV_id);
        favsVenues_RV = (RecyclerView) findViewById(R.id.favsVenuesRV_id);
        allVenues_RV = (RecyclerView) findViewById(R.id.allVenuesRV_id);

        kraken_LL = (LinearLayout) findViewById(R.id.kraken_id_LL);
        krakenAPIs_RV = (RecyclerView) findViewById(R.id.krakenAPIsRV_id);
        //krakenCryptos_RV = (RecyclerView) findViewById(R.id.krakenCryptosRV_id);

        botones = new Button[]{btn_mapa, btn_cerca, btn_favs, btn_kraken, btn_all};
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        AddKrakenAPI_FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFragmentNewAPI.show(getSupportFragmentManager(), "GoogleMapsDialogFragmentNewExchangeAPI");
            }
        });
    }

    private void initButtons() {
        btn_mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comprobarBTN((short) 0);
            }
        });
        btn_cerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SERVICIO
                if (!isLocationEnabled()) {
                    CommonUtils.crearAlert((short) 1, getApplicationContext());
                }
                //Tarda unos segundos en mostrar la ubicacion actual
                else if (coordenadasOrigen == null) {
                    if (map.getMyLocation() != null) {
                        coordenadasOrigen = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
                    }
                    mostrarToast("Espere a obtener la ubicacion actual");
                } else {
                    comprobarBTN((short) 1);
                }
            }
        });
        btn_favs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comprobarBTN((short) 2);
            }
        });
        btn_kraken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comprobarBTN((short) 3);
            }
        });
        btn_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comprobarBTN((short) 4);
            }
        });
    }

    private void comprobarBTN(short btn) {
        if (!view_btn_selected[btn]) {
            initRV(btn);
            view_btn_selected[btn_selected] = false;
            cambiarFondoBTNMenu(botones[btn_selected], ContextCompat.getColor(getApplicationContext(), R.color.ligthBlueAccent), Color.BLACK);
            btn_selected = btn;
            view_btn_selected[btn] = true;
            cambiarFondoBTNMenu(botones[btn_selected], Color.RED, Color.BLUE);
            //BTN toolbar cambiar tipo de mapa
            if (btn == 0) {
                cambiar_mapa.setVisibility(View.VISIBLE);
            } else if (cambiar_mapa.getVisibility() == View.VISIBLE) {// Para llamar solo cuando (View.VISIBLE);
                cambiar_mapa.setVisibility(View.GONE);
            }
        }
    }

    private void cambiarFondoBTNMenu(Button btn, int bg_color, int text_color) {
        Drawable buttonDrawable = btn.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        DrawableCompat.setTint(buttonDrawable, bg_color);
        btn.setBackground(buttonDrawable);
        btn.setTextColor(text_color);
    }

    private void initRV(short RVcode) {
        //Inicialmente
        /*map_LL.setVisibility(View.VISIBLE);
        cercaVenues_RV.setVisibility(View.GONE);
        favsVenues_RV.setVisibility(View.GONE);
        krakenVenues_RV.setVisibility(View.GONE);
        buscarVenues_RV.setVisibility(View.GONE);*/
        if (RVcode == 0) {//mapa
            //gestionarVisibilityMenuBTN();
            search_and_num_id_LL.setVisibility(View.GONE);
            map_LL.setVisibility(View.VISIBLE);
            cercaVenues_RV.setVisibility(View.GONE);
            favsVenues_RV.setVisibility(View.GONE);
            kraken_LL.setVisibility(View.GONE);
            allVenues_RV.setVisibility(View.GONE);
            AddKrakenAPI_FAB.setVisibility(View.GONE);
        } else if (RVcode == 1) {//cerca
            //NO LLAMAR MUCHAS VECES A LA API DE COINMAP
            if (nearRadiusAL.size() > 0) {
                initAdapterNear();
                mostrarToast("NO PET API");
            } else {
                crearBoxAL();
                mostrarToast("PET API COINMAP");
            }
            search_and_num_id_LL.setVisibility(View.VISIBLE);
            map_LL.setVisibility(View.GONE);
            cercaVenues_RV.setVisibility(View.VISIBLE);
            favsVenues_RV.setVisibility(View.GONE);
            kraken_LL.setVisibility(View.GONE);
            allVenues_RV.setVisibility(View.GONE);
            AddKrakenAPI_FAB.setVisibility(View.GONE);
        } else if (RVcode == 2) {//favs
            //initAdapters();
            leerFavs();
            /*initAdapterFavs();
            map_LL.setVisibility(View.GONE);
            cercaVenues_RV.setVisibility(View.GONE);
            favsVenues_RV.setVisibility(View.VISIBLE);
            kraken_LL.setVisibility(View.GONE);
            buscarVenues_RV.setVisibility(View.GONE);*/
        } else if (RVcode == 3) {//kraken
            //initAdapters();
            //init
            String key = "";
            String secret = "";
            apisAL.add(new ExchangeAPI("test", key, secret));
            apisAL.add(new ExchangeAPI("test2", key, secret));
            initAdapterKrakenAPIs();
            //initAdapterKrakenCryptos();

            search_and_num_id_LL.setVisibility(View.VISIBLE);
            map_LL.setVisibility(View.GONE);
            cercaVenues_RV.setVisibility(View.GONE);
            favsVenues_RV.setVisibility(View.GONE);
            allVenues_RV.setVisibility(View.GONE);
            kraken_LL.setVisibility(View.VISIBLE);
            //krakenAPIs_RV
            //krakenCryptos_RV.setVisibility(View.VISIBLE);
            krakenAPIs_RV.setVisibility(View.VISIBLE);
            AddKrakenAPI_FAB.setVisibility(View.VISIBLE);
        } else {// RVcode == 4 todos
            //initAdapters();
            initAdapterAll();
            search_and_num_id_LL.setVisibility(View.VISIBLE);
            map_LL.setVisibility(View.GONE);
            cercaVenues_RV.setVisibility(View.GONE);
            favsVenues_RV.setVisibility(View.GONE);
            kraken_LL.setVisibility(View.GONE);
            allVenues_RV.setVisibility(View.VISIBLE);
            AddKrakenAPI_FAB.setVisibility(View.GONE);
        }
    }

    private void gestionarVisibilityMenuBTN(short btn) {

    }

    private void initAdapterNear() {
        cercaVenues_RV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        cercaVenues_RV.setNestedScrollingEnabled(true);

        cantidad_tv.setText(getResources().getString(R.string.cantidad1_tv) + "" + nearRadiusAL.size() + getResources().getString(R.string.cantidad2_tv));

        cerca_adaptadorVenuesRV = new AdaptadorRecyclerViewVenue(nearRadiusAL, this, getSupportFragmentManager());
        cercaVenues_RV.setAdapter(cerca_adaptadorVenuesRV);
    }

    private void initAdapterFavs() {
        favsVenues_RV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        favsVenues_RV.setNestedScrollingEnabled(true);

        cantidad_tv.setText(getResources().getString(R.string.cantidad1_tv) + "" + favsVenuesAL.size() + getResources().getString(R.string.cantidad2_tv));

        favs_adaptadorVenuesRV = new AdaptadorRecyclerViewVenue(favsVenuesAL, this, getSupportFragmentManager());
        favsVenues_RV.setAdapter(favs_adaptadorVenuesRV);
    }

    private void initAdapterAll() {
        allVenues_RV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        allVenues_RV.setNestedScrollingEnabled(true);

        cantidad_tv.setText(getResources().getString(R.string.cantidad1_tv) + "" + venuesAL.size() + getResources().getString(R.string.cantidad2_tv));

        all_adaptadorVenuesRV = new AdaptadorRecyclerViewVenue(venuesAL, this, getSupportFragmentManager());//allVenuesAL
        allVenues_RV.setAdapter(all_adaptadorVenuesRV);
    }

    private void initAdapterKrakenAPIs() {//krakenapis
        krakenAPIs_RV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        krakenAPIs_RV.setNestedScrollingEnabled(true);

        cantidad_tv.setText(getResources().getString(R.string.cantidad1_tv) + apisAL.size() + getResources().getString(R.string.cantidad3_apis_tv));

        kraken_adaptadorAPIsRV = new AdaptadorRecyclerViewAPIs(apisAL, cantidad_tv, this);
        krakenAPIs_RV.setAdapter(kraken_adaptadorAPIsRV);
    }

    private void initAdapterKrakenCryptos() {
        krakenCryptos_RV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        krakenCryptos_RV.setNestedScrollingEnabled(true);

        cantidad_tv.setText(getResources().getString(R.string.cantidad1_tv) + "" + cryptosAL.size() + getResources().getString(R.string.cantidad2_cryptos_tv));

        kraken_adaptadorCriptomonedasRV = new AdaptadorRecyclerViewBalance(cryptosAL);
        krakenCryptos_RV.setAdapter(kraken_adaptadorCriptomonedasRV);
    }

    private void initAdapters() {
        cercaVenues_RV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        cercaVenues_RV.setNestedScrollingEnabled(true);

        cerca_adaptadorVenuesRV = new AdaptadorRecyclerViewVenue(nearRadiusAL, this, getSupportFragmentManager());
        cercaVenues_RV.setAdapter(cerca_adaptadorVenuesRV);
        // favs
        favsVenues_RV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        favsVenues_RV.setNestedScrollingEnabled(true);

        favs_adaptadorVenuesRV = new AdaptadorRecyclerViewVenue(venuesAL, this, getSupportFragmentManager());
        favsVenues_RV.setAdapter(favs_adaptadorVenuesRV);
    }

    private void crearBoxAL() {
        //https://coinmap.org/api/v1/venues/?lat1=27.045583057045775&lat2=44.136101471190756&lon1=-19.78248272986727&lon2=6.318913375760843&limit=100
        coordenadasOrigen = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());

        //radio ecuatorial
        double earthEquatorialRadiusKM = 6378.137; //Radio ecuatorial de la Tierra en KM
        double distancia = 2857.1428;// = 100 KM (1000.0 = 70 km)
        //double offset = 100.0 / 1000.0;
        double offset = distancia / earthEquatorialRadiusKM;
        double latMax = coordenadasOrigen.latitude + offset;
        double latMin = coordenadasOrigen.latitude - offset;

        //double lngOffset = offset * Math.cos(coordenadasOrigen.latitude * Math.PI / 180.0);
        double lngOffset = distancia / (earthEquatorialRadiusKM * Math.cos(coordenadasOrigen.latitude * Math.PI / 180.0));
        double lngMax = coordenadasOrigen.longitude + lngOffset;
        double lngMin = coordenadasOrigen.longitude - lngOffset;

        LatLng lugar1 = new LatLng(latMax, lngMax);
        LatLng lugar2 = new LatLng(latMin, lngMin);
        Long idTag = new Long(33);
        map.addMarker(new MarkerOptions()
                        .position(lugar1)
                        .title("MAX")
                )
                .setTag(idTag);
        map.addMarker(new MarkerOptions()
                        .position(lugar2)
                        .title("min")
                )
                .setTag(idTag);
        jsonParse("https://coinmap.org/api/v1/venues/?lat1=" + latMin + "&lat2=" + latMax + "&lon1=" + lngMin + "&lon2=" + lngMax + "&limit=100", 3);

        //System.out.println("xxxxxxxx, https://coinmap.org/api/v1/venues/?lat1="+latMin+"&lat2="+latMax+"&lon1="+lngMin+"&lon2="+lngMax+"&limit=100");
    }

    private void createAL100km(ArrayList<Venue> nearBoxAL) {
        coordenadasOrigen = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());

        nearRadiusAL = nearBoxAL.stream()
                .filter(l -> haversine(l.getLat(), l.getLon(),
                        coordenadasOrigen.latitude, coordenadasOrigen.longitude) <= 10)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private double haversine(double lat1, double lng1, double lat2, double lng2) {
        int r = 6371; // average radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = r * c;
        return d;
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.google_maps_main_toolbar);
        setSupportActionBar(toolbar);
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
        if (buscadorSV.getQueryHint().toString() == getResources().getString(R.string.hint_searchview_name)) {
            if (cercaVenues_RV.getVisibility() == View.VISIBLE) {
                cerca_adaptadorVenuesRV.filtrado(newText);
            } else if (favsVenues_RV.getVisibility() == View.VISIBLE) {
                favs_adaptadorVenuesRV.filtrado(newText);
            } else if (allVenues_RV.getVisibility() == View.VISIBLE) {
                all_adaptadorVenuesRV.filtrado(newText);
            }
        } else {
            //adaptadorRV.filtrado(newText, 1);
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.google_maps_menu, menu);
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

        if (id == R.id.about_us) {
            CommonUtils.crearAlert((short) 0, this);
        } else if (id == R.id.exit) {
            mostrarToast(getResources().getString(R.string.exit_app));
            finish();
        } else if (id == R.id.search) {
            //BORRAR SERA CAMBIAR MAPA VISUALIZADO

            //Otra actividad o mostrar un menu para mostrar ruta//
            mostrarToast("RUTA");

            // Comprobar localizacion
            if (map.getMyLocation() == null) {
                mostrarToast(getResources().getString(R.string.toast_error_coordenadas_origen_null));
            } else if (coordenadasDestino == null) {
                mostrarToast(getResources().getString(R.string.toast_error_coordenadas_destino_null));
            } else {
                coordenadasOrigen = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
                mostrarToast("inicio: " + coordenadasOrigen + " destino: " + coordenadasDestino);
                Log.d("coor", "inicio: " + coordenadasOrigen + " destino: " + coordenadasDestino);
                //peticionGoogle(coordenadasOrigen, coordenadasDestino);
            }
        } else if (id == R.id.refresh) {
            //Actualizar tiendas en el mapa [Quitar] Check internet
            jsonParse(urlCoinmap200Establecimientos, 0);
        } else if (id == R.id.map_type_1) {
            cambiarTipoMapa((short) 0);
        } else if (id == R.id.map_type_2) {
            cambiarTipoMapa((short) 1);
        } else if (id == R.id.map_type_3) {
            cambiarTipoMapa((short) 2);
        } else if (id == R.id.map_type_4) {
            cambiarTipoMapa((short) 3);
        }
        return true;
    }

    private void cambiarTipoMapa(short map_type) {
        if (map.getMapType() != tiposMapas[map_type]) {
            mostrarToast(getResources().getString(msjToastMapType[map_type]));
            map.setMapType(tiposMapas[map_type]);
        }
    }

    // Abrir frqagmento para ver venue info
    private void crearDialogFragmentVenueInfo(Long id) {
        String url = "https://coinmap.org/api/v1/venues/" + id;
        jsonParse(url, 2);
    }

    // Mostrar un toast corto
    private void mostrarToast(String msj) {
        Toast.makeText(this, msj, Toast.LENGTH_SHORT).show();
    }

    // Comprobar si la localizacion esta activada y salta una alerta para activarlo
    private void checkLocation() {
        if (!isLocationEnabled())
            CommonUtils.crearAlert((short) 1, this);
    }

    // Comprobar si el GPS esta activado
    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    //Limpiar mapa y AL de marcadores
    private void cleanMapData() {
        if (venuesAL.size() != 0) {
            map.clear();
            venuesAL.clear();
        }
    }


    // Peticion para obtener 200 establecimientos en una zona delimitada (Toda España y parte del norte de Africa)
    private void jsonParse(String url, Integer code) {
        if (!isInternetEnabled()) {// No internet
            CommonUtils.crearAlert((short) 2, this);
        } else {// Si internet
            mQueue = Volley.newRequestQueue(this);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (code == 0) {// Peticion 100 establecimientos API coinmap
                                cleanMapData();
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
                                        //NO PONER COSAS AQUI!!!!!!!!!!!!!!!
                                    }
                                    /*favsVenuesAL.add(venuesAL.get(1));
                                    favsVenuesAL.add(venuesAL.get(1));
                                    saveFavs();*/
                                    addMarkersAL(venuesAL);// VA MEJOR AQUI xD
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if (code == 1) {// Peticion API Google
                                PolylineOptions polyOptions = new PolylineOptions();
                                try {
                                    String statusPET = response.getString("status");

                                    if (statusPET.equals("ZERO_RESULTS")) {
                                        Pattern p = Pattern.compile("mode=(.*?)&key");
                                        Matcher m = p.matcher(url);
                                        m.find();//Sino no busca
                                        mostrarToast("No hay rutas para el modo: " + m.group(1));
                                    } else {
                                        //inicio
                                        mostrarToast(getResources().getString(R.string.toast_calculando_ruta));

                                        JSONObject jsonObjectRutas = response.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0);
                                        Log.d("arrayxd", "" + jsonObjectRutas);
                                        Log.d("arrayxd", "" + jsonObjectRutas.getJSONObject("distance").getString("text"));
                                        Log.d("arrayxd", "" + jsonObjectRutas.getJSONArray("steps"));
                                        // mostrar datos
                                        String distanciaJSON = jsonObjectRutas.getJSONObject("distance").getString("text");
                                        String duracionJSON = jsonObjectRutas.getJSONObject("duration").getString("text");
                                        distancia.setText(getResources().getString(R.string.distancia_tv) + distanciaJSON);
                                        duracion.setText(getResources().getString(R.string.duracion_tv) + duracionJSON);
                                        mostrarToast("Distancia: " + distanciaJSON);

                                        //pintarRuta(jsonObjectRutas.getJSONArray("steps"));

                                        JSONArray jsonArraySteps = jsonObjectRutas.getJSONArray("steps");

                                        JSONObject iniLatLon;
                                        JSONObject finLatLon;
                                        rutaActualAL.clear();
                                        for (int i = 0; i < jsonArraySteps.length(); i++) {
                                            iniLatLon = jsonArraySteps.getJSONObject(i).getJSONObject("start_location");
                                            finLatLon = jsonArraySteps.getJSONObject(i).getJSONObject("end_location");

                                            rutaActualAL.add(new LatLng(iniLatLon.getDouble("lat"), iniLatLon.getDouble("lng")));
                                            rutaActualAL.add(new LatLng(finLatLon.getDouble("lat"), finLatLon.getDouble("lng")));
                                        }


                                        //Borrar ultima ruta
                                        if (rutaActualPolyline != null) {
                                            rutaActualPolyline.remove();
                                        }

                                        rutaActualpolyOptions = new PolylineOptions();
                                        polyOptions.color(colors[0]);
                                        polyOptions.width(5);
                                        polyOptions.addAll(rutaActualAL);
                                        rutaActualPolyline = map.addPolyline(polyOptions);
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            //NEAR BOXAL
                            else if (code == 3) {// Peticion 100 establecimientos API coinmap
                                try {
                                    JSONArray jsonArray = response.getJSONArray("venues");
                                    ArrayList<Venue> nearBoxAL = new ArrayList<Venue>();

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject venue = jsonArray.getJSONObject(i);

                                        nearBoxAL.add(new Venue(
                                                venue.getLong("id"),
                                                venue.getDouble("lat"),
                                                venue.getDouble("lon"),
                                                venue.getString("category"),
                                                venue.getString("name"),
                                                venue.getLong("created_on"),
                                                venue.getString("geolocation_degrees")
                                        ));
                                    }
                                    createAL100km(nearBoxAL);
                                    initAdapterNear();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            // Peticion API Coinmap venue info
                            else {
                                try {//pasarlo al coommonutils
                                    jsonObjectVenueDetails = response.getJSONObject("venue");

                                    // Guardar todos los datos, pero no da tiempo
                                    venueDFFB = convertDataForVenue(
                                            jsonObjectVenueDetails.getString("id"),
                                            jsonObjectVenueDetails.getString("lat"),
                                            jsonObjectVenueDetails.getString("lon"),
                                            jsonObjectVenueDetails.getString("category"),
                                            jsonObjectVenueDetails.getString("name"),
                                            jsonObjectVenueDetails.getString("created_on"),
                                            jsonObjectVenueDetails.getString("geolocation_degrees")
                                    );
                                    coordenadasDestino = new LatLng(venueDFFB.getLat(), venueDFFB.getLon());

                                    //DialogFragmentVenueInfo dialogFragmentVenueInfo = new DialogFragmentVenueInfo();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("name", jsonObjectVenueDetails.getString("name"));
                                    bundle.putString("category", jsonObjectVenueDetails.getString("category"));
                                    bundle.putString("country", countryBasedOncoordenates(venueDFFB.getLat(), venueDFFB.getLon()));
                                    bundle.putString("created_on", jsonObjectVenueDetails.getString("created_on"));
                                    bundle.putString("updated_on", jsonObjectVenueDetails.getString("updated_on"));
                                    bundle.putString("phone", jsonObjectVenueDetails.getString("phone"));
                                    bundle.putString("share", "https://www.google.com/maps/search/" + venueDFFB.getLat() + "," + venueDFFB.getLon());
                                    bundle.putString("favChecked", String.valueOf(isVenueInFavsAL()));

                                    System.out.println(" " + venueDFFB.getLat());
                                    System.out.println(" " + venueDFFB.getLon());

                                    dialogFragmentVenueInfo.setArguments(bundle);
                                    dialogFragmentVenueInfo.show(getSupportFragmentManager(), "GoogleMapsDialogFragmentVenueInfo");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (code == 0) {// Peticion 200 establecimientos API coinmap
                        mostrarToast(getResources().getString(R.string.toast_error_peticion_coinmap) + error);
                    } else if (code == 1) {// Peticion API Google
                        mostrarToast(getResources().getString(R.string.toast_error_peticion_google) + error);
                    } else { // Peticion API Coinmap venue info
                        mostrarToast(getResources().getString(R.string.toast_error_peticion_coinmap) + error);
                    }
                    error.printStackTrace();
                }
            });

            mQueue.add(request);
        }
    }

    // Crear los 200 marcadores en el mapa
    private void addMarkersAL(ArrayList<Venue> listaVenues) {
        LatLng lugar;

        for (Venue v : listaVenues) {

            lugar = new LatLng(v.getLat(), v.getLon());
            map.addMarker(new MarkerOptions()
                            .position(lugar)
                            .title(v.getName())
                            .icon(asignarColor(v.getCategory()))
                            .snippet(getResources().getString(R.string.marker_snippet_text) + v.getCategory())
                    )
                    .setTag(v.getId());
        }
    }

    // Asignar un color a cada marcador dependiendo de su categoria
    private BitmapDescriptor asignarColor(String category) {
        Integer index;
        if (category.equals(Arrays.asList(todasTiposEstablecimientos).indexOf("ATM"))) {
            category = todasTiposEstablecimientos[0];
        } else if (category.equals(Arrays.asList(todasTiposEstablecimientos).indexOf("Grocery"))) {
            category = todasTiposEstablecimientos[2];
        } else if (category.equals(Arrays.asList(todasTiposEstablecimientos).indexOf("Category: default"))) {
            category = todasTiposEstablecimientos[3];
        }
        index = Arrays.asList(todasTiposEstablecimientos).indexOf(category);
        if (index == -1) {// Por si se añade una categoria nueva, que no hemos contemplado
            index = 17;
        }

        BitmapDescriptor bitmap = BitmapDescriptorFactory.defaultMarker(iconosColores[index]);
        return bitmap;

    }

    // Comprobar si hay conexion a internet
    private boolean isInternetEnabled() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    // Funcion para crear alertas y reducir codigo
    private void crearAlert(int code) {

        AlertDialog.Builder constructor = new AlertDialog.Builder(this);
        constructor.setCancelable(true);
        if (code == 0) {// Muestra la informacion de Acerca de
            constructor.setTitle(getResources().getString(R.string.title_alertdialog_aboutus) + getResources().getString(R.string.app_name))
                    .setMessage(getResources().getString(R.string.msg_alertdialog_aboutus))
                    .setPositiveButton(getResources().getString(R.string.btn_alertdialog_aboutus), null);
        } else if (code == 1) {// ACTIVAR LOCALIZACION
            constructor.setTitle(getResources().getString(R.string.title_alertdialog_activate_location))
                    .setMessage(getResources().getString(R.string.msg_alertdialog_activate_location))
                    .setPositiveButton(getResources().getString(R.string.btn_positive_alertdialog_activate_location), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.btn_negative_alertdialog_activate_location), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            mostrarToast(getResources().getString(R.string.toast_negative_alertdialog_activate_location));
                        }
                    });
        } else { // NO HAY INTERNET
            constructor.setTitle(getResources().getString(R.string.title_alertdialog3));
            constructor.setMessage(getResources().getString(R.string.msg_alertdialog3));
            constructor.setNeutralButton(getResources().getString(R.string.neutral_btn_alertdialog), null);
        }
        constructor.create().show();
    }

    // Peticion Volley a la API de Google, solo coche
    private void peticionGoogle(LatLng origin, LatLng dest) {
        String url = buildUrl(origin, dest);
        jsonParse(url, 1);
    }

    /**
     * Construimos la url para hacer la peticion a la API de Google y obtener la ruta del punto origin al dest
     *
     * @param origin
     * @param dest
     * @return
     */
    private String buildUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "&mode=walking";// transit, driving, walking, or cycling

        return "https://maps.googleapis.com/maps/api/directions/json?" + str_origin + "&" + str_dest + mode + "&key=" + getResources().getString(R.string.google_maps_key);
        //https://maps.googleapis.com/maps/api/directions/json?origin=40.4018501,-3.6751223&destination=28.4696927,-16.2865003&mode=transit&key=AIzaSyB1oi6o3-gE4kWwEFHCfBwdn2LesuMp0XI
    }


    /**
     * CODE FROM ANDROID STUDIO DOC PERMISSIONS MANAGEMENT
     *
     * @see <a href="https://github.com/googlemaps/android-samples/tree/master/ApiDemos/java/app/src/gms/java/com/example/mapdemo">Repo GitHub</a>
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);// Quitar apps de google
        map.setIndoorEnabled(false);// Quitar el interior de los edificios

        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        enableMyLocation();
        checkLocation();
        jsonParse("https://coinmap.org/api/v1/venues/?lat1=40.3018183&lat2=40.501818300000004&lon1=-3.7512897738859636&lon2=-3.5989862261140364&limit=100", 0);
        //https://coinmap.org/api/v1/venues/?lat1=39.4018336&lat2=41.4018336&lon1=-4.436671765782216&lon2=-2.9136366342177844&limit=100
        map.setOnMarkerClickListener(this);//Saber que marcador hemos seleccionado

        /*map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                // para el boton aunque seguramente quito
            }
        });*/

    }

    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Long venueId = (Long) marker.getTag();

        //Se puede quitar

        /*Integer index = -1;
        // Check if a click count was set, then display the click count.
        for (int i = 0; i < venuesAL.size(); i++) {
            if (venuesAL.get(i).getId() == venueId) {
                index = i;
                break;
            }
        }
        coordenadasDestino = new LatLng(venuesAL.get(index).getLat(), venuesAL.get(index).getLon());
        mostrarToast("Destino: " + coordenadasDestino);*/

        crearDialogFragmentVenueInfo(venueId);

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        // [START maps_check_location_permission]
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
        // [END maps_check_location_permission]
    }


    /**
     * Centrar camara sobre nuestra posicion actual, simepre y cuando tengamos la ubicacion activada
     * y acceso a internet
     *
     * @return false
     */
    @Override
    public boolean onMyLocationButtonClick() {
        if (isLocationEnabled() && isInternetEnabled()) {
            mostrarToast(getResources().getString(R.string.toast_btn_centrar_ubicacion));
        }

        checkLocation();
        if (!isInternetEnabled()) {
            CommonUtils.crearAlert((short) 2, this);
        }
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    /**
     * Cuando el usuario haga click sobre el punto azul que representa la ubicacion actual, mostramos informacion relacionada
     */
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    // [START maps_check_location_permission_result]
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // [START_EXCLUDE]
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
            // [END_EXCLUDE]
        }
    }
    // [END maps_check_location_permission_result]

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    /**
     * Clustering
     */

    private void setUpClusterer() {
        ClusterManager<MarkerClusterItem> clusterManager = new ClusterManager<>(getApplicationContext(), map);
        map.setOnCameraIdleListener(clusterManager);

        // Position the map.
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude()), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        //clusterManager = new ClusterManager<MarkerClusterItem>(getApplicationContext(), map);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        //map.setOnCameraIdleListener(clusterManager);
        //map.setOnMarkerClickListener(clusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {
        /*// Set some lat/lng coordinates to start with.
        double lat = map.getMyLocation().getLatitude();
        double lng = map.getMyLocation().getLongitude();

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 10; i++) {
            double offset = i / 60d;
            lat = lat + offset;
            lng = lng + offset;
            MarkerClusterItem offsetItem = new MarkerClusterItem(lat, lng, "Title " + i, "Snippet " + i);
            clusterManager.addItem(offsetItem);
        }*/
    }


    public void deleteApiBTN(View view) {

    }


    /**
     * ***** *****   CODE DIALOG FRAGMENT VENUE INFO   ***** *****
     */

    // ADD FAVORITOS
    public void ToogleBTNClick(View view) {
        ToggleButton tb = (ToggleButton) view.findViewById(R.id.toggleBTN_id);
        if (tb.isChecked()) {
            mostrarToast("chekeado");
            //ADD FAV
            favsVenuesAL.add(venueDFFB);
        } else {
            mostrarToast("no chekeado");
            // DELETE FAV
            favsVenuesAL.remove(venueDFFB);
        }
    }

    // GENERATE ROUTE TO VENUE
    public void routeBTN(View view) {
        //Y poner la vista del mapa
        if (!isLocationEnabled()) {
            CommonUtils.crearAlert((short) 1, this);
        } else {
            dialogFragmentVenueInfo.dismiss();
            coordenadasOrigen = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
            peticionGoogle(coordenadasOrigen, coordenadasDestino);
        }
    }

    private boolean isVenueInFavsAL() {
        Boolean resul = false;
        for (Venue v : favsVenuesAL) {
            if (v.getId() == venueDFFB.getId()) {
                resul = true;
                break;
            }
        }
        return resul;
    }

    private Venue convertDataForVenue(String s1, String s2, String s3, String s4, String s5, String s6, String s7) {
        return new Venue(Long.parseLong(s1), Double.parseDouble(s2), Double.parseDouble(s3), s4, s5, Long.parseLong(s6), s7);
    }

    /**
     * ***** *****   CODE FIREBASE   ***** ***** EN EL LOGIN
     */
    //
    private ArrayList<Venue> leerFavs() {
        // DocRef
        db.document("users/test@mail.es")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {//No es la primera vez o no ha fallado, crear de nuevo

                                ArrayList<?> listFavs = (ArrayList<?>) document.get("listFavs");
                                for (int i = 0; i < listFavs.size(); i++) {
                                    favsVenuesAL.add(convertDataForVenue(
                                            String.valueOf(((Map) listFavs.get(i)).get("id")),
                                            String.valueOf(((Map) listFavs.get(i)).get("lat")),
                                            String.valueOf(((Map) listFavs.get(i)).get("lon")),
                                            String.valueOf(((Map) listFavs.get(i)).get("category")),
                                            String.valueOf(((Map) listFavs.get(i)).get("name")),
                                            String.valueOf(((Map) listFavs.get(i)).get("createdOn")),
                                            String.valueOf(((Map) listFavs.get(i)).get("geolocation_degrees"))
                                    ));
                                    initAdapterFavs();
                                    search_and_num_id_LL.setVisibility(View.VISIBLE);
                                    map_LL.setVisibility(View.GONE);
                                    cercaVenues_RV.setVisibility(View.GONE);
                                    favsVenues_RV.setVisibility(View.VISIBLE);
                                    kraken_LL.setVisibility(View.GONE);
                                    allVenues_RV.setVisibility(View.GONE);
                                    AddKrakenAPI_FAB.setVisibility(View.GONE);
                                }
                            } else {
                                mostrarToast("No such document");
                            }
                        } else {
                            mostrarToast("Se produjo un error al leer de Firebase");
                        }
                    }
                });

        return favsVenuesAL;
    }

    //salvar datos
    private void saveFavs() {
        List<Venue> listofFavs = favsVenuesAL;
        Map<String, Object> listFavs = new HashMap<>();
        listFavs.put("listFavs", favsVenuesAL);

        //DocRef
        db.document("users/test@mail.es")
                .set(listFavs)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mostrarToast("Datos favs guardados correctamente");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mostrarToast("Error al guardar datos favs " + e);
                    }
                });


    }


    public void crearNewAPIBTN(View view) {
        /*EditText apiName = (EditText) view.findViewById(R.id.NombreAPI_ET_id);
        EditText key = (EditText) view.findViewById(R.id.keyAPI_ET_id);
        TextView tv1 = view.findViewById(R.id.tv1);
        tv1.setText(key.getText().toString());
        String str = "apiName.getText().toString()";
        Button boton_crear = view.findViewById(R.id.boton_crear_id);
        mostrarToast("xxx"+tv1.getText());*/
        ExchangeAPI newAPI = new ExchangeAPI("hola", "key.getText().toString()", "secret.getText().toString()");
        apisAL.add(newAPI);
        dialogFragmentNewAPI.dismiss();
        initAdapterKrakenAPIs();
    }
}