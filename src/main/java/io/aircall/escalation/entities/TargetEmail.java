package io.aircall.escalation.entities;

import io.aircall.escalation.adapters.EmailAdapter;

/**
 * Entity used to represent the target which notification has to be done by mail
 */
public class TargetEmail implements Target{

  String emailAddress;
  EmailAdapter emailAdapter;

  public TargetEmail(String emailAddress, EmailAdapter emailAdapter) {
    this.emailAddress = emailAddress;
    this.emailAdapter = emailAdapter;
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
