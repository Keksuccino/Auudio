package de.keksuccino.auudio.tritonus.share.sampled.convert;

import java.util.Collection;
import java.util.Iterator;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import de.keksuccino.auudio.tritonus.share.ArraySet;

public abstract class TEncodingFormatConversionProvider extends TSimpleFormatConversionProvider {

   protected TEncodingFormatConversionProvider(Collection sourceFormats, Collection targetFormats) {
      super(sourceFormats, targetFormats);
   }

   public AudioFormat[] getTargetFormats(Encoding targetEncoding, AudioFormat sourceFormat) {

      if(!this.isConversionSupported(targetEncoding, sourceFormat)) {
         return EMPTY_FORMAT_ARRAY;
      } else {
         ArraySet result = new ArraySet();
         Iterator iterator = this.getCollectionTargetFormats().iterator();

         while(iterator.hasNext()) {
            AudioFormat targetFormat = (AudioFormat)iterator.next();
            targetFormat = this.replaceNotSpecified(sourceFormat, targetFormat);
            result.add(targetFormat);
         }

         return (AudioFormat[])result.toArray(EMPTY_FORMAT_ARRAY);
      }
   }
}
