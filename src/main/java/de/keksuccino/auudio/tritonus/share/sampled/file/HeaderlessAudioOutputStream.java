package de.keksuccino.auudio.tritonus.share.sampled.file;

import java.io.IOException;
import javax.sound.sampled.AudioFormat;

public class HeaderlessAudioOutputStream extends TAudioOutputStream {

   public HeaderlessAudioOutputStream(AudioFormat audioFormat, long lLength, TDataOutputStream dataOutputStream) {
      super(audioFormat, lLength, dataOutputStream, false);
   }

   protected void writeHeader() throws IOException {}
}
