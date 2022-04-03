package de.keksuccino.auudio.javazoom.spi.mpeg.sampled.file;

import java.util.Map;

import de.keksuccino.auudio.tritonus.share.sampled.TAudioFormat;

public class MpegAudioFormat extends TAudioFormat {

   public MpegAudioFormat(Encoding encoding, float nFrequency, int SampleSizeInBits, int nChannels, int FrameSize, float FrameRate, boolean isBigEndian, Map properties) {
      super(encoding, nFrequency, SampleSizeInBits, nChannels, FrameSize, FrameRate, isBigEndian, properties);
   }

   public Map properties() {
      return super.properties();
   }
}
