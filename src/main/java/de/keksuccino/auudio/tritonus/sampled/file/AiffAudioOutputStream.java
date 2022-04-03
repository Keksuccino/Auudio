package de.keksuccino.auudio.tritonus.sampled.file;

import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat.Encoding;

import de.keksuccino.auudio.tritonus.share.sampled.file.TAudioOutputStream;
import de.keksuccino.auudio.tritonus.share.sampled.file.TDataOutputStream;

public class AiffAudioOutputStream extends TAudioOutputStream {

   private static final int LENGTH_NOT_KNOWN = -1;
   private Type m_FileType;


   public AiffAudioOutputStream(AudioFormat audioFormat, Type fileType, long lLength, TDataOutputStream dataOutputStream) {
      super(audioFormat, lLength, dataOutputStream, dataOutputStream.supportsSeek());
      if(lLength != -1L && lLength > 2147483647L) {
         throw new IllegalArgumentException("AIFF files cannot be larger than 2GB.");
      } else {
         this.m_FileType = fileType;
         if(!audioFormat.getEncoding().equals(Encoding.PCM_SIGNED) && !audioFormat.getEncoding().equals(Encoding.PCM_UNSIGNED)) {
            this.m_FileType = Type.AIFC;
         }

         if(AiffTool.getFormatCode(audioFormat) == 0) {
            throw new IllegalArgumentException("Unknown encoding/format for AIFF file: " + audioFormat);
         } else {
            this.requireSign8bit(true);
            this.requireEndianness(true);

         }
      }
   }

   protected void writeHeader() throws IOException {

      AudioFormat format = this.getFormat();
      boolean bIsAifc = this.m_FileType.equals(Type.AIFC);
      long lLength = this.getLength();
      TDataOutputStream dos = this.getDataOutputStream();
      int nCommChunkSize = 18;
      int nFormatCode = AiffTool.getFormatCode(format);
      if(bIsAifc) {
         nCommChunkSize += 6;
      }

      int nHeaderSize = 12 + nCommChunkSize + 8;
      if(bIsAifc) {
         nHeaderSize += 12;
      }

      if(lLength != -1L && lLength + (long)nHeaderSize > 2147483647L) {
         lLength = 2147483647L - (long)nHeaderSize;
      }

      long lSSndChunkSize = lLength != -1L?lLength + lLength % 2L + 8L:-1L;
      dos.writeInt(1179603533);
      dos.writeInt(lLength != -1L?(int)(lSSndChunkSize + (long)nHeaderSize):-1);
      if(bIsAifc) {
         dos.writeInt(1095321155);
         dos.writeInt(1180058962);
         dos.writeInt(4);
         dos.writeInt(-1568648896);
      } else {
         dos.writeInt(1095321158);
      }

      dos.writeInt(1129270605);
      dos.writeInt(nCommChunkSize);
      dos.writeShort((short)format.getChannels());
      dos.writeInt(lLength != -1L?(int)(lLength / (long)format.getFrameSize()):-1);
      if(nFormatCode == 1970037111) {
         dos.writeShort(16);
      } else {
         dos.writeShort((short)format.getSampleSizeInBits());
      }

      this.writeIeeeExtended(dos, format.getSampleRate());
      if(bIsAifc) {
         dos.writeInt(nFormatCode);
         dos.writeShort(0);
      }

      dos.writeInt(1397968452);
      dos.writeInt(lLength != -1L?(int)(lLength + 8L):-1);
      dos.writeInt(0);
      dos.writeInt(0);
   }

   protected void patchHeader() throws IOException {
      TDataOutputStream tdos = this.getDataOutputStream();
      tdos.seek(0L);
      this.setLengthFromCalculatedLength();
      this.writeHeader();
   }

   public void close() throws IOException {
      long nBytesWritten = this.getCalculatedLength();
      if(nBytesWritten % 2L == 1L) {

         TDataOutputStream tdos = this.getDataOutputStream();
         tdos.writeByte(0);
      }

      super.close();
   }

   public void writeIeeeExtended(TDataOutputStream dos, float sampleRate) throws IOException {
      int nSampleRate = (int)sampleRate;

      short ieeeExponent;
      for(ieeeExponent = 0; nSampleRate != 0 && (nSampleRate & Integer.MIN_VALUE) == 0; nSampleRate <<= 1) {
         ++ieeeExponent;
      }

      dos.writeShort(16414 - ieeeExponent);
      dos.writeInt(nSampleRate);
      dos.writeInt(0);
   }
}
