package io.aircall.pagerservice.adapters;

import io.aircall.pagerservice.entities.MonitoredService;

/**
 * It will contains the methods to manage the information from the Pager store system
 */
public interface PersistencePagerAdapter {

  /**
   * It will get the information related to the monitored service
   * @param serviceId Identifier for the monitored service
   * @return the monitored service entity retrieved
   */
  MonitoredService getMonitoredServiceById(int serviceId);

  /**
   * It will stoed the information related to a monitored service
   * @param monitoredService monitored service entity to be stored
   */
  void saveMonitoredService(MonitoredService monitoredService);
}
