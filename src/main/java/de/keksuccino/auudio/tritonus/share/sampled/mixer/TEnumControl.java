package de.keksuccino.auudio.tritonus.share.sampled.mixer;

import javax.sound.sampled.EnumControl;

public class TEnumControl extends EnumControl implements TControllable {

   private TControlController m_controller;


   public TEnumControl(Type type, Object[] aValues, Object value) {
      super(type, aValues, value);

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
