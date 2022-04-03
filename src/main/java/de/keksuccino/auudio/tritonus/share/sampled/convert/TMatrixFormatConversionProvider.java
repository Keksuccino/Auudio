package de.keksuccino.auudio.tritonus.share.sampled.convert;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import de.keksuccino.auudio.tritonus.share.ArraySet;
import de.keksuccino.auudio.tritonus.share.sampled.AudioFormats;

public abstract class TMatrixFormatConversionProvider extends TSimpleFormatConversionProvider {

   private Map m_targetEncodingsFromSourceFormat = new HashMap();
   private Map m_targetFormatsFromSourceFormat = new HashMap();


   protected TMatrixFormatConversionProvider(List sourceFormats, List targetFormats, boolean[][] abConversionPossible) {
      super(sourceFormats, targetFormats);

      for(int nSourceFormat = 0; nSourceFormat < sourceFormats.size(); ++nSourceFormat) {
         AudioFormat sourceFormat = (AudioFormat)sourceFormats.get(nSourceFormat);
         ArraySet supportedTargetEncodings = new ArraySet();
         this.m_targetEncodingsFromSourceFormat.put(sourceFormat, supportedTargetEncodings);
         HashMap targetFormatsFromTargetEncodings = new HashMap();
         this.m_targetFormatsFromSourceFormat.put(sourceFormat, targetFormatsFromTargetEncodings);

         for(int nTargetFormat = 0; nTargetFormat < targetFormats.size(); ++nTargetFormat) {
            AudioFormat targetFormat = (AudioFormat)targetFormats.get(nTargetFormat);
            if(abConversionPossible[nSourceFormat][nTargetFormat]) {
               Encoding targetEncoding = targetFormat.getEncoding();
               supportedTargetEncodings.add(targetEncoding);
               Object supportedTargetFormats = (Collection)targetFormatsFromTargetEncodings.get(targetEncoding);
               if(supportedTargetFormats == null) {
                  supportedTargetFormats = new ArraySet();
                  targetFormatsFromTargetEncodings.put(targetEncoding, supportedTargetFormats);
               }

               ((Collection)supportedTargetFormats).add(targetFormat);
            }
         }
      }

   }

   public Encoding[] getTargetEncodings(AudioFormat sourceFormat) {
      Iterator iterator = this.m_targetEncodingsFromSourceFormat.entrySet().iterator();

      Entry entry;
      AudioFormat format;
      do {
         if(!iterator.hasNext()) {
            return EMPTY_ENCODING_ARRAY;
         }

         entry = (Entry)iterator.next();
         format = (AudioFormat)entry.getKey();
      } while(!AudioFormats.matches(format, sourceFormat));

      Collection targetEncodings = (Collection)entry.getValue();
      return (Encoding[])((Encoding[])targetEncodings.toArray(EMPTY_ENCODING_ARRAY));
   }

   public AudioFormat[] getTargetFormats(Encoding targetEncoding, AudioFormat sourceFormat) {
      Iterator iterator = this.m_targetFormatsFromSourceFormat.entrySet().iterator();

      Entry entry;
      AudioFormat format;
      do {
         if(!iterator.hasNext()) {
            return EMPTY_FORMAT_ARRAY;
         }

         entry = (Entry)iterator.next();
         format = (AudioFormat)entry.getKey();
      } while(!AudioFormats.matches(format, sourceFormat));

      Map targetEncodings = (Map)entry.getValue();
      Collection targetFormats = (Collection)targetEncodings.get(targetEncoding);
      if(targetFormats != null) {
         return (AudioFormat[])((AudioFormat[])targetFormats.toArray(EMPTY_FORMAT_ARRAY));
      } else {
         return EMPTY_FORMAT_ARRAY;
      }
   }
}
