package com.uncreated.civilized.entity.behaviour.worker;

import com.uncreated.civilized.entity.behaviour.BehaviourStateMachine;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class WorkStateMachine extends BehaviourStateMachine {
//   private boolean importDesired;
//   private boolean exportDesired;
//   private boolean hasWorkInputItemsInInventory;
//   private boolean hasWorkOutputItemsInInventory;
//   private boolean hasNonIdleWorkTask;

   public WorkStateMachine() {
      // this.hasNonIdleWorkTask = false;
      // this.importDesired = false;
      // this.exportDesired = false;
      // this.hasWorkInputItemsInInventory = false;
      // this.hasWorkOutputItemsInInventory = false;
   }
}
