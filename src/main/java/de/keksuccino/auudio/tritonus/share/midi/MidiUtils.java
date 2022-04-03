package de.keksuccino.auudio.tritonus.share.midi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MidiUtils {

   public static int getUnsignedInteger(byte b) {
      return b < 0?b + 256:b;
   }

   public static int get14bitValue(int nLSB, int nMSB) {
      return nLSB & 127 | (nMSB & 127) << 7;
   }

   public static int get14bitMSB(int nValue) {
      return nValue >> 7 & 127;
   }

   public static int get14bitLSB(int nValue) {
      return nValue & 127;
   }

   public static byte[] getVariableLengthQuantity(long lValue) {
      ByteArrayOutputStream data = new ByteArrayOutputStream();

      try {
         writeVariableLengthQuantity(lValue, data);
      } catch (IOException var4) {
      }

      return data.toByteArray();
   }

   public static int writeVariableLengthQuantity(long lValue, OutputStream outputStream) throws IOException {
      int nLength = 0;
      boolean bWritingStarted = false;
      int nByte = (int)(lValue >> 21 & 127L);
      if(nByte != 0) {
         if(outputStream != null) {
            outputStream.write(nByte | 128);
         }

         ++nLength;
         bWritingStarted = true;
      }

      nByte = (int)(lValue >> 14 & 127L);
      if(nByte != 0 || bWritingStarted) {
         if(outputStream != null) {
            outputStream.write(nByte | 128);
         }

         ++nLength;
         bWritingStarted = true;
      }

      nByte = (int)(lValue >> 7 & 127L);
      if(nByte != 0 || bWritingStarted) {
         if(outputStream != null) {
            outputStream.write(nByte | 128);
         }

         ++nLength;
      }

      nByte = (int)(lValue & 127L);
      if(outputStream != null) {
         outputStream.write(nByte);
      }

      ++nLength;
      return nLength;
   }
}
