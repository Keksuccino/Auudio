package de.keksuccino.auudio.tritonus.share.sampled.mixer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import javax.sound.sampled.*;

public class TClip extends TDataLine implements Clip {

   public TClip(DataLine.Info info) {
      super((TMixer)null, info);
   }

   public TClip(DataLine.Info info, Collection controls) {
      super((TMixer)null, info, controls);
   }

   public void open(AudioFormat audioFormat, byte[] abData, int nOffset, int nLength) throws LineUnavailableException {
      ByteArrayInputStream bais = new ByteArrayInputStream(abData, nOffset, nLength);
      AudioInputStream audioInputStream = new AudioInputStream(bais, audioFormat, -1L);

      try {
         this.open(audioInputStream);
      } catch (IOException var8) {

         throw new LineUnavailableException("IOException occured");
      }
   }

   public void open(AudioInputStream audioInputStream) throws LineUnavailableException, IOException {
      AudioFormat audioFormat = audioInputStream.getFormat();
      DataLine.Info info = new DataLine.Info(Clip.class, audioFormat, -1);
      this.setLineInfo(info);
   }

   public int getFrameLength() {
      return -1;
   }

   public long getMicrosecondLength() {
      return -1L;
   }

   public void setFramePosition(int nPosition) {}

   public void setMicrosecondPosition(long lPosition) {}

   public int getFramePosition() {
      return -1;
   }

   public long getMicrosecondPosition() {
      return -1L;
   }

   public void setLoopPoints(int nStart, int nEnd) {}

   public void loop(int nCount) {

      if(nCount == 0) {
      }

   }

   public void flush() {}

   public void drain() {}

   public void close() {}

   public void open() {}

   public void start() {

      this.loop(0);
   }

   public void stop() {}

   public int available() {
      return -1;
   }
}
