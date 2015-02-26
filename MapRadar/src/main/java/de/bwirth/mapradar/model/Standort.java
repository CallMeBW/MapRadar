package de.bwirth.mapradar.model;
public class Standort {
    private long id;
    private String n;
    private String k;
    private double lon;
    private double lat;
    private int tele;
    private String e;
    private String standort;

    // -----ID-----
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    // ----NAME----
    public String getName() {
        return n;
    }

    public void setName(String n) {
        this.n = n;
    }

    // -----KATEGORIE-----
    public String getKategorie() {
        return k;
    }

    public void setKategorie(String k) {
        this.k = k;
    }

    // -----LONGITUDE------
    public double getLongitude() {
        return lon;
    }

    public void setLongitude(double lon) {
        this.lon = lon;
    }

    // -----LONGITUDE-----
    public double getLatitude() {
        return lat;
    }

    public void setLatitude(double lat) {
        this.lat = lat;
    }

    // ------TELEFON------
    public int getTelefon() {
        return tele;
    }

    public void setTelefon(int tele) {
        this.tele = tele;
    }

    // ------EMAIL-------
    public String getEmail() {
        return e;
    }

    public void setEmail(String e) {
        this.e = e;
    }

    //-------STANDORT-----
    public String getStandort() {
        return standort;
    }

    public void setStandort(String standort) {
        this.standort = standort;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return standort;
    }
}
