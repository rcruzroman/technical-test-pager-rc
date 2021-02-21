package io.aircall.escalation.entities;

import java.util.List;

/**
 * Entity that represent the information that will manage the escalation policy service
 */
public class EscalationPolicy {

  private List<Level> levels;

  public List<Level> getLevels() {
    return levels;
  }

  public void setLevels(List<Level> levels) {
    this.levels = levels;
  }
}
