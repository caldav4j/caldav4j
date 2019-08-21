package com.github.caldav4j.model;

import java.io.Serializable;

public class ContactInfo implements Serializable {
    
    private static final long serialVersionUID = 4304579720612441396L;

    private String givenName;
    
    private String familyName;
    
    private String mobilePhone;

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

}
