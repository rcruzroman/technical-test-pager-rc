package io.aircall.pagerservice.adapters.impl;

import io.aircall.pagerservice.adapters.ConsoleAdapter;
import io.aircall.pagerservice.services.PagerService;

public class ConsoleAdapterImpl implements ConsoleAdapter {

  PagerService pagerService;


  public ConsoleAdapterImpl(PagerService pagerService) {
    this.pagerService = pagerService;
  }

  /**
   * @inheritDoc
   * @param serviceId Monitored Service Id that has to be updated
   */
  @Override
  public void sendAck(int serviceId) {
    this.pagerService.notifyAck(serviceId);
  }

  /**
   * @inheritDoc
   * @param serviceId Monitored Service Id that has to be updated
   */
  @Override
  public void sendHealthyEvent(int serviceId) {
    this.pagerService.notifyHealthyEvent(serviceId);
  }
}
