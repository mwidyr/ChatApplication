package com.duxina.chatapplication;

import java.util.Objects;

public class UserInformation {
    public String fullName;
    public String emailId;
    public String mobileNumber;
    public String location;

    public UserInformation(){

    }

    public UserInformation(String fullNameSave, String emailIdSave, String mobileNumberSave, String locationSave){
        this.fullName = fullNameSave;
        this.emailId = emailIdSave;
        this.mobileNumber = mobileNumberSave;
        this.location = locationSave;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "UserInformation{" +
                "fullName='" + fullName + '\'' +
                ", emailId='" + emailId + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
