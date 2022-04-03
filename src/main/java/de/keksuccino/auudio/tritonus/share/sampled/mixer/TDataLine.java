package de.keksuccino.auudio.tritonus.share.sampled.mixer;

import java.util.Collection;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;

public abstract class TDataLine extends TLine implements DataLine {

   private static final int DEFAULT_BUFFER_SIZE = 128000;
   private AudioFormat m_format;
   private int m_nBufferSize;
   private boolean m_bRunning;


   public TDataLine(TMixer mixer, DataLine.Info info) {
      super(mixer, info);
      this.init(info);
   }

   public TDataLine(TMixer mixer, DataLine.Info info, Collection controls) {
      super(mixer, info, controls);
      this.init(info);
   }

   private void init(DataLine.Info info) {
      this.m_format = null;
      this.m_nBufferSize = -1;
      this.setRunning(false);
   }

   public void start() {

      this.setRunning(true);
   }

   public void stop() {

      this.setRunning(false);
   }

   public boolean isRunning() {
      return this.m_bRunning;
   }

   protected void setRunning(boolean bRunning) {
      boolean bOldValue = this.isRunning();
      this.m_bRunning = bRunning;
      if(bOldValue != this.isRunning()) {
         if(this.isRunning()) {
            this.startImpl();
            this.notifyLineEvent(Type.START);
         } else {
            this.stopImpl();
            this.notifyLineEvent(Type.STOP);
         }
      }

   }

   protected void startImpl() {}

   protected void stopImpl() {}

   public boolean isActive() {
      return this.isRunning();
   }

   public AudioFormat getFormat() {
      return this.m_format;
   }

   protected void setFormat(AudioFormat format) {

      this.m_format = format;
   }

   public int getBufferSize() {
      return this.m_nBufferSize;
   }

   protected void setBufferSize(int nBufferSize) {

      this.m_nBufferSize = nBufferSize;
   }

   public int getFramePosition() {
      return -1;
   }

   public long getLongFramePosition() {
      return -1L;
   }

   public long getMicrosecondPosition() {
      return (long)((float)this.getFramePosition() * this.getFormat().getFrameRate() * 1000000.0F);
   }

   public float getLevel() {
      return -1.0F;
   }

   protected void checkOpen() {
      if(this.getFormat() == null) {
         throw new IllegalStateException("format must be specified");
      } else {
         if(this.getBufferSize() == -1) {
            this.setBufferSize(this.getDefaultBufferSize());
         }

      }
   }

   protected int getDefaultBufferSize() {
      return 128000;
   }

   protected void notifyLineEvent(Type type) {
      this.notifyLineEvent(new LineEvent(this, type, (long)this.getFramePosition()));
   }
}
