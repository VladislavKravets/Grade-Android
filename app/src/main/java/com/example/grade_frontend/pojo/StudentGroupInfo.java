package com.example.grade_frontend.pojo;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class StudentGroupInfo {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("active")
    @Expose
    private Boolean active;
    @SerializedName("beginYears")
    @Expose
    private Integer beginYears;
    @SerializedName("creationYear")
    @Expose
    private Integer creationYear;
    @SerializedName("studySemesters")
    @Expose
    private Integer studySemesters;
    @SerializedName("studyYears")
    @Expose
    private BigDecimal studyYears;
    @SerializedName("specializationName")
    @Expose
    private String specializationName;
    @SerializedName("specializationDegreeName")
    @Expose
    private String specializationDegreeName;
    @SerializedName("specializationDepartmentName")
    @Expose
    private String specializationDepartmentName;
    @SerializedName("specializationFacultyName")
    @Expose
    private String specializationFacultyName;
    @SerializedName("specializationSpecialityName")
    @Expose
    private String specializationSpecialityName;
    @SerializedName("specializationProgramHeadName")
    @Expose
    private String specializationProgramHeadName;
    @SerializedName("specializationProgramHeadPatronimic")
    @Expose
    private String specializationProgramHeadPatronimic;
    @SerializedName("specializationProgramHeadSurname")
    @Expose
    private String specializationProgramHeadSurname;

    @NonNull
    @Override
    public String toString() {
        return "Назва групи: '" + name + '\n' +
                "Рік навчання: " + beginYears + '\n' +
                "Рік вступу: " + creationYear + '\n' +
                "Навчальні семестр: " + studySemesters + '\n' +
                "Роки навчання: " + studyYears + '\n' +
                "Назва спеціалізації: '" + specializationName + '\n' +
                "Спеціалізація Ступінь Найменування: '" + specializationDegreeName + '\n' +
                "Назва кафедри: '" + specializationDepartmentName + '\n' +
                "Назва Факультету: '" + specializationFacultyName + '\n' +
                "Назва спеціальності: '" + specializationSpecialityName + '\n' +
                "Куратор: " + specializationProgramHeadName
                + specializationProgramHeadPatronimic +  specializationProgramHeadSurname;
    }
}
