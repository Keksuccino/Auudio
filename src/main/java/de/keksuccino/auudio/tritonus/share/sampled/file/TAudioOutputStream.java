package de.keksuccino.auudio.tritonus.share.sampled.file;

import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import de.keksuccino.auudio.tritonus.share.sampled.AudioUtils;
import de.keksuccino.auudio.tritonus.share.sampled.TConversionTool;

public abstract class TAudioOutputStream implements AudioOutputStream {

   private AudioFormat m_audioFormat;
   private long m_lLength;
   private long m_lCalculatedLength;
   private TDataOutputStream m_dataOutputStream;
   private boolean m_bDoBackPatching;
   private boolean m_bHeaderWritten;
   private boolean m_doSignConversion;
   private boolean m_doEndianConversion;


   protected TAudioOutputStream(AudioFormat audioFormat, long lLength, TDataOutputStream dataOutputStream, boolean bDoBackPatching) {
      this.m_audioFormat = audioFormat;
      this.m_lLength = lLength;
      this.m_lCalculatedLength = 0L;
      this.m_dataOutputStream = dataOutputStream;
      this.m_bDoBackPatching = bDoBackPatching;
      this.m_bHeaderWritten = false;
   }

   protected void requireSign8bit(boolean signed) {
      if(this.m_audioFormat.getSampleSizeInBits() == 8 && AudioUtils.isPCM(this.m_audioFormat)) {
         boolean si = this.m_audioFormat.getEncoding().equals(Encoding.PCM_SIGNED);
         this.m_doSignConversion = signed != si;
      }

   }

   protected void requireEndianness(boolean bigEndian) {
      int ssib = this.m_audioFormat.getSampleSizeInBits();
      if((ssib == 16 || ssib == 24 || ssib == 32) && AudioUtils.isPCM(this.m_audioFormat)) {
         this.m_doEndianConversion = bigEndian != this.m_audioFormat.isBigEndian();
      }

   }

   public AudioFormat getFormat() {
      return this.m_audioFormat;
   }

   public long getLength() {
      return this.m_lLength;
   }

   public long getCalculatedLength() {
      return this.m_lCalculatedLength;
   }

   protected TDataOutputStream getDataOutputStream() {
      return this.m_dataOutputStream;
   }

   private void handleImplicitConversions(byte[] abData, int nOffset, int nLength) {
      if(this.m_doSignConversion) {
         TConversionTool.convertSign8(abData, nOffset, nLength);
      }

      if(this.m_doEndianConversion) {
         switch(this.m_audioFormat.getSampleSizeInBits()) {
         case 16:
            TConversionTool.swapOrder16(abData, nOffset, nLength / 2);
            break;
         case 24:
            TConversionTool.swapOrder24(abData, nOffset, nLength / 3);
            break;
         case 32:
            TConversionTool.swapOrder32(abData, nOffset, nLength / 4);
         }
      }

   }

   public int write(byte[] abData, int nOffset, int nLength) throws IOException {

      if(!this.m_bHeaderWritten) {
         this.writeHeader();
         this.m_bHeaderWritten = true;
      }

      long lTotalLength = this.getLength();
      if(lTotalLength != -1L && this.m_lCalculatedLength + (long)nLength > lTotalLength) {

         nLength = (int)(lTotalLength - this.m_lCalculatedLength);
         if(nLength < 0) {
            nLength = 0;
         }
      }

      if(nLength > 0) {
         this.handleImplicitConversions(abData, nOffset, nLength);
         this.m_dataOutputStream.write(abData, nOffset, nLength);
         this.m_lCalculatedLength += (long)nLength;
         this.handleImplicitConversions(abData, nOffset, nLength);
      }

      return nLength;
   }

   protected abstract void writeHeader() throws IOException;

   public void close() throws IOException {

      if(this.m_bDoBackPatching) {

         this.patchHeader();
      }

      this.m_dataOutputStream.close();
   }

   protected void patchHeader() throws IOException {
   }

   protected void setLengthFromCalculatedLength() {
      this.m_lLength = this.m_lCalculatedLength;
   }
}
