package de.keksuccino.auudio.tritonus.share.sampled;

import java.util.List;
import java.util.Random;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

public class FloatSampleTools {

   public static final float DEFAULT_DITHER_BITS = 0.7F;
   private static Random random = null;
   static final int F_8 = 1;
   static final int F_16 = 2;
   static final int F_24_3 = 3;
   static final int F_24_4 = 4;
   static final int F_32 = 5;
   static final int F_SAMPLE_WIDTH_MASK = 7;
   static final int F_SIGNED = 8;
   static final int F_BIGENDIAN = 16;
   static final int CT_8S = 9;
   static final int CT_8U = 1;
   static final int CT_16SB = 26;
   static final int CT_16SL = 10;
   static final int CT_24_3SB = 27;
   static final int CT_24_3SL = 11;
   static final int CT_24_4SB = 28;
   static final int CT_24_4SL = 12;
   static final int CT_32SB = 29;
   static final int CT_32SL = 13;
   private static final float twoPower7 = 128.0F;
   private static final float twoPower15 = 32768.0F;
   private static final float twoPower23 = 8388608.0F;
   private static final float twoPower31 = 2.14748365E9F;
   private static final float invTwoPower7 = 0.0078125F;
   private static final float invTwoPower15 = 3.0517578E-5F;
   private static final float invTwoPower23 = 1.1920929E-7F;
   private static final float invTwoPower31 = 4.656613E-10F;


   static void checkSupportedSampleSize(int ssib, int channels, int frameSize) {
      if(ssib != 24 || frameSize != 4 * channels) {
         if(ssib * channels != frameSize * 8) {
            throw new IllegalArgumentException("unsupported sample size: " + ssib + " bits stored in " + frameSize / channels + " bytes.");
         }
      }
   }

   static int getFormatType(AudioFormat format) {
      boolean signed = format.getEncoding().equals(Encoding.PCM_SIGNED);
      if(!signed && !format.getEncoding().equals(Encoding.PCM_UNSIGNED)) {
         throw new IllegalArgumentException("unsupported encoding: only PCM encoding supported.");
      } else if(!signed && format.getSampleSizeInBits() != 8) {
         throw new IllegalArgumentException("unsupported encoding: only 8-bit can be unsigned");
      } else {
         checkSupportedSampleSize(format.getSampleSizeInBits(), format.getChannels(), format.getFrameSize());
         int formatType = getFormatType(format.getSampleSizeInBits(), format.getFrameSize() / format.getChannels(), signed, format.isBigEndian());
         return formatType;
      }
   }

   static int getFormatType(int ssib, int bytesPerSample, boolean signed, boolean bigEndian) {
      int res = 0;
      if(ssib == 24 || bytesPerSample == ssib / 8) {
         if(ssib == 8) {
            res = 1;
         } else if(ssib == 16) {
            res = 2;
         } else if(ssib == 24) {
            if(bytesPerSample == 3) {
               res = 3;
            } else if(bytesPerSample == 4) {
               res = 4;
            }
         } else if(ssib == 32) {
            res = 5;
         }
      }

      if(res == 0) {
         throw new IllegalArgumentException("ConversionTool: unsupported sample size of " + ssib + " bits per sample in " + bytesPerSample + " bytes.");
      } else if(!signed && bytesPerSample > 1) {
         throw new IllegalArgumentException("ConversionTool: unsigned samples larger than 8 bit are not supported");
      } else {
         if(signed) {
            res |= 8;
         }

         if(bigEndian && ssib != 8) {
            res |= 16;
         }

         return res;
      }
   }

   static int getSampleSize(int formatType) {
      switch(formatType & 7) {
      case 1:
         return 1;
      case 2:
         return 2;
      case 3:
         return 3;
      case 4:
         return 4;
      case 5:
         return 4;
      default:
         return 0;
      }
   }

   static String formatType2Str(int formatType) {
      String res = "" + formatType + ": ";
      switch(formatType & 7) {
      case 1:
         res = res + "8bit";
         break;
      case 2:
         res = res + "16bit";
         break;
      case 3:
         res = res + "24_3bit";
         break;
      case 4:
         res = res + "24_4bit";
         break;
      case 5:
         res = res + "32bit";
      }

      res = res + ((formatType & 8) == 8?" signed":" unsigned");
      if((formatType & 7) != 1) {
         res = res + ((formatType & 16) == 16?" big endian":" little endian");
      }

      return res;
   }

   public static void byte2float(byte[] input, int inByteOffset, List output, int outOffset, int frameCount, AudioFormat format) {
      byte2float(input, inByteOffset, output, outOffset, frameCount, format, true);
   }

   public static void byte2float(byte[] input, int inByteOffset, Object[] output, int outOffset, int frameCount, AudioFormat format) {
      byte2float(input, inByteOffset, output, outOffset, frameCount, format, true);
   }

   public static void byte2float(byte[] input, int inByteOffset, Object[] output, int outOffset, int frameCount, AudioFormat format, boolean allowAddChannel) {
      int channels = format.getChannels();
      if(!allowAddChannel && channels > output.length) {
         channels = output.length;
      }

      if(output.length < channels) {
         throw new ArrayIndexOutOfBoundsException("too few channel output array");
      } else {
         for(int channel = 0; channel < channels; ++channel) {
            float[] data = (float[])((float[])output[channel]);
            if(data.length < frameCount + outOffset) {
               data = new float[frameCount + outOffset];
               output[channel] = data;
            }

            byte2floatGeneric(input, inByteOffset, format.getFrameSize(), data, outOffset, frameCount, format);
            inByteOffset += format.getFrameSize() / format.getChannels();
         }

      }
   }

   public static void byte2float(byte[] input, int inByteOffset, List output, int outOffset, int frameCount, AudioFormat format, boolean allowAddChannel) {
      int channels = format.getChannels();
      if(!allowAddChannel && channels > output.size()) {
         channels = output.size();
      }

      for(int channel = 0; channel < channels; ++channel) {
         float[] data;
         if(output.size() < channel) {
            data = new float[frameCount + outOffset];
            output.add(data);
         } else {
            data = (float[])output.get(channel);
            if(data.length < frameCount + outOffset) {
               data = new float[frameCount + outOffset];
               output.set(channel, data);
            }
         }

         byte2floatGeneric(input, inByteOffset, format.getFrameSize(), data, outOffset, frameCount, format);
         inByteOffset += format.getFrameSize() / format.getChannels();
      }

   }

   public static void byte2float(int channel, byte[] input, int inByteOffset, float[] output, int outOffset, int frameCount, AudioFormat format) {
      if(channel >= format.getChannels()) {
         throw new IllegalArgumentException("channel out of bounds");
      } else if(output.length < frameCount + outOffset) {
         throw new IllegalArgumentException("data is too small");
      } else {
         inByteOffset += format.getFrameSize() / format.getChannels() * channel;
         byte2floatGeneric(input, inByteOffset, format.getFrameSize(), output, outOffset, frameCount, format);
      }
   }

   public static void byte2floatInterleaved(byte[] input, int inByteOffset, float[] output, int outOffset, int frameCount, AudioFormat format) {
      byte2floatGeneric(input, inByteOffset, format.getFrameSize() / format.getChannels(), output, outOffset, frameCount * format.getChannels(), format);
   }

   static void byte2floatGeneric(byte[] input, int inByteOffset, int inByteStep, float[] output, int outOffset, int sampleCount, AudioFormat format) {
      int formatType = getFormatType(format);
      byte2floatGeneric(input, inByteOffset, inByteStep, output, outOffset, sampleCount, formatType);
   }

   static void byte2floatGeneric(byte[] input, int inByteOffset, int inByteStep, float[] output, int outOffset, int sampleCount, int formatType) {
      int endCount = outOffset + sampleCount;
      int inIndex = inByteOffset;

      for(int outIndex = outOffset; outIndex < endCount; inIndex += inByteStep) {
         switch(formatType) {
         case 1:
            output[outIndex] = (float)((input[inIndex] & 255) - 128) * 0.0078125F;
            break;
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         default:
            throw new IllegalArgumentException("unsupported format=" + formatType2Str(formatType));
         case 9:
            output[outIndex] = (float)input[inIndex] * 0.0078125F;
            break;
         case 10:
            output[outIndex] = (float)(input[inIndex + 1] << 8 | input[inIndex] & 255) * 3.0517578E-5F;
            break;
         case 11:
            output[outIndex] = (float)(input[inIndex + 2] << 16 | (input[inIndex + 1] & 255) << 8 | input[inIndex] & 255) * 1.1920929E-7F;
            break;
         case 12:
            output[outIndex] = (float)(input[inIndex + 3] << 16 | (input[inIndex + 2] & 255) << 8 | input[inIndex + 1] & 255) * 1.1920929E-7F;
            break;
         case 13:
            output[outIndex] = (float)(input[inIndex + 3] << 24 | (input[inIndex + 2] & 255) << 16 | (input[inIndex + 1] & 255) << 8 | input[inIndex] & 255) * 4.656613E-10F;
            break;
         case 26:
            output[outIndex] = (float)(input[inIndex] << 8 | input[inIndex + 1] & 255) * 3.0517578E-5F;
            break;
         case 27:
            output[outIndex] = (float)(input[inIndex] << 16 | (input[inIndex + 1] & 255) << 8 | input[inIndex + 2] & 255) * 1.1920929E-7F;
            break;
         case 28:
            output[outIndex] = (float)(input[inIndex + 1] << 16 | (input[inIndex + 2] & 255) << 8 | input[inIndex + 3] & 255) * 1.1920929E-7F;
            break;
         case 29:
            output[outIndex] = (float)(input[inIndex] << 24 | (input[inIndex + 1] & 255) << 16 | (input[inIndex + 2] & 255) << 8 | input[inIndex + 3] & 255) * 4.656613E-10F;
         }

         ++outIndex;
      }

   }

   private static byte quantize8(float sample, float ditherBits) {
      if(ditherBits != 0.0F) {
         sample += random.nextFloat() * ditherBits;
      }

      return sample >= 127.0F?127:(sample <= -128.0F?-128:(byte)((int)(sample < 0.0F?sample - 0.5F:sample + 0.5F)));
   }

   private static int quantize16(float sample, float ditherBits) {
      if(ditherBits != 0.0F) {
         sample += random.nextFloat() * ditherBits;
      }

      return sample >= 32767.0F?32767:(sample <= -32768.0F?-32768:(int)(sample < 0.0F?sample - 0.5F:sample + 0.5F));
   }

   private static int quantize24(float sample, float ditherBits) {
      if(ditherBits != 0.0F) {
         sample += random.nextFloat() * ditherBits;
      }

      return sample >= 8388607.0F?8388607:(sample <= -8388608.0F?-8388608:(int)(sample < 0.0F?sample - 0.5F:sample + 0.5F));
   }

   private static int quantize32(float sample, float ditherBits) {
      if(ditherBits != 0.0F) {
         sample += random.nextFloat() * ditherBits;
      }

      return sample >= 2.14748365E9F?Integer.MAX_VALUE:(sample <= -2.14748365E9F?Integer.MIN_VALUE:(int)(sample < 0.0F?sample - 0.5F:sample + 0.5F));
   }

   public static void float2byte(List input, int inOffset, byte[] output, int outByteOffset, int frameCount, AudioFormat format, float ditherBits) {
      for(int channel = 0; channel < format.getChannels(); ++channel) {
         float[] data = (float[])input.get(channel);
         float2byteGeneric(data, inOffset, output, outByteOffset, format.getFrameSize(), frameCount, format, ditherBits);
         outByteOffset += format.getFrameSize() / format.getChannels();
      }

   }

   public static void float2byte(Object[] input, int inOffset, byte[] output, int outByteOffset, int frameCount, AudioFormat format, float ditherBits) {
      int channels = format.getChannels();

      for(int channel = 0; channel < channels; ++channel) {
         float[] data = (float[])((float[])input[channel]);
         float2byteGeneric(data, inOffset, output, outByteOffset, format.getFrameSize(), frameCount, format, ditherBits);
         outByteOffset += format.getFrameSize() / format.getChannels();
      }

   }

   static void float2byte(Object[] input, int inOffset, byte[] output, int outByteOffset, int frameCount, int formatCode, int channels, int frameSize, float ditherBits) {
      int sampleSize = frameSize / channels;

      for(int channel = 0; channel < channels; ++channel) {
         float[] data = (float[])((float[])input[channel]);
         float2byteGeneric(data, inOffset, output, outByteOffset, frameSize, frameCount, formatCode, ditherBits);
         outByteOffset += sampleSize;
      }

   }

   public static void float2byteInterleaved(float[] input, int inOffset, byte[] output, int outByteOffset, int frameCount, AudioFormat format, float ditherBits) {
      float2byteGeneric(input, inOffset, output, outByteOffset, format.getFrameSize() / format.getChannels(), frameCount * format.getChannels(), format, ditherBits);
   }

   static void float2byteGeneric(float[] input, int inOffset, byte[] output, int outByteOffset, int outByteStep, int sampleCount, AudioFormat format, float ditherBits) {
      int formatType = getFormatType(format);
      float2byteGeneric(input, inOffset, output, outByteOffset, outByteStep, sampleCount, formatType, ditherBits);
   }

   static void float2byteGeneric(float[] input, int inOffset, byte[] output, int outByteOffset, int outByteStep, int sampleCount, int formatType, float ditherBits) {
      if(inOffset >= 0 && inOffset + sampleCount <= input.length && sampleCount >= 0) {
         if(outByteOffset >= 0 && outByteOffset + sampleCount * outByteStep < output.length + outByteStep && outByteStep >= getSampleSize(formatType)) {
            if(ditherBits != 0.0F && random == null) {
               random = new Random();
            }

            int endSample = inOffset + sampleCount;
            int outIndex = outByteOffset;

            for(int inIndex = inOffset; inIndex < endSample; outIndex += outByteStep) {
               int iSample;
               switch(formatType) {
               case 1:
                  output[outIndex] = (byte)(quantize8(input[inIndex] * 128.0F, ditherBits) + 128);
                  break;
               case 2:
               case 3:
               case 4:
               case 5:
               case 6:
               case 7:
               case 8:
               case 14:
               case 15:
               case 16:
               case 17:
               case 18:
               case 19:
               case 20:
               case 21:
               case 22:
               case 23:
               case 24:
               case 25:
               default:
                  throw new IllegalArgumentException("unsupported format=" + formatType2Str(formatType));
               case 9:
                  output[outIndex] = quantize8(input[inIndex] * 128.0F, ditherBits);
                  break;
               case 10:
                  iSample = quantize16(input[inIndex] * 32768.0F, ditherBits);
                  output[outIndex + 1] = (byte)(iSample >> 8);
                  output[outIndex] = (byte)(iSample & 255);
                  break;
               case 11:
                  iSample = quantize24(input[inIndex] * 8388608.0F, ditherBits);
                  output[outIndex + 2] = (byte)(iSample >> 16);
                  output[outIndex + 1] = (byte)(iSample >>> 8 & 255);
                  output[outIndex] = (byte)(iSample & 255);
                  break;
               case 12:
                  iSample = quantize24(input[inIndex] * 8388608.0F, ditherBits);
                  output[outIndex + 3] = (byte)(iSample >> 16);
                  output[outIndex + 2] = (byte)(iSample >>> 8 & 255);
                  output[outIndex + 1] = (byte)(iSample & 255);
                  output[outIndex + 0] = 0;
                  break;
               case 13:
                  iSample = quantize32(input[inIndex] * 2.14748365E9F, ditherBits);
                  output[outIndex + 3] = (byte)(iSample >> 24);
                  output[outIndex + 2] = (byte)(iSample >>> 16 & 255);
                  output[outIndex + 1] = (byte)(iSample >>> 8 & 255);
                  output[outIndex] = (byte)(iSample & 255);
                  break;
               case 26:
                  iSample = quantize16(input[inIndex] * 32768.0F, ditherBits);
                  output[outIndex] = (byte)(iSample >> 8);
                  output[outIndex + 1] = (byte)(iSample & 255);
                  break;
               case 27:
                  iSample = quantize24(input[inIndex] * 8388608.0F, ditherBits);
                  output[outIndex] = (byte)(iSample >> 16);
                  output[outIndex + 1] = (byte)(iSample >>> 8 & 255);
                  output[outIndex + 2] = (byte)(iSample & 255);
                  break;
               case 28:
                  iSample = quantize24(input[inIndex] * 8388608.0F, ditherBits);
                  output[outIndex + 0] = 0;
                  output[outIndex + 1] = (byte)(iSample >> 16);
                  output[outIndex + 2] = (byte)(iSample >>> 8 & 255);
                  output[outIndex + 3] = (byte)(iSample & 255);
                  break;
               case 29:
                  iSample = quantize32(input[inIndex] * 2.14748365E9F, ditherBits);
                  output[outIndex] = (byte)(iSample >> 24);
                  output[outIndex + 1] = (byte)(iSample >>> 16 & 255);
                  output[outIndex + 2] = (byte)(iSample >>> 8 & 255);
                  output[outIndex + 3] = (byte)(iSample & 255);
               }

               ++inIndex;
            }

         } else {
            throw new IllegalArgumentException("invalid output index: output.length=" + output.length + " outByteOffset=" + outByteOffset + " outByteStep=" + outByteStep + " sampleCount=" + sampleCount + " format=" + formatType2Str(formatType));
         }
      } else {
         throw new IllegalArgumentException("invalid input index: input.length=" + input.length + " inOffset=" + inOffset + " sampleCount=" + sampleCount);
      }
   }

}
