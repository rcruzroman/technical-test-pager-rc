package io.aircall.pagerservice;

import io.aircall.escalation.entities.*;
import io.aircall.pagerservice.entities.AlertService;
import io.aircall.pagerservice.entities.MonitoredService;
import io.aircall.pagerservice.entities.MonitoredServiceStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to manage the creation of fake entities for mocking
 */
public class FakeFactory {

  public static class FakeMonitoredService {

    public static MonitoredService getMonitedServiceHealthyStatus(int serviceId) {
      MonitoredService monitoredService = new MonitoredService();
      monitoredService.setId(serviceId);
      monitoredService.setStatus(MonitoredServiceStatus.HEALTHY);
      return monitoredService;
    }

    public static MonitoredService getMonitedServiceUnHealthyStatus(int serviceId, String message) {
      MonitoredService monitoredService = new MonitoredService();
      monitoredService.setId(serviceId);
      monitoredService.setStatus(MonitoredServiceStatus.PENDING_ACK);
      monitoredService.setIdLevelNotified(1);
      monitoredService.setAlertMessage(message);
      return monitoredService;
    }

    public static MonitoredService getMonitedServiceUnHealthyStatusWithLevelNotified(int serviceId, String message, int levelNotified) {
      MonitoredService monitoredService = new MonitoredService();
      monitoredService.setId(serviceId);
      monitoredService.setStatus(MonitoredServiceStatus.PENDING_ACK);
      monitoredService.setIdLevelNotified(levelNotified);
      monitoredService.setAlertMessage(message);
      return monitoredService;
    }

    public static MonitoredService getMonitedServiceUnHealthyStatusWithACK() {
      MonitoredService monitoredService = new MonitoredService();
      monitoredService.setId(1);
      monitoredService.setStatus(MonitoredServiceStatus.ACKNOWLEDGED);
      monitoredService.setIdLevelNotified(2);
      return monitoredService;
    }
  }

  public static class FakeTarget {

    public static final List<Integer> AVAILABILITY_HOURS = List.of(12,17,20);
    public static final LocalDateTime CURRENT_TIME_OUT_OF_AVAILABILITY_HOURS = LocalDateTime.of(2021,01,01,11,00,00);
    public static final LocalDateTime CURRENT_TIME_WITHIN_AVAILABILITY_HOURS = LocalDateTime.of(2021,01,01,12,00,00);

    public static List<Target> getListTargetWithEmailAndSMS(TargetEmail targetEmail, TargetSMS targetSMS) {
      List<Target> result = new ArrayList<>();
      result.add(targetEmail);
      result.add(targetSMS);
      return result;
    }
  }

  public static class FakeLevel {
    public static List<Level> getListLevels(List<Target> targets) {
      List<Level> result = new ArrayList<>();
      result.add(getLevel(1, targets));
      result.add(getLevel(2, targets));
      result.add(getLevel(3, targets));
      return result;
    }

    public static Level getLevel(int levelId, List<Target> targets) {
      Level level = new Level();
      level.setId(levelId);
      level.setTargets(targets);
      return level;
    }
  }

  public static class FakeAlert {
    public static AlertService getFakeAlertService(int serviceId, String message){
      AlertService alertService = new AlertService();
      alertService.setServiceId(serviceId);
      alertService.setMessage(message);
      return alertService;
    }
  }
}
