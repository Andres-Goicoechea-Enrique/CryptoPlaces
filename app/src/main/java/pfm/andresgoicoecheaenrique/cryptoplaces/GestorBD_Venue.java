package pfm.andresgoicoecheaenrique.cryptoplaces;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class GestorBD_Venue extends SQLiteOpenHelper {

    private static final String NOMBRE_BD = "CRYPTO_PLACES";
    private static final int VERSION_BD = 1;
    private String TABLA_FAVS_BD;

    //Columnas de la tabla CONTACTOS
    private static final String col_id = "ID";
    private static final String col_nombre = "NAME";
    private static final String col_latitud = "LAT";
    private static final String col_longitud = "LON";
    private static final String col_categoria = "CATEGORY";
    private static final String col_fechaCreacion = "CREATED_ON";
    private static final String col_gradosGeolocalizacion = "GEOLOCATION_DEGREES";

    //Consulta de creacion de tabla FAVS
    private String CREATE_TABLE_FAVORITOS;


    public GestorBD_Venue(@Nullable Context context, String tabla_FAVS_USUARIO) {
        super(context, NOMBRE_BD, null, VERSION_BD);
        this.TABLA_FAVS_BD = tabla_FAVS_USUARIO;
        this.CREATE_TABLE_FAVORITOS = "CREATE TABLE IF NOT EXISTS " + TABLA_FAVS_BD + "("
                + col_id + " LONG PRIMARY KEY,"
                + col_nombre + " TEXT,"
                + col_latitud + " DOUBLE,"
                + col_longitud + " DOUBLE,"
                + col_categoria + " TEXT,"
                + col_fechaCreacion + " LONG,"
                + col_gradosGeolocalizacion + " TEXT"
                + ")";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(CREATE_TABLE_FAVORITOS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FAVORITOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_FAVS_BD);
        onCreate(db);
    }

    // Por categoria de otra manera o en el propio buscador
    //Leer todos los venues de la BD.  EL (orderBy) ORDER BY SERA LA COLUMNA NOMBRE DE FORMA ALFABETICA
    public ArrayList<Venue> getAllVenues(String column, String order){
        System.out.println("getAllVenues");
        ArrayList<Venue> venuesAL = new ArrayList<Venue>();

        //String consulta_getAllContacts = "SELECT * FROM " + NOMBRE_TABLA_BD + " ORDER BY " + col_nombre + " " + order;
        String consulta_getAllVenues = "SELECT * FROM " + TABLA_FAVS_BD + " ORDER BY " + column + " " + order;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(consulta_getAllVenues, null);

        //false si el cursor c vacio
        if(cursor.moveToFirst()){
            do{
                @SuppressLint("Range") Venue venue = new Venue(
                        cursor.getLong(cursor.getColumnIndex(col_id)),
                        cursor.getDouble(cursor.getColumnIndex(col_latitud)),
                        cursor.getDouble(cursor.getColumnIndex(col_longitud)),
                        ""+cursor.getString(cursor.getColumnIndex(col_categoria)),
                        ""+cursor.getString(cursor.getColumnIndex(col_nombre)),
                        cursor.getLong(cursor.getColumnIndex(col_fechaCreacion)),
                        ""+cursor.getString(cursor.getColumnIndex(col_gradosGeolocalizacion))
                );
                //Add al arraylist de venues
                venuesAL.add(venue);
            }while (cursor.moveToNext());
        }
        db.close();

        return venuesAL;
    }

    //Crear un new venue
    public long insertarNewVenue(Venue newVenue) {
        System.out.println("insertarNewVenue");

        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(col_id, newVenue.getId());
        contentValues.put(col_nombre, newVenue.getName());
        contentValues.put(col_latitud, newVenue.getLat());
        contentValues.put(col_longitud, newVenue.getLon());
        contentValues.put(col_categoria, newVenue.getCategory());
        contentValues.put(col_fechaCreacion, newVenue.getCreatedOn());
        contentValues.put(col_gradosGeolocalizacion, newVenue.getGeolocation_degrees());

        long id = db.insert(TABLA_FAVS_BD, null, contentValues);
        // Los métodos insert() muestran el ID de la fila recién creada o -1 si hubo un error al insertar los datos.
        // Esto puede suceder si se hay conflicto con los datos preexistentes en la base de datos.
        db.close();

        return id;// x o -1 algo mal
    }

    //Borrar un venue
    public void borrarVenue(Long id){
        SQLiteDatabase db = getReadableDatabase();
        db.delete(TABLA_FAVS_BD, col_id +" = ?", new String[]{id.toString()});//db.delete(nombre_tabla_BD, "_id=" + id, null);
        db.close();
    }

    //Guardar cambios o editar no se va a usar
    /*public void editarContacto(String id, String nombre, String dir, String movil, String tlf, String correo){

        //Get the writable database, because we want to add into it
        SQLiteDatabase db = this.getWritableDatabase();

        //Insert data
        ContentValues contentValues = new ContentValues();

        contentValues.put(col_nombre, nombre);
        contentValues.put(col_direccion, dir);
        contentValues.put(col_movil, movil);
        contentValues.put(col_telefono, tlf);
        contentValues.put(col_correo, correo);

        db.update(nombre_tabla_BD, contentValues, col_id + " = ?", new String[]{id});
        db.close();
    }*/

}
