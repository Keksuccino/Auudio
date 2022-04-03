package de.keksuccino.auudio.tritonus.share.sampled.file;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TNonSeekableDataOutputStream extends DataOutputStream implements TDataOutputStream {

   public TNonSeekableDataOutputStream(OutputStream outputStream) {
      super(outputStream);
   }

   public boolean supportsSeek() {
      return false;
   }

   public void seek(long position) throws IOException {
      throw new IllegalArgumentException("TNonSeekableDataOutputStream: Call to seek not allowed.");
   }

   public long getFilePointer() throws IOException {
      throw new IllegalArgumentException("TNonSeekableDataOutputStream: Call to getFilePointer not allowed.");
   }

   public long length() throws IOException {
      throw new IllegalArgumentException("TNonSeekableDataOutputStream: Call to length not allowed.");
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
