package io.aircall.pagerservice.adapters.impl;

import io.aircall.pagerservice.adapters.TimerAdapter;
import io.aircall.pagerservice.services.PagerService;
import io.aircall.pagerservice.services.impl.PagerServiceImpl;

import java.util.Objects;

public class TimerAdapterImpl implements TimerAdapter {

  PagerService pagerService;

  public TimerAdapterImpl() {
  }

  public TimerAdapterImpl(PagerService pagerService) {
    this.pagerService = pagerService;
  }

  /**
   * @inheritDoc
   * @param serviceId Identifier of the monitored service
   * @param minutes Timeout duration in minutes
   */
  @Override
  public void setAckTimeout(int serviceId, int minutes) {
    System.out.println("The Ack timeout was sent to the timer service");
  }

  /**
   * @inheritDoc
   * @param serviceId Identifier of the monitored service
   */
  @Override
  public void ackExpired(int serviceId) {
    getPagerService().notifyAckTimeout(serviceId);
  }

  public PagerService getPagerService(){
    if(Objects.isNull(pagerService)){
      this.pagerService = new PagerServiceImpl();
    }
    return pagerService;
  }
}
