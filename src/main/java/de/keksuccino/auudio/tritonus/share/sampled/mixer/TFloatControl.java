package de.keksuccino.auudio.tritonus.share.sampled.mixer;

import javax.sound.sampled.FloatControl;

public class TFloatControl extends FloatControl implements TControllable {

   private TControlController m_controller;


   public TFloatControl(Type type, float fMinimum, float fMaximum, float fPrecision, int nUpdatePeriod, float fInitialValue, String strUnits) {
      super(type, fMinimum, fMaximum, fPrecision, nUpdatePeriod, fInitialValue, strUnits);

      this.m_controller = new TControlController();

   }

   public TFloatControl(Type type, float fMinimum, float fMaximum, float fPrecision, int nUpdatePeriod, float fInitialValue, String strUnits, String strMinLabel, String strMidLabel, String strMaxLabel) {
      super(type, fMinimum, fMaximum, fPrecision, nUpdatePeriod, fInitialValue, strUnits, strMinLabel, strMidLabel, strMaxLabel);

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
