package com.santiago.fitsforever;

public class ProfileUser {
    public String fullName, email, weight, height, profileImage;

    public ProfileUser(String fullName, String email, String weight, String height, String profileImage) {
        this.fullName = fullName;
        this.email = email;
        this.weight = weight;
        this.height = height;
        this.profileImage = profileImage;
    }

    public ProfileUser(String fullName, String email, String weight, String height) {
        this.fullName = fullName;
        this.email = email;
        this.weight = weight;
        this.height = height;
    }
}
