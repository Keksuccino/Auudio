package de.keksuccino.auudio.tritonus.share.sampled.file;

import java.io.DataOutput;
import java.io.IOException;

public interface TDataOutputStream extends DataOutput {

   boolean supportsSeek();

   void seek(long var1) throws IOException;

   long getFilePointer() throws IOException;

   long length() throws IOException;

   void writeLittleEndian32(int var1) throws IOException;

   void writeLittleEndian16(short var1) throws IOException;

   void close() throws IOException;
}
