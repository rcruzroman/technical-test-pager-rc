package io.aircall.escalation.adapters.impl;

import io.aircall.escalation.adapters.SMSAdapter;

public class SMSAdapterImpl implements SMSAdapter {
  @Override
  public void sendSms(String phoneNumber, String message) {
    System.out.println("SMS sent properly to: " + phoneNumber);
  }
}
