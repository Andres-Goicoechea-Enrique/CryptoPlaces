package pfm.andresgoicoecheaenrique.cryptoplaces;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.widget.Toast;

public abstract class CommonUtils {


    /**
     *
     * @param code
     */
    protected static void crearAlert(short code, Context context) {

        AlertDialog.Builder constructor = new AlertDialog.Builder(context);
        constructor.setCancelable(true);
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
}
