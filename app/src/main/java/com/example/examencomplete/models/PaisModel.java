package com.example.examencomplete.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class PaisModel {
    private String name;
    private String dlst;
    private double td;
    private int flg;
    private String nameCapital;
    private double[] geoPtCapital;
    private double[] geoPt;
    private double west;
    private double east;
    private double north;
    private double south;
    private String tld;
    private String iso3;
    private String iso2;
    private String fips;
    private String prefix;
    private int isoN;

    public PaisModel(JSONObject jsonObject) throws JSONException {
        JSONObject results = jsonObject.getJSONObject("Results");
        name = results.getString("Name");
        prefix=results.getString("TelPref");
        dlst = results.getJSONObject("Capital").getString("DLST");
        td = results.getJSONObject("Capital").getDouble("TD");
        flg = results.getJSONObject("Capital").getInt("Flg");
        nameCapital = results.getJSONObject("Capital").getString("Name");

        JSONArray geoPtCapitalArray = results.getJSONObject("Capital").getJSONArray("GeoPt");
        geoPtCapital = new double[]{geoPtCapitalArray.getDouble(0), geoPtCapitalArray.getDouble(1)};
        JSONArray geoPtr = results.getJSONArray("GeoPt");
        geoPt=new double[]{geoPtr.getDouble(0), geoPtr.getDouble(1)};
        west = results.getJSONObject("GeoRectangle").getDouble("West");
        east = results.getJSONObject("GeoRectangle").getDouble("East");
        north = results.getJSONObject("GeoRectangle").getDouble("North");
        south = results.getJSONObject("GeoRectangle").getDouble("South");

        tld = results.getJSONObject("CountryCodes").getString("tld");
        iso3 = results.getJSONObject("CountryCodes").getString("iso3");
        iso2 = results.getJSONObject("CountryCodes").getString("iso2");
        fips = results.getJSONObject("CountryCodes").getString("fips");
        isoN = results.getJSONObject("CountryCodes").getInt("isoN");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDlst() {
        return dlst;
    }

    public void setDlst(String dlst) {
        this.dlst = dlst;
    }

    public double getTd() {
        return td;
    }

    public void setTd(double td) {
        this.td = td;
    }

    public int getFlg() {
        return flg;
    }

    public void setFlg(int flg) {
        this.flg = flg;
    }

    public String getNameCapital() {
        return nameCapital;
    }

    public void setNameCapital(String nameCapital) {
        this.nameCapital = nameCapital;
    }

    public double[] getGeoPtCapital() {
        return geoPtCapital;
    }
    public double[] getgeoPt() {
        return geoPt;
    }

    public void setGeoPtCapital(double[] geoPtCapital) {
        this.geoPtCapital = geoPtCapital;
    }

    public double getWest() {
        return west;
    }

    public void setWest(double west) {
        this.west = west;
    }

    public double getEast() {
        return east;
    }

    public void setEast(double east) {
        this.east = east;
    }

    public double getNorth() {
        return north;
    }

    public void setNorth(double north) {
        this.north = north;
    }

    public double getSouth() {
        return south;
    }

    public void setSouth(double south) {
        this.south = south;
    }

    public String getTld() {
        return tld;
    }

    public void setTld(String tld) {
        this.tld = tld;
    }

    public String getIso3() {
        return iso3;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }

    public String getIso2() {
        return iso2;
    }

    public void setIso2(String iso2) {
        this.iso2 = iso2;
    }

    public String getFips() {
        return fips;
    }
    public String getPrefix() {return  prefix;}
    public void setFips(String fips) {
        this.fips = fips;
    }

    public int getIsoN() {
        return isoN;
    }

}
