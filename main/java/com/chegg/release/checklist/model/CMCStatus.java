package com.chegg.release.checklist.model;

public enum CMCStatus {
  APPROVED("Approved"),
  WAITING_ON_APPROVAL("Waiting on Approval"),
  OPEN("Open"),
  CLOSED("Closed"),
  IN_PROGRESS("In progress"),
  REVIEW("Review");


  private String cmcStatus;

  CMCStatus(String cmcStatus) {
    this.cmcStatus = cmcStatus;
  }

  public String getCmcStatus() {
    return this.cmcStatus;
  }
}
