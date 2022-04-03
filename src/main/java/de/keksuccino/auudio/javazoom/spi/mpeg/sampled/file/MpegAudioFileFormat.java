package de.keksuccino.auudio.javazoom.spi.mpeg.sampled.file;

import java.util.Map;
import javax.sound.sampled.AudioFormat;

import de.keksuccino.auudio.tritonus.share.sampled.file.TAudioFileFormat;

public class MpegAudioFileFormat extends TAudioFileFormat {

   public MpegAudioFileFormat(Type type, AudioFormat audioFormat, int nLengthInFrames, int nLengthInBytes, Map properties) {
      super(type, audioFormat, nLengthInFrames, nLengthInBytes, properties);
   }

   public Map properties() {
      return super.properties();
   }
}
