package de.keksuccino.auudio.tritonus.share.sampled.convert;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import de.keksuccino.auudio.tritonus.share.TCircularBuffer;

public abstract class TAsynchronousFilteredAudioInputStream extends TAudioInputStream implements TCircularBuffer.Trigger {

   private static final int DEFAULT_BUFFER_SIZE = 327670;
   private static final int DEFAULT_MIN_AVAILABLE = 4096;
   private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
   protected TCircularBuffer m_circularBuffer;
   private int m_nMinAvailable;
   private byte[] m_abSingleByte;


   public TAsynchronousFilteredAudioInputStream(AudioFormat outputFormat, long lLength) {
      this(outputFormat, lLength, 327670, 4096);
   }

   public TAsynchronousFilteredAudioInputStream(AudioFormat outputFormat, long lLength, int nBufferSize, int nMinAvailable) {
      super(new ByteArrayInputStream(EMPTY_BYTE_ARRAY), outputFormat, lLength);

      this.m_circularBuffer = new TCircularBuffer(nBufferSize, false, true, this);
      this.m_nMinAvailable = nMinAvailable;

   }

   protected TCircularBuffer getCircularBuffer() {
      return this.m_circularBuffer;
   }

   protected boolean writeMore() {
      return this.getCircularBuffer().availableWrite() > this.m_nMinAvailable;
   }

   public int read() throws IOException {
      boolean nByte = true;
      if(this.m_abSingleByte == null) {
         this.m_abSingleByte = new byte[1];
      }

      int nReturn = this.read(this.m_abSingleByte);
      int nByte1;
      if(nReturn == -1) {
         nByte1 = -1;
      } else {
         nByte1 = this.m_abSingleByte[0] & 255;
      }

      return nByte1;
   }

   public int read(byte[] abData) throws IOException {

      int nRead = this.read(abData, 0, abData.length);

      return nRead;
   }

   public int read(byte[] abData, int nOffset, int nLength) throws IOException {

      int nRead = this.m_circularBuffer.read(abData, nOffset, nLength);

      return nRead;
   }

   public long skip(long lSkip) throws IOException {
      for(long lSkipped = 0L; lSkipped < lSkip; ++lSkipped) {
         int nReturn = this.read();
         if(nReturn == -1) {
            return lSkipped;
         }
      }

      return lSkip;
   }

   public int available() throws IOException {
      return this.m_circularBuffer.availableRead();
   }

   public void close() throws IOException {
      this.m_circularBuffer.close();
   }

   public boolean markSupported() {
      return false;
   }

   public void mark(int nReadLimit) {}

   public void reset() throws IOException {
      throw new IOException("mark not supported");
   }

}
