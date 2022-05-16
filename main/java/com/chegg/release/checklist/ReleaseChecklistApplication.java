package com.chegg.release.checklist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** @author Gopinath Rangappa */
@SpringBootApplication
public class ReleaseChecklistApplication {

  /** Default constructor. */
  public ReleaseChecklistApplication() {}

  /** @param args */
  public static void main(String[] args) {
    System.exit(
        SpringApplication.exit(SpringApplication.run(ReleaseChecklistApplication.class, args)));
  }
}
