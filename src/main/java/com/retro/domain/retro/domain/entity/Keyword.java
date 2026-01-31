package com.retro.domain.retro.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "keywords")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Keyword {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String name;

  private String category;


  @Builder(access = AccessLevel.PRIVATE)
  private Keyword(String name, String category) {
    this.name = name;
    this.category = category;
  }
  
  public static Keyword of(String name, String category) {
    return Keyword.builder()
        .name(name)
        .category(category)
        .build();
  }
}