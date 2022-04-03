package de.keksuccino.auudio.javazoom.jl.player;

import de.keksuccino.auudio.javazoom.jl.decoder.Decoder;
import de.keksuccino.auudio.javazoom.jl.decoder.JavaLayerException;

public interface AudioDevice {

   void open(Decoder var1) throws JavaLayerException;

   boolean isOpen();

   void write(short[] var1, int var2, int var3) throws JavaLayerException;

   void close();

   void flush();

   int getPosition();
}
