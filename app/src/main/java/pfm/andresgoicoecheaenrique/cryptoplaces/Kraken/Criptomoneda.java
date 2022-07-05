package pfm.andresgoicoecheaenrique.cryptoplaces.Kraken;

import java.io.Serializable;

public class Criptomoneda implements Serializable {

    private String name;
    private String amount;

    public Criptomoneda(String name, String amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
