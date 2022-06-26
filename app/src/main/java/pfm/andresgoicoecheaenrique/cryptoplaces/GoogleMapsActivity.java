package pfm.andresgoicoecheaenrique.cryptoplaces;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
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
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class GoogleMapsActivity extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        SearchView.OnQueryTextListener{

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
    private TextView distancia = null;
    private TextView duracion = null;
    private SearchView buscadorSV;
    private LocationManager locationManager;
    private RequestQueue mQueue;
    private ArrayList<Venue> venuesAL = new ArrayList<Venue>();
    private LatLng coordenadasOrigen;
    private LatLng coordenadasDestino;
    private static final String[] todasTiposEstablecimientos = {"atm", "cafe", "grocery", "default", "shopping", "lodging", "nightlife", "attraction", "food", "transport", "sports", "trezor retailer", "Travel Agency", "ATM", "Grocery", "Educational Business", "services", "retail", "Categoria: padrão", "Category: default"};
    private final float[] iconosColores = {BitmapDescriptorFactory.HUE_AZURE, BitmapDescriptorFactory.HUE_BLUE, BitmapDescriptorFactory.HUE_CYAN, BitmapDescriptorFactory.HUE_GREEN, BitmapDescriptorFactory.HUE_MAGENTA, BitmapDescriptorFactory.HUE_ORANGE, BitmapDescriptorFactory.HUE_RED, BitmapDescriptorFactory.HUE_ROSE, BitmapDescriptorFactory.HUE_VIOLET, BitmapDescriptorFactory.HUE_YELLOW, BitmapDescriptorFactory.HUE_AZURE, BitmapDescriptorFactory.HUE_BLUE, BitmapDescriptorFactory.HUE_CYAN, BitmapDescriptorFactory.HUE_GREEN, BitmapDescriptorFactory.HUE_MAGENTA, BitmapDescriptorFactory.HUE_ORANGE, BitmapDescriptorFactory.HUE_RED};

    private final String urlCoinmap200Establecimientos = "https://coinmap.org/api/v1/venues/?lat1=27.045583057045775&lat2=44.136101471190756&lon1=-19.78248272986727&lon2=6.318913375760843&limit=100"; //200

    private int[] colors = new int[]{R.color.tipo0,R.color.tipo1,R.color.tipo2,R.color.tipo3,R.color.tipo4};

    private Polyline rutaActualPolyline;
    private PolylineOptions rutaActualpolyOptions;
    private ArrayList<LatLng> rutaActualAL = new ArrayList<LatLng>();

    private ClusterManager<MarkerClusterItem> clusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Inits
        initToolbar();
        initControls();
        initListener();
    }
    private void initControls(){
        if(buscadorSV == null){
            buscadorSV = (SearchView)findViewById(R.id.searchView);
            buscadorSV.setInputType(InputType.TYPE_CLASS_TEXT);
            buscadorSV.setVisibility(View.GONE);//Se añadira mas adelante
        }
        if(distancia == null){
            distancia = (TextView) findViewById(R.id.textViewDistancia);
        }
        if(duracion == null){
            duracion = (TextView) findViewById(R.id.textView2Duracion);
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }
    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.google_maps_main_toolbar);
        setSupportActionBar(toolbar);
    }
    //Inicializar el listener del searchView
    private void initListener(){
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
        if(buscadorSV.getQueryHint().toString() == getResources().getString(R.string.hint_searchview_name)){
            //adaptadorRV.filtrado(newText, 0);
        }
        else{
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

        if(id == R.id.about_us){
            crearAlert( 0);
        }
        else if(id == R.id.exit){
            mostrarToast(getResources().getString(R.string.exit_app));
            finish();
        }
        else if(id == R.id.buscar_ruta){
            //Otra actividad o mostrar un menu para mostrar ruta
            mostrarToast("RUTA");

            // Comprobar localizacion
            if(map.getMyLocation() == null){
                mostrarToast(getResources().getString(R.string.toast_error_coordenadas_origen_null));
            }
            else if(coordenadasDestino == null){
                mostrarToast(getResources().getString(R.string.toast_error_coordenadas_destino_null));
            }
            else{
                coordenadasOrigen = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
                mostrarToast("inicio: "+coordenadasOrigen+ " destino: "+coordenadasDestino);
                Log.d("coor", "inicio: "+coordenadasOrigen+ " destino: "+coordenadasDestino);
                peticionGoogle(coordenadasOrigen, coordenadasDestino);
            }
        }
        else if(id == R.id.refresh){
            //Actualizar tiendas en el mapa
            checkInternetConnection();
        }
        else if(id == R.id.search){
            //mejor añadir un search view o mostrar un searchview
            //activity//O quitar
            mostrarToast("SIN IMPLEMENTAR");
        }
        return true;
    }

    // Mostrar un toast corto
    private void mostrarToast(String msj){
        Toast.makeText(this, msj, Toast.LENGTH_SHORT).show();
    }
    // Comprobar si la localizacion esta activada y salta una alerta para activarlo
    private void checkLocation() {
        if (!isLocationEnabled())
            crearAlert(1);
    }
    // Comprobar si el GPS esta activado
    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    // Peticion para obtener 200 establecimientos en una zona delimitada (Toda España y parte del norte de Africa)
    private void jsonParse(String url, Integer code) {
        mQueue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(code == 0){// Peticion 100 establecimientos API coinmap
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

                                    addMarkersAL(venuesAL);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else{// Peticion API Google
                            PolylineOptions polyOptions = new PolylineOptions();
                            try {
                                //inicio
                                mostrarToast(getResources().getString(R.string.toast_calculando_ruta));

                                JSONObject jsonObjectRutas = response.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0);
                                Log.d("arrayxd", ""+jsonObjectRutas);
                                Log.d("arrayxd", ""+jsonObjectRutas.getJSONObject("distance").getString("text"));
                                Log.d("arrayxd", ""+jsonObjectRutas.getJSONArray("steps"));
                                // mostrar datos
                                String distanciaJSON = jsonObjectRutas.getJSONObject("distance").getString("text");
                                String duracionJSON = jsonObjectRutas.getJSONObject("duration").getString("text");
                                distancia.setText(getResources().getString(R.string.distancia_tv) + distanciaJSON);
                                duracion.setText(getResources().getString(R.string.duracion_tv) + duracionJSON);

                                //pintarRuta(jsonObjectRutas.getJSONArray("steps"));

                                JSONArray jsonArraySteps = jsonObjectRutas.getJSONArray("steps");

                                JSONObject iniLatLon;
                                JSONObject finLatLon;
                                rutaActualAL.clear();
                                for(int i = 0;i<jsonArraySteps.length();i++){
                                    iniLatLon = jsonArraySteps.getJSONObject(i).getJSONObject("start_location");
                                    finLatLon = jsonArraySteps.getJSONObject(i).getJSONObject("end_location");

                                    rutaActualAL.add(new LatLng(iniLatLon.getDouble("lat"), iniLatLon.getDouble("lng")));
                                    rutaActualAL.add(new LatLng(finLatLon.getDouble("lat"), finLatLon.getDouble("lng")));
                                }


                                //Borrar ultima ruta
                                if(rutaActualPolyline != null){
                                    rutaActualPolyline.remove();
                                }

                                rutaActualpolyOptions = new PolylineOptions();
                                polyOptions.color(colors[0]);
                                polyOptions.width(5);
                                polyOptions.addAll(rutaActualAL);
                                rutaActualPolyline = map.addPolyline(polyOptions);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(code == 0){// Peticion 200 establecimientos API coinmap
                    mostrarToast(getResources().getString(R.string.toast_error_peticion_coinmap) + error);
                }
                else{// Peticion API Google
                    mostrarToast(getResources().getString(R.string.toast_error_peticion_google) + error);
                }
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }
    // Crear los 200 marcadores en el mapa
    private void addMarkersAL(ArrayList<Venue> listaVenues){
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
    private BitmapDescriptor asignarColor(String category){
        Integer index;
        if(category.equals(Arrays.asList(todasTiposEstablecimientos).indexOf("ATM"))){
            category = todasTiposEstablecimientos[0];
        }
        else if(category.equals(Arrays.asList(todasTiposEstablecimientos).indexOf("Grocery"))){
            category = todasTiposEstablecimientos[2];
        }
        else if(category.equals(Arrays.asList(todasTiposEstablecimientos).indexOf("Category: default"))){
            category = todasTiposEstablecimientos[3];
        }
        index = Arrays.asList(todasTiposEstablecimientos).indexOf(category);
        if(index == -1){// Por si se añade una categoria nueva, que no hemos contemplado
            index = 17;
        }

        BitmapDescriptor bitmap = BitmapDescriptorFactory.defaultMarker(iconosColores[index]);
        return bitmap;

    }
    // comprobar si hay conexion a internet antes de hacer peticiones Volley
    private void checkInternetConnection(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            if(venuesAL.size() != 0){
                map.clear();
                venuesAL.clear();
            }
            jsonParse(urlCoinmap200Establecimientos, 0);
        }
        else{
            crearAlert( 2);
        }
    }
    // Funcion para crear alertas y reducir codigo
    private void crearAlert(int code) {

        AlertDialog.Builder constructor = new AlertDialog.Builder(this);
        constructor.setCancelable(true);
        if(code == 0){// Muestra la informacion de Acerca de
            constructor.setTitle(getResources().getString(R.string.title_alertdialog_aboutus) + getResources().getString(R.string.app_name))
                    .setMessage(getResources().getString(R.string.msg_alertdialog_aboutus))
                    .setPositiveButton(getResources().getString(R.string.btn_alertdialog_aboutus),null);
        }
        else if(code == 1){// ACTIVAR LOCALIZACION
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
        }
        else{ // NO HAY INTERNET
            constructor.setTitle(getResources().getString(R.string.title_alertdialog3));
            constructor.setMessage(getResources().getString(R.string.msg_alertdialog3));
            constructor.setNeutralButton(getResources().getString(R.string.neutral_btn_alertdialog), null);
        }
        constructor.create().show();
    }

    // Peticion Volley a la API de Google, solo coche
    private void peticionGoogle(LatLng origin, LatLng dest){
        String url = buildUrl(origin, dest);
        jsonParse(url, 1);
    }

    /**
     * Construimos la url para hacer la peticion a la API de Google y obtener la ruta del punto origin al dest
     * @param origin
     * @param dest
     * @return
     */
    private String buildUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "&mode=driving";// walking

        return "https://maps.googleapis.com/maps/api/directions/json?"+ str_origin +"&"+ str_dest + mode +"&key="+ getResources().getString(R.string.google_maps_key);
    }


    /**
     * CODE FROM ANDROID STUDIO DOC PERMISSIONS MANAGEMENT
     * @see <a href="https://github.com/googlemaps/android-samples/tree/master/ApiDemos/java/app/src/gms/java/com/example/mapdemo">Repo GitHub</a>
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);// Quitar apps de google

        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        enableMyLocation();
        checkInternetConnection();
        map.setOnMarkerClickListener(this);//Saber que marcador hemos seleccionado

        /*map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                // para el boton aunque seguramente quito
            }
        });*/

    }

    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Long venueId = (Long) marker.getTag();
        Integer index = -1;
        // Check if a click count was set, then display the click count.
        for(int i=0;i<venuesAL.size();i++){
            if(venuesAL.get(i).getId() == venueId){
                index = i;
                break;
            }
        }

        coordenadasDestino = new LatLng(venuesAL.get(index).getLat(), venuesAL.get(index).getLon());
        mostrarToast("Destino: "+ coordenadasDestino);

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
     *
     * @return false
     */
    @Override
    public boolean onMyLocationButtonClick() {
        checkLocation();
        mostrarToast(getResources().getString(R.string.toast_btn_centrar_ubicacion));
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
        // Position the map.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude()), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = new ClusterManager<MarkerClusterItem>(getApplicationContext(), map);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {

        // Set some lat/lng coordinates to start with.
        double lat = map.getMyLocation().getLatitude();
        double lng = map.getMyLocation().getLongitude();

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 10; i++) {
            double offset = i / 60d;
            lat = lat + offset;
            lng = lng + offset;
            MarkerClusterItem offsetItem = new MarkerClusterItem(lat, lng, "Title " + i, "Snippet " + i);
            clusterManager.addItem(offsetItem);
        }
    }
}