package io.aircall.escalation.entities;

/**
 * Actors to be notified when an alert for a monitored service is raised
 */
public interface Target {

  /**
   * It will send the notification to the target
   * @param message Message that will contain the notification
   */
  public void sendNotification(String message);
}
