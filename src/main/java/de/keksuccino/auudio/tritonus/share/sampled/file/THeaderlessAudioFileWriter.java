package de.keksuccino.auudio.tritonus.share.sampled.file;

import java.io.IOException;
import java.util.Collection;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFileFormat.Type;

public class THeaderlessAudioFileWriter extends TAudioFileWriter {

   protected THeaderlessAudioFileWriter(Collection fileTypes, Collection audioFormats) {
      super(fileTypes, audioFormats);

   }

   protected AudioOutputStream getAudioOutputStream(AudioFormat audioFormat, long lLengthInBytes, Type fileType, TDataOutputStream dataOutputStream) throws IOException {

      HeaderlessAudioOutputStream aos = new HeaderlessAudioOutputStream(audioFormat, lLengthInBytes, dataOutputStream);

      return aos;
   }
}
