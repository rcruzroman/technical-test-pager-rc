package io.aircall.pagerservice.adapters.impl;

import io.aircall.pagerservice.adapters.AlertingAdapter;
import io.aircall.pagerservice.entities.AlertService;
import io.aircall.pagerservice.services.PagerService;
import io.aircall.pagerservice.services.impl.PagerServiceImpl;

import java.util.Objects;

public class AlertingAdapterImpl implements AlertingAdapter {

  private PagerService pagerService;

  public AlertingAdapterImpl(PagerService pagerService) {
    this.pagerService = pagerService;
  }

  /**
   * @inheritDoc
   * @param alertService Alert information
   */
  @Override
  public void sendAlertToPager(AlertService alertService) {
      getPagerService().notifyAlert(alertService);
  }

  private PagerService getPagerService(){
    if(Objects.isNull(this.pagerService)) {
      this.pagerService = new PagerServiceImpl();
    }
    return this.pagerService;
  }
}
