package io.aircall.pagerservice.adapters;

import io.aircall.pagerservice.entities.AlertService;

/**
 * It will contains the methods allowed to be used with the alerting service.
 */
public interface AlertingAdapter {

  /**
   * It will notify to the pager a new alert
   * @param alertService Alert information
   */
  void sendAlertToPager(AlertService alertService);
}
