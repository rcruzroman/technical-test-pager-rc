package io.aircall.escalation.entities;

import io.aircall.escalation.adapters.SMSAdapter;

import java.util.List;

/**
 * Entity used to represent the target which notification has to be done by sms
 */
public class TargetSMS extends TargetAbstract {

  private String phoneNumber;
  private SMSAdapter smsAdapter;

  public TargetSMS(String phoneNumber, SMSAdapter smsAdapter, List<Integer> availabilityHours) {
    this.phoneNumber = phoneNumber;
    this.smsAdapter = smsAdapter;
    this.availailabilityHours = availabilityHours;
  }

  /**
   * @inheritDoc
   * @param message Message that will contain the notification
   */
  @Override
  public void sendNotification(String message) {
    this.smsAdapter.sendSms(this.phoneNumber, message);
  }
}
