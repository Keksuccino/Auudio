package de.keksuccino.auudio.tritonus.share.sampled.mixer;

import javax.sound.sampled.BooleanControl;

public class TBooleanControl extends BooleanControl implements TControllable {

   private TControlController m_controller;


   public TBooleanControl(Type type, boolean bInitialValue) {
      this(type, bInitialValue, (TCompoundControl)null);
   }

   public TBooleanControl(Type type, boolean bInitialValue, TCompoundControl parentControl) {
      super(type, bInitialValue);

      this.m_controller = new TControlController();

   }

   public TBooleanControl(Type type, boolean bInitialValue, String strTrueStateLabel, String strFalseStateLabel) {
      this(type, bInitialValue, strTrueStateLabel, strFalseStateLabel, (TCompoundControl)null);
   }

   public TBooleanControl(Type type, boolean bInitialValue, String strTrueStateLabel, String strFalseStateLabel, TCompoundControl parentControl) {
      super(type, bInitialValue, strTrueStateLabel, strFalseStateLabel);

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
