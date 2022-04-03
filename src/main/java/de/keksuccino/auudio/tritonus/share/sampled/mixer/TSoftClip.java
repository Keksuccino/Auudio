package de.keksuccino.auudio.tritonus.share.sampled.mixer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.sound.sampled.*;

public class TSoftClip extends TClip implements Runnable {

   private static final int BUFFER_SIZE = 16384;
   private SourceDataLine m_line;
   private byte[] m_abClip;
   private int m_nRepeatCount;
   private Thread m_thread;


   public TSoftClip(Mixer mixer, AudioFormat format) throws LineUnavailableException {
      super((DataLine.Info)null);
      DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
      this.m_line = (SourceDataLine)AudioSystem.getLine(info);
   }

   public void open(AudioInputStream audioInputStream) throws LineUnavailableException, IOException {
      AudioFormat audioFormat = audioInputStream.getFormat();
      this.setFormat(audioFormat);
      int nFrameSize = audioFormat.getFrameSize();
      if(nFrameSize < 1) {
         throw new IllegalArgumentException("frame size must be positive");
      } else {

         byte[] abData = new byte[16384];
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         int nBytesRead = 0;

         while(nBytesRead != -1) {
            try {
               nBytesRead = audioInputStream.read(abData, 0, abData.length);
            } catch (IOException var8) {
            }

            if(nBytesRead >= 0) {

               baos.write(abData, 0, nBytesRead);
            }
         }

         this.m_abClip = baos.toByteArray();
         this.setBufferSize(this.m_abClip.length);
         this.m_line.open(this.getFormat());
      }
   }

   public int getFrameLength() {
      return this.isOpen()?this.getBufferSize() / this.getFormat().getFrameSize():-1;
   }

   public long getMicrosecondLength() {
      return this.isOpen()?(long)((float)this.getFrameLength() * this.getFormat().getFrameRate() * 1000000.0F):-1L;
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

      this.m_nRepeatCount = nCount;
      this.m_thread = new Thread(this);
      this.m_thread.start();
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

   public void run() {
      while(this.m_nRepeatCount >= 0) {
         this.m_line.write(this.m_abClip, 0, this.m_abClip.length);
         --this.m_nRepeatCount;
      }

   }
}
