package de.keksuccino.auudio.tritonus.share.sampled.mixer;

public class TControlController implements TControllable {

   private TCompoundControl m_parentControl;


   public void setParentControl(TCompoundControl compoundControl) {
      this.m_parentControl = compoundControl;
   }

   public TCompoundControl getParentControl() {
      return this.m_parentControl;
   }

   public void commit() {

      if(this.getParentControl() != null) {
         this.getParentControl().commit();
      }

   }
}
