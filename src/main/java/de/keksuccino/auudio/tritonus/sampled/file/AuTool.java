package de.keksuccino.auudio.tritonus.sampled.file;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

public class AuTool {

   public static final int AU_HEADER_MAGIC = 779316836;
   public static final int AUDIO_UNKNOWN_SIZE = -1;
   public static final int DATA_OFFSET = 24;
   public static final int SND_FORMAT_UNSPECIFIED = 0;
   public static final int SND_FORMAT_MULAW_8 = 1;
   public static final int SND_FORMAT_LINEAR_8 = 2;
   public static final int SND_FORMAT_LINEAR_16 = 3;
   public static final int SND_FORMAT_LINEAR_24 = 4;
   public static final int SND_FORMAT_LINEAR_32 = 5;
   public static final int SND_FORMAT_FLOAT = 6;
   public static final int SND_FORMAT_DOUBLE = 7;
   public static final int SND_FORMAT_ADPCM_G721 = 23;
   public static final int SND_FORMAT_ADPCM_G722 = 24;
   public static final int SND_FORMAT_ADPCM_G723_3 = 25;
   public static final int SND_FORMAT_ADPCM_G723_5 = 26;
   public static final int SND_FORMAT_ALAW_8 = 27;


   public static int getFormatCode(AudioFormat format) {
      Encoding encoding = format.getEncoding();
      int nSampleSize = format.getSampleSizeInBits();
      boolean frameSizeOK = format.getFrameSize() == -1 || format.getChannels() != -1 || format.getFrameSize() == nSampleSize / 8 * format.getChannels();
      boolean signed = encoding.equals(Encoding.PCM_SIGNED);
      boolean unsigned = encoding.equals(Encoding.PCM_UNSIGNED);
      if(encoding.equals(Encoding.ULAW) && nSampleSize == 8 && frameSizeOK) {
         return 1;
      } else if(nSampleSize == 8 && frameSizeOK && (signed || unsigned)) {
         return 2;
      } else {
         if(signed && frameSizeOK) {
            if(nSampleSize == 16) {
               return 3;
            }

            if(nSampleSize == 24) {
               return 4;
            }

            if(nSampleSize == 32) {
               return 5;
            }
         } else if(encoding.equals(Encoding.ALAW) && nSampleSize == 8 && frameSizeOK) {
            return 27;
         }

         return 0;
      }
   }
}
