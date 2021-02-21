package io.aircall.pagerservice.adapters;

/**
 * It will contains the methods that can be used with the Timer Service
 */
public interface TimerAdapter {

  /**
   * It will be used to set an ACK timeout in the timer service
   * @param serviceId Identifier of the monitored service
   * @param minutes Timeout duration in minutes
   */
  void setAckTimeout(int serviceId, int minutes);

  /**
   * It will be used to notify to the Pager Service that ack timeout is expired
   * @param serviceId Identifier of the monitored service
   */
  void ackExpired(int serviceId);
}
