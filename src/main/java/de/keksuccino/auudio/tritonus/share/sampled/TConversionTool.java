package de.keksuccino.auudio.tritonus.share.sampled;


public class TConversionTool {

   private static final boolean ZEROTRAP = true;
   private static final short BIAS = 132;
   private static final int CLIP = 32635;
   private static final int[] exp_lut1 = new int[]{0, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7};
   private static short[] u2l = new short[]{(short)-32124, (short)-31100, (short)-30076, (short)-29052, (short)-28028, (short)-27004, (short)-25980, (short)-24956, (short)-23932, (short)-22908, (short)-21884, (short)-20860, (short)-19836, (short)-18812, (short)-17788, (short)-16764, (short)-15996, (short)-15484, (short)-14972, (short)-14460, (short)-13948, (short)-13436, (short)-12924, (short)-12412, (short)-11900, (short)-11388, (short)-10876, (short)-10364, (short)-9852, (short)-9340, (short)-8828, (short)-8316, (short)-7932, (short)-7676, (short)-7420, (short)-7164, (short)-6908, (short)-6652, (short)-6396, (short)-6140, (short)-5884, (short)-5628, (short)-5372, (short)-5116, (short)-4860, (short)-4604, (short)-4348, (short)-4092, (short)-3900, (short)-3772, (short)-3644, (short)-3516, (short)-3388, (short)-3260, (short)-3132, (short)-3004, (short)-2876, (short)-2748, (short)-2620, (short)-2492, (short)-2364, (short)-2236, (short)-2108, (short)-1980, (short)-1884, (short)-1820, (short)-1756, (short)-1692, (short)-1628, (short)-1564, (short)-1500, (short)-1436, (short)-1372, (short)-1308, (short)-1244, (short)-1180, (short)-1116, (short)-1052, (short)-988, (short)-924, (short)-876, (short)-844, (short)-812, (short)-780, (short)-748, (short)-716, (short)-684, (short)-652, (short)-620, (short)-588, (short)-556, (short)-524, (short)-492, (short)-460, (short)-428, (short)-396, (short)-372, (short)-356, (short)-340, (short)-324, (short)-308, (short)-292, (short)-276, (short)-260, (short)-244, (short)-228, (short)-212, (short)-196, (short)-180, (short)-164, (short)-148, (short)-132, (short)-120, (short)-112, (short)-104, (short)-96, (short)-88, (short)-80, (short)-72, (short)-64, (short)-56, (short)-48, (short)-40, (short)-32, (short)-24, (short)-16, (short)-8, (short)0, (short)32124, (short)31100, (short)30076, (short)29052, (short)28028, (short)27004, (short)25980, (short)24956, (short)23932, (short)22908, (short)21884, (short)20860, (short)19836, (short)18812, (short)17788, (short)16764, (short)15996, (short)15484, (short)14972, (short)14460, (short)13948, (short)13436, (short)12924, (short)12412, (short)11900, (short)11388, (short)10876, (short)10364, (short)9852, (short)9340, (short)8828, (short)8316, (short)7932, (short)7676, (short)7420, (short)7164, (short)6908, (short)6652, (short)6396, (short)6140, (short)5884, (short)5628, (short)5372, (short)5116, (short)4860, (short)4604, (short)4348, (short)4092, (short)3900, (short)3772, (short)3644, (short)3516, (short)3388, (short)3260, (short)3132, (short)3004, (short)2876, (short)2748, (short)2620, (short)2492, (short)2364, (short)2236, (short)2108, (short)1980, (short)1884, (short)1820, (short)1756, (short)1692, (short)1628, (short)1564, (short)1500, (short)1436, (short)1372, (short)1308, (short)1244, (short)1180, (short)1116, (short)1052, (short)988, (short)924, (short)876, (short)844, (short)812, (short)780, (short)748, (short)716, (short)684, (short)652, (short)620, (short)588, (short)556, (short)524, (short)492, (short)460, (short)428, (short)396, (short)372, (short)356, (short)340, (short)324, (short)308, (short)292, (short)276, (short)260, (short)244, (short)228, (short)212, (short)196, (short)180, (short)164, (short)148, (short)132, (short)120, (short)112, (short)104, (short)96, (short)88, (short)80, (short)72, (short)64, (short)56, (short)48, (short)40, (short)32, (short)24, (short)16, (short)8, (short)0};
   private static final byte QUANT_MASK = 15;
   private static final byte SEG_SHIFT = 4;
   private static final short[] seg_end = new short[]{(short)255, (short)511, (short)1023, (short)2047, (short)4095, (short)8191, (short)16383, (short)32767};
   private static short[] a2l = new short[]{(short)-5504, (short)-5248, (short)-6016, (short)-5760, (short)-4480, (short)-4224, (short)-4992, (short)-4736, (short)-7552, (short)-7296, (short)-8064, (short)-7808, (short)-6528, (short)-6272, (short)-7040, (short)-6784, (short)-2752, (short)-2624, (short)-3008, (short)-2880, (short)-2240, (short)-2112, (short)-2496, (short)-2368, (short)-3776, (short)-3648, (short)-4032, (short)-3904, (short)-3264, (short)-3136, (short)-3520, (short)-3392, (short)-22016, (short)-20992, (short)-24064, (short)-23040, (short)-17920, (short)-16896, (short)-19968, (short)-18944, (short)-30208, (short)-29184, (short)-32256, (short)-31232, (short)-26112, (short)-25088, (short)-28160, (short)-27136, (short)-11008, (short)-10496, (short)-12032, (short)-11520, (short)-8960, (short)-8448, (short)-9984, (short)-9472, (short)-15104, (short)-14592, (short)-16128, (short)-15616, (short)-13056, (short)-12544, (short)-14080, (short)-13568, (short)-344, (short)-328, (short)-376, (short)-360, (short)-280, (short)-264, (short)-312, (short)-296, (short)-472, (short)-456, (short)-504, (short)-488, (short)-408, (short)-392, (short)-440, (short)-424, (short)-88, (short)-72, (short)-120, (short)-104, (short)-24, (short)-8, (short)-56, (short)-40, (short)-216, (short)-200, (short)-248, (short)-232, (short)-152, (short)-136, (short)-184, (short)-168, (short)-1376, (short)-1312, (short)-1504, (short)-1440, (short)-1120, (short)-1056, (short)-1248, (short)-1184, (short)-1888, (short)-1824, (short)-2016, (short)-1952, (short)-1632, (short)-1568, (short)-1760, (short)-1696, (short)-688, (short)-656, (short)-752, (short)-720, (short)-560, (short)-528, (short)-624, (short)-592, (short)-944, (short)-912, (short)-1008, (short)-976, (short)-816, (short)-784, (short)-880, (short)-848, (short)5504, (short)5248, (short)6016, (short)5760, (short)4480, (short)4224, (short)4992, (short)4736, (short)7552, (short)7296, (short)8064, (short)7808, (short)6528, (short)6272, (short)7040, (short)6784, (short)2752, (short)2624, (short)3008, (short)2880, (short)2240, (short)2112, (short)2496, (short)2368, (short)3776, (short)3648, (short)4032, (short)3904, (short)3264, (short)3136, (short)3520, (short)3392, (short)22016, (short)20992, (short)24064, (short)23040, (short)17920, (short)16896, (short)19968, (short)18944, (short)30208, (short)29184, (short)32256, (short)31232, (short)26112, (short)25088, (short)28160, (short)27136, (short)11008, (short)10496, (short)12032, (short)11520, (short)8960, (short)8448, (short)9984, (short)9472, (short)15104, (short)14592, (short)16128, (short)15616, (short)13056, (short)12544, (short)14080, (short)13568, (short)344, (short)328, (short)376, (short)360, (short)280, (short)264, (short)312, (short)296, (short)472, (short)456, (short)504, (short)488, (short)408, (short)392, (short)440, (short)424, (short)88, (short)72, (short)120, (short)104, (short)24, (short)8, (short)56, (short)40, (short)216, (short)200, (short)248, (short)232, (short)152, (short)136, (short)184, (short)168, (short)1376, (short)1312, (short)1504, (short)1440, (short)1120, (short)1056, (short)1248, (short)1184, (short)1888, (short)1824, (short)2016, (short)1952, (short)1632, (short)1568, (short)1760, (short)1696, (short)688, (short)656, (short)752, (short)720, (short)560, (short)528, (short)624, (short)592, (short)944, (short)912, (short)1008, (short)976, (short)816, (short)784, (short)880, (short)848};
   private static byte[] u2a = new byte[]{(byte)-86, (byte)-85, (byte)-88, (byte)-87, (byte)-82, (byte)-81, (byte)-84, (byte)-83, (byte)-94, (byte)-93, (byte)-96, (byte)-95, (byte)-90, (byte)-89, (byte)-92, (byte)-91, (byte)-70, (byte)-69, (byte)-72, (byte)-71, (byte)-66, (byte)-65, (byte)-68, (byte)-67, (byte)-78, (byte)-77, (byte)-80, (byte)-79, (byte)-74, (byte)-73, (byte)-76, (byte)-75, (byte)-118, (byte)-117, (byte)-120, (byte)-119, (byte)-114, (byte)-113, (byte)-116, (byte)-115, (byte)-126, (byte)-125, (byte)-128, (byte)-127, (byte)-122, (byte)-121, (byte)-124, (byte)-123, (byte)-101, (byte)-104, (byte)-103, (byte)-98, (byte)-97, (byte)-100, (byte)-99, (byte)-110, (byte)-109, (byte)-112, (byte)-111, (byte)-106, (byte)-105, (byte)-108, (byte)-107, (byte)-22, (byte)-24, (byte)-23, (byte)-18, (byte)-17, (byte)-20, (byte)-19, (byte)-30, (byte)-29, (byte)-32, (byte)-31, (byte)-26, (byte)-25, (byte)-28, (byte)-27, (byte)-6, (byte)-8, (byte)-2, (byte)-1, (byte)-4, (byte)-3, (byte)-14, (byte)-13, (byte)-16, (byte)-15, (byte)-10, (byte)-9, (byte)-12, (byte)-11, (byte)-53, (byte)-55, (byte)-49, (byte)-51, (byte)-62, (byte)-61, (byte)-64, (byte)-63, (byte)-58, (byte)-57, (byte)-60, (byte)-59, (byte)-38, (byte)-37, (byte)-40, (byte)-39, (byte)-34, (byte)-33, (byte)-36, (byte)-35, (byte)-46, (byte)-46, (byte)-45, (byte)-45, (byte)-48, (byte)-48, (byte)-47, (byte)-47, (byte)-42, (byte)-42, (byte)-41, (byte)-41, (byte)-44, (byte)-44, (byte)-43, (byte)-43, (byte)42, (byte)43, (byte)40, (byte)41, (byte)46, (byte)47, (byte)44, (byte)45, (byte)34, (byte)35, (byte)32, (byte)33, (byte)38, (byte)39, (byte)36, (byte)37, (byte)58, (byte)59, (byte)56, (byte)57, (byte)62, (byte)63, (byte)60, (byte)61, (byte)50, (byte)51, (byte)48, (byte)49, (byte)54, (byte)55, (byte)52, (byte)53, (byte)10, (byte)11, (byte)8, (byte)9, (byte)14, (byte)15, (byte)12, (byte)13, (byte)2, (byte)3, (byte)0, (byte)1, (byte)6, (byte)7, (byte)4, (byte)5, (byte)27, (byte)24, (byte)25, (byte)30, (byte)31, (byte)28, (byte)29, (byte)18, (byte)19, (byte)16, (byte)17, (byte)22, (byte)23, (byte)20, (byte)21, (byte)106, (byte)104, (byte)105, (byte)110, (byte)111, (byte)108, (byte)109, (byte)98, (byte)99, (byte)96, (byte)97, (byte)102, (byte)103, (byte)100, (byte)101, (byte)122, (byte)120, (byte)126, (byte)127, (byte)124, (byte)125, (byte)114, (byte)115, (byte)112, (byte)113, (byte)118, (byte)119, (byte)116, (byte)117, (byte)75, (byte)73, (byte)79, (byte)77, (byte)66, (byte)67, (byte)64, (byte)65, (byte)70, (byte)71, (byte)68, (byte)69, (byte)90, (byte)91, (byte)88, (byte)89, (byte)94, (byte)95, (byte)92, (byte)93, (byte)82, (byte)82, (byte)83, (byte)83, (byte)80, (byte)80, (byte)81, (byte)81, (byte)86, (byte)86, (byte)87, (byte)87, (byte)84, (byte)84, (byte)85, (byte)85};
   private static byte[] a2u = new byte[]{(byte)-86, (byte)-85, (byte)-88, (byte)-87, (byte)-82, (byte)-81, (byte)-84, (byte)-83, (byte)-94, (byte)-93, (byte)-96, (byte)-95, (byte)-90, (byte)-89, (byte)-92, (byte)-91, (byte)-71, (byte)-70, (byte)-73, (byte)-72, (byte)-67, (byte)-66, (byte)-69, (byte)-68, (byte)-79, (byte)-78, (byte)-80, (byte)-80, (byte)-75, (byte)-74, (byte)-77, (byte)-76, (byte)-118, (byte)-117, (byte)-120, (byte)-119, (byte)-114, (byte)-113, (byte)-116, (byte)-115, (byte)-126, (byte)-125, (byte)-128, (byte)-127, (byte)-122, (byte)-121, (byte)-124, (byte)-123, (byte)-102, (byte)-101, (byte)-104, (byte)-103, (byte)-98, (byte)-97, (byte)-100, (byte)-99, (byte)-110, (byte)-109, (byte)-112, (byte)-111, (byte)-106, (byte)-105, (byte)-108, (byte)-107, (byte)-30, (byte)-29, (byte)-32, (byte)-31, (byte)-26, (byte)-25, (byte)-28, (byte)-27, (byte)-35, (byte)-35, (byte)-36, (byte)-36, (byte)-33, (byte)-33, (byte)-34, (byte)-34, (byte)-12, (byte)-10, (byte)-16, (byte)-14, (byte)-4, (byte)-2, (byte)-8, (byte)-6, (byte)-22, (byte)-21, (byte)-24, (byte)-23, (byte)-18, (byte)-17, (byte)-20, (byte)-19, (byte)-56, (byte)-55, (byte)-58, (byte)-57, (byte)-52, (byte)-51, (byte)-54, (byte)-53, (byte)-64, (byte)-63, (byte)-65, (byte)-65, (byte)-60, (byte)-59, (byte)-62, (byte)-61, (byte)-42, (byte)-41, (byte)-44, (byte)-43, (byte)-38, (byte)-37, (byte)-40, (byte)-39, (byte)-49, (byte)-49, (byte)-50, (byte)-50, (byte)-46, (byte)-45, (byte)-48, (byte)-47, (byte)42, (byte)43, (byte)40, (byte)41, (byte)46, (byte)47, (byte)44, (byte)45, (byte)34, (byte)35, (byte)32, (byte)33, (byte)38, (byte)39, (byte)36, (byte)37, (byte)57, (byte)58, (byte)55, (byte)56, (byte)61, (byte)62, (byte)59, (byte)60, (byte)49, (byte)50, (byte)48, (byte)48, (byte)53, (byte)54, (byte)51, (byte)52, (byte)10, (byte)11, (byte)8, (byte)9, (byte)14, (byte)15, (byte)12, (byte)13, (byte)2, (byte)3, (byte)0, (byte)1, (byte)6, (byte)7, (byte)4, (byte)5, (byte)26, (byte)27, (byte)24, (byte)25, (byte)30, (byte)31, (byte)28, (byte)29, (byte)18, (byte)19, (byte)16, (byte)17, (byte)22, (byte)23, (byte)20, (byte)21, (byte)98, (byte)99, (byte)96, (byte)97, (byte)102, (byte)103, (byte)100, (byte)101, (byte)93, (byte)93, (byte)92, (byte)92, (byte)95, (byte)95, (byte)94, (byte)94, (byte)116, (byte)118, (byte)112, (byte)114, (byte)124, (byte)126, (byte)120, (byte)122, (byte)106, (byte)107, (byte)104, (byte)105, (byte)110, (byte)111, (byte)108, (byte)109, (byte)72, (byte)73, (byte)70, (byte)71, (byte)76, (byte)77, (byte)74, (byte)75, (byte)64, (byte)65, (byte)63, (byte)63, (byte)68, (byte)69, (byte)66, (byte)67, (byte)86, (byte)87, (byte)84, (byte)85, (byte)90, (byte)91, (byte)88, (byte)89, (byte)79, (byte)79, (byte)78, (byte)78, (byte)82, (byte)83, (byte)80, (byte)81};


   public static void convertSign8(byte[] buffer, int byteOffset, int sampleCount) {
      sampleCount += byteOffset;

      for(int i = byteOffset; i < sampleCount; ++i) {
         buffer[i] = (byte)(buffer[i] + 128);
      }

   }

   public static void swapOrder16(byte[] buffer, int byteOffset, int sampleCount) {
      int byteMax = sampleCount * 2 + byteOffset - 1;

      byte h;
      for(int i = byteOffset; i < byteMax; buffer[i++] = h) {
         h = buffer[i];
         buffer[i++] = buffer[i];
      }

   }

   public static void swapOrder24(byte[] buffer, int byteOffset, int sampleCount) {
      int byteMax = sampleCount * 3 + byteOffset - 2;

      for(int i = byteOffset; i < byteMax; ++i) {
         byte h = buffer[i];
         buffer[i++] = buffer[i + 1];
         ++i;
         buffer[i] = h;
      }

   }

   public static void swapOrder32(byte[] buffer, int byteOffset, int sampleCount) {
      int byteMax = sampleCount * 4 + byteOffset - 3;

      for(int i = byteOffset; i < byteMax; ++i) {
         byte h = buffer[i];
         buffer[i] = buffer[i + 3];
         buffer[i + 3] = h;
         ++i;
         h = buffer[i];
         buffer[i++] = buffer[i];
         buffer[i++] = h;
      }

   }

   public static void convertSign8(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount) {
      while(sampleCount > 0) {
         outBuffer[outByteOffset++] = (byte)(inBuffer[inByteOffset++] + 128);
         --sampleCount;
      }

   }

   public static void swapOrder16(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount) {
      while(sampleCount > 0) {
         outBuffer[outByteOffset++] = inBuffer[inByteOffset + 1];
         outBuffer[outByteOffset++] = inBuffer[inByteOffset++];
         ++inByteOffset;
         --sampleCount;
      }

   }

   public static void swapOrder24(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount) {
      while(sampleCount > 0) {
         outBuffer[outByteOffset++] = inBuffer[inByteOffset + 2];
         ++outByteOffset;
         outBuffer[outByteOffset++] = inBuffer[inByteOffset++];
         ++inByteOffset;
         ++inByteOffset;
         --sampleCount;
      }

   }

   public static void swapOrder32(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount) {
      while(sampleCount > 0) {
         outBuffer[outByteOffset++] = inBuffer[inByteOffset + 3];
         outBuffer[outByteOffset++] = inBuffer[inByteOffset + 2];
         outBuffer[outByteOffset++] = inBuffer[inByteOffset + 1];
         outBuffer[outByteOffset++] = inBuffer[inByteOffset++];
         ++inByteOffset;
         ++inByteOffset;
         ++inByteOffset;
         --sampleCount;
      }

   }

   public static short bytesToShort16(byte highByte, byte lowByte) {
      return (short)(highByte << 8 | lowByte & 255);
   }

   public static short bytesToShort16(byte[] buffer, int byteOffset, boolean bigEndian) {
      return bigEndian?(short)(buffer[byteOffset] << 8 | buffer[byteOffset + 1] & 255):(short)(buffer[byteOffset + 1] << 8 | buffer[byteOffset] & 255);
   }

   public static int bytesToInt16(byte highByte, byte lowByte) {
      return highByte << 8 | lowByte & 255;
   }

   public static int bytesToInt16(byte[] buffer, int byteOffset, boolean bigEndian) {
      return bigEndian?buffer[byteOffset] << 8 | buffer[byteOffset + 1] & 255:buffer[byteOffset + 1] << 8 | buffer[byteOffset] & 255;
   }

   public static int bytesToInt24(byte[] buffer, int byteOffset, boolean bigEndian) {
      return bigEndian?buffer[byteOffset] << 16 | (buffer[byteOffset + 1] & 255) << 8 | buffer[byteOffset + 2] & 255:buffer[byteOffset + 2] << 16 | (buffer[byteOffset + 1] & 255) << 8 | buffer[byteOffset] & 255;
   }

   public static int bytesToInt32(byte[] buffer, int byteOffset, boolean bigEndian) {
      return bigEndian?buffer[byteOffset] << 24 | (buffer[byteOffset + 1] & 255) << 16 | (buffer[byteOffset + 2] & 255) << 8 | buffer[byteOffset + 3] & 255:buffer[byteOffset + 3] << 24 | (buffer[byteOffset + 2] & 255) << 16 | (buffer[byteOffset + 1] & 255) << 8 | buffer[byteOffset] & 255;
   }

   public static void shortToBytes16(short sample, byte[] buffer, int byteOffset, boolean bigEndian) {
      intToBytes16(sample, buffer, byteOffset, bigEndian);
   }

   public static void intToBytes16(int sample, byte[] buffer, int byteOffset, boolean bigEndian) {
      if(bigEndian) {
         buffer[byteOffset++] = (byte)(sample >> 8);
         buffer[byteOffset] = (byte)(sample & 255);
      } else {
         buffer[byteOffset++] = (byte)(sample & 255);
         buffer[byteOffset] = (byte)(sample >> 8);
      }

   }

   public static void intToBytes24(int sample, byte[] buffer, int byteOffset, boolean bigEndian) {
      if(bigEndian) {
         buffer[byteOffset++] = (byte)(sample >> 16);
         buffer[byteOffset++] = (byte)(sample >>> 8 & 255);
         buffer[byteOffset] = (byte)(sample & 255);
      } else {
         buffer[byteOffset++] = (byte)(sample & 255);
         buffer[byteOffset++] = (byte)(sample >>> 8 & 255);
         buffer[byteOffset] = (byte)(sample >> 16);
      }

   }

   public static void intToBytes32(int sample, byte[] buffer, int byteOffset, boolean bigEndian) {
      if(bigEndian) {
         buffer[byteOffset++] = (byte)(sample >> 24);
         buffer[byteOffset++] = (byte)(sample >>> 16 & 255);
         buffer[byteOffset++] = (byte)(sample >>> 8 & 255);
         buffer[byteOffset] = (byte)(sample & 255);
      } else {
         buffer[byteOffset++] = (byte)(sample & 255);
         buffer[byteOffset++] = (byte)(sample >>> 8 & 255);
         buffer[byteOffset++] = (byte)(sample >>> 16 & 255);
         buffer[byteOffset] = (byte)(sample >> 24);
      }

   }

   public static byte linear2ulaw(int sample) {
      if(sample > 32767) {
         sample = 32767;
      } else if(sample < -32768) {
         sample = -32768;
      }

      int sign = sample >> 8 & 128;
      if(sign != 0) {
         sample = -sample;
      }

      if(sample > 32635) {
         sample = 32635;
      }

      sample += 132;
      int exponent = exp_lut1[sample >> 7 & 255];
      int mantissa = sample >> exponent + 3 & 15;
      int ulawbyte = ~(sign | exponent << 4 | mantissa);
      if(ulawbyte == 0) {
         ulawbyte = 2;
      }

      return (byte)ulawbyte;
   }

   public static short ulaw2linear(byte ulawbyte) {
      return u2l[ulawbyte & 255];
   }

   public static void pcm162ulaw(byte[] buffer, int byteOffset, int sampleCount, boolean bigEndian) {
      int shortIndex = byteOffset;
      int ulawIndex = byteOffset;
      if(bigEndian) {
         while(sampleCount > 0) {
            buffer[ulawIndex++] = linear2ulaw(bytesToInt16(buffer[shortIndex], buffer[shortIndex + 1]));
            ++shortIndex;
            ++shortIndex;
            --sampleCount;
         }
      } else {
         while(sampleCount > 0) {
            buffer[ulawIndex++] = linear2ulaw(bytesToInt16(buffer[shortIndex + 1], buffer[shortIndex]));
            ++shortIndex;
            ++shortIndex;
            --sampleCount;
         }
      }

   }

   public static void pcm162ulaw(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount, boolean bigEndian) {
      int shortIndex = inByteOffset;
      int ulawIndex = outByteOffset;
      if(bigEndian) {
         while(sampleCount > 0) {
            outBuffer[ulawIndex++] = linear2ulaw(bytesToInt16(inBuffer[shortIndex], inBuffer[shortIndex + 1]));
            ++shortIndex;
            ++shortIndex;
            --sampleCount;
         }
      } else {
         while(sampleCount > 0) {
            outBuffer[ulawIndex++] = linear2ulaw(bytesToInt16(inBuffer[shortIndex + 1], inBuffer[shortIndex]));
            ++shortIndex;
            ++shortIndex;
            --sampleCount;
         }
      }

   }

   public static void pcm82ulaw(byte[] buffer, int byteOffset, int sampleCount, boolean signed) {
      sampleCount += byteOffset;
      int i;
      if(signed) {
         for(i = byteOffset; i < sampleCount; ++i) {
            buffer[i] = linear2ulaw(buffer[i] << 8);
         }
      } else {
         for(i = byteOffset; i < sampleCount; ++i) {
            buffer[i] = linear2ulaw((byte)(buffer[i] + 128) << 8);
         }
      }

   }

   public static void pcm82ulaw(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount, boolean signed) {
      int ulawIndex = outByteOffset;
      int pcmIndex = inByteOffset;
      if(signed) {
         while(sampleCount > 0) {
            outBuffer[ulawIndex++] = linear2ulaw(inBuffer[pcmIndex++] << 8);
            --sampleCount;
         }
      } else {
         while(sampleCount > 0) {
            outBuffer[ulawIndex++] = linear2ulaw((byte)(inBuffer[pcmIndex++] + 128) << 8);
            --sampleCount;
         }
      }

   }

   public static void ulaw2pcm16(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount, boolean bigEndian) {
      int shortIndex = outByteOffset;

      for(int ulawIndex = inByteOffset; sampleCount > 0; --sampleCount) {
         intToBytes16(u2l[inBuffer[ulawIndex++] & 255], outBuffer, shortIndex++, bigEndian);
         ++shortIndex;
      }

   }

   public static void ulaw2pcm8(byte[] buffer, int byteOffset, int sampleCount, boolean signed) {
      sampleCount += byteOffset;
      int i;
      if(signed) {
         for(i = byteOffset; i < sampleCount; ++i) {
            buffer[i] = (byte)(u2l[buffer[i] & 255] >> 8 & 255);
         }
      } else {
         for(i = byteOffset; i < sampleCount; ++i) {
            buffer[i] = (byte)((u2l[buffer[i] & 255] >> 8) + 128);
         }
      }

   }

   public static void ulaw2pcm8(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount, boolean signed) {
      int ulawIndex = inByteOffset;
      int pcmIndex = outByteOffset;
      if(signed) {
         while(sampleCount > 0) {
            outBuffer[pcmIndex++] = (byte)(u2l[inBuffer[ulawIndex++] & 255] >> 8 & 255);
            --sampleCount;
         }
      } else {
         while(sampleCount > 0) {
            outBuffer[pcmIndex++] = (byte)((u2l[inBuffer[ulawIndex++] & 255] >> 8) + 128);
            --sampleCount;
         }
      }

   }

   public static byte linear2alaw(short pcm_val) {
      byte seg = 8;
      byte mask;
      if(pcm_val >= 0) {
         mask = -43;
      } else {
         mask = 85;
         pcm_val = (short)(-pcm_val - 8);
      }

      for(int i = 0; i < 8; ++i) {
         if(pcm_val <= seg_end[i]) {
            seg = (byte)i;
            break;
         }
      }

      if(seg >= 8) {
         return (byte)((127 ^ mask) & 255);
      } else {
         byte aval = (byte)(seg << 4);
         if(seg < 2) {
            aval = (byte)(aval | pcm_val >> 4 & 15);
         } else {
            aval = (byte)(aval | pcm_val >> seg + 3 & 15);
         }

         return (byte)((aval ^ mask) & 255);
      }
   }

   public static short alaw2linear(byte ulawbyte) {
      return a2l[ulawbyte & 255];
   }

   public static void pcm162alaw(byte[] buffer, int byteOffset, int sampleCount, boolean bigEndian) {
      int shortIndex = byteOffset;
      int alawIndex = byteOffset;
      if(bigEndian) {
         while(sampleCount > 0) {
            buffer[alawIndex++] = linear2alaw(bytesToShort16(buffer[shortIndex], buffer[shortIndex + 1]));
            ++shortIndex;
            ++shortIndex;
            --sampleCount;
         }
      } else {
         while(sampleCount > 0) {
            buffer[alawIndex++] = linear2alaw(bytesToShort16(buffer[shortIndex + 1], buffer[shortIndex]));
            ++shortIndex;
            ++shortIndex;
            --sampleCount;
         }
      }

   }

   public static void pcm162alaw(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount, boolean bigEndian) {
      int shortIndex = inByteOffset;
      int alawIndex = outByteOffset;
      if(bigEndian) {
         while(sampleCount > 0) {
            outBuffer[alawIndex++] = linear2alaw(bytesToShort16(inBuffer[shortIndex], inBuffer[shortIndex + 1]));
            ++shortIndex;
            ++shortIndex;
            --sampleCount;
         }
      } else {
         while(sampleCount > 0) {
            outBuffer[alawIndex++] = linear2alaw(bytesToShort16(inBuffer[shortIndex + 1], inBuffer[shortIndex]));
            ++shortIndex;
            ++shortIndex;
            --sampleCount;
         }
      }

   }

   public static void pcm82alaw(byte[] buffer, int byteOffset, int sampleCount, boolean signed) {
      sampleCount += byteOffset;
      int i;
      if(signed) {
         for(i = byteOffset; i < sampleCount; ++i) {
            buffer[i] = linear2alaw((short)(buffer[i] << 8));
         }
      } else {
         for(i = byteOffset; i < sampleCount; ++i) {
            buffer[i] = linear2alaw((short)((byte)(buffer[i] + 128) << 8));
         }
      }

   }

   public static void pcm82alaw(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount, boolean signed) {
      int alawIndex = outByteOffset;
      int pcmIndex = inByteOffset;
      if(signed) {
         while(sampleCount > 0) {
            outBuffer[alawIndex++] = linear2alaw((short)(inBuffer[pcmIndex++] << 8));
            --sampleCount;
         }
      } else {
         while(sampleCount > 0) {
            outBuffer[alawIndex++] = linear2alaw((short)((byte)(inBuffer[pcmIndex++] + 128) << 8));
            --sampleCount;
         }
      }

   }

   public static void alaw2pcm8(byte[] buffer, int byteOffset, int sampleCount, boolean signed) {
      sampleCount += byteOffset;
      int i;
      if(signed) {
         for(i = byteOffset; i < sampleCount; ++i) {
            buffer[i] = (byte)(a2l[buffer[i] & 255] >> 8 & 255);
         }
      } else {
         for(i = byteOffset; i < sampleCount; ++i) {
            buffer[i] = (byte)((a2l[buffer[i] & 255] >> 8) + 128);
         }
      }

   }

   public static void alaw2pcm8(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount, boolean signed) {
      int alawIndex = inByteOffset;
      int pcmIndex = outByteOffset;
      if(signed) {
         while(sampleCount > 0) {
            outBuffer[pcmIndex++] = (byte)(a2l[inBuffer[alawIndex++] & 255] >> 8 & 255);
            --sampleCount;
         }
      } else {
         while(sampleCount > 0) {
            outBuffer[pcmIndex++] = (byte)((a2l[inBuffer[alawIndex++] & 255] >> 8) + 128);
            --sampleCount;
         }
      }

   }

   public static void alaw2pcm16(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount, boolean bigEndian) {
      int shortIndex = outByteOffset;

      for(int alawIndex = inByteOffset; sampleCount > 0; --sampleCount) {
         intToBytes16(a2l[inBuffer[alawIndex++] & 255], outBuffer, shortIndex++, bigEndian);
         ++shortIndex;
      }

   }

   public static byte ulaw2alaw(byte sample) {
      return u2a[sample & 255];
   }

   public static void ulaw2alaw(byte[] buffer, int byteOffset, int sampleCount) {
      sampleCount += byteOffset;

      for(int i = byteOffset; i < sampleCount; ++i) {
         buffer[i] = u2a[buffer[i] & 255];
      }

   }

   public static void ulaw2alaw(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount) {
      int ulawIndex = outByteOffset;

      for(int alawIndex = inByteOffset; sampleCount > 0; --sampleCount) {
         outBuffer[alawIndex++] = u2a[inBuffer[ulawIndex++] & 255];
      }

   }

   public static byte alaw2ulaw(byte sample) {
      return a2u[sample & 255];
   }

   public static void alaw2ulaw(byte[] buffer, int byteOffset, int sampleCount) {
      sampleCount += byteOffset;

      for(int i = byteOffset; i < sampleCount; ++i) {
         buffer[i] = a2u[buffer[i] & 255];
      }

   }

   public static void alaw2ulaw(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount) {
      int ulawIndex = outByteOffset;

      for(int alawIndex = inByteOffset; sampleCount > 0; --sampleCount) {
         outBuffer[ulawIndex++] = a2u[inBuffer[alawIndex++] & 255];
      }

   }

   public static void changeOrderOrSign(byte[] buffer, int nOffset, int nByteLength, int nBytesPerSample) {
      switch(nBytesPerSample) {
      case 1:
         convertSign8(buffer, nOffset, nByteLength);
         break;
      case 2:
         swapOrder16(buffer, nOffset, nByteLength / 2);
         break;
      case 3:
         swapOrder24(buffer, nOffset, nByteLength / 3);
         break;
      case 4:
         swapOrder32(buffer, nOffset, nByteLength / 4);
      }

   }

   public static void changeOrderOrSign(byte[] inBuffer, int nInOffset, byte[] outBuffer, int nOutOffset, int nByteLength, int nBytesPerSample) {
      switch(nBytesPerSample) {
      case 1:
         convertSign8(inBuffer, nInOffset, outBuffer, nOutOffset, nByteLength);
         break;
      case 2:
         swapOrder16(inBuffer, nInOffset, outBuffer, nOutOffset, nByteLength / 2);
         break;
      case 3:
         swapOrder24(inBuffer, nInOffset, outBuffer, nOutOffset, nByteLength / 3);
         break;
      case 4:
         swapOrder32(inBuffer, nInOffset, outBuffer, nOutOffset, nByteLength / 4);
      }

   }

}
