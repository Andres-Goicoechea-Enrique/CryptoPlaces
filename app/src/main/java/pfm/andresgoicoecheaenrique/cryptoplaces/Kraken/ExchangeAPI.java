package pfm.andresgoicoecheaenrique.cryptoplaces.Kraken;

import java.io.Serializable;

public class ExchangeAPI implements Serializable {

    private String name;
    private String key;
    private String secret;

    public ExchangeAPI(String name, String key, String secret) {
        this.name = name;
        this.key = key;
        this.secret = secret;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
