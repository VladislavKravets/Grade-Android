package com.example.grade_frontend.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Student {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("patronimic")
    @Expose
    private String patronimic;
    @SerializedName("surname")
    @Expose
    private String surname;
    @SerializedName("email")
    @Expose
    private String email;


    public String getName() {
        return name;
    }

    public String getPatronimic() {
        return patronimic;
    }

    public String getSurname() {
        return surname;
    }

    @Override
    public String toString() {
        return surname + " " + name + " " + patronimic;
    }
}
