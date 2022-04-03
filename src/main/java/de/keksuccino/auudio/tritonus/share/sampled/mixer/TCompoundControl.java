package de.keksuccino.auudio.tritonus.share.sampled.mixer;

import javax.sound.sampled.CompoundControl;
import javax.sound.sampled.Control;

public class TCompoundControl extends CompoundControl implements TControllable {

   private TControlController m_controller;


   public TCompoundControl(Type type, Control[] aMemberControls) {
      super(type, aMemberControls);

      this.m_controller = new TControlController();

   }

   public void setParentControl(TCompoundControl compoundControl) {
      this.m_controller.setParentControl(compoundControl);
   }

   public TCompoundControl getParentControl() {
      return this.m_controller.getParentControl();
   }

   public void commit() {
      this.m_controller.commit();
   }
}
