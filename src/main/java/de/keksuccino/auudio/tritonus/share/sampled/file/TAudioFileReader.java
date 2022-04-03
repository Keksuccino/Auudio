package de.keksuccino.auudio.tritonus.share.sampled.file;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;

public abstract class TAudioFileReader extends AudioFileReader {

   private int m_nMarkLimit;
   private boolean m_bRereading;


   protected TAudioFileReader(int nMarkLimit) {
      this(nMarkLimit, false);
   }

   protected TAudioFileReader(int nMarkLimit, boolean bRereading) {
      this.m_nMarkLimit = -1;
      this.m_nMarkLimit = nMarkLimit;
      this.m_bRereading = bRereading;
   }

   private int getMarkLimit() {
      return this.m_nMarkLimit;
   }

   private boolean isRereading() {
      return this.m_bRereading;
   }

   public AudioFileFormat getAudioFileFormat(File file) throws UnsupportedAudioFileException, IOException {

      long lFileLengthInBytes = file.length();
      FileInputStream inputStream = new FileInputStream(file);
      AudioFileFormat audioFileFormat = null;

      try {
         audioFileFormat = this.getAudioFileFormat(inputStream, lFileLengthInBytes);
      } finally {
         inputStream.close();
      }

      return audioFileFormat;
   }

   public AudioFileFormat getAudioFileFormat(URL url) throws UnsupportedAudioFileException, IOException {

      long lFileLengthInBytes = getDataLength(url);
      InputStream inputStream = url.openStream();
      AudioFileFormat audioFileFormat = null;

      try {
         audioFileFormat = this.getAudioFileFormat(inputStream, lFileLengthInBytes);
      } finally {
         inputStream.close();
      }

      return audioFileFormat;
   }

   public AudioFileFormat getAudioFileFormat(InputStream inputStream) throws UnsupportedAudioFileException, IOException {

      long lFileLengthInBytes = -1L;
      if(!((InputStream)inputStream).markSupported()) {
         inputStream = new BufferedInputStream((InputStream)inputStream, this.getMarkLimit());
      }

      ((InputStream)inputStream).mark(this.getMarkLimit());
      AudioFileFormat audioFileFormat = null;

      try {
         audioFileFormat = this.getAudioFileFormat((InputStream)inputStream, lFileLengthInBytes);
      } finally {
         ((InputStream)inputStream).reset();
      }

      return audioFileFormat;
   }

   protected abstract AudioFileFormat getAudioFileFormat(InputStream var1, long var2) throws UnsupportedAudioFileException, IOException;

   public AudioInputStream getAudioInputStream(File file) throws UnsupportedAudioFileException, IOException {

      long lFileLengthInBytes = file.length();
      FileInputStream inputStream = new FileInputStream(file);
      AudioInputStream audioInputStream = null;

      try {
         audioInputStream = this.getAudioInputStream(inputStream, lFileLengthInBytes);
      } catch (UnsupportedAudioFileException var7) {
         inputStream.close();
         throw var7;
      } catch (IOException var8) {
         inputStream.close();
         throw var8;
      }

      return audioInputStream;
   }

   public AudioInputStream getAudioInputStream(URL url) throws UnsupportedAudioFileException, IOException {

      long lFileLengthInBytes = getDataLength(url);
      InputStream inputStream = url.openStream();
      AudioInputStream audioInputStream = null;

      try {
         audioInputStream = this.getAudioInputStream(inputStream, lFileLengthInBytes);
      } catch (UnsupportedAudioFileException var7) {
         inputStream.close();
         throw var7;
      } catch (IOException var8) {
         inputStream.close();
         throw var8;
      }

      return audioInputStream;
   }

   public AudioInputStream getAudioInputStream(InputStream inputStream) throws UnsupportedAudioFileException, IOException {

      long lFileLengthInBytes = -1L;
      AudioInputStream audioInputStream = null;
      if(!((InputStream)inputStream).markSupported()) {
         inputStream = new BufferedInputStream((InputStream)inputStream, this.getMarkLimit());
      }

      ((InputStream)inputStream).mark(this.getMarkLimit());

      try {
         audioInputStream = this.getAudioInputStream((InputStream)inputStream, lFileLengthInBytes);
      } catch (UnsupportedAudioFileException var8) {
         ((InputStream)inputStream).reset();
         throw var8;
      } catch (IOException var9) {
         try {
            ((InputStream)inputStream).reset();
         } catch (IOException var7) {
            if(var7.getCause() == null) {
               var7.initCause(var9);
               throw var7;
            }
         }

         throw var9;
      }

      return audioInputStream;
   }

   protected AudioInputStream getAudioInputStream(InputStream inputStream, long lFileLengthInBytes) throws UnsupportedAudioFileException, IOException {

      if(this.isRereading()) {
         if(!((InputStream)inputStream).markSupported()) {
            inputStream = new BufferedInputStream((InputStream)inputStream, this.getMarkLimit());
         }

         ((InputStream)inputStream).mark(this.getMarkLimit());
      }

      AudioFileFormat audioFileFormat = this.getAudioFileFormat((InputStream)inputStream, lFileLengthInBytes);
      if(this.isRereading()) {
         ((InputStream)inputStream).reset();
      }

      AudioInputStream audioInputStream = new AudioInputStream((InputStream)inputStream, audioFileFormat.getFormat(), (long)audioFileFormat.getFrameLength());

      return audioInputStream;
   }

   protected static int calculateFrameSize(int nSampleSize, int nNumChannels) {
      return (nSampleSize + 7) / 8 * nNumChannels;
   }

   private static long getDataLength(URL url) throws IOException {
      long lFileLengthInBytes = -1L;
      URLConnection connection = url.openConnection();
      connection.connect();
      int nLength = connection.getContentLength();
      if(nLength > 0) {
         lFileLengthInBytes = (long)nLength;
      }

      return lFileLengthInBytes;
   }

   public static int readLittleEndianInt(InputStream is) throws IOException {
      int b0 = is.read();
      int b1 = is.read();
      int b2 = is.read();
      int b3 = is.read();
      if((b0 | b1 | b2 | b3) < 0) {
         throw new EOFException();
      } else {
         return (b3 << 24) + (b2 << 16) + (b1 << 8) + (b0 << 0);
      }
   }

   public static short readLittleEndianShort(InputStream is) throws IOException {
      int b0 = is.read();
      int b1 = is.read();
      if((b0 | b1) < 0) {
         throw new EOFException();
      } else {
         return (short)((b1 << 8) + (b0 << 0));
      }
   }

   public static double readIeeeExtended(DataInputStream dis) throws IOException {
      double f = 0.0D;
      boolean expon = false;
      long hiMant = 0L;
      long loMant = 0L;
      double HUGE = 3.4028234663852886E38D;
      int expon1 = dis.readUnsignedShort();
      long t1 = (long)dis.readUnsignedShort();
      long t2 = (long)dis.readUnsignedShort();
      hiMant = t1 << 16 | t2;
      t1 = (long)dis.readUnsignedShort();
      t2 = (long)dis.readUnsignedShort();
      loMant = t1 << 16 | t2;
      if(expon1 == 0 && hiMant == 0L && loMant == 0L) {
         f = 0.0D;
      } else if(expon1 == 32767) {
         f = HUGE;
      } else {
         expon1 -= 16383;
         expon1 -= 31;
         f = (double)hiMant * Math.pow(2.0D, (double)expon1);
         expon1 -= 32;
         f += (double)loMant * Math.pow(2.0D, (double)expon1);
      }

      return f;
   }
}
