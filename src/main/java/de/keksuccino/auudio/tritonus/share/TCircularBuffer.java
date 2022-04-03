package de.keksuccino.auudio.tritonus.share;

public class TCircularBuffer {

   private boolean m_bBlockingRead;
   private boolean m_bBlockingWrite;
   private byte[] m_abData;
   private int m_nSize;
   private long m_lReadPos;
   private long m_lWritePos;
   private TCircularBuffer.Trigger m_trigger;
   private boolean m_bOpen;


   public TCircularBuffer(int nSize, boolean bBlockingRead, boolean bBlockingWrite, TCircularBuffer.Trigger trigger) {
      this.m_bBlockingRead = bBlockingRead;
      this.m_bBlockingWrite = bBlockingWrite;
      this.m_nSize = nSize;
      this.m_abData = new byte[this.m_nSize];
      this.m_lReadPos = 0L;
      this.m_lWritePos = 0L;
      this.m_trigger = trigger;
      this.m_bOpen = true;
   }

   public void close() {
      this.m_bOpen = false;
   }

   private boolean isOpen() {
      return this.m_bOpen;
   }

   public int availableRead() {
      return (int)(this.m_lWritePos - this.m_lReadPos);
   }

   public int availableWrite() {
      return this.m_nSize - this.availableRead();
   }

   private int getReadPos() {
      return (int)(this.m_lReadPos % (long)this.m_nSize);
   }

   private int getWritePos() {
      return (int)(this.m_lWritePos % (long)this.m_nSize);
   }

   public int read(byte[] abData) {
      return this.read(abData, 0, abData.length);
   }

   public int read(byte[] abData, int nOffset, int nLength) {

      if(!this.isOpen()) {
         if(this.availableRead() <= 0) {

            return -1;
         }

         nLength = Math.min(nLength, this.availableRead());
      }

      synchronized(this) {
         if(this.m_trigger != null && this.availableRead() < nLength) {

            this.m_trigger.execute();
         }

         if(!this.m_bBlockingRead) {
            nLength = Math.min(this.availableRead(), nLength);
         }

         int nRemainingBytes = nLength;

         while(nRemainingBytes > 0) {
            while(this.availableRead() == 0) {
               try {
                  this.wait();
               } catch (InterruptedException var9) {
               }
            }

            int nToRead;
            for(int nAvailable = Math.min(this.availableRead(), nRemainingBytes); nAvailable > 0; nRemainingBytes -= nToRead) {
               nToRead = Math.min(nAvailable, this.m_nSize - this.getReadPos());
               System.arraycopy(this.m_abData, this.getReadPos(), abData, nOffset, nToRead);
               this.m_lReadPos += (long)nToRead;
               nOffset += nToRead;
               nAvailable -= nToRead;
            }

            this.notifyAll();
         }

         return nLength;
      }
   }

   public int write(byte[] abData) {
      return this.write(abData, 0, abData.length);
   }

   public int write(byte[] abData, int nOffset, int nLength) {

      synchronized(this) {

         if(!this.m_bBlockingWrite) {
            nLength = Math.min(this.availableWrite(), nLength);
         }

         int nRemainingBytes = nLength;

         while(nRemainingBytes > 0) {
            while(this.availableWrite() == 0) {
               try {
                  this.wait();
               } catch (InterruptedException var9) {
               }
            }

            int nToWrite;
            for(int nAvailable = Math.min(this.availableWrite(), nRemainingBytes); nAvailable > 0; nRemainingBytes -= nToWrite) {
               nToWrite = Math.min(nAvailable, this.m_nSize - this.getWritePos());
               System.arraycopy(abData, nOffset, this.m_abData, this.getWritePos(), nToWrite);
               this.m_lWritePos += (long)nToWrite;
               nOffset += nToWrite;
               nAvailable -= nToWrite;
            }

            this.notifyAll();
         }

         return nLength;
      }
   }

   private void dumpInternalState() {

   }

   public interface Trigger {

      void execute();
   }
}
