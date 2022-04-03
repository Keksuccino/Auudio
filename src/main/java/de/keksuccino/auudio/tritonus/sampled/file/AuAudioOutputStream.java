package de.keksuccino.auudio.tritonus.sampled.file;

import java.io.IOException;
import javax.sound.sampled.AudioFormat;

import de.keksuccino.auudio.tritonus.share.sampled.file.TAudioOutputStream;
import de.keksuccino.auudio.tritonus.share.sampled.file.TDataOutputStream;

public class AuAudioOutputStream extends TAudioOutputStream {

   private static String description = "Created by Tritonus";


   protected static void writeText(TDataOutputStream dos, String s) throws IOException {
      if(s.length() > 0) {
         dos.writeBytes(s);
         dos.writeByte(0);
         if(s.length() % 2 == 0) {
            dos.writeByte(0);
         }
      }

   }

   protected static int getTextLength(String s) {
      return s.length() == 0?0:s.length() + 2 & -2;
   }

   public AuAudioOutputStream(AudioFormat audioFormat, long lLength, TDataOutputStream dataOutputStream) {
      super(audioFormat, lLength > 2147483647L?-1L:lLength, dataOutputStream, dataOutputStream.supportsSeek());
      if(AuTool.getFormatCode(audioFormat) == 0) {
         throw new IllegalArgumentException("Unknown encoding/format for AU file: " + audioFormat);
      } else {
         this.requireSign8bit(true);
         this.requireEndianness(true);

      }
   }

   protected void writeHeader() throws IOException {

      AudioFormat format = this.getFormat();
      long lLength = this.getLength();
      TDataOutputStream dos = this.getDataOutputStream();

      dos.writeInt(779316836);
      dos.writeInt(24 + getTextLength(description));
      dos.writeInt(lLength != -1L?(int)lLength:-1);
      dos.writeInt(AuTool.getFormatCode(format));
      dos.writeInt((int)format.getSampleRate());
      dos.writeInt(format.getChannels());
      writeText(dos, description);
   }

   protected void patchHeader() throws IOException {
      TDataOutputStream tdos = this.getDataOutputStream();
      tdos.seek(0L);
      this.setLengthFromCalculatedLength();
      this.writeHeader();
   }

}
