package com.retro.domain.notification.domain.entity;

public enum TimeAgoText {

  JUST_NOW("방금 전"),
  MINUTES_AGO("분 전"),
  HOURS_AGO("시간 전"),
  DAYS_AGO("일 전");

  private final String text;

  TimeAgoText(String text) {
    this.text = text;
  }

  public String value() {
    return text;
  }
}