package io.aircall.escalation.entities;

import java.util.List;

/**
 * Entity that will represent the different levels that a monitored service can have
 */
public class Level {

  private int id;
  private List<Target> targets;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public List<Target> getTargets() {
    return targets;
  }

  public void setTargets(List<Target> targets) {
    this.targets = targets;
  }
}
