package com.system.design.prototypes.DatabaseSharding;

public class User {
    private String id;
    private String name;
    private String countryCode;

    public User(String userName, String userCountryCode){
        this.name = userName;
        this.countryCode = userCountryCode;
    }

    public String getName(){
        return name;
    }

    public String getCountryCode(){
        return countryCode;
    }

}
