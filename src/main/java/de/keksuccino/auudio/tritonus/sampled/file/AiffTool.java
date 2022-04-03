package de.keksuccino.auudio.tritonus.sampled.file;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

public class AiffTool {

   public static final int AIFF_FORM_MAGIC = 1179603533;
   public static final int AIFF_AIFF_MAGIC = 1095321158;
   public static final int AIFF_AIFC_MAGIC = 1095321155;
   public static final int AIFF_COMM_MAGIC = 1129270605;
   public static final int AIFF_SSND_MAGIC = 1397968452;
   public static final int AIFF_FVER_MAGIC = 1180058962;
   public static final int AIFF_COMM_UNSPECIFIED = 0;
   public static final int AIFF_COMM_PCM = 1313820229;
   public static final int AIFF_COMM_ULAW = 1970037111;
   public static final int AIFF_COMM_IMA_ADPCM = 1768775988;
   public static final int AIFF_FVER_TIME_STAMP = -1568648896;


   public static int getFormatCode(AudioFormat format) {
      Encoding encoding = format.getEncoding();
      int nSampleSize = format.getSampleSizeInBits();
      boolean frameSizeOK = format.getFrameSize() == -1 || format.getChannels() != -1 || format.getFrameSize() == nSampleSize / 8 * format.getChannels();
      boolean signed = encoding.equals(Encoding.PCM_SIGNED);
      boolean unsigned = encoding.equals(Encoding.PCM_UNSIGNED);
      return nSampleSize == 8 && frameSizeOK && (signed || unsigned)?1313820229:(nSampleSize > 8 && nSampleSize <= 32 && frameSizeOK && signed?1313820229:(encoding.equals(Encoding.ULAW) && nSampleSize == 8 && frameSizeOK?1970037111:(encoding.equals(new Encoding("IMA_ADPCM")) && nSampleSize == 4?1768775988:0)));
   }
}
