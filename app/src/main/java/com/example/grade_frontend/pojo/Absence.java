package com.example.grade_frontend.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Absence implements Serializable{
  @SerializedName("courseCourseNameName")
  @Expose
  private String courseName;
  @SerializedName("data")
  @Expose
  private String date;
}
