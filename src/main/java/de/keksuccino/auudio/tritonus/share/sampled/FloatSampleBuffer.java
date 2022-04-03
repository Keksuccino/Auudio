package de.keksuccino.auudio.tritonus.share.sampled;

import javax.sound.sampled.AudioFormat;

public class FloatSampleBuffer {

   private static final boolean LAZY_DEFAULT = true;
   private Object[] channels;
   private int sampleCount;
   private int channelCount;
   private float sampleRate;
   private int originalFormatType;
   public static final int DITHER_MODE_AUTOMATIC = 0;
   public static final int DITHER_MODE_ON = 1;
   public static final int DITHER_MODE_OFF = 2;
   private float ditherBits;
   private int ditherMode;
   private AudioFormat lastConvertToByteArrayFormat;
   private int lastConvertToByteArrayFormatCode;


   public FloatSampleBuffer() {
      this(0, 0, 1.0F);
   }

   public FloatSampleBuffer(int channelCount, int sampleCount, float sampleRate) {
      this.channels = new Object[2];
      this.sampleCount = 0;
      this.channelCount = 0;
      this.sampleRate = 0.0F;
      this.originalFormatType = 0;
      this.ditherBits = 0.7F;
      this.ditherMode = 0;
      this.lastConvertToByteArrayFormat = null;
      this.lastConvertToByteArrayFormatCode = 0;
      this.init(channelCount, sampleCount, sampleRate, true);
   }

   public FloatSampleBuffer(byte[] buffer, int offset, int byteCount, AudioFormat format) {
      this(format.getChannels(), byteCount / (format.getSampleSizeInBits() / 8 * format.getChannels()), format.getSampleRate());
      this.initFromByteArray(buffer, offset, byteCount, format);
   }

   public void init(int newChannelCount, int newSampleCount, float newSampleRate) {
      this.init(newChannelCount, newSampleCount, newSampleRate, true);
   }

   public void init(int newChannelCount, int newSampleCount, float newSampleRate, boolean lazy) {
      if(newChannelCount >= 0 && newSampleCount >= 0 && newSampleRate > 0.0F) {
         this.setSampleRate(newSampleRate);
         if(this.sampleCount != newSampleCount || this.channelCount != newChannelCount) {
            this.createChannels(newChannelCount, newSampleCount, lazy);
         }

      } else {
         throw new IllegalArgumentException("invalid parameters in initialization of FloatSampleBuffer.");
      }
   }

   public static void checkFormatSupported(AudioFormat format) {
      FloatSampleTools.getFormatType(format);
   }

   private final void grow(int newChannelCount, boolean lazy) {
      if(this.channels.length < newChannelCount || !lazy) {
         Object[] newChannels = new Object[newChannelCount];
         System.arraycopy(this.channels, 0, newChannels, 0, this.channelCount < newChannelCount?this.channelCount:newChannelCount);
         this.channels = newChannels;
      }

   }

   private final void createChannels(int newChannelCount, int newSampleCount, boolean lazy) {
      if(lazy && newChannelCount <= this.channelCount && newSampleCount <= this.sampleCount) {
         this.setSampleCountImpl(newSampleCount);
         this.setChannelCountImpl(newChannelCount);
      } else {
         this.setSampleCountImpl(newSampleCount);
         this.grow(newChannelCount, true);
         this.setChannelCountImpl(0);

         for(int ch = 0; ch < newChannelCount; ++ch) {
            this.insertChannel(ch, false, lazy);
         }

         this.grow(newChannelCount, lazy);
      }
   }

   public void initFromByteArray(byte[] buffer, int offset, int byteCount, AudioFormat format) {
      this.initFromByteArray(buffer, offset, byteCount, format, true);
   }

   public void initFromByteArray(byte[] buffer, int offset, int byteCount, AudioFormat format, boolean lazy) {
      if(offset + byteCount > buffer.length) {
         throw new IllegalArgumentException("FloatSampleBuffer.initFromByteArray: buffer too small.");
      } else {
         int thisSampleCount = byteCount / format.getFrameSize();
         this.init(format.getChannels(), thisSampleCount, format.getSampleRate(), lazy);
         this.originalFormatType = FloatSampleTools.getFormatType(format);
         FloatSampleTools.byte2float(buffer, offset, this.channels, 0, this.sampleCount, format);
      }
   }

   public void initFromFloatSampleBuffer(FloatSampleBuffer source) {
      this.init(source.getChannelCount(), source.getSampleCount(), source.getSampleRate());

      for(int ch = 0; ch < this.getChannelCount(); ++ch) {
         System.arraycopy(source.getChannel(ch), 0, this.getChannel(ch), 0, this.sampleCount);
      }

   }

   public int writeByteBuffer(byte[] buffer, int srcByteOffset, AudioFormat format, int dstSampleOffset, int aSampleCount) {
      if(dstSampleOffset + aSampleCount > this.getSampleCount()) {
         aSampleCount = this.getSampleCount() - dstSampleOffset;
      }

      int lChannels = format.getChannels();
      if(lChannels > this.getChannelCount()) {
         lChannels = this.getChannelCount();
      }

      if(lChannels > format.getChannels()) {
         lChannels = format.getChannels();
      }

      for(int channel = 0; channel < lChannels; ++channel) {
         float[] data = this.getChannel(channel);
         FloatSampleTools.byte2floatGeneric(buffer, srcByteOffset, format.getFrameSize(), data, dstSampleOffset, aSampleCount, format);
         srcByteOffset += format.getFrameSize() / format.getChannels();
      }

      return aSampleCount;
   }

   public void reset() {
      this.init(0, 0, 1.0F, false);
   }

   public void reset(int newChannels, int newSampleCount, float newSampleRate) {
      this.init(newChannels, newSampleCount, newSampleRate, false);
   }

   public int getByteArrayBufferSize(AudioFormat format) {
      return this.getByteArrayBufferSize(format, this.getSampleCount());
   }

   public int getByteArrayBufferSize(AudioFormat format, int lenInSamples) {
      checkFormatSupported(format);
      return format.getFrameSize() * lenInSamples;
   }

   public int convertToByteArray(byte[] buffer, int offset, AudioFormat format) {
      return this.convertToByteArray(0, this.getSampleCount(), buffer, offset, format);
   }

   public int convertToByteArray(int readOffset, int lenInSamples, byte[] buffer, int writeOffset, AudioFormat format) {
      int byteCount = format.getFrameSize() * lenInSamples;
      if(writeOffset + byteCount > buffer.length) {
         throw new IllegalArgumentException("FloatSampleBuffer.convertToByteArray: buffer too small.");
      } else {
         if(format != this.lastConvertToByteArrayFormat) {
            if(format.getSampleRate() != this.getSampleRate()) {
               throw new IllegalArgumentException("FloatSampleBuffer.convertToByteArray: different samplerates.");
            }

            if(format.getChannels() != this.getChannelCount()) {
               throw new IllegalArgumentException("FloatSampleBuffer.convertToByteArray: different channel count.");
            }

            this.lastConvertToByteArrayFormat = format;
            this.lastConvertToByteArrayFormatCode = FloatSampleTools.getFormatType(format);
         }

         FloatSampleTools.float2byte(this.channels, readOffset, buffer, writeOffset, lenInSamples, this.lastConvertToByteArrayFormatCode, format.getChannels(), format.getFrameSize(), this.getConvertDitherBits(this.lastConvertToByteArrayFormatCode));
         return byteCount;
      }
   }

   public byte[] convertToByteArray(AudioFormat format) {
      byte[] res = new byte[this.getByteArrayBufferSize(format)];
      this.convertToByteArray(res, 0, format);
      return res;
   }

   public void changeSampleCount(int newSampleCount, boolean keepOldSamples) {
      int oldSampleCount = this.getSampleCount();
      if(oldSampleCount >= newSampleCount) {
         this.setSampleCountImpl(newSampleCount);
      } else {
         int var11;
         if(this.channelCount != 1 && this.channelCount != 2) {
            Object[] var10 = null;
            if(keepOldSamples) {
               var10 = this.getAllChannels();
            }

            this.init(this.getChannelCount(), newSampleCount, this.getSampleRate());
            if(keepOldSamples) {
               var11 = newSampleCount < oldSampleCount?newSampleCount:oldSampleCount;

               for(int ch = 0; ch < this.channelCount; ++ch) {
                  float[] oldSamples = (float[])((float[])var10[ch]);
                  float[] newSamples = (float[])((float[])this.channels[ch]);
                  if(oldSamples != newSamples) {
                     System.arraycopy(oldSamples, 0, newSamples, 0, var11);
                  }

                  if(oldSampleCount < newSampleCount) {
                     for(int i = oldSampleCount; i < newSampleCount; ++i) {
                        newSamples[i] = 0.0F;
                     }
                  }
               }
            }

         } else {
            float[] oldChannels = this.getChannel(0);
            float[] copyCount;
            if(oldChannels.length < newSampleCount) {
               copyCount = new float[newSampleCount];
               if(keepOldSamples && oldSampleCount > 0) {
                  System.arraycopy(oldChannels, 0, copyCount, 0, oldSampleCount);
               }

               this.channels[0] = copyCount;
            } else if(keepOldSamples) {
               for(var11 = oldSampleCount; var11 < newSampleCount; ++var11) {
                  oldChannels[var11] = 0.0F;
               }
            }

            if(this.channelCount == 2) {
               oldChannels = this.getChannel(1);
               if(oldChannels.length < newSampleCount) {
                  copyCount = new float[newSampleCount];
                  if(keepOldSamples && oldSampleCount > 0) {
                     System.arraycopy(oldChannels, 0, copyCount, 0, oldSampleCount);
                  }

                  this.channels[1] = copyCount;
               } else if(keepOldSamples) {
                  for(var11 = oldSampleCount; var11 < newSampleCount; ++var11) {
                     oldChannels[var11] = 0.0F;
                  }
               }
            }

            this.setSampleCountImpl(newSampleCount);
         }
      }
   }

   public void makeSilence() {
      this.makeSilence(0, this.getSampleCount());
   }

   public void makeSilence(int offset, int count) {
      if(offset >= 0 && count + offset <= this.getSampleCount() && count >= 0) {
         int localChannelCount = this.getChannelCount();

         for(int ch = 0; ch < localChannelCount; ++ch) {
            this.makeSilence(this.getChannel(ch), offset, count);
         }

      } else {
         throw new IllegalArgumentException("offset and/or sampleCount out of bounds");
      }
   }

   public void makeSilence(int channel) {
      this.makeSilence(channel, 0, this.getSampleCount());
   }

   public void makeSilence(int channel, int offset, int count) {
      if(offset >= 0 && count + offset <= this.getSampleCount() && count >= 0) {
         this.makeSilence(this.getChannel(channel), offset, count);
      } else {
         throw new IllegalArgumentException("offset and/or sampleCount out of bounds");
      }
   }

   private void makeSilence(float[] samples, int offset, int count) {
      count += offset;

      for(int i = offset; i < count; ++i) {
         samples[i] = 0.0F;
      }

   }

   public void linearFade(float startVol, float endVol) {
      this.linearFade(startVol, endVol, 0, this.getSampleCount());
   }

   public void linearFade(float startVol, float endVol, int offset, int count) {
      for(int channel = 0; channel < this.getChannelCount(); ++channel) {
         this.linearFade(channel, startVol, endVol, offset, count);
      }

   }

   public void linearFade(int channel, float startVol, float endVol, int offset, int count) {
      if(count > 0) {
         float end = (float)(count + offset);
         float inc = (endVol - startVol) / (float)count;
         float[] samples = this.getChannel(channel);
         float curr = startVol;

         for(int i = offset; (float)i < end; ++i) {
            samples[i] *= curr;
            curr += inc;
         }

      }
   }

   public void addChannel(boolean silent) {
      this.insertChannel(this.getChannelCount(), silent);
   }

   public void insertChannel(int index, boolean silent) {
      this.insertChannel(index, silent, true);
   }

   public void insertChannel(int index, boolean silent, boolean lazy) {
      this.grow(this.channelCount + 1, true);
      int physSize = this.channels.length;
      int virtSize = this.channelCount;
      float[] newChannel = null;
      int i;
      if(physSize > virtSize) {
         for(i = virtSize; i < physSize; ++i) {
            float[] thisChannel = (float[])((float[])this.channels[i]);
            if(thisChannel != null && (lazy && thisChannel.length >= this.getSampleCount() || !lazy && thisChannel.length == this.getSampleCount())) {
               newChannel = thisChannel;
               this.channels[i] = null;
               break;
            }
         }
      }

      if(newChannel == null) {
         newChannel = new float[this.getSampleCount()];
      }

      for(i = index; i < virtSize; ++i) {
         this.channels[i + 1] = this.channels[i];
      }

      this.channels[index] = newChannel;
      this.setChannelCountImpl(this.channelCount + 1);
      if(silent) {
         this.makeSilence(index);
      }

      this.grow(this.channelCount, lazy);
   }

   public void removeChannel(int channel) {
      this.removeChannel(channel, true);
   }

   public void removeChannel(int channel, boolean lazy) {
      float[] toBeDeleted = (float[])((float[])this.channels[channel]);

      for(int i = channel; i < this.channelCount - 1; ++i) {
         this.channels[i] = this.channels[i + 1];
      }

      if(!lazy) {
         this.grow(this.channelCount - 1, true);
      } else {
         this.channels[this.channelCount - 1] = toBeDeleted;
      }

      this.setChannelCountImpl(this.channelCount - 1);
   }

   public void copyChannel(int sourceChannel, int targetChannel) {
      float[] source = this.getChannel(sourceChannel);
      float[] target = this.getChannel(targetChannel);
      System.arraycopy(source, 0, target, 0, this.getSampleCount());
   }

   public void copyChannel(int sourceChannel, int sourceOffset, int targetChannel, int targetOffset, int aSampleCount) {
      float[] source = this.getChannel(sourceChannel);
      float[] target = this.getChannel(targetChannel);
      System.arraycopy(source, sourceOffset, target, targetOffset, aSampleCount);
   }

   public void copy(int sourceIndex, int destIndex, int length) {
      int count = this.getChannelCount();

      for(int i = 0; i < count; ++i) {
         this.copy(i, sourceIndex, destIndex, length);
      }

   }

   public void copy(int channel, int sourceIndex, int destIndex, int length) {
      float[] data = this.getChannel(channel);
      int bufferCount = this.getSampleCount();
      if(sourceIndex + length <= bufferCount && destIndex + length <= bufferCount && sourceIndex >= 0 && destIndex >= 0 && length >= 0) {
         System.arraycopy(data, sourceIndex, data, destIndex, length);
      } else {
         throw new IndexOutOfBoundsException("parameters exceed buffer size");
      }
   }

   public void expandChannel(int targetChannelCount) {
      if(this.getChannelCount() != 1) {
         throw new IllegalArgumentException("FloatSampleBuffer: can only expand channels for mono signals.");
      } else {
         for(int ch = 1; ch < targetChannelCount; ++ch) {
            this.addChannel(false);
            this.copyChannel(0, ch);
         }

      }
   }

   public void mixDownChannels() {
      float[] firstChannel = this.getChannel(0);
      int localSampleCount = this.getSampleCount();

      for(int ch = this.getChannelCount() - 1; ch > 0; --ch) {
         float[] thisChannel = this.getChannel(ch);

         for(int i = 0; i < localSampleCount; ++i) {
            firstChannel[i] += thisChannel[i];
         }

         this.removeChannel(ch);
      }

   }

   public void mix(FloatSampleBuffer source) {
      int count = this.getSampleCount();
      if(count > source.getSampleCount()) {
         count = source.getSampleCount();
      }

      int localChannelCount = this.getChannelCount();
      if(localChannelCount > source.getChannelCount()) {
         localChannelCount = source.getChannelCount();
      }

      for(int ch = 0; ch < localChannelCount; ++ch) {
         float[] thisChannel = this.getChannel(ch);
         float[] otherChannel = source.getChannel(ch);

         for(int i = 0; i < count; ++i) {
            thisChannel[i] += otherChannel[i];
         }
      }

   }

   public void mix(FloatSampleBuffer source, int sourceOffset, int thisOffset, int count) {
      int localChannelCount = this.getChannelCount();

      for(int ch = 0; ch < localChannelCount; ++ch) {
         float[] thisChannel = this.getChannel(ch);
         float[] otherChannel = source.getChannel(ch);

         for(int i = 0; i < count; ++i) {
            thisChannel[i + thisOffset] += otherChannel[i + sourceOffset];
         }
      }

   }

   public int copyTo(FloatSampleBuffer dest, int destOffset, int count) {
      return this.copyTo(0, dest, destOffset, count);
   }

   public int copyTo(int srcOffset, FloatSampleBuffer dest, int destOffset, int count) {
      if(srcOffset + count > this.getSampleCount()) {
         count = this.getSampleCount() - srcOffset;
      }

      if(count + destOffset > dest.getSampleCount()) {
         count = dest.getSampleCount() - destOffset;
      }

      int localChannelCount = this.getChannelCount();
      if(localChannelCount > dest.getChannelCount()) {
         localChannelCount = dest.getChannelCount();
      }

      for(int ch = 0; ch < localChannelCount; ++ch) {
         System.arraycopy(this.getChannel(ch), srcOffset, dest.getChannel(ch), destOffset, count);
      }

      return count;
   }

   public void setSamplesFromBytes(byte[] input, int inByteOffset, AudioFormat format, int floatOffset, int frameCount) {
      if(floatOffset >= 0 && frameCount >= 0 && inByteOffset >= 0) {
         if(inByteOffset + frameCount * format.getFrameSize() > input.length) {
            throw new IllegalArgumentException("FloatSampleBuffer.setSamplesFromBytes: input buffer too small.");
         } else if(floatOffset + frameCount > this.getSampleCount()) {
            throw new IllegalArgumentException("FloatSampleBuffer.setSamplesFromBytes: frameCount too large");
         } else {
            FloatSampleTools.byte2float(input, inByteOffset, this.channels, floatOffset, frameCount, format, false);
         }
      } else {
         throw new IllegalArgumentException("FloatSampleBuffer.setSamplesFromBytes: negative inByteOffset, floatOffset, or frameCount");
      }
   }

   public int getChannelCount() {
      return this.channelCount;
   }

   public int getSampleCount() {
      return this.sampleCount;
   }

   public float getSampleRate() {
      return this.sampleRate;
   }

   protected void setChannelCountImpl(int newChannelCount) {
      if(this.channelCount != newChannelCount) {
         this.channelCount = newChannelCount;
         this.lastConvertToByteArrayFormat = null;
      }

   }

   protected void setSampleCountImpl(int newSampleCount) {
      if(this.sampleCount != newSampleCount) {
         this.sampleCount = newSampleCount;
      }

   }

   public void setSampleCount(int newSampleCount, boolean keepOldSamples) {
      this.changeSampleCount(newSampleCount, keepOldSamples);
   }

   public void setSampleRate(float sampleRate) {
      if(sampleRate <= 0.0F) {
         throw new IllegalArgumentException("Invalid samplerate for FloatSampleBuffer.");
      } else {
         if(this.sampleRate != sampleRate) {
            this.sampleRate = sampleRate;
            this.lastConvertToByteArrayFormat = null;
         }

      }
   }

   public float[] getChannel(int channel) {
      if(channel >= this.channelCount) {
         throw new IllegalArgumentException("FloatSampleBuffer: invalid channel number.");
      } else {
         return (float[])((float[])this.channels[channel]);
      }
   }

   public float[] setRawChannel(int channel, float[] data) {
      if(data == null) {
         throw new IllegalArgumentException("cannot set a channel to a null array");
      } else {
         float[] ret = this.getChannel(channel);
         this.channels[channel] = data;
         return ret;
      }
   }

   public Object[] getAllChannels() {
      Object[] res = new Object[this.getChannelCount()];

      for(int ch = 0; ch < this.getChannelCount(); ++ch) {
         res[ch] = this.getChannel(ch);
      }

      return res;
   }

   public void setDitherBits(float ditherBits) {
      if(ditherBits <= 0.0F) {
         throw new IllegalArgumentException("DitherBits must be greater than 0");
      } else {
         this.ditherBits = ditherBits;
      }
   }

   public float getDitherBits() {
      return this.ditherBits;
   }

   public void setDitherMode(int mode) {
      if(mode != 0 && mode != 1 && mode != 2) {
         throw new IllegalArgumentException("Illegal DitherMode");
      } else {
         this.ditherMode = mode;
      }
   }

   public int getDitherMode() {
      return this.ditherMode;
   }

   protected float getConvertDitherBits(int newFormatType) {
      boolean doDither = false;
      switch(this.ditherMode) {
      case 0:
         doDither = (this.originalFormatType & 7) > (newFormatType & 7);
         break;
      case 1:
         doDither = true;
         break;
      case 2:
         doDither = false;
      }

      return doDither?this.ditherBits:0.0F;
   }
}
