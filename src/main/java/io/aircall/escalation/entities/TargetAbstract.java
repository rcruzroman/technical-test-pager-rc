package io.aircall.escalation.entities;

import java.util.List;

public abstract class TargetAbstract implements Target{

  protected List<Integer> availailabilityHours;

  public abstract void sendNotification(String message);

  public List<Integer> getAvailailabilityHours() {
    return availailabilityHours;
  }

  public void setAvailailabilityHours(List<Integer> availailabilityHours) {
    this.availailabilityHours = availailabilityHours;
  }
}
