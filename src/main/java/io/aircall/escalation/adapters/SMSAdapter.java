package io.aircall.escalation.adapters;

/**
 * It will contains the methods that can be used with the SMS Service
 */
public interface SMSAdapter {

  /**
   * It will send an sms
   * @param phoneNumber Phone number that will used to send an SMS
   * @param message Message that will be sent in the SMS
   */
  void sendSms(String phoneNumber, String message);
}
