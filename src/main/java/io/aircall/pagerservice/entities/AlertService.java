package io.aircall.pagerservice.entities;

/**
 * Entity that will be used by the alert service to notify the alerts
 */
public class AlertService {

  private int serviceId;
  private String message;

  public int getServiceId() {
    return serviceId;
  }

  public void setServiceId(int serviceId) {
    this.serviceId = serviceId;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
