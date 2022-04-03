package de.keksuccino.auudio.tritonus.sampled.file;

import java.io.IOException;
import javax.sound.sampled.AudioFormat;

import de.keksuccino.auudio.tritonus.share.sampled.file.TAudioOutputStream;
import de.keksuccino.auudio.tritonus.share.sampled.file.TDataOutputStream;

public class WaveAudioOutputStream extends TAudioOutputStream {

   private static final int LENGTH_NOT_KNOWN = -1;


   public WaveAudioOutputStream(AudioFormat audioFormat, long lLength, TDataOutputStream dataOutputStream) {
      super(audioFormat, lLength, dataOutputStream, dataOutputStream.supportsSeek());
      if(lLength != -1L && lLength + 46L > 4294967295L) {

         throw new IllegalArgumentException("Wave files cannot be larger than 4GB.");
      } else if(WaveTool.getFormatCode(this.getFormat()) == 0) {
         throw new IllegalArgumentException("Unknown encoding/format for WAVE file: " + audioFormat);
      } else {
         this.requireSign8bit(false);
         this.requireEndianness(false);

      }
   }

   protected void writeHeader() throws IOException {

      short formatCode = WaveTool.getFormatCode(this.getFormat());
      AudioFormat format = this.getFormat();
      long lLength = this.getLength();
      int formatChunkAdd = 0;
      if(formatCode == 49) {
         formatChunkAdd += 2;
      }

      int dataOffset = 46 + formatChunkAdd;
      if(formatCode != 1) {
         dataOffset += 12;
      }

      if(lLength != -1L && lLength + (long)dataOffset > 4294967295L) {
         lLength = 4294967295L - (long)dataOffset;
      }

      long lDataChunkSize = lLength + lLength % 2L;
      if(lLength == -1L || lDataChunkSize > 4294967295L) {
         lDataChunkSize = 4294967295L;
      }

      long RIFF_Size = lDataChunkSize + (long)dataOffset - 8L;
      if(lLength == -1L || RIFF_Size > 4294967295L) {
         RIFF_Size = 4294967295L;
      }

      TDataOutputStream dos = this.getDataOutputStream();
      dos.writeInt(1380533830);
      dos.writeLittleEndian32((int)RIFF_Size);
      dos.writeInt(1463899717);
      int formatChunkSize = 18 + formatChunkAdd;
      short sampleSizeInBits = (short)format.getSampleSizeInBits();
      int decodedSamplesPerBlock = 1;
      if(formatCode == 49) {
         if(format.getFrameSize() == 33) {
            decodedSamplesPerBlock = 320;
         } else if(format.getFrameSize() == 65) {
            decodedSamplesPerBlock = 320;
         } else {
            decodedSamplesPerBlock = (int)((float)format.getFrameSize() * 4.923077F);
         }

         sampleSizeInBits = 0;
      }

      int avgBytesPerSec = (int)format.getSampleRate() / decodedSamplesPerBlock * format.getFrameSize();
      dos.writeInt(1718449184);
      dos.writeLittleEndian32(formatChunkSize);
      dos.writeLittleEndian16((short)formatCode);
      dos.writeLittleEndian16((short)format.getChannels());
      dos.writeLittleEndian32((int)format.getSampleRate());
      dos.writeLittleEndian32(avgBytesPerSec);
      dos.writeLittleEndian16((short)format.getFrameSize());
      dos.writeLittleEndian16(sampleSizeInBits);
      dos.writeLittleEndian16((short)formatChunkAdd);
      if(formatCode == 49) {
         dos.writeLittleEndian16((short)decodedSamplesPerBlock);
      }

      if(formatCode != 1) {
         long samples = 0L;
         if(lLength != -1L) {
            samples = lLength / (long)format.getFrameSize() * (long)decodedSamplesPerBlock;
         }

         if(samples > 4294967295L) {
            samples = 4294967295L / (long)decodedSamplesPerBlock * (long)decodedSamplesPerBlock;
         }

         dos.writeInt(1717658484);
         dos.writeLittleEndian32(4);
         dos.writeLittleEndian32((int)(samples & -1L));
      }

      dos.writeInt(1684108385);
      dos.writeLittleEndian32(lLength != -1L?(int)lLength:-1);
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
}
