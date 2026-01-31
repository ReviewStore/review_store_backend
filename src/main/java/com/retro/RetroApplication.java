package com.retro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RetroApplication {

  public static void main(String[] args) {
    SpringApplication.run(RetroApplication.class, args);
  }
}
