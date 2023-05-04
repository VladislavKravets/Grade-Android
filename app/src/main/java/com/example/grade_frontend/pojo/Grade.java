package com.example.grade_frontend.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Date;

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

  public String getCreatedAt() {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    try {
      Date date = formatter.parse(createdAt);
      return formatter.format(date);
    } catch (ParseException e) {
      e.printStackTrace();
      return null;
    }
  }
}
