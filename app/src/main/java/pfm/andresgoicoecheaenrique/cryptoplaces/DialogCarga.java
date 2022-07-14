package pfm.andresgoicoecheaenrique.cryptoplaces;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class DialogCarga {

    private Activity activity;
    private AlertDialog alertDialog;

    public DialogCarga(Activity activity) {
        this.activity = activity;
    }

    public void startLoadingDialog(){
        AlertDialog.Builder constructor = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        constructor.setView(inflater.inflate(R.layout.custom_loading_dialog, null));
        constructor.setCancelable(false);

        alertDialog = constructor.create();
        alertDialog.show();
    }

    public void dismissDialog(){
        alertDialog.dismiss();
    }
}
