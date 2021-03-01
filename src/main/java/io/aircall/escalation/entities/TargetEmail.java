package io.aircall.escalation.entities;

import io.aircall.escalation.adapters.EmailAdapter;

import java.util.List;

/**
 * Entity used to represent the target which notification has to be done by mail
 */
public class TargetEmail extends TargetAbstract {

  String emailAddress;
  EmailAdapter emailAdapter;

    public TargetEmail(String emailAddress, EmailAdapter emailAdapter, List<Integer> availabilityHours) {
    this.emailAddress = emailAddress;
    this.emailAdapter = emailAdapter;
    this.availailabilityHours = availabilityHours;
  }

  /**
   * @inheritDoc
   * @param message Message that will contain the notification
   */
  @Override
  public void sendNotification(String message) {
    this.emailAdapter.sendEmail(this.emailAddress, message);
  }
}
