package ua.org.ahf.ahfdb.model;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.clustering.ClusterItem;

import ua.org.ahf.ahfdb.R;

public class Company implements ClusterItem {

    private Long id;
    private Integer isMember;
    private Integer isHuntingGround;
    private Integer isFishingGround;
    private Integer isPondFarm;
    private Double area;
    private LatLng position = null;
    private String name;
    private String description;
    private String website;
    private String email;
    private String juridicalAddress;
    private String actualAddress;
    private String director;
    private Integer isEnabled;
    private Integer oblastId;
    private String locale;
    private String phone1;
    private String phone2;
    private String phone3;
    private Integer favorite;
    private String territoryCoords;
    private PolygonOptions polygonOptions = null;
    private Polygon polygon = null;
//    private String logo;
//    private String[] oblast;
//    private String[] raion;
//    private String[] gallery;
    private Context context;

    public Company(Context context, Long id, Integer isMember, Integer isHuntingGround, Integer isFishingGround,
                   Integer isPondFarm, Double lat, Double lng, String name, Double area, String territoryCoords) {
        this.context = context;
        this.id = id;
        this.isMember = isMember;
        this.isHuntingGround = isHuntingGround;
        this.isFishingGround = isFishingGround;
        this.isPondFarm = isPondFarm;
        setPosition(lat, lng);
        this.name = name;
        this.area = area;
        setTerritoryCoords(territoryCoords);
    }

    public Company(Context context, Long id, Integer isMember, Integer isHuntingGround, Integer isFishingGround,
                   Integer isPondFarm, Double area, Double lat, Double lng, String name,
                   String description, String website, String email, String juridicalAddress,
                   String actualAddress, String director, Integer isEnabled, Integer oblastId,
                   String locale, String phone1, String phone2, String phone3, Integer favorite,
                   String territoryCoords) {
        this.context = context;
        this.id = id;
        this.isMember = isMember;
        this.isHuntingGround = isHuntingGround;
        this.isFishingGround = isFishingGround;
        this.isPondFarm = isPondFarm;
        this.area = area;
        setPosition(lat, lng);
        this.name = name;
        this.description = description;
        this.website = website;
        this.email = email;
        this.juridicalAddress = juridicalAddress;
        this.actualAddress = actualAddress;
        this.director = director;
        this.isEnabled = isEnabled;
        this.oblastId = oblastId;
        this.locale = locale;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.phone3 = phone3;
        this.favorite = favorite;
        setTerritoryCoords(territoryCoords);
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer isMember() {
        return isMember;
    }

    public Integer isHuntingGround() {
        return isHuntingGround;
    }

    public Integer isFishingGround() {
        return isFishingGround;
    }

    public Integer isPondFarm() {
        return isPondFarm;
    }

    public Double getArea() {
        return area;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(Double lat, Double lng) {
        if(lat != null && lng != null) {
            this.position = new LatLng(lat, lng);
        }
    }

    public Double getLat() {
        if (position == null) {
            return null;
        }
        return position.latitude;
    }

    public Double getLng() {
        if (position == null) {
            return null;
        }
        return position.longitude;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getWebsite() {
        return website;
    }

    public String getEmail() {
        return email;
    }

    public String getJuridicalAddress() {
        return juridicalAddress;
    }

    public String getActualAddress() {
        return actualAddress;
    }

    public String getDirector() {
        return director;
    }

    public Integer isEnabled() {
        return isEnabled;
    }

    public Integer getOblastId() {
        return oblastId;
    }

    public String getLocale() {
        return locale;
    }

    public String getNameLowercase() {
        return name.toLowerCase();
    }

    public String getPhone1() {
        return phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public String getPhone3() {
        return phone3;
    }

    public Integer isFavorite() {
        return favorite;
    }

    public void setFavorite(Integer favorite) {
        this.favorite = favorite;
    }

    public String getTerritoryCoords() {
        return territoryCoords;
    }

    public PolygonOptions getPolygonOptions() {
        return polygonOptions;
    }

    public void setTerritoryCoords(String territoryCoords) {
        this.territoryCoords = territoryCoords;
        if(territoryCoords == null) {
            return;
        }

        String[] allLatLng = territoryCoords.split(" ");
        polygonOptions = new PolygonOptions();

        for (int i = 0; i < allLatLng.length; i++) {
            String[] latLng = allLatLng[i].split(",");
            polygonOptions.add(new LatLng(Double.parseDouble(latLng[1]), Double.parseDouble(latLng[0])));
            System.out.println("coords:" + latLng[1] + " " + latLng[0]);
        }
        polygonOptions.strokeWidth(2.0f);
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
        setPolygonColor("deselected");
    }

    public void setPolygonColor(String type) {
        if(polygon == null) {
            return;
        }
        switch (type) {
            case "selected":
                polygon.setStrokeColor(ContextCompat.getColor(context, R.color.selectedPolygonStrokeColor));
                polygon.setFillColor(ContextCompat.getColor(context, R.color.selectedPolygonFillColor));
                return;
            case "deselected":
            default:
                polygon.setStrokeColor(ContextCompat.getColor(context, R.color.deselectedPolygonStrokeColor));
                polygon.setFillColor(ContextCompat.getColor(context, R.color.deselectedPolygonFillColor));
        }
    }
}


