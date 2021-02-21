package io.aircall.pagerservice.adapters;

/**
 * It will contains the methods that can be used with Pager Web Console
 */
public interface ConsoleAdapter {

  /**
   * It will be used to notify the ACK from the Aircall engineer
   * @param serviceId Monitored Service Id that has to be updated
   */
  void sendAck(int serviceId);

  /**
   * It will be used to notify an Healthy event from the Aircall engineer
   * @param serviceId Monitored Service Id that has to be updated
   */
  void sendHealthyEvent(int serviceId);

}
