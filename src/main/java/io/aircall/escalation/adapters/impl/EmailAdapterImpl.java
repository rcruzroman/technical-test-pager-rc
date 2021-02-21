package io.aircall.escalation.adapters.impl;

import io.aircall.escalation.adapters.EmailAdapter;

public class EmailAdapterImpl implements EmailAdapter {
  @Override
  public void sendEmail(String emailAddress, String message) {
    System.out.println("Email sent successfully to: " + emailAddress);
  }
}
