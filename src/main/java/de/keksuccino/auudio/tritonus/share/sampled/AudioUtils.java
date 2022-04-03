package de.keksuccino.auudio.tritonus.share.sampled;

import java.nio.ByteOrder;
import java.util.Iterator;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.Mixer.Info;

public class AudioUtils {

   public static double SILENCE_DECIBEL = -100.0D;


   public static long getLengthInBytes(AudioInputStream audioInputStream) {
      return getLengthInBytes(audioInputStream.getFormat(), audioInputStream.getFrameLength());
   }

   public static long getLengthInBytes(AudioFormat audioFormat, long lLengthInFrames) {
      int nFrameSize = audioFormat.getFrameSize();
      return lLengthInFrames >= 0L && nFrameSize >= 1?lLengthInFrames * (long)nFrameSize:-1L;
   }

   public static boolean containsFormat(AudioFormat sourceFormat, Iterator possibleFormats) {
      while(true) {
         if(possibleFormats.hasNext()) {
            AudioFormat format = (AudioFormat)possibleFormats.next();
            if(!AudioFormats.matches(format, sourceFormat)) {
               continue;
            }

            return true;
         }

         return false;
      }
   }

   public static int getFrameSize(int channels, int sampleSizeInBits) {
      return channels >= 0 && sampleSizeInBits >= 0?(sampleSizeInBits + 7) / 8 * channels:-1;
   }

   public static long millis2Bytes(long ms, AudioFormat format) {
      return millis2Bytes(ms, (double)format.getFrameRate(), format.getFrameSize());
   }

   public static long millis2Bytes(long ms, double frameRate, int frameSize) {
      return (long)((double)ms * frameRate / 1000.0D * (double)frameSize);
   }

   public static int millis2Bytes(int ms, AudioFormat format) {
      return millis2Bytes(ms, (double)format.getFrameRate(), format.getFrameSize());
   }

   public static int millis2Bytes(int ms, double frameRate, int frameSize) {
      return (int)((double)ms * frameRate / 1000.0D * (double)frameSize);
   }

   public static long millis2Bytes(double ms, AudioFormat format) {
      return millis2Bytes(ms, (double)format.getFrameRate(), format.getFrameSize());
   }

   public static long millis2Bytes(double ms, double frameRate, int frameSize) {
      return (long)(ms * frameRate / 1000.0D * (double)frameSize);
   }

   public static long millis2BytesFrameAligned(long ms, AudioFormat format) {
      return millis2BytesFrameAligned(ms, (double)format.getFrameRate(), format.getFrameSize());
   }

   public static long millis2BytesFrameAligned(long ms, double frameRate, int frameSize) {
      return (long)((double)ms * frameRate / 1000.0D) * (long)frameSize;
   }

   public static int millis2BytesFrameAligned(int ms, AudioFormat format) {
      return millis2BytesFrameAligned(ms, (double)format.getFrameRate(), format.getFrameSize());
   }

   public static int millis2BytesFrameAligned(int ms, double frameRate, int frameSize) {
      return (int)((double)ms * frameRate / 1000.0D) * frameSize;
   }

   public static long millis2BytesFrameAligned(double ms, AudioFormat format) {
      return millis2BytesFrameAligned(ms, (double)format.getFrameRate(), format.getFrameSize());
   }

   public static long millis2BytesFrameAligned(double ms, double frameRate, int frameSize) {
      return (long)(ms * frameRate / 1000.0D) * (long)frameSize;
   }

   public static long millis2Frames(long ms, AudioFormat format) {
      return millis2Frames(ms, (double)format.getFrameRate());
   }

   public static long millis2Frames(long ms, double frameRate) {
      return (long)((double)ms * frameRate / 1000.0D);
   }

   public static int millis2Frames(int ms, AudioFormat format) {
      return millis2Frames(ms, (double)format.getFrameRate());
   }

   public static int millis2Frames(int ms, double frameRate) {
      return (int)((double)ms * frameRate / 1000.0D);
   }

   public static long millis2Frames(double ms, AudioFormat format) {
      return (long)millis2FramesD(ms, (double)format.getFrameRate());
   }

   public static long millis2Frames(double ms, double frameRate) {
      return (long)millis2FramesD(ms, frameRate);
   }

   public static double millis2FramesD(double ms, AudioFormat format) {
      return millis2FramesD(ms, (double)format.getFrameRate());
   }

   public static double millis2FramesD(double ms, double frameRate) {
      return ms * frameRate / 1000.0D;
   }

   public static long bytes2Millis(long bytes, AudioFormat format) {
      return (long)frames2MillisD(bytes / (long)format.getFrameSize(), (double)format.getFrameRate());
   }

   public static int bytes2Millis(int bytes, AudioFormat format) {
      return (int)frames2MillisD((long)(bytes / format.getFrameSize()), (double)format.getFrameRate());
   }

   public static double bytes2MillisD(long bytes, AudioFormat format) {
      return frames2MillisD(bytes / (long)format.getFrameSize(), (double)format.getFrameRate());
   }

   public static double bytes2MillisD(long bytes, double frameRate, int frameSize) {
      return frames2MillisD(bytes / (long)frameSize, frameRate);
   }

   public static long frames2Millis(long frames, AudioFormat format) {
      return (long)frames2MillisD(frames, (double)format.getFrameRate());
   }

   public static int frames2Millis(int frames, AudioFormat format) {
      return (int)frames2MillisD((long)frames, (double)format.getFrameRate());
   }

   public static double frames2MillisD(long frames, AudioFormat format) {
      return frames2MillisD(frames, (double)format.getFrameRate());
   }

   public static double frames2MillisD(long frames, double frameRate) {
      return (double)frames / frameRate * 1000.0D;
   }

   public static boolean sampleRateEquals(float sr1, float sr2) {
      return (double)Math.abs(sr1 - sr2) < 1.0E-7D;
   }

   public static boolean isPCM(AudioFormat format) {
      return format.getEncoding().equals(Encoding.PCM_SIGNED) || format.getEncoding().equals(Encoding.PCM_UNSIGNED);
   }

   public static boolean isJavaSoundAudioEngine(Info mixerInfo) {
      return mixerInfo != null && mixerInfo.getName() != null && mixerInfo.getName().equals("Java Sound Audio Engine");
   }

   public static boolean isJavaSoundAudioEngine(DataLine line) {
      if(line == null) {
         return false;
      } else {
         String clazz = line.getClass().toString();
         return clazz.indexOf("MixerSourceLine") >= 0 || clazz.indexOf("MixerClip") >= 0 || clazz.indexOf("SimpleInputDevice") >= 0;
      }
   }

   public static boolean isSystemBigEndian() {
      return ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN);
   }

   public static String NS_or_number(int number) {
      return number == -1?"NOT_SPECIFIED":String.valueOf(number);
   }

   public static String NS_or_number(float number) {
      return number == -1.0F?"NOT_SPECIFIED":String.valueOf(number);
   }

   public static String format2ShortStr(AudioFormat format) {
      return format.getEncoding() + "-" + NS_or_number(format.getChannels()) + "ch-" + NS_or_number(format.getSampleSizeInBits()) + "bit-" + NS_or_number((int)format.getSampleRate()) + "Hz-" + (format.isBigEndian()?"be":"le");
   }

   public static final double linear2decibel(double linearFactor) {
      if(linearFactor <= 0.0D) {
         return SILENCE_DECIBEL;
      } else {
         double ret = Math.log10(linearFactor) * 20.0D;
         if(ret < SILENCE_DECIBEL) {
            ret = SILENCE_DECIBEL;
         }

         return ret;
      }
   }

   public static final double decibel2linear(double decibels) {
      return decibels <= SILENCE_DECIBEL?0.0D:Math.pow(10.0D, decibels * 0.05D);
   }

}
