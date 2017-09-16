package ua.org.ahf.ahfdb;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class Company implements ClusterItem {

    private int id;
    private int isMember;
    private int isHuntingGround;
    private int isFishingGround;
    private int isPondFarm;
//    private double territorySize;
    private LatLng position;
    private String name;
    private String description;
//    private String website;
//    private String email;
//    private String[] phone;
//    private String logo;
//    private String[] oblast;
//    private String[] raion;
//    private String language;
//    private String juridicalAddress;
//    private String actualAddress;
//    private String director;
//    private String[] gallery;
//    private int isEnabled;

    public Company(int id, int isMember, int isHuntingGround, int isFishingGround, int isPondFarm,
                   /*double territorySize,*/ double lat, double lng, String name, String description/*,
                   String website, String email, String[] phone, String logo*/) {
        this.id = id;
        this.isMember = isMember;
        this.isHuntingGround = isHuntingGround;
        this.isFishingGround = isFishingGround;
        this.isPondFarm = isPondFarm;
//        this.territorySize = territorySize;
        this.position = new LatLng(lat, lng);
        this.name = name;
        this.description = description;
//        this.website = website;
//        this.email = email;
//        this.phone = phone;
//        this.logo = logo;
    }

    public int getID() {
        return id;
    }

    public int isMember() {
        return isMember;
    }

    public int isHuntingGround() {
        return isHuntingGround;
    }

    public int isFishingGround() {
        return isFishingGround;
    }

    public int isPondFarm() {
        return isPondFarm;
    }

//    public double getTerritorySize() {
//        return territorySize;
//    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public double getLat() {
        return position.latitude;
    }

    public double getLng() {
        return position.longitude;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}


