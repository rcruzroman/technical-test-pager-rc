package io.aircall.escalation.services;

import io.aircall.escalation.entities.Level;
import io.aircall.escalation.entities.Target;

import java.util.List;

/**
 * Service with the logic to manage the escalation policy service
 */
public interface EscalationService {

  /**
   * It will get the target by service and level
   * @param serviceId Monitored service id that will be used to get the target
   * @param levelId Level id that will be used to get the target
   * @return List of targets
   */
  List<Target> getTargetByServiceAndLevel(int serviceId, int levelId);

  /**
   * It will get all the level per service monitored
   * @param serviceId Monitored service id that will used to get the levels
   * @return List of levels
   */
  List<Level> getLevelsByService(int serviceId);

}
