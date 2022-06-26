package pfm.andresgoicoecheaenrique.cryptoplaces;

import java.io.Serializable;

public class Venue implements Serializable {
    private long id;
    private double lat;
    private double lon;
    private String category;
    private String name;
    private long createdOn;
    private String geolocation_degrees;

    public Venue(long id, double lat, double lon, String category, String name, long createdOn, String geolocation_degrees) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.category = category;
        this.name = name;
        this.createdOn = createdOn;
        this.geolocation_degrees = geolocation_degrees;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }

    public String getGeolocation_degrees() {
        return geolocation_degrees;
    }

    public void setGeolocation_degrees(String geolocation_degrees) {
        this.geolocation_degrees = geolocation_degrees;
    }
}

