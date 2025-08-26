package com.example.hanaharmonybackend.web.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionType {
  IN("입금"),
  OUT("출금");

  private final String label;

  TransactionType(String label) {
	this.label = label;
  }

  @JsonValue
  public String getLabel() {
	return label;
  }
}
