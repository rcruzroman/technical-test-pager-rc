package io.aircall.escalation.services.impl;

import io.aircall.escalation.adapters.EscalationPolicyAdapter;
import io.aircall.escalation.entities.Level;
import io.aircall.escalation.entities.Target;
import io.aircall.escalation.services.EscalationService;

import java.util.List;

public class EscalationServiceImpl implements EscalationService {

  private EscalationPolicyAdapter escalationPolicyAdapter;

  /**
   * @inheritDoc
   * @param serviceId Monitored service id that will be used to get the target
   * @param levelId Level id that will be used to get the target
   * @return
   */
  @Override
  public List<Target> getTargetByServiceAndLevel(int serviceId, int levelId) {
    return escalationPolicyAdapter.getTargetByServiceAndLevel(serviceId, levelId);
  }

  /**
   * @inheritDoc
   * @param serviceId Monitored service id that will used to get the levels
   * @return
   */
  @Override
  public List<Level> getLevelsByService(int serviceId) {
    return escalationPolicyAdapter.getLevelsByService(serviceId);
  }
}
