package de.keksuccino.auudio.tritonus.share.sampled;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFileFormat.Type;

import de.keksuccino.auudio.tritonus.sampled.file.AiffAudioOutputStream;
import de.keksuccino.auudio.tritonus.sampled.file.AuAudioOutputStream;
import de.keksuccino.auudio.tritonus.sampled.file.WaveAudioOutputStream;
import de.keksuccino.auudio.tritonus.share.sampled.file.AudioOutputStream;
import de.keksuccino.auudio.tritonus.share.sampled.file.TDataOutputStream;
import de.keksuccino.auudio.tritonus.share.sampled.file.TNonSeekableDataOutputStream;
import de.keksuccino.auudio.tritonus.share.sampled.file.TSeekableDataOutputStream;

public class AudioSystemShadow {

   public static TDataOutputStream getDataOutputStream(File file) throws IOException {
      return new TSeekableDataOutputStream(file);
   }

   public static TDataOutputStream getDataOutputStream(OutputStream stream) throws IOException {
      return new TNonSeekableDataOutputStream(stream);
   }

   public static AudioOutputStream getAudioOutputStream(Type type, AudioFormat audioFormat, long lLengthInBytes, TDataOutputStream dataOutputStream) {
      Object audioOutputStream = null;
      if(!type.equals(Type.AIFF) && !type.equals(Type.AIFF)) {
         if(type.equals(Type.AU)) {
            audioOutputStream = new AuAudioOutputStream(audioFormat, lLengthInBytes, dataOutputStream);
         } else if(type.equals(Type.WAVE)) {
            audioOutputStream = new WaveAudioOutputStream(audioFormat, lLengthInBytes, dataOutputStream);
         }
      } else {
         audioOutputStream = new AiffAudioOutputStream(audioFormat, type, lLengthInBytes, dataOutputStream);
      }

      return (AudioOutputStream)audioOutputStream;
   }

   public static AudioOutputStream getAudioOutputStream(Type type, AudioFormat audioFormat, long lLengthInBytes, File file) throws IOException {
      TDataOutputStream dataOutputStream = getDataOutputStream(file);
      AudioOutputStream audioOutputStream = getAudioOutputStream(type, audioFormat, lLengthInBytes, dataOutputStream);
      return audioOutputStream;
   }

   public static AudioOutputStream getAudioOutputStream(Type type, AudioFormat audioFormat, long lLengthInBytes, OutputStream outputStream) throws IOException {
      TDataOutputStream dataOutputStream = getDataOutputStream(outputStream);
      AudioOutputStream audioOutputStream = getAudioOutputStream(type, audioFormat, lLengthInBytes, dataOutputStream);
      return audioOutputStream;
   }
}
