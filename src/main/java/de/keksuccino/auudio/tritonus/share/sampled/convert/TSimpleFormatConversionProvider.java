package de.keksuccino.auudio.tritonus.share.sampled.convert;

import java.util.Collection;
import java.util.Iterator;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import de.keksuccino.auudio.tritonus.share.ArraySet;
import de.keksuccino.auudio.tritonus.share.sampled.AudioFormats;

public abstract class TSimpleFormatConversionProvider extends TFormatConversionProvider {

   private Collection m_sourceEncodings = new ArraySet();
   private Collection m_targetEncodings = new ArraySet();
   private Collection m_sourceFormats;
   private Collection m_targetFormats;


   protected TSimpleFormatConversionProvider(Collection sourceFormats, Collection targetFormats) {
      if(sourceFormats == null) {
         sourceFormats = new ArraySet();
      }

      if(targetFormats == null) {
         targetFormats = new ArraySet();
      }

      this.m_sourceFormats = (Collection)sourceFormats;
      this.m_targetFormats = (Collection)targetFormats;
      collectEncodings(this.m_sourceFormats, this.m_sourceEncodings);
      collectEncodings(this.m_targetFormats, this.m_targetEncodings);
   }

   protected void disable() {
      this.m_sourceEncodings = new ArraySet();
      this.m_targetEncodings = new ArraySet();
      this.m_sourceFormats = new ArraySet();
      this.m_targetFormats = new ArraySet();
   }

   private static void collectEncodings(Collection formats, Collection encodings) {
      Iterator iterator = formats.iterator();

      while(iterator.hasNext()) {
         AudioFormat format = (AudioFormat)iterator.next();
         encodings.add(format.getEncoding());
      }

   }

   public Encoding[] getSourceEncodings() {
      return (Encoding[])this.m_sourceEncodings.toArray(EMPTY_ENCODING_ARRAY);
   }

   public Encoding[] getTargetEncodings() {
      return (Encoding[])this.m_targetEncodings.toArray(EMPTY_ENCODING_ARRAY);
   }

   public boolean isSourceEncodingSupported(Encoding sourceEncoding) {
      return this.m_sourceEncodings.contains(sourceEncoding);
   }

   public boolean isTargetEncodingSupported(Encoding targetEncoding) {
      return this.m_targetEncodings.contains(targetEncoding);
   }

   public Encoding[] getTargetEncodings(AudioFormat sourceFormat) {
      return this.isAllowedSourceFormat(sourceFormat)?this.getTargetEncodings():EMPTY_ENCODING_ARRAY;
   }

   public AudioFormat[] getTargetFormats(Encoding targetEncoding, AudioFormat sourceFormat) {
      return this.isConversionSupported(targetEncoding, sourceFormat)?(AudioFormat[])this.m_targetFormats.toArray(EMPTY_FORMAT_ARRAY):EMPTY_FORMAT_ARRAY;
   }

   protected boolean isAllowedSourceEncoding(Encoding sourceEncoding) {
      return this.m_sourceEncodings.contains(sourceEncoding);
   }

   protected boolean isAllowedTargetEncoding(Encoding targetEncoding) {
      return this.m_targetEncodings.contains(targetEncoding);
   }

   protected boolean isAllowedSourceFormat(AudioFormat sourceFormat) {
      Iterator iterator = this.m_sourceFormats.iterator();

      AudioFormat format;
      do {
         if(!iterator.hasNext()) {
            return false;
         }

         format = (AudioFormat)iterator.next();
      } while(!AudioFormats.matches(format, sourceFormat));

      return true;
   }

   protected boolean isAllowedTargetFormat(AudioFormat targetFormat) {
      Iterator iterator = this.m_targetFormats.iterator();

      AudioFormat format;
      do {
         if(!iterator.hasNext()) {
            return false;
         }

         format = (AudioFormat)iterator.next();
      } while(!AudioFormats.matches(format, targetFormat));

      return true;
   }

   protected Collection getCollectionSourceEncodings() {
      return this.m_sourceEncodings;
   }

   protected Collection getCollectionTargetEncodings() {
      return this.m_targetEncodings;
   }

   protected Collection getCollectionSourceFormats() {
      return this.m_sourceFormats;
   }

   protected Collection getCollectionTargetFormats() {
      return this.m_targetFormats;
   }

   protected static boolean doMatch(int i1, int i2) {
      return i1 == -1 || i2 == -1 || i1 == i2;
   }

   protected static boolean doMatch(float f1, float f2) {
      return f1 == -1.0F || f2 == -1.0F || (double)Math.abs(f1 - f2) < 1.0E-9D;
   }

   protected AudioFormat replaceNotSpecified(AudioFormat sourceFormat, AudioFormat targetFormat) {
      boolean bSetSampleSize = false;
      boolean bSetChannels = false;
      boolean bSetSampleRate = false;
      boolean bSetFrameRate = false;
      if(targetFormat.getSampleSizeInBits() == -1 && sourceFormat.getSampleSizeInBits() != -1) {
         bSetSampleSize = true;
      }

      if(targetFormat.getChannels() == -1 && sourceFormat.getChannels() != -1) {
         bSetChannels = true;
      }

      if(targetFormat.getSampleRate() == -1.0F && sourceFormat.getSampleRate() != -1.0F) {
         bSetSampleRate = true;
      }

      if(targetFormat.getFrameRate() == -1.0F && sourceFormat.getFrameRate() != -1.0F) {
         bSetFrameRate = true;
      }

      if(bSetSampleSize || bSetChannels || bSetSampleRate || bSetFrameRate || targetFormat.getFrameSize() == -1 && sourceFormat.getFrameSize() != -1) {
         float sampleRate = bSetSampleRate?sourceFormat.getSampleRate():targetFormat.getSampleRate();
         float frameRate = bSetFrameRate?sourceFormat.getFrameRate():targetFormat.getFrameRate();
         int sampleSize = bSetSampleSize?sourceFormat.getSampleSizeInBits():targetFormat.getSampleSizeInBits();
         int channels = bSetChannels?sourceFormat.getChannels():targetFormat.getChannels();
         int frameSize = this.getFrameSize(targetFormat.getEncoding(), sampleRate, sampleSize, channels, frameRate, targetFormat.isBigEndian(), targetFormat.getFrameSize());
         targetFormat = new AudioFormat(targetFormat.getEncoding(), sampleRate, sampleSize, channels, frameSize, frameRate, targetFormat.isBigEndian());
      }

      return targetFormat;
   }

   protected int getFrameSize(Encoding encoding, float sampleRate, int sampleSize, int channels, float frameRate, boolean bigEndian, int oldFrameSize) {
      return sampleSize != -1 && channels != -1?(sampleSize + 7) / 8 * channels:-1;
   }
}
