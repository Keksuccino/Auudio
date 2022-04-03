package de.keksuccino.auudio.tritonus.share.sampled.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class TSeekableDataOutputStream extends RandomAccessFile implements TDataOutputStream {

   public TSeekableDataOutputStream(File file) throws IOException {
      super(file, "rw");
   }

   public boolean supportsSeek() {
      return true;
   }

   public void writeLittleEndian32(int value) throws IOException {
      this.writeByte(value & 255);
      this.writeByte(value >> 8 & 255);
      this.writeByte(value >> 16 & 255);
      this.writeByte(value >> 24 & 255);
   }

   public void writeLittleEndian16(short value) throws IOException {
      this.writeByte(value & 255);
      this.writeByte(value >> 8 & 255);
   }
}
