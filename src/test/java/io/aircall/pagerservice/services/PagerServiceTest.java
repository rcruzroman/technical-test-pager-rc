package io.aircall.pagerservice.services;

import io.aircall.escalation.entities.Level;
import io.aircall.escalation.entities.TargetEmail;
import io.aircall.escalation.entities.TargetSMS;
import io.aircall.escalation.services.EscalationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import io.aircall.pagerservice.FakeFactory;
import io.aircall.pagerservice.adapters.AlertingAdapter;
import io.aircall.pagerservice.adapters.PersistencePagerAdapter;
import io.aircall.pagerservice.adapters.TimerAdapter;
import io.aircall.pagerservice.adapters.impl.AlertingAdapterImpl;
import io.aircall.pagerservice.adapters.impl.ConsoleAdapterImpl;
import io.aircall.pagerservice.adapters.impl.TimerAdapterImpl;
import io.aircall.pagerservice.entities.MonitoredService;
import io.aircall.pagerservice.entities.MonitoredServiceStatus;
import io.aircall.pagerservice.services.impl.PagerServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

public class PagerServiceTest {

  private static final int SERVICE_ID = 1;
  private static final Integer FIRST_LEVEL = 1;
  private static final int TIMEOUT_ACK_MINUTES = 15;
  private static final String MESSAGE = "TEST MESSAGE 1";

  PersistencePagerAdapter persistencePagerAdapter;
  PagerService pagerService;
  EscalationService escalationService;
  TimerAdapter timerAdapter;
  AlertingAdapter alertingAdapter;
  TargetEmail targetEmail;
  TargetSMS targetSMS;

  @Before
  public void before() {
    targetSMS = mock(TargetSMS.class);
    targetEmail = mock(TargetEmail.class);
    persistencePagerAdapter = mock(PersistencePagerAdapter.class);
    escalationService = mock(EscalationService.class);
    timerAdapter = mock(TimerAdapter.class);
    pagerService = new PagerServiceImpl(escalationService, persistencePagerAdapter, timerAdapter);
    alertingAdapter = new AlertingAdapterImpl(pagerService);

    when(escalationService.getTargetByServiceAndLevel(anyInt(), anyInt())).thenReturn(FakeFactory.FakeTarget.getListTargetWithEmailAndSMS(targetEmail, targetSMS));
    when(escalationService.getLevelsByService(SERVICE_ID)).thenReturn(FakeFactory.FakeLevel.getListLevels(Arrays.asList(targetEmail, targetSMS)));
  }

  /**
   * Scenario 1:
   * Given a Monitored Service in a Healthy State,
   * when the Pager receives an Alert related to this Monitored Service,
   * then the Monitored Service becomes Unhealthy,
   * the Pager notifies all targets of the first level of the io.aircall.escalation policy,
   * and sets a 15-minutes acknowledgement delay
   */
  @Test
  public void receiveAlert() {
    MonitoredService monitoredService = FakeFactory.FakeMonitoredService.getMonitedServiceHealthyStatus(SERVICE_ID);
    when(persistencePagerAdapter.getMonitoredServiceById(Mockito.anyInt())).thenReturn(monitoredService);
    when(escalationService.getTargetByServiceAndLevel(SERVICE_ID, FIRST_LEVEL)).thenReturn(FakeFactory.FakeTarget.getListTargetWithEmailAndSMS(targetEmail, targetSMS));

    alertingAdapter.sendAlertToPager(FakeFactory.FakeAlert.getFakeAlertService(SERVICE_ID, MESSAGE));

    ArgumentCaptor<MonitoredService> argumentPersistenceAdapter = ArgumentCaptor.forClass(MonitoredService.class);
    verify(persistencePagerAdapter).saveMonitoredService(argumentPersistenceAdapter.capture());
    Assert.assertEquals(SERVICE_ID, argumentPersistenceAdapter.getValue().getId());
    Assert.assertEquals(MonitoredServiceStatus.PENDING_ACK, argumentPersistenceAdapter.getValue().getStatus());
    Assert.assertEquals(FIRST_LEVEL, argumentPersistenceAdapter.getValue().getIdLevelNotified());
    Assert.assertEquals(MESSAGE, argumentPersistenceAdapter.getValue().getAlertMessage());

    verify(targetEmail).sendNotification(MESSAGE);
    verify(targetSMS).sendNotification(MESSAGE);

    verify(timerAdapter).setAckTimeout(SERVICE_ID, TIMEOUT_ACK_MINUTES);
  }

  /**
   * Scenario 2:
   * Given a Monitored Service in an Unhealthy State,
   * the corresponding Alert is not Acknowledged
   * and the last level has not been notified,
   * when the Pager receives the Acknowledgement Timeout,
   * then the Pager notifies all targets of the next level of the io.aircall.escalation policy
   * and sets a 15-minutes acknowledgement delay.
   */
  @Test
  public void receiveAckTimeout() {
    MonitoredService monitoredService = FakeFactory.FakeMonitoredService.getMonitedServiceUnHealthyStatus(SERVICE_ID, MESSAGE);
    Integer levelToBeNotified = monitoredService.getIdLevelNotified() + 1;
    when(persistencePagerAdapter.getMonitoredServiceById(Mockito.anyInt())).thenReturn(monitoredService);
    when(escalationService.getTargetByServiceAndLevel(SERVICE_ID, levelToBeNotified)).thenReturn(FakeFactory.FakeTarget.getListTargetWithEmailAndSMS(targetEmail, targetSMS));

    new TimerAdapterImpl(pagerService).ackExpired(SERVICE_ID);

    ArgumentCaptor<MonitoredService> argumentPersistenceAdapter = ArgumentCaptor.forClass(MonitoredService.class);
    verify(persistencePagerAdapter).saveMonitoredService(argumentPersistenceAdapter.capture());
    Assert.assertEquals(SERVICE_ID, argumentPersistenceAdapter.getValue().getId());
    Assert.assertEquals(MonitoredServiceStatus.PENDING_ACK, argumentPersistenceAdapter.getValue().getStatus());
    Assert.assertEquals(levelToBeNotified, argumentPersistenceAdapter.getValue().getIdLevelNotified());
    Assert.assertEquals(MESSAGE, argumentPersistenceAdapter.getValue().getAlertMessage());

    verify(targetEmail).sendNotification(MESSAGE);
    verify(targetSMS).sendNotification(MESSAGE);

    verify(timerAdapter).setAckTimeout(SERVICE_ID, TIMEOUT_ACK_MINUTES);
  }

  /**
   * Scenario 3:
   * Given a Monitored Service in an Unhealthy State
   * when the Pager receives the Acknowledgement
   * and later receives the Acknowledgement Timeout,
   * then the Pager doesn't notify any Target
   * and doesn't set an acknowledgement delay.
   */
  @Test
  public void receiveAckTimeoutToUnhealthyService() {
    MonitoredService monitoredService = FakeFactory.FakeMonitoredService.getMonitedServiceUnHealthyStatus(SERVICE_ID, MESSAGE);
    when(persistencePagerAdapter.getMonitoredServiceById(SERVICE_ID)).thenReturn(monitoredService);

    new ConsoleAdapterImpl(pagerService).sendAck(SERVICE_ID);

    monitoredService = FakeFactory.FakeMonitoredService.getMonitedServiceUnHealthyStatusWithACK();
    when(persistencePagerAdapter.getMonitoredServiceById(SERVICE_ID)).thenReturn(monitoredService);

    new TimerAdapterImpl(pagerService).ackExpired(SERVICE_ID);

    verify(targetEmail, never()).sendNotification(MESSAGE);
    verify(targetSMS, never()).sendNotification(MESSAGE);

    verify(timerAdapter, never()).setAckTimeout(SERVICE_ID, TIMEOUT_ACK_MINUTES);
  }

  /**
   * Scenario 4:
   * Given a Monitored Service in an Unhealthy State,
   * when the Pager receives an Alert related to this Monitored Service,
   * then the Pager doesn’t notify any Target
   * and doesn’t set an acknowledgement delay
   */
  @Test
  public void receiveAlertToUnhealthyService() {
    MonitoredService monitoredService = FakeFactory.FakeMonitoredService.getMonitedServiceUnHealthyStatus(SERVICE_ID, MESSAGE);
    when(persistencePagerAdapter.getMonitoredServiceById(SERVICE_ID)).thenReturn(monitoredService);

    alertingAdapter.sendAlertToPager(FakeFactory.FakeAlert.getFakeAlertService(SERVICE_ID, MESSAGE));

    verify(targetEmail, never()).sendNotification(MESSAGE);
    verify(targetSMS, never()).sendNotification(MESSAGE);

    verify(timerAdapter, never()).setAckTimeout(SERVICE_ID, TIMEOUT_ACK_MINUTES);

  }

  /**
   * Scenario 5:
   * Given a Monitored Service in an Unhealthy State,
   * when the Pager receives a Healthy event related to this Monitored Service
   * and later receives the Acknowledgement Timeout,
   * then the Monitored Service becomes Healthy,
   * the Pager doesn’t notify any Target
   * and doesn’t set an acknowledgement delay
   */
  @Test
  public void notifyHealthyEventAndAckTimeout() {
    MonitoredService monitoredService = FakeFactory.FakeMonitoredService.getMonitedServiceUnHealthyStatus(SERVICE_ID, MESSAGE);
    when(persistencePagerAdapter.getMonitoredServiceById(SERVICE_ID)).thenReturn(monitoredService);

    new ConsoleAdapterImpl(pagerService).sendHealthyEvent(SERVICE_ID);

    ArgumentCaptor<MonitoredService> argumentPersistenceAdapter = ArgumentCaptor.forClass(MonitoredService.class);
    verify(persistencePagerAdapter).saveMonitoredService(argumentPersistenceAdapter.capture());
    Assert.assertEquals(SERVICE_ID, argumentPersistenceAdapter.getValue().getId());
    Assert.assertEquals(MonitoredServiceStatus.HEALTHY, argumentPersistenceAdapter.getValue().getStatus());
    Assert.assertNull(argumentPersistenceAdapter.getValue().getIdLevelNotified());
    Assert.assertNull(argumentPersistenceAdapter.getValue().getAlertMessage());

    monitoredService = FakeFactory.FakeMonitoredService.getMonitedServiceHealthyStatus(SERVICE_ID);
    when(persistencePagerAdapter.getMonitoredServiceById(SERVICE_ID)).thenReturn(monitoredService);
    new TimerAdapterImpl(pagerService).ackExpired(SERVICE_ID);

    verify(targetEmail, never()).sendNotification(MESSAGE);
    verify(targetSMS, never()).sendNotification(MESSAGE);

    verify(timerAdapter, never()).setAckTimeout(SERVICE_ID, TIMEOUT_ACK_MINUTES);
  }

  /**
   * Scenario 6:
   * Given a monitored service in an Unhealthy State,
   * the corresponding qlert is not Acknowledged
   * and the last level has been notified,
   * when the Pager receives the Acknowledgement Timeout,
   * the Pager doesn’t notify any Target
   * and doesn’t set an acknowledgement delay
   */
  @Test
  public void notifyAckTimeoutWhenMaxLevelWasAlreadyNotified() {
    List<Level> fakeListLevels = FakeFactory.FakeLevel.getListLevels(Arrays.asList(targetEmail, targetSMS));
    MonitoredService monitoredService = FakeFactory.FakeMonitoredService.getMonitedServiceUnHealthyStatusWithLevelNotified(SERVICE_ID, MESSAGE, fakeListLevels.size());
    when(persistencePagerAdapter.getMonitoredServiceById(SERVICE_ID)).thenReturn(monitoredService);
    when(escalationService.getLevelsByService(SERVICE_ID)).thenReturn(fakeListLevels);

    new TimerAdapterImpl(pagerService).ackExpired(SERVICE_ID);

    verify(targetEmail, never()).sendNotification(MESSAGE);
    verify(targetSMS, never()).sendNotification(MESSAGE);
    verify(timerAdapter, never()).setAckTimeout(SERVICE_ID, TIMEOUT_ACK_MINUTES);
  }

}
