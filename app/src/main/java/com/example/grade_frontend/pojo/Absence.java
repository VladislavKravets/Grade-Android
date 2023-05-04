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

  public String getDate() {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    try {
      Date formatDate = formatter.parse(date);
      return formatter.format(formatDate);
    } catch (ParseException e) {
      e.printStackTrace();
      return null;
    }
  }
}
