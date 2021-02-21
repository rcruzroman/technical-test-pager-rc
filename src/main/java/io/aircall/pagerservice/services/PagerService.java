package io.aircall.pagerservice.services;

import io.aircall.pagerservice.entities.AlertService;

/**
 * Service with the logic to manage the Pager Service
 */
public interface PagerService {

  /**
   * It will receive an alert from Alerting Service and it will notify
   * to the right target only if the monitored service was healthy.
   * @param alertService Service alert to be processed
   */
  void notifyAlert(AlertService alertService);

  /**
   * It will receive the acknowledgment timeout from the Timer. If needed (no healthy and not acknowledged) it will notify the next target level.
   * If the notification was already sent to the last level, nothing will be done.
   * @param serviceId Service alert id to be notified the timeout ack
   */
  void notifyAckTimeout(int serviceId);

  /**
   * It will process the acknowledgement from the console. It will be hold on the acknowledged status.
   * @param serviceId Service alert id which will be marked as acknowledged
   */
  void notifyAck(int serviceId);

  /**
   * It will process an healthy event from the console.
   * @param serviceId Service alert id which will be marked as healthy
   */
  void notifyHealthyEvent(int serviceId);
}
