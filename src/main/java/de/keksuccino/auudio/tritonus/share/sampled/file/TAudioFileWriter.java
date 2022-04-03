package de.keksuccino.auudio.tritonus.share.sampled.file;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.spi.AudioFileWriter;
import de.keksuccino.auudio.tritonus.share.ArraySet;
import de.keksuccino.auudio.tritonus.share.sampled.AudioFormats;
import de.keksuccino.auudio.tritonus.share.sampled.AudioUtils;
import de.keksuccino.auudio.tritonus.share.sampled.TConversionTool;

public abstract class TAudioFileWriter extends AudioFileWriter {

   protected static final int ALL = -1;
   protected static final Encoding PCM_SIGNED = Encoding.PCM_SIGNED;
   protected static final Encoding PCM_UNSIGNED = Encoding.PCM_UNSIGNED;
   private static final int BUFFER_LENGTH = 16384;
   protected static final Type[] NULL_TYPE_ARRAY = new Type[0];
   private Collection m_audioFileTypes;
   private Collection m_audioFormats;


   protected TAudioFileWriter(Collection fileTypes, Collection audioFormats) {

      this.m_audioFileTypes = fileTypes;
      this.m_audioFormats = audioFormats;

   }

   public Type[] getAudioFileTypes() {
      return (Type[])this.m_audioFileTypes.toArray(NULL_TYPE_ARRAY);
   }

   public boolean isFileTypeSupported(Type fileType) {
      return this.m_audioFileTypes.contains(fileType);
   }

   public Type[] getAudioFileTypes(AudioInputStream audioInputStream) {
      AudioFormat format = audioInputStream.getFormat();
      ArraySet res = new ArraySet();
      Iterator it = this.m_audioFileTypes.iterator();

      while(it.hasNext()) {
         Type thisType = (Type)it.next();
         if(this.isAudioFormatSupportedImpl(format, thisType)) {
            res.add(thisType);
         }
      }

      return (Type[])res.toArray(NULL_TYPE_ARRAY);
   }

   public boolean isFileTypeSupported(Type fileType, AudioInputStream audioInputStream) {
      return this.isFileTypeSupported(fileType) && (this.isAudioFormatSupportedImpl(audioInputStream.getFormat(), fileType) || this.findConvertableFormat(audioInputStream.getFormat(), fileType) != null);
   }

   public int write(AudioInputStream audioInputStream, Type fileType, File file) throws IOException {

      if(!this.isFileTypeSupported(fileType)) {

         throw new IllegalArgumentException("file type is not supported.");
      } else {
         AudioFormat inputFormat = audioInputStream.getFormat();

         AudioFormat outputFormat = null;
         boolean bNeedsConversion = false;
         if(this.isAudioFormatSupportedImpl(inputFormat, fileType)) {

            outputFormat = inputFormat;
            bNeedsConversion = false;
         } else {

            outputFormat = this.findConvertableFormat(inputFormat, fileType);
            if(outputFormat == null) {

               throw new IllegalArgumentException("format not supported and not convertable");
            }

            bNeedsConversion = true;
            if(outputFormat.getSampleSizeInBits() == 8 && outputFormat.getEncoding().equals(inputFormat.getEncoding())) {
               bNeedsConversion = false;
            }
         }

         long lLengthInBytes = AudioUtils.getLengthInBytes(audioInputStream);
         TSeekableDataOutputStream dataOutputStream = new TSeekableDataOutputStream(file);
         AudioOutputStream audioOutputStream = this.getAudioOutputStream(outputFormat, lLengthInBytes, fileType, dataOutputStream);
         int written = this.writeImpl(audioInputStream, audioOutputStream, bNeedsConversion);

         return written;
      }
   }

   public int write(AudioInputStream audioInputStream, Type fileType, OutputStream outputStream) throws IOException {
      if(!this.isFileTypeSupported(fileType)) {
         throw new IllegalArgumentException("file type is not supported.");
      } else {

         AudioFormat inputFormat = audioInputStream.getFormat();

         AudioFormat outputFormat = null;
         boolean bNeedsConversion = false;
         if(this.isAudioFormatSupportedImpl(inputFormat, fileType)) {

            outputFormat = inputFormat;
            bNeedsConversion = false;
         } else {

            outputFormat = this.findConvertableFormat(inputFormat, fileType);
            if(outputFormat == null) {

               throw new IllegalArgumentException("format not supported and not convertable");
            }

            bNeedsConversion = true;
            if(outputFormat.getSampleSizeInBits() == 8 && outputFormat.getEncoding().equals(inputFormat.getEncoding())) {
               bNeedsConversion = false;
            }
         }

         long lLengthInBytes = AudioUtils.getLengthInBytes(audioInputStream);
         TNonSeekableDataOutputStream dataOutputStream = new TNonSeekableDataOutputStream(outputStream);
         AudioOutputStream audioOutputStream = this.getAudioOutputStream(outputFormat, lLengthInBytes, fileType, dataOutputStream);
         int written = this.writeImpl(audioInputStream, audioOutputStream, bNeedsConversion);

         return written;
      }
   }

   protected int writeImpl(AudioInputStream audioInputStream, AudioOutputStream audioOutputStream, boolean bNeedsConversion) throws IOException {

      int nTotalWritten = 0;
      AudioFormat outputFormat = audioOutputStream.getFormat();
      int nBytesPerSample = outputFormat.getFrameSize() / outputFormat.getChannels();
      int nBufferSize = 16384 / outputFormat.getFrameSize() * outputFormat.getFrameSize();
      byte[] abBuffer = new byte[nBufferSize];

      while(true) {

         int nBytesRead = audioInputStream.read(abBuffer);

         if(nBytesRead == -1) {

            audioOutputStream.close();
            return nTotalWritten;
         }

         if(bNeedsConversion) {
            TConversionTool.changeOrderOrSign(abBuffer, 0, nBytesRead, nBytesPerSample);
         }

         int nWritten = audioOutputStream.write(abBuffer, 0, nBytesRead);
         nTotalWritten += nWritten;
      }
   }

   protected Iterator getSupportedAudioFormats(Type fileType) {
      return this.m_audioFormats.iterator();
   }

   protected boolean isAudioFormatSupportedImpl(AudioFormat audioFormat, Type fileType) {

      Iterator audioFormats = this.getSupportedAudioFormats(fileType);

      AudioFormat handledFormat;
      do {
         if(!audioFormats.hasNext()) {

            return false;
         }

         handledFormat = (AudioFormat)audioFormats.next();

      } while(!AudioFormats.matches(handledFormat, audioFormat));

      return true;
   }

   protected abstract AudioOutputStream getAudioOutputStream(AudioFormat var1, long var2, Type var4, TDataOutputStream var5) throws IOException;

   private AudioFormat findConvertableFormat(AudioFormat inputFormat, Type fileType) {

      if(!this.isFileTypeSupported(fileType)) {

         return null;
      } else {
         Encoding inputEncoding = inputFormat.getEncoding();
         AudioFormat outputFormat;
         if((inputEncoding.equals(PCM_SIGNED) || inputEncoding.equals(PCM_UNSIGNED)) && inputFormat.getSampleSizeInBits() == 8) {
            outputFormat = this.convertFormat(inputFormat, true, false);

            if(this.isAudioFormatSupportedImpl(outputFormat, fileType)) {

               return outputFormat;
            } else {
               outputFormat = this.convertFormat(inputFormat, false, true);

               if(this.isAudioFormatSupportedImpl(outputFormat, fileType)) {

                  return outputFormat;
               } else {
                  outputFormat = this.convertFormat(inputFormat, true, true);

                  if(this.isAudioFormatSupportedImpl(outputFormat, fileType)) {

                     return outputFormat;
                  } else {

                     return null;
                  }
               }
            }
         } else if(inputEncoding.equals(PCM_SIGNED) && (inputFormat.getSampleSizeInBits() == 16 || inputFormat.getSampleSizeInBits() == 24 || inputFormat.getSampleSizeInBits() == 32)) {
            outputFormat = this.convertFormat(inputFormat, false, true);

            if(this.isAudioFormatSupportedImpl(outputFormat, fileType)) {

               return outputFormat;
            } else {

               return null;
            }
         } else {

            return null;
         }
      }
   }

   private AudioFormat convertFormat(AudioFormat format, boolean changeSign, boolean changeEndian) {
      Encoding enc = PCM_SIGNED;
      if(format.getEncoding().equals(PCM_UNSIGNED) != changeSign) {
         enc = PCM_UNSIGNED;
      }

      return new AudioFormat(enc, format.getSampleRate(), format.getSampleSizeInBits(), format.getChannels(), format.getFrameSize(), format.getFrameRate(), format.isBigEndian() ^ changeEndian);
   }

}
