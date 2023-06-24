package com.example.grade_frontend.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CourseForGroup implements Serializable {
  @SerializedName("courseCourseNameId")
  @Expose
  private int courseNameId;
  @SerializedName("courseCourseNameName")
  @Expose
  private String courseName;
}
