package de.keksuccino.auudio.tritonus.share.sampled.convert;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.spi.FormatConversionProvider;
import de.keksuccino.auudio.tritonus.share.sampled.AudioFormats;

public abstract class TFormatConversionProvider extends FormatConversionProvider {

   protected static final Encoding[] EMPTY_ENCODING_ARRAY = new Encoding[0];
   protected static final AudioFormat[] EMPTY_FORMAT_ARRAY = new AudioFormat[0];


   public AudioInputStream getAudioInputStream(Encoding targetEncoding, AudioInputStream audioInputStream) {
      AudioFormat sourceFormat = audioInputStream.getFormat();
      AudioFormat targetFormat = new AudioFormat(targetEncoding, -1.0F, -1, -1, -1, -1.0F, sourceFormat.isBigEndian());

      return this.getAudioInputStream(targetFormat, audioInputStream);
   }

   public boolean isConversionSupported(AudioFormat targetFormat, AudioFormat sourceFormat) {

      AudioFormat[] aTargetFormats = this.getTargetFormats(targetFormat.getEncoding(), sourceFormat);

      for(int i = 0; i < aTargetFormats.length; ++i) {

         if(aTargetFormats[i] != null && AudioFormats.matches(aTargetFormats[i], targetFormat)) {

            return true;
         }
      }

      return false;
   }

   public AudioFormat getMatchingFormat(AudioFormat targetFormat, AudioFormat sourceFormat) {
      AudioFormat[] aTargetFormats = this.getTargetFormats(targetFormat.getEncoding(), sourceFormat);

      for(int i = 0; i < aTargetFormats.length; ++i) {

         if(aTargetFormats[i] != null && AudioFormats.matches(aTargetFormats[i], targetFormat)) {

            return aTargetFormats[i];
         }
      }

      return null;
   }

}
