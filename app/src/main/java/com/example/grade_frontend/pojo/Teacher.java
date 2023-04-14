package com.example.grade_frontend.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("patronimic")
    @Expose
    private String patronimic;

    @SerializedName("sex")
    @Expose
    private String sex;

    @SerializedName("surname")
    @Expose
    private String surname;

    @SerializedName("active")
    @Expose
    private Boolean active;

    @SerializedName("departmentName")
    @Expose
    private String departmentName;

    @SerializedName("departmentFacultyName")
    @Expose
    private String departmentFacultyName;

    @SerializedName("positionName")
    @Expose
    private String positionName;

    @SerializedName("scientificDegreeName")
    @Expose
    private String scientificDegreeName;
}
