package io.aircall.escalation.adapters;

/**
 * It will contains the methods that can be used with the Email Service
 */
public interface EmailAdapter {

  /**
   * It will send an email
   * @param emailAddress Email address use to send the email
   * @param message Message included in the email
   */
  void sendEmail(String emailAddress, String message);
}
