package pfm.andresgoicoecheaenrique.cryptoplaces;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import pfm.andresgoicoecheaenrique.cryptoplaces.Kraken.ExchangeAPI;


public class GestorBD_API_Kraken extends SQLiteOpenHelper {

    private static final String NOMBRE_BD = "CRYPTO_PLACES_APIS";
    private static final int VERSION_BD = 1;
    private String TABLA_APIS_BD;

    //Columnas de la tabla CONTACTOS
    private static final String col_id = "ID";
    private static final String col_nombre = "NAME";
    private static final String col_llave = "KEY";
    private static final String col_secreto = "SECRET";

    //Consulta de creacion de tabla FAVS
    private String CREATE_TABLE_APIS;


    public GestorBD_API_Kraken(@Nullable Context context, String tabla_FAVS_USUARIO) {
        super(context, NOMBRE_BD, null, VERSION_BD);
        this.TABLA_APIS_BD = tabla_FAVS_USUARIO;
        this.CREATE_TABLE_APIS = "CREATE TABLE IF NOT EXISTS " + TABLA_APIS_BD + "("
                + col_id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + col_nombre + " TEXT,"
                + col_llave + " TEXT,"
                + col_secreto + " TEXT"
                + ")";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(CREATE_TABLE_APIS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_APIS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_APIS_BD);
        onCreate(db);
    }

    //Leer todas las apis de la BD.  EL (orderBy) ORDER BY SERA LA COLUMNA NOMBRE DE FORMA ALFABETICA
    public ArrayList<ExchangeAPI> getAllApis(String column, String order){
        ArrayList<ExchangeAPI> apisAL = new ArrayList<ExchangeAPI>();

        String consulta_getAllApis = "SELECT * FROM " + TABLA_APIS_BD + " ORDER BY " + column + " " + order;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(consulta_getAllApis, null);

        //false si el cursor c vacio
        if(cursor.moveToFirst()){
            do{
                @SuppressLint("Range") ExchangeAPI exchangeAPI = new ExchangeAPI(
                        cursor.getInt(cursor.getColumnIndex(col_id)),
                        cursor.getString(cursor.getColumnIndex(col_nombre)),
                        cursor.getString(cursor.getColumnIndex(col_llave)),
                        cursor.getString(cursor.getColumnIndex(col_secreto))
                );
                //Add al arraylist de apis
                apisAL.add(exchangeAPI);
            }while (cursor.moveToNext());
        }
        db.close();

        return apisAL;
    }

    //Crear un new api
    public long insertarNewAPI(ExchangeAPI newAPI) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(col_nombre, newAPI.getName());
        contentValues.put(col_llave, newAPI.getKey());
        contentValues.put(col_secreto, newAPI.getSecret());

        long id = db.insert(TABLA_APIS_BD, null, contentValues);
        // Los métodos insert() muestran el ID de la fila recién creada o -1 si hubo un error al insertar los datos.
        // Esto puede suceder si se hay conflicto con los datos preexistentes en la base de datos.
        db.close();

        return id;// x o -1 algo mal
    }

    //Borrar un api
    public void borrarAPI(Integer id){
        SQLiteDatabase db = getReadableDatabase();
        db.delete(TABLA_APIS_BD, col_id +" = ?", new String[]{id.toString()});
        db.close();
    }

}
