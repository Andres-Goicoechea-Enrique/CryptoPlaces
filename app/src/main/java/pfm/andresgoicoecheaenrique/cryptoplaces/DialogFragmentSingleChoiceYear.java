package pfm.andresgoicoecheaenrique.cryptoplaces;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class DialogFragmentSingleChoiceYear extends DialogFragment {

    private int position;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            position = getArguments().getInt("year_selected", 0);
        } else {
            CommonUtils.mostrarToast("Error", getContext());
            dismiss();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String[] list = generateYears();

        builder.setTitle(getActivity().getResources().getString(R.string.year_title_txt))
                .setSingleChoiceItems(list, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        position = i;
                    }
                })
                .setPositiveButton(getActivity().getResources().getString(R.string.year_pos_txt), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((GoogleMapsActivity)getActivity()).changeMapaData(list[position], position);
                        dismiss();
                    }
                })
                .setNegativeButton(getActivity().getResources().getString(R.string.btn_negative_alertdialog_activate_location), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                });

        return builder.create();
    }

    private String[] generateYears() {
        Calendar c = Calendar.getInstance();
        int maxYear = c.get(Calendar.YEAR);
        int minYear = 2013;
        int numYears = maxYear - minYear;
        String[] choices = new String[numYears + 1];
        
        for (int i = numYears; i > -1; i--) {
            choices[i] = String.valueOf(minYear);
            minYear += 1;
        }
        return choices;
    }

}
