package pfm.andresgoicoecheaenrique.cryptoplaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.Locale;

public class DialogFragmentFilterOptions extends DialogFragment {

    private CheckBox c0, c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16;
    private CheckBox[] checkBoxes;
    private static final String[] todasTiposEstablecimientos = {"atm", "cafe", "grocery", "default", "shopping", "lodging", "nightlife", "attraction", "food", "transport", "sports", "trezor retailer", "travel agency", "Educational Business", "services", "retail"};
    private SharedPreferences sharedPrefs;
    private static final String MY_PREFERENCE = "CryptoPlaces";
    private static final String[] CODE_SHARED_PREFS_CHECKS_FILTER = {"c0", "c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "c9", "c10", "c11", "c12", "c13", "c14", "c15", "c16"};


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_fragment_filter_options, container, false);

        initControls(view);
        initGeneralValues();

        /*applyFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean [] checks = new Boolean[]{c0.isChecked(), c1.isChecked(), c2.isChecked(), c3.isChecked(), c4.isChecked(), c5.isChecked(), c6.isChecked(), c7.isChecked(), c8.isChecked(), c9.isChecked(), c10.isChecked(), c11.isChecked(), c12.isChecked(), c13.isChecked(), c14.isChecked(), c15.isChecked(), c16.isChecked()};
                for (int i = 0; i < checks.length; i++){
                    System.out.println("xxxxxx "+checks[i]);
                }
            }
        });*/

        setOnClickListeners();

        return view;
    }

    private void initControls(View view) {
        c0 = view.findViewById(R.id.check_0_id);
        c1 = view.findViewById(R.id.check_1_id);
        c2 = view.findViewById(R.id.check_2_id);
        c3 = view.findViewById(R.id.check_3_id);
        c4 = view.findViewById(R.id.check_4_id);
        c5 = view.findViewById(R.id.check_5_id);
        c6 = view.findViewById(R.id.check_6_id);
        c7 = view.findViewById(R.id.check_7_id);
        c8 = view.findViewById(R.id.check_8_id);
        c9 = view.findViewById(R.id.check_9_id);
        c10 = view.findViewById(R.id.check_10_id);
        c11 = view.findViewById(R.id.check_11_id);
        c12 = view.findViewById(R.id.check_12_id);
        c13 = view.findViewById(R.id.check_13_id);
        c14 = view.findViewById(R.id.check_14_id);
        c15 = view.findViewById(R.id.check_15_id);
        c16 = view.findViewById(R.id.check_16_id);

        checkBoxes = new CheckBox[]{c0, c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16};
        sharedPrefs = getActivity().getSharedPreferences(MY_PREFERENCE, 0);
    }

    private void initGeneralValues() {
        checkBoxes[0].setChecked(sharedPrefs.getBoolean(CODE_SHARED_PREFS_CHECKS_FILTER[0], false));
        for (int i = 1; i < checkBoxes.length ; i++) {
            checkBoxes[i].setText(CommonUtils.traducirCategory(todasTiposEstablecimientos[i-1], getContext()));
            checkBoxes[i].setChecked(sharedPrefs.getBoolean(CODE_SHARED_PREFS_CHECKS_FILTER[i], false));
        }
    }

    private void setOnClickListeners() {
        c0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDB(c0.isChecked(), (short) 0);
            }
        });

        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDB(c1.isChecked(), (short) 1);
            }
        });
        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDB(c2.isChecked(), (short) 2);
            }
        });
        c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDB(c3.isChecked(), (short) 3);
            }
        });
        c4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDB(c4.isChecked(), (short) 4);
            }
        });
        c5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDB(c5.isChecked(), (short) 5);
            }
        });
        c6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDB(c6.isChecked(), (short) 6);
            }
        });
        c7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDB(c7.isChecked(), (short) 7);
            }
        });
        c8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDB(c8.isChecked(), (short) 8);
            }
        });
        c9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDB(c9.isChecked(), (short) 9);
            }
        });
        c10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDB(c10.isChecked(), (short) 10);
            }
        });
        c11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDB(c11.isChecked(), (short) 11);
            }
        });
        c12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDB(c12.isChecked(), (short) 12);
            }
        });
        c13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDB(c13.isChecked(), (short) 13);
            }
        });
        c14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDB(c14.isChecked(), (short) 14);
            }
        });
        c15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDB(c15.isChecked(), (short) 15);
            }
        });
        c16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDB(c16.isChecked(), (short) 16);
            }
        });
    }

    private void writeDB(Boolean bool, short code_CB) {
        SharedPreferences.Editor editor = sharedPrefs.edit();

        if (code_CB == 0) {
            for (int i = 0; i < checkBoxes.length; i++) {
                checkBoxes[i].setChecked(bool);
                editor.putBoolean(CODE_SHARED_PREFS_CHECKS_FILTER[i], bool);
            }
        }

        editor.putBoolean(CODE_SHARED_PREFS_CHECKS_FILTER[code_CB], bool);
        editor.commit();
    }
}