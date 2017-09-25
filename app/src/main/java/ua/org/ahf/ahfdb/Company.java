package ua.org.ahf.ahfdb;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class Company implements ClusterItem {

    private Long id;
    private Integer isMember;
    private Integer isHuntingGround;
    private Integer isFishingGround;
    private Integer isPondFarm;
    private Double area;
    private LatLng position;
    private String name;
    private String description;
    private String website;
    private String email;
    private String juridicalAddress;
    private String actualAddress;
    private String director;
    private Integer isEnabled;
//    private String[] phone;
//    private String logo;
//    private String[] oblast;
//    private String[] raion;
//    private String language;
//    private String[] gallery;

    public Company(Long id, Integer isMember, Integer isHuntingGround, Integer isFishingGround,
                   Integer isPondFarm, Double lat, Double lng, String name) {
        this.id = id;
        this.isMember = isMember;
        this.isHuntingGround = isHuntingGround;
        this.isFishingGround = isFishingGround;
        this.isPondFarm = isPondFarm;
        setPosition(lat, lng);
        this.name = name;
    }

    public Company(Long id, Integer isMember, Integer isHuntingGround, Integer isFishingGround,
                   Integer isPondFarm, Double area, Double lat, Double lng, String name,
                   String description, String website, String email, String juridicalAddress,
                   String actualAddress, String director, Integer isEnabled) {
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
        if(lat == null || lng == null) {
            this.position = null;
        } else {
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
}


