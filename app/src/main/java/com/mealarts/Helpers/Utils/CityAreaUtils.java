package com.mealarts.Helpers.Utils;

import java.util.ArrayList;

public class CityAreaUtils {

    private String CityId, CityName;
    private ArrayList<AreaUtils> AreaArray;
    private ArrayList<String> AreaNameList;

    public String getCityId() {
        return CityId;
    }

    public void setCityId(String cityId) {
        CityId = cityId;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public ArrayList<AreaUtils> getAreaArray() {
        return AreaArray;
    }

    public void setAreaArray(ArrayList<AreaUtils> areaArray) {
        AreaArray = areaArray;
    }

    public ArrayList<String> getAreaNameList() {
        return AreaNameList;
    }

    public void setAreaNameList(ArrayList<String> areaNameList) {
        AreaNameList = areaNameList;
    }
}
