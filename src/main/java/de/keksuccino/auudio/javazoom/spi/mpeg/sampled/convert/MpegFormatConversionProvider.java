package de.keksuccino.auudio.javazoom.spi.mpeg.sampled.convert;

import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat.Encoding;

import de.keksuccino.auudio.javazoom.spi.mpeg.sampled.file.MpegEncoding;
import de.keksuccino.auudio.tritonus.share.sampled.Encodings;
import de.keksuccino.auudio.tritonus.share.sampled.convert.TEncodingFormatConversionProvider;

public class MpegFormatConversionProvider extends TEncodingFormatConversionProvider {

   private static final Encoding MP3 = Encodings.getEncoding("MP3");
   private static final Encoding PCM_SIGNED = Encodings.getEncoding("PCM_SIGNED");
   private static final AudioFormat[] INPUT_FORMATS = new AudioFormat[]{new AudioFormat(MP3, -1.0F, -1, 1, -1, -1.0F, false), new AudioFormat(MP3, -1.0F, -1, 1, -1, -1.0F, true), new AudioFormat(MP3, -1.0F, -1, 2, -1, -1.0F, false), new AudioFormat(MP3, -1.0F, -1, 2, -1, -1.0F, true)};
   private static final AudioFormat[] OUTPUT_FORMATS = new AudioFormat[]{new AudioFormat(PCM_SIGNED, -1.0F, 16, 1, 2, -1.0F, false), new AudioFormat(PCM_SIGNED, -1.0F, 16, 1, 2, -1.0F, true), new AudioFormat(PCM_SIGNED, -1.0F, 16, 2, 4, -1.0F, false), new AudioFormat(PCM_SIGNED, -1.0F, 16, 2, 4, -1.0F, true)};


   public MpegFormatConversionProvider() {
      super(Arrays.asList(INPUT_FORMATS), Arrays.asList(OUTPUT_FORMATS));
   }

   public AudioInputStream getAudioInputStream(AudioFormat targetFormat, AudioInputStream audioInputStream) {
      return new DecodedMpegAudioInputStream(targetFormat, audioInputStream);
   }

   public boolean isConversionSupported(AudioFormat targetFormat, AudioFormat sourceFormat) {
      boolean conversion = super.isConversionSupported(targetFormat, sourceFormat);
      if(!conversion) {
         Encoding enc = sourceFormat.getEncoding();
         if(enc instanceof MpegEncoding && (sourceFormat.getFrameRate() != -1.0F || sourceFormat.getFrameSize() != -1)) {
            conversion = true;
         }
      }

      return conversion;
   }

}
