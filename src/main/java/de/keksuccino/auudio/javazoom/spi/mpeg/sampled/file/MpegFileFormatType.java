package de.keksuccino.auudio.javazoom.spi.mpeg.sampled.file;

import javax.sound.sampled.AudioFileFormat.Type;

public class MpegFileFormatType extends Type {

   public static final Type MPEG = new MpegFileFormatType("MPEG", "mpeg");
   public static final Type MP3 = new MpegFileFormatType("MP3", "mp3");


   public MpegFileFormatType(String strName, String strExtension) {
      super(strName, strExtension);
   }

}
