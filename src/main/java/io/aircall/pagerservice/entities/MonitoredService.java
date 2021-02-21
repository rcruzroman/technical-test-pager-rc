package io.aircall.pagerservice.entities;

/**
 * Entity used to represent the different monitored services available
 */
public class MonitoredService {

  private int id;
  private MonitoredServiceStatus status;
  private Integer idLevelNotified;
  private String alertMessage;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public MonitoredServiceStatus getStatus() {
    return status;
  }

  public void setStatus(MonitoredServiceStatus status) {
    this.status = status;
  }

  public Integer getIdLevelNotified() {
    return idLevelNotified;
  }

  public void setIdLevelNotified(Integer idLevelNotified) {
    this.idLevelNotified = idLevelNotified;
  }

  public String getAlertMessage() {
    return alertMessage;
  }

  public void setAlertMessage(String alertMessage) {
    this.alertMessage = alertMessage;
  }
}
