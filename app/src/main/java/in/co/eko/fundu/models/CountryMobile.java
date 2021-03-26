package in.co.eko.fundu.models;/*
 * Created by Bhuvnesh
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CountryMobile implements Serializable {
    public String getCountryName
            () {
        return countryName;
    }

    public void setCountryName(String name) {
        this.countryName = name;
    }

    @SerializedName("country_name")
    private String countryName;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @SerializedName("country_code")
    private String countryCode;

    public String getStartsWith() {
        return startsWith;
    }

    public void setStartsWith(String startsWith) {
        this.startsWith = startsWith;
    }

    @SerializedName("startsWith")
    private String startsWith;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @SerializedName("length")
    private int length;

    private String symbol;

    @SerializedName("symbol")
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    private String country_shortname;

    @SerializedName("countryShortname")
    public String getcountry_shortname() {
        return country_shortname;
    }

    public void setcountry_shortname(String country_shortname) {
        this.country_shortname = country_shortname;
    }
    @SerializedName("enable")
    public boolean enable = true;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

}
