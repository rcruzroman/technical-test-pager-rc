package io.aircall.escalation.adapters;

import io.aircall.escalation.entities.Level;
import io.aircall.escalation.entities.Target;

import java.util.List;

/**
 * It will contains the methods that can be used with the escalation policy service
 */
public interface EscalationPolicyAdapter {

  List<Target> getTargetByServiceAndLevel(int serviceId, int levelId);

  List<Level> getLevelsByService(int serviceId);
}
