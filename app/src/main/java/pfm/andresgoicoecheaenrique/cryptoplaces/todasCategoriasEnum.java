package pfm.andresgoicoecheaenrique.cryptoplaces;

import android.content.Context;
import android.content.res.Resources;

public enum todasCategoriasEnum {

    CATEGORIA1("atm", (short) 0, (float) 210.0),
    CATEGORIA2("cafe", (short) 1, (float) 240.0),
    CATEGORIA3("grocery", (short) 2, (float) 180.0),
    CATEGORIA4("default", (short) 3, (float) 90.0),
    CATEGORIA5("shopping", (short) 4, (float) 300),
    CATEGORIA6("lodging", (short) 5, (float) 30.0),
    CATEGORIA7("nightlife", (short) 6, (float) 0.0),
    CATEGORIA8("attraction", (short) 7, (float) 330.0),
    CATEGORIA9("food", (short) 8, (float) 270.0),
    CATEGORIA10("transport", (short) 9, (float) 60.0),
    CATEGORIA11("sports", (short) 10, (float) 120.0),
    CATEGORIA12("trezor retailer", (short) 11, (float) 80.0),
    CATEGORIA13("travel agency", (short) 12, (float) 220.0),
    CATEGORIA14("educational business", (short) 13, (float) 250.0),
    CATEGORIA15("services", (short) 14, (float) 190.0),
    CATEGORIA16("retail", (short) 15, (float) 310.0);

    private final String categoria;
    private final short index;
    private final Float colorIcono;


    todasCategoriasEnum(String categoria, short index, Float colorIcono) {
        this.categoria = categoria;
        this.index = index;
        this.colorIcono = colorIcono;
    }

    public String getCategoria() {
        return categoria;
    }

    public short getIndex() {
        return index;
    }

    public Float getColorIcono() {
        return colorIcono;
    }

    public String getLabel(Context context) {
        Resources res = context.getResources();
        int resId = res.getIdentifier(this.name(), "string", context.getPackageName());
        if (0 != resId) {
            return (res.getString(resId));
        }
        return (name());
    }
}

