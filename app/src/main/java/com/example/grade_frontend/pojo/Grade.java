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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Grade implements Serializable {
  @SerializedName("grade")
  @Expose
  private Integer grade;
  @SerializedName("courseCourseNameName")
  @Expose
  private String courseName;
  @SerializedName("createdAt")
  @Expose
  private String createdAt;
}
