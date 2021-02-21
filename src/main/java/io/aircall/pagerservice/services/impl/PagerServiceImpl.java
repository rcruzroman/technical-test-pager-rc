package io.aircall.pagerservice.services.impl;

import io.aircall.escalation.entities.Level;
import io.aircall.escalation.entities.Target;
import io.aircall.escalation.services.EscalationService;
import io.aircall.escalation.services.impl.EscalationServiceImpl;
import io.aircall.pagerservice.adapters.PersistencePagerAdapter;
import io.aircall.pagerservice.adapters.TimerAdapter;
import io.aircall.pagerservice.adapters.impl.PersistencePagerAdapterImpl;
import io.aircall.pagerservice.adapters.impl.TimerAdapterImpl;
import io.aircall.pagerservice.entities.AlertService;
import io.aircall.pagerservice.entities.MonitoredService;
import io.aircall.pagerservice.entities.MonitoredServiceStatus;
import io.aircall.pagerservice.services.PagerService;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class PagerServiceImpl implements PagerService {

  private static final int ACK_TIMEOUT_MINUTES_VALUE = 15;
  EscalationService escalationService;
  PersistencePagerAdapter persistencePagerAdapter;
  TimerAdapter timerAdapter;

  public PagerServiceImpl() {

  }

  public PagerServiceImpl(EscalationService escalationService, PersistencePagerAdapter persistencePagerAdapter, TimerAdapter timerAdapter) {
    this.escalationService = escalationService;
    this.persistencePagerAdapter = persistencePagerAdapter;
    this.timerAdapter = timerAdapter;
  }

  /**
   * @inheritDoc
   * @param alertService Service alert to be processed
   */
  @Override
  public void notifyAlert(AlertService alertService) {
    MonitoredService monitoredService = getMonitoredService(alertService.getServiceId());
    if (isHealthy(monitoredService)) {
      sendNotificationAndChangeStatus(monitoredService, alertService.getMessage());
      sendTimeoutDelay(monitoredService.getId());
    }
  }

  /**
   * @inheritDoc
   * @param serviceId Service alert id to be notified the timeout ack
   */
  @Override
  public void notifyAckTimeout(int serviceId) {
    MonitoredService monitoredService = getMonitoredService(serviceId);
    if (!isAcknowledged(monitoredService) && !isHealthy(monitoredService) && !isMaxLevel(serviceId, monitoredService.getIdLevelNotified())) {
      sendNotificationAndChangeStatus(monitoredService, monitoredService.getAlertMessage());
      sendTimeoutDelay(monitoredService.getId());
    }
  }

  /**
   * @inheritDoc
   * @param serviceId Service alert id which will be marked as acknowledged
   */
  @Override
  public void notifyAck(int serviceId) {
    MonitoredService monitoredService = getMonitoredService(serviceId);
    monitoredService.setStatus(MonitoredServiceStatus.ACKNOWLEDGED);
    getPersistencePagerAdapter().saveMonitoredService(monitoredService);
  }

  /**
   * @inheritDoc
   * @param serviceId Service alert id which will be marked as healthy
   */
  @Override
  public void notifyHealthyEvent(int serviceId) {
    MonitoredService monitoredService = getMonitoredService(serviceId);
    monitoredService.setStatus(MonitoredServiceStatus.HEALTHY);
    monitoredService.setIdLevelNotified(null);
    monitoredService.setAlertMessage(null);
    getPersistencePagerAdapter().saveMonitoredService(monitoredService);
  }

  /**
   * It will user send to the time the Ack Timeout.
   * @param serviceId Service alert id for which will be set the ack timeout
   */
  private void sendTimeoutDelay(int serviceId) {
    getTimerAdapter().setAckTimeout(serviceId, ACK_TIMEOUT_MINUTES_VALUE);
  }

  /**
   * It will send the notification to the next level targets.
   * Also, it will set the right status {@link io.aircall.pagerservice.entities.MonitoredServiceStatus PENDING_ACK} with the alert message
   * @param monitoredService Monitored service which has the alert
   * @param message Alert message to be sent to the targets
   */
  private void sendNotificationAndChangeStatus(MonitoredService monitoredService, String message) {

    int levelToBeNotified = getLevelIdToBeNotified(monitoredService);

    notifyTargets(monitoredService, levelToBeNotified, message);

    monitoredService.setStatus(MonitoredServiceStatus.PENDING_ACK);
    monitoredService.setIdLevelNotified(levelToBeNotified);
    monitoredService.setAlertMessage(message);
    getPersistencePagerAdapter().saveMonitoredService(monitoredService);
  }

  /**
   * It will send the notification for each target.
   * @param monitoredService Monitored Service which has the alert
   * @param levelToBeNotified Target level which should receive the notifications
   * @param message Alert message that has to be sent to the targets
   * @throws IllegalArgumentException if no target is retrieved for the level specified
   */
  private void notifyTargets(MonitoredService monitoredService, int levelToBeNotified, String message){
    List<Target> targetsToBeNotified = getEscalationService().getTargetByServiceAndLevel(monitoredService.getId(), levelToBeNotified);

    if (targetsToBeNotified.isEmpty()) {
      throw new IllegalArgumentException("The target list to be notified for the serviceId " + monitoredService.getId() + " and levelId " + levelToBeNotified + " is not available.");
    }

    targetsToBeNotified.forEach(target -> target.sendNotification(message));
  }

  /**
   * It will check if the current monitored service level is the maximum level available for the service passed
   * @param serviceId Service id for which will be calculated the maximum level
   * @param currentLevelNotified Last level that was notified for the service passed
   * @return True is current level is the maximum or False if not or the current level notified is null
   */
  private boolean isMaxLevel(int serviceId, Integer currentLevelNotified){
    if(Objects.isNull(currentLevelNotified)){
      return false;
    }

    List<Level> levels = getEscalationService().getLevelsByService(serviceId);
    Level maxLevel = levels.stream().max(Comparator.comparing(Level::getId)).orElseThrow(NoSuchElementException::new);
    return currentLevelNotified >= maxLevel.getId();
  }

  /**
   * It will calculate the next level to be notified
   * @param monitoredService Monitored Service which has the alert
   * @return It will sum one to the current notified level. If the current one is null it will initialize the level
   */
  private int getLevelIdToBeNotified(MonitoredService monitoredService) {
    if (Objects.isNull(monitoredService.getIdLevelNotified())) {
      return 1;
    }
    return (monitoredService.getIdLevelNotified()) + 1;
  }

  private boolean isHealthy(MonitoredService monitoredService) {
    return monitoredService.getStatus().equals(MonitoredServiceStatus.HEALTHY);
  }

  private boolean isAcknowledged(MonitoredService monitoredService) {
    return monitoredService.getStatus().equals(MonitoredServiceStatus.ACKNOWLEDGED);
  }

  private MonitoredService getMonitoredService(int serviceId) {
    MonitoredService monitoredService = getPersistencePagerAdapter().getMonitoredServiceById(serviceId);

    if (Objects.isNull(monitoredService)) {
      throw new IllegalArgumentException("Monitored service with id " + serviceId + " not found");
    }
    return monitoredService;
  }

  private EscalationService getEscalationService() {
    if (Objects.isNull(this.escalationService)) {
      this.escalationService = new EscalationServiceImpl();
    }
    return this.escalationService;
  }

  private PersistencePagerAdapter getPersistencePagerAdapter() {
    if (Objects.isNull(this.persistencePagerAdapter)) {
      this.persistencePagerAdapter = new PersistencePagerAdapterImpl();
    }
    return this.persistencePagerAdapter;
  }

  private TimerAdapter getTimerAdapter() {
    if (Objects.isNull(this.timerAdapter)) {
      this.timerAdapter = new TimerAdapterImpl();
    }
    return this.timerAdapter;
  }
}
