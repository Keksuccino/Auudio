package de.keksuccino.auudio.tritonus.share.sampled.file;

import java.io.IOException;
import javax.sound.sampled.AudioFormat;

public interface AudioOutputStream {

   AudioFormat getFormat();

   long getLength();

   int write(byte[] var1, int var2, int var3) throws IOException;

   void close() throws IOException;
}
