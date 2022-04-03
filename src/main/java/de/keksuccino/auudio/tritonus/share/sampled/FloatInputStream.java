package de.keksuccino.auudio.tritonus.share.sampled;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public class FloatInputStream extends AudioInputStream implements FloatSampleInput {

   private InputStream sourceStream;
   private FloatSampleInput sourceInput;
   private boolean eofReached = false;
   private byte[] tempBuffer = null;
   private FloatSampleBuffer tempFloatBuffer = null;


   public FloatInputStream(AudioInputStream sourceStream) {
      super(sourceStream, sourceStream.getFormat(), sourceStream.getFrameLength());
      this.sourceStream = sourceStream;
      this.init();
   }

   public FloatInputStream(InputStream sourceStream, AudioFormat format, long frameLength) {
      super(sourceStream, format, frameLength);
      this.sourceStream = sourceStream;
      this.init();
   }

   public FloatInputStream(FloatSampleInput sourceInput, AudioFormat format, long frameLength) {
      super(new ByteArrayInputStream(new byte[0]), format, frameLength);
      this.sourceStream = null;
      this.sourceInput = sourceInput;
      this.init();
   }

   public void read(FloatSampleBuffer outBuffer) {
      this.read(outBuffer, 0, outBuffer.getSampleCount());
   }

   private void init() {
      if(this.sourceStream != null && this.sourceStream instanceof FloatSampleInput) {
         this.sourceInput = (FloatSampleInput)this.sourceStream;
      }

      FloatSampleBuffer.checkFormatSupported(this.format);
   }

   public void read(FloatSampleBuffer buffer, int offset, int sampleCount) {
      if(sampleCount != 0 && !this.isDone()) {
         if(buffer.getChannelCount() != this.getChannels()) {
            throw new IllegalArgumentException("read: passed buffer has different channel count");
         } else {
            if(this.sourceInput != null) {
               this.sourceInput.read(buffer, offset, sampleCount);
            } else {
               int byteBufferSize = buffer.getSampleCount() * this.getFormat().getFrameSize();
               byte[] lTempBuffer = this.tempBuffer;
               if(lTempBuffer == null || byteBufferSize > lTempBuffer.length) {
                  lTempBuffer = new byte[byteBufferSize];
                  this.tempBuffer = lTempBuffer;
               }

               int readSamples = 0;
               int byteOffset = 0;

               while(readSamples < sampleCount) {
                  int readBytes;
                  try {
                     readBytes = this.sourceStream.read(lTempBuffer, byteOffset, byteBufferSize);
                  } catch (IOException var10) {
                     readBytes = -1;
                  }

                  if(readBytes < 0) {
                     this.eofReached = true;
                     boolean readBytes1 = false;
                     break;
                  }

                  if(readBytes == 0) {
                     Thread.yield();
                  } else {
                     readSamples += readBytes / this.getFormat().getFrameSize();
                     byteBufferSize -= readBytes;
                     byteOffset += readBytes;
                  }
               }

               buffer.setSampleCount(offset + readSamples, offset > 0);
               if(readSamples > 0) {
                  buffer.setSamplesFromBytes(lTempBuffer, 0, this.getFormat(), offset, readSamples);
               }
            }

         }
      } else {
         buffer.setSampleCount(offset, true);
      }
   }

   public int getChannels() {
      return this.getFormat().getChannels();
   }

   public float getSampleRate() {
      return this.getFormat().getSampleRate();
   }

   public boolean isDone() {
      return !this.eofReached && this.sourceInput != null?this.sourceInput.isDone():this.eofReached;
   }

   public int read() throws IOException {
      if(this.getFormat().getFrameSize() != 1) {
         throw new IOException("frame size must be 1 to read a single byte");
      } else {
         byte[] temp = new byte[1];
         int result = this.read(temp);
         return result <= 0?-1:temp[0] & 255;
      }
   }

   public int read(byte[] abData) throws IOException {
      return this.read(abData, 0, abData.length);
   }

   public int read(byte[] abData, int nOffset, int nLength) throws IOException {
      return this.isDone()?-1:(this.sourceStream != null?this.readBytesFromInputStream(abData, nOffset, nLength):this.readBytesFromFloatInput(abData, nOffset, nLength));
   }

   protected int readBytesFromInputStream(byte[] abData, int nOffset, int nLength) throws IOException {
      int readBytes = this.sourceStream.read(abData, nOffset, nLength);
      if(readBytes < 0) {
         this.eofReached = true;
      }

      return readBytes;
   }

   protected int readBytesFromFloatInput(byte[] abData, int nOffset, int nLength) throws IOException {
      FloatSampleInput lInput = this.sourceInput;
      if(lInput.isDone()) {
         return -1;
      } else {
         int frameCount = nLength / this.getFormat().getFrameSize();
         FloatSampleBuffer lTempBuffer = this.tempFloatBuffer;
         if(lTempBuffer == null) {
            lTempBuffer = new FloatSampleBuffer(this.getFormat().getChannels(), frameCount, this.getFormat().getSampleRate());
            this.tempFloatBuffer = lTempBuffer;
         } else {
            lTempBuffer.setSampleCount(frameCount, false);
         }

         lInput.read(lTempBuffer);
         if(lInput.isDone()) {
            return -1;
         } else if(abData != null) {
            int writtenBytes = this.tempFloatBuffer.convertToByteArray(abData, nOffset, this.getFormat());
            return writtenBytes;
         } else {
            return frameCount * this.getFormat().getFrameSize();
         }
      }
   }

   public synchronized long skip(long nSkip) throws IOException {
      long skipFrames = nSkip / (long)this.getFormat().getFrameSize();
      return this.sourceStream != null?this.sourceStream.skip(skipFrames * (long)this.getFormat().getFrameSize()):(!this.isDone() && skipFrames > 0L?(long)this.readBytesFromFloatInput((byte[])null, 0, (int)(skipFrames * (long)this.getFormat().getFrameSize())):0L);
   }

   public int available() throws IOException {
      return this.sourceStream != null?this.sourceStream.available():-1;
   }

   public void mark(int readlimit) {
      if(this.sourceStream != null) {
         this.sourceStream.mark(readlimit);
      }

   }

   public void reset() throws IOException {
      if(this.sourceStream != null) {
         this.sourceStream.reset();
      }

   }

   public boolean markSupported() {
      return this.sourceStream != null?this.sourceStream.markSupported():false;
   }

   public void close() throws IOException {
      if(!this.eofReached) {
         this.eofReached = true;
         if(this.sourceStream != null) {
            this.sourceStream.close();
         }

         this.tempBuffer = null;
         this.tempFloatBuffer = null;
      }
   }
}
