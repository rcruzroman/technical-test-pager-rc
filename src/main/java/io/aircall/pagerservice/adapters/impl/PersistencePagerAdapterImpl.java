package io.aircall.pagerservice.adapters.impl;

import io.aircall.pagerservice.adapters.PersistencePagerAdapter;
import io.aircall.pagerservice.entities.MonitoredService;

public class PersistencePagerAdapterImpl implements PersistencePagerAdapter {

  /**
   * @inheritDoc
   * @param serviceId Identifier for the monitored service
   * @return the monitored service entity retrieved
   */
  @Override
  public MonitoredService getMonitoredServiceById(int serviceId) {
    return null;
  }

  /**
   * @inheritDoc
   * @param monitoredService monitored service entity to be stored
   */
  @Override
  public void saveMonitoredService(MonitoredService monitoredService) {
    System.out.println("Monitored Service saved");
  }
}
