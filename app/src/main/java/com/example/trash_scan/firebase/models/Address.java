package com.example.trash_scan.firebase.models;

public class Address {

    String barangay;
    String city;
    String province;
    int zipcode;
    String buildingHouseNo;

    public Address() {
    }

    public Address(String barangay, String buildingHouseNo) {
        this.barangay = barangay;
        city = "Urdaneta";
        province = "Pangasinan";
        zipcode = 2428;
        this.buildingHouseNo = buildingHouseNo;
    }
    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getZipcode() {
        return zipcode;
    }

    public void setZipcode(int zipcode) {
        this.zipcode = zipcode;
    }

    public String getBuildingHouseNo() {
        return buildingHouseNo;
    }

    public void setBuildingHouseNo(String buildingHouseNo) {
        this.buildingHouseNo = buildingHouseNo;
    }
}
