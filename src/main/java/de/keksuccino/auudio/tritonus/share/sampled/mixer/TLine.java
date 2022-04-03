package de.keksuccino.auudio.tritonus.share.sampled.mixer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.sound.sampled.Control;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.LineEvent.Type;
import de.keksuccino.auudio.tritonus.share.TNotifier;

public abstract class TLine implements Line {

   private static final Control[] EMPTY_CONTROL_ARRAY = new Control[0];
   private Info m_info;
   private boolean m_bOpen;
   private List m_controls;
   private Set m_lineListeners;
   private TMixer m_mixer;


   protected TLine(TMixer mixer, Info info) {
      this.setLineInfo(info);
      this.setOpen(false);
      this.m_controls = new ArrayList();
      this.m_lineListeners = new HashSet();
      this.m_mixer = mixer;
   }

   protected TLine(TMixer mixer, Info info, Collection controls) {
      this(mixer, info);
      this.m_controls.addAll(controls);
   }

   protected TMixer getMixer() {
      return this.m_mixer;
   }

   public Info getLineInfo() {
      return this.m_info;
   }

   protected void setLineInfo(Info info) {

      synchronized(this) {
         this.m_info = info;
      }
   }

   public void open() throws LineUnavailableException {

      if(!this.isOpen()) {

         this.openImpl();
         if(this.getMixer() != null) {
            this.getMixer().registerOpenLine(this);
         }

         this.setOpen(true);
      }

   }

   protected void openImpl() throws LineUnavailableException {

   }

   public void close() {

      if(this.isOpen()) {

         if(this.getMixer() != null) {
            this.getMixer().unregisterOpenLine(this);
         }

         this.closeImpl();
         this.setOpen(false);
      }

   }

   protected void closeImpl() {

   }

   public boolean isOpen() {
      return this.m_bOpen;
   }

   protected void setOpen(boolean bOpen) {

      boolean bOldValue = this.isOpen();
      this.m_bOpen = bOpen;
      if(bOldValue != this.isOpen()) {
         if(this.isOpen()) {

            this.notifyLineEvent(Type.OPEN);
         } else {

            this.notifyLineEvent(Type.CLOSE);
         }
      }

   }

   protected void addControl(Control control) {
      List var2 = this.m_controls;
      synchronized(this.m_controls) {
         this.m_controls.add(control);
      }
   }

   protected void removeControl(Control control) {
      List var2 = this.m_controls;
      synchronized(this.m_controls) {
         this.m_controls.remove(control);
      }
   }

   public Control[] getControls() {
      List var1 = this.m_controls;
      synchronized(this.m_controls) {
         return (Control[])this.m_controls.toArray(EMPTY_CONTROL_ARRAY);
      }
   }

   public Control getControl(javax.sound.sampled.Control.Type controlType) {
      List var2 = this.m_controls;
      synchronized(this.m_controls) {
         Iterator it = this.m_controls.iterator();

         Control control;
         do {
            if(!it.hasNext()) {
               throw new IllegalArgumentException("no control of type " + controlType);
            }

            control = (Control)it.next();
         } while(!control.getType().equals(controlType));

         return control;
      }
   }

   public boolean isControlSupported(javax.sound.sampled.Control.Type controlType) {
      try {
         return this.getControl(controlType) != null;
      } catch (IllegalArgumentException var3) {

         return false;
      }
   }

   public void addLineListener(LineListener listener) {
      Set var2 = this.m_lineListeners;
      synchronized(this.m_lineListeners) {
         this.m_lineListeners.add(listener);
      }
   }

   public void removeLineListener(LineListener listener) {
      Set var2 = this.m_lineListeners;
      synchronized(this.m_lineListeners) {
         this.m_lineListeners.remove(listener);
      }
   }

   private Set getLineListeners() {
      Set var1 = this.m_lineListeners;
      synchronized(this.m_lineListeners) {
         return new HashSet(this.m_lineListeners);
      }
   }

   protected void notifyLineEvent(Type type) {
      this.notifyLineEvent(new LineEvent(this, type, -1L));
   }

   protected void notifyLineEvent(LineEvent event) {
      TNotifier.notifier.addEntry(event, this.getLineListeners());
   }

}
