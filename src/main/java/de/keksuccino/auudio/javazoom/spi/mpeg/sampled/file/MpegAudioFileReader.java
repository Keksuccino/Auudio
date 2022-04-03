package de.keksuccino.auudio.javazoom.spi.mpeg.sampled.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessControlException;
import java.util.HashMap;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFormat.Encoding;

import de.keksuccino.auudio.javazoom.jl.decoder.Bitstream;
import de.keksuccino.auudio.javazoom.jl.decoder.Header;
import de.keksuccino.auudio.javazoom.spi.mpeg.sampled.file.tag.IcyInputStream;
import de.keksuccino.auudio.javazoom.spi.mpeg.sampled.file.tag.MP3Tag;
import de.keksuccino.auudio.tritonus.share.sampled.file.TAudioFileReader;

public class MpegAudioFileReader extends TAudioFileReader {

   public static final String VERSION = "MP3SPI 1.9.5";
   private final int SYNC = -2097152;
   private String weak = null;
   private final Encoding[][] sm_aEncodings;
   public static int INITAL_READ_LENGTH = 4096000;
   private static int MARK_LIMIT = INITAL_READ_LENGTH + 1;
   private static final String[] id3v1genres;


   public MpegAudioFileReader() {
      super(MARK_LIMIT, true);
      this.sm_aEncodings = new Encoding[][]{{MpegEncoding.MPEG2L1, MpegEncoding.MPEG2L2, MpegEncoding.MPEG2L3}, {MpegEncoding.MPEG1L1, MpegEncoding.MPEG1L2, MpegEncoding.MPEG1L3}, {MpegEncoding.MPEG2DOT5L1, MpegEncoding.MPEG2DOT5L2, MpegEncoding.MPEG2DOT5L3}};

      try {
         this.weak = System.getProperty("mp3spi.weak");
      } catch (AccessControlException var2) {
         ;
      }

   }

   public AudioFileFormat getAudioFileFormat(File file) throws UnsupportedAudioFileException, IOException {
      return super.getAudioFileFormat(file);
   }

   public AudioFileFormat getAudioFileFormat(URL url) throws UnsupportedAudioFileException, IOException {

      long lFileLengthInBytes = -1L;
      URLConnection conn = url.openConnection();
      conn.setRequestProperty("Icy-Metadata", "1");
      InputStream inputStream = conn.getInputStream();
      AudioFileFormat audioFileFormat = null;

      try {
         audioFileFormat = this.getAudioFileFormat(inputStream, lFileLengthInBytes);
      } finally {
         inputStream.close();
      }

      return audioFileFormat;
   }

   public AudioFileFormat getAudioFileFormat(InputStream inputStream, long mediaLength) throws UnsupportedAudioFileException, IOException {

      HashMap aff_properties = new HashMap();
      HashMap af_properties = new HashMap();
      int mLength = (int)mediaLength;
      int size = inputStream.available();
      PushbackInputStream pis = new PushbackInputStream(inputStream, MARK_LIMIT);
      byte[] head = new byte[22];
      pis.read(head);

      int nVersion;
      if(head[0] == 82 && head[1] == 73 && head[2] == 70 && head[3] == 70 && head[8] == 87 && head[9] == 65 && head[10] == 86 && head[11] == 69) {

         nVersion = head[21] << 8 & '\uff00' | head[20] & 255;
         if(this.weak == null && nVersion == 1) {
            throw new UnsupportedAudioFileException("WAV PCM stream found");
         }
      } else if(head[0] == 46 && head[1] == 115 && head[2] == 110 && head[3] == 100) {

         if(this.weak == null) {
            throw new UnsupportedAudioFileException("AU stream found");
         }
      } else if(head[0] == 70 && head[1] == 79 && head[2] == 82 && head[3] == 77 && head[8] == 65 && head[9] == 73 && head[10] == 70 && head[11] == 70) {

         if(this.weak == null) {
            throw new UnsupportedAudioFileException("AIFF stream found");
         }
      } else if(head[0] == 77 | head[0] == 109 && head[1] == 65 | head[1] == 97 && head[2] == 67 | head[2] == 99) {

         if(this.weak == null) {
            throw new UnsupportedAudioFileException("APE stream found");
         }
      } else if(head[0] == 70 | head[0] == 102 && head[1] == 76 | head[1] == 108 && head[2] == 65 | head[2] == 97 && head[3] == 67 | head[3] == 99) {

         if(this.weak == null) {
            throw new UnsupportedAudioFileException("FLAC stream found");
         }
      } else if(head[0] == 73 | head[0] == 105 && head[1] == 67 | head[1] == 99 && head[2] == 89 | head[2] == 121) {
         pis.unread(head);
         this.loadShoutcastInfo(pis, aff_properties);
      } else if(head[0] == 79 | head[0] == 111 && head[1] == 71 | head[1] == 103 && head[2] == 71 | head[2] == 103) {

         if(this.weak == null) {
            throw new UnsupportedAudioFileException("Ogg stream found");
         }
      } else {
         pis.unread(head);
      }

      boolean nVersion1 = true;
      boolean nLayer = true;
      boolean nSFIndex = true;
      boolean nMode = true;
      boolean FrameSize = true;
      boolean nFrameSize = true;
      boolean nFrequency = true;
      int nTotalFrames = -1;
      float FrameRate = -1.0F;
      boolean BitRate = true;
      boolean nChannels = true;
      boolean nHeader = true;
      boolean nTotalMS = true;
      boolean nVBR = false;
      Encoding encoding = null;

      int cSFIndex;
      int nFrequency1;
      int nChannels1;
      int nHeader1;
      try {
         Bitstream cVersion = new Bitstream(pis);
         cSFIndex = cVersion.header_pos();
         aff_properties.put("mp3.header.pos", new Integer(cSFIndex));
         Header format = cVersion.readFrame();
         nVersion = format.version();
         if(nVersion == 2) {
            aff_properties.put("mp3.version.mpeg", Float.toString(2.5F));
         } else {
            aff_properties.put("mp3.version.mpeg", Integer.toString(2 - nVersion));
         }

         int nLayer1 = format.layer();
         aff_properties.put("mp3.version.layer", Integer.toString(nLayer1));
         int nSFIndex1 = format.sample_frequency();
         int nMode1 = format.mode();
         aff_properties.put("mp3.mode", nMode1);
         nChannels1 = nMode1 == 3?1:2;
         aff_properties.put("mp3.channels", nChannels1);
         nVBR = format.vbr();
         af_properties.put("vbr", nVBR);
         aff_properties.put("mp3.vbr", nVBR);
         aff_properties.put("mp3.vbr.scale", format.vbr_scale());
         int FrameSize1 = format.calculate_framesize();
         aff_properties.put("mp3.framesize.bytes", new Integer(FrameSize1));
         if(FrameSize1 < 0) {
            throw new UnsupportedAudioFileException("Invalid FrameSize : " + FrameSize1);
         }

         nFrequency1 = format.frequency();
         aff_properties.put("mp3.frequency.hz", new Integer(nFrequency1));
         FrameRate = (float)(1.0D / (double)format.ms_per_frame() * 1000.0D);
         aff_properties.put("mp3.framerate.fps", new Float(FrameRate));
         if(FrameRate < 0.0F) {
            throw new UnsupportedAudioFileException("Invalid FrameRate : " + FrameRate);
         }

         int id3v1 = mLength;
         if(cSFIndex > 0 && mLength != -1 && cSFIndex < mLength) {
            id3v1 = mLength - cSFIndex;
         }

         if(mLength != -1) {
            aff_properties.put("mp3.length.bytes", mLength);
            nTotalFrames = format.max_number_of_frames(id3v1);
            aff_properties.put("mp3.length.frames", nTotalFrames);
         }

         int BitRate1 = format.bitrate();
         af_properties.put("bitrate", BitRate1);
         aff_properties.put("mp3.bitrate.nominal.bps", BitRate1);
         nHeader1 = format.getSyncHeader();
         encoding = this.sm_aEncodings[nVersion][nLayer1 - 1];
         aff_properties.put("mp3.version.encoding", encoding.toString());
         if(mLength != -1) {
            int nTotalMS1 = Math.round(format.total_ms(id3v1));
            aff_properties.put("duration", new Long((long)nTotalMS1 * 1000L));
         }

         aff_properties.put("mp3.copyright", new Boolean(format.copyright()));
         aff_properties.put("mp3.original", new Boolean(format.original()));
         aff_properties.put("mp3.crc", new Boolean(format.checksums()));
         aff_properties.put("mp3.padding", new Boolean(format.padding()));
         InputStream bytesSkipped = cVersion.getRawID3v2();
         if(bytesSkipped != null) {
            aff_properties.put("mp3.id3tag.v2", bytesSkipped);
            this.parseID3v2Frames(bytesSkipped, aff_properties);
         }

      } catch (Exception var32) {

         throw new UnsupportedAudioFileException("not a MPEG stream:" + var32.getMessage());
      }

      int cVersion1 = nHeader1 >> 19 & 3;
      if(cVersion1 == 1) {

         throw new UnsupportedAudioFileException("not a MPEG stream: wrong version");
      } else {
         cSFIndex = nHeader1 >> 10 & 3;
         if(cSFIndex == 3) {

            throw new UnsupportedAudioFileException("not a MPEG stream: wrong sampling rate");
         } else {
            if((long)size == mediaLength && mediaLength != -1L) {
               FileInputStream format1 = (FileInputStream)inputStream;
               byte[] id3v11 = new byte[128];
               format1.skip((long)(inputStream.available() - id3v11.length));
               format1.read(id3v11, 0, id3v11.length);
               if(id3v11[0] == 84 && id3v11[1] == 65 && id3v11[2] == 71) {
                  this.parseID3v1Frames(id3v11, aff_properties);
               }
            }

            MpegAudioFormat format2 = new MpegAudioFormat(encoding, (float)nFrequency1, -1, nChannels1, -1, FrameRate, true, af_properties);
            return new MpegAudioFileFormat(MpegFileFormatType.MP3, format2, nTotalFrames, mLength, aff_properties);
         }
      }
   }

   public AudioInputStream getAudioInputStream(File file) throws UnsupportedAudioFileException, IOException {

      FileInputStream inputStream = new FileInputStream(file);

      try {
         return this.getAudioInputStream((InputStream)inputStream);
      } catch (UnsupportedAudioFileException var4) {
         if(inputStream != null) {
            inputStream.close();
         }

         throw var4;
      } catch (IOException var5) {
         if(inputStream != null) {
            inputStream.close();
         }

         throw var5;
      }
   }

   public AudioInputStream getAudioInputStream(URL url) throws UnsupportedAudioFileException, IOException {

      long lFileLengthInBytes = -1L;
      URLConnection conn = url.openConnection();
      boolean isShout = false;
      byte toRead = 4;
      byte[] head = new byte[toRead];
      conn.setRequestProperty("Icy-Metadata", "1");
      BufferedInputStream bInputStream = new BufferedInputStream(conn.getInputStream());
      bInputStream.mark(toRead);
      int read = bInputStream.read(head, 0, toRead);
      if(read > 2 && head[0] == 73 | head[0] == 105 && head[1] == 67 | head[1] == 99 && head[2] == 89 | head[2] == 121) {
         isShout = true;
      }

      bInputStream.reset();
      Object inputStream = null;
      IcyInputStream audioInputStream;
      if(isShout) {
         audioInputStream = new IcyInputStream(bInputStream);
         audioInputStream.addTagParseListener(IcyListener.getInstance());
         inputStream = audioInputStream;
      } else {
         String audioInputStream1 = conn.getHeaderField("icy-metaint");
         if(audioInputStream1 != null) {
            IcyInputStream e = new IcyInputStream(bInputStream, audioInputStream1);
            e.addTagParseListener(IcyListener.getInstance());
            inputStream = e;
         } else {
            inputStream = bInputStream;
         }
      }

      audioInputStream = null;

      AudioInputStream audioInputStream2;
      try {
         audioInputStream2 = this.getAudioInputStream((InputStream)inputStream, lFileLengthInBytes);
      } catch (UnsupportedAudioFileException var13) {
         ((InputStream)inputStream).close();
         throw var13;
      } catch (IOException var14) {
         ((InputStream)inputStream).close();
         throw var14;
      }

      return audioInputStream2;
   }

   public AudioInputStream getAudioInputStream(InputStream inputStream) throws UnsupportedAudioFileException, IOException {

      if(!((InputStream)inputStream).markSupported()) {
         inputStream = new BufferedInputStream((InputStream)inputStream);
      }

      return super.getAudioInputStream((InputStream)inputStream);
   }

   protected void parseID3v1Frames(byte[] frames, HashMap props) {

      String tag = null;

      try {
         tag = new String(frames, 0, frames.length, "ISO-8859-1");
      } catch (UnsupportedEncodingException var19) {
         tag = new String(frames, 0, frames.length);
      }

      byte start = 3;
      byte var10002 = start;
      int start1 = start + 30;
      String titlev1 = this.chopSubstring(tag, var10002, start1);
      String titlev2 = (String)props.get("title");
      if((titlev2 == null || titlev2.length() == 0) && titlev1 != null) {
         props.put("title", titlev1);
      }

      int var21 = start1;
      start1 += 30;
      String artistv1 = this.chopSubstring(tag, var21, start1);
      String artistv2 = (String)props.get("author");
      if((artistv2 == null || artistv2.length() == 0) && artistv1 != null) {
         props.put("author", artistv1);
      }

      var21 = start1;
      start1 += 30;
      String albumv1 = this.chopSubstring(tag, var21, start1);
      String albumv2 = (String)props.get("album");
      if((albumv2 == null || albumv2.length() == 0) && albumv1 != null) {
         props.put("album", albumv1);
      }

      var21 = start1;
      start1 += 4;
      String yearv1 = this.chopSubstring(tag, var21, start1);
      String yearv2 = (String)props.get("year");
      if((yearv2 == null || yearv2.length() == 0) && yearv1 != null) {
         props.put("date", yearv1);
      }

      var21 = start1;
      start1 += 28;
      String commentv1 = this.chopSubstring(tag, var21, start1);
      String commentv2 = (String)props.get("comment");
      if((commentv2 == null || commentv2.length() == 0) && commentv1 != null) {
         props.put("comment", commentv1);
      }

      String trackv1 = "" + (frames[126] & 255);
      String trackv2 = (String)props.get("mp3.id3tag.track");
      if((trackv2 == null || trackv2.length() == 0) && trackv1 != null) {
         props.put("mp3.id3tag.track", trackv1);
      }

      int genrev1 = frames[127] & 255;
      if(genrev1 >= 0 && genrev1 < id3v1genres.length) {
         String genrev2 = (String)props.get("mp3.id3tag.genre");
         if(genrev2 == null || genrev2.length() == 0) {
            props.put("mp3.id3tag.genre", id3v1genres[genrev1]);
         }
      }

   }

   private String chopSubstring(String s, int start, int end) {
      String str = null;

      try {
         str = s.substring(start, end);
         int e = str.indexOf(0);
         if(e != -1) {
            str = str.substring(0, e);
         }
      } catch (StringIndexOutOfBoundsException var6) {
      }

      return str;
   }

   protected void parseID3v2Frames(InputStream frames, HashMap props) {

      byte[] bframes = null;
      boolean size = true;

      int size1;
      try {
         size1 = frames.available();
         bframes = new byte[size1];
         frames.mark(size1);
         frames.read(bframes);
         frames.reset();
      } catch (IOException var10) {
      }

      if(!"ID3".equals(new String(bframes, 0, 3))) {
      } else {
         int v2version = bframes[3] & 255;
         props.put("mp3.id3tag.v2.version", String.valueOf(v2version));
         if(v2version >= 2 && v2version <= 4) {
            try {

               String e = null;

               for(int i = 10; i < bframes.length && bframes[i] > 0; i += size1) {
                  String scode;
                  if(v2version != 3 && v2version != 4) {
                     scode = new String(bframes, i, 3);
                     size1 = 0 + (bframes[i + 3] << 16) + (bframes[i + 4] << 8) + bframes[i + 5];
                     i += 6;
                     if(scode.equals("TAL") || scode.equals("TT2") || scode.equals("TP1") || scode.equals("TYE") || scode.equals("TRK") || scode.equals("TPA") || scode.equals("TCR") || scode.equals("TCO") || scode.equals("TCM") || scode.equals("COM") || scode.equals("TT1") || scode.equals("TEN") || scode.equals("TPB") || scode.equals("TP2") || scode.equals("TLE")) {
                        if(scode.equals("COM")) {
                           e = this.parseText(bframes, i, size1, 5);
                        } else {
                           e = this.parseText(bframes, i, size1, 1);
                        }

                        if(e != null && e.length() > 0) {
                           if(scode.equals("TAL")) {
                              props.put("album", e);
                           } else if(scode.equals("TT2")) {
                              props.put("title", e);
                           } else if(scode.equals("TYE")) {
                              props.put("date", e);
                           } else if(scode.equals("TP1")) {
                              props.put("author", e);
                           } else if(scode.equals("TCR")) {
                              props.put("copyright", e);
                           } else if(scode.equals("COM")) {
                              props.put("comment", e);
                           } else if(scode.equals("TCO")) {
                              props.put("mp3.id3tag.genre", e);
                           } else if(scode.equals("TRK")) {
                              props.put("mp3.id3tag.track", e);
                           } else if(scode.equals("TPA")) {
                              props.put("mp3.id3tag.disc", e);
                           } else if(scode.equals("TCM")) {
                              props.put("mp3.id3tag.composer", e);
                           } else if(scode.equals("TT1")) {
                              props.put("mp3.id3tag.grouping", e);
                           } else if(scode.equals("TEN")) {
                              props.put("mp3.id3tag.encoded", e);
                           } else if(scode.equals("TPB")) {
                              props.put("mp3.id3tag.publisher", e);
                           } else if(scode.equals("TP2")) {
                              props.put("mp3.id3tag.orchestra", e);
                           } else if(scode.equals("TLE")) {
                              props.put("mp3.id3tag.length", e);
                           }
                        }
                     }
                  } else {
                     scode = new String(bframes, i, 4);
                     size1 = bframes[i + 4] << 24 & -16777216 | bframes[i + 5] << 16 & 16711680 | bframes[i + 6] << 8 & '\uff00' | bframes[i + 7] & 255;
                     i += 10;
                     if(scode.equals("TALB") || scode.equals("TIT2") || scode.equals("TYER") || scode.equals("TPE1") || scode.equals("TCOP") || scode.equals("COMM") || scode.equals("TCON") || scode.equals("TRCK") || scode.equals("TPOS") || scode.equals("TDRC") || scode.equals("TCOM") || scode.equals("TIT1") || scode.equals("TENC") || scode.equals("TPUB") || scode.equals("TPE2") || scode.equals("TLEN")) {
                        if(scode.equals("COMM")) {
                           e = this.parseText(bframes, i, size1, 5);
                        } else {
                           e = this.parseText(bframes, i, size1, 1);
                        }

                        if(e != null && e.length() > 0) {
                           if(scode.equals("TALB")) {
                              props.put("album", e);
                           } else if(scode.equals("TIT2")) {
                              props.put("title", e);
                           } else if(scode.equals("TYER")) {
                              props.put("date", e);
                           } else if(scode.equals("TDRC")) {
                              props.put("date", e);
                           } else if(scode.equals("TPE1")) {
                              props.put("author", e);
                           } else if(scode.equals("TCOP")) {
                              props.put("copyright", e);
                           } else if(scode.equals("COMM")) {
                              props.put("comment", e);
                           } else if(scode.equals("TCON")) {
                              props.put("mp3.id3tag.genre", e);
                           } else if(scode.equals("TRCK")) {
                              props.put("mp3.id3tag.track", e);
                           } else if(scode.equals("TPOS")) {
                              props.put("mp3.id3tag.disc", e);
                           } else if(scode.equals("TCOM")) {
                              props.put("mp3.id3tag.composer", e);
                           } else if(scode.equals("TIT1")) {
                              props.put("mp3.id3tag.grouping", e);
                           } else if(scode.equals("TENC")) {
                              props.put("mp3.id3tag.encoded", e);
                           } else if(scode.equals("TPUB")) {
                              props.put("mp3.id3tag.publisher", e);
                           } else if(scode.equals("TPE2")) {
                              props.put("mp3.id3tag.orchestra", e);
                           } else if(scode.equals("TLEN")) {
                              props.put("mp3.id3tag.length", e);
                           }
                        }
                     }
                  }
               }
            } catch (RuntimeException var9) {
            }

         } else {
         }
      }
   }

   protected String parseText(byte[] bframes, int offset, int size, int skip) {
      String value = null;

      try {
         String[] e = new String[]{"ISO-8859-1", "UTF16", "UTF-16BE", "UTF-8"};
         value = new String(bframes, offset + skip, size - skip, e[bframes[offset]]);
         value = this.chopSubstring(value, 0, value.length());
      } catch (UnsupportedEncodingException var7) {
      }

      return value;
   }

   protected void loadShoutcastInfo(InputStream input, HashMap props) throws IOException {
      IcyInputStream icy = new IcyInputStream(new BufferedInputStream(input));
      HashMap metadata = icy.getTagHash();
      MP3Tag titleMP3Tag = icy.getTag("icy-name");
      if(titleMP3Tag != null) {
         props.put("title", ((String)titleMP3Tag.getValue()).trim());
      }

      MP3Tag[] meta = icy.getTags();
      if(meta != null) {
         new StringBuffer();

         for(int i = 0; i < meta.length; ++i) {
            String key = meta[i].getName();
            String value = ((String)icy.getTag(key).getValue()).trim();
            props.put("mp3.shoutcast.metadata." + key, value);
         }
      }

   }

   static {
      String s = System.getProperty("marklimit");
      if(s != null) {
         try {
            INITAL_READ_LENGTH = Integer.parseInt(s);
            MARK_LIMIT = INITAL_READ_LENGTH + 1;
         } catch (NumberFormatException var2) {
            var2.printStackTrace();
         }
      }

      id3v1genres = new String[]{"Blues", "Classic Rock", "Country", "Dance", "Disco", "Funk", "Grunge", "Hip-Hop", "Jazz", "Metal", "New Age", "Oldies", "Other", "Pop", "R&B", "Rap", "Reggae", "Rock", "Techno", "Industrial", "Alternative", "Ska", "Death Metal", "Pranks", "Soundtrack", "Euro-Techno", "Ambient", "Trip-Hop", "Vocal", "Jazz+Funk", "Fusion", "Trance", "Classical", "Instrumental", "Acid", "House", "Game", "Sound Clip", "Gospel", "Noise", "AlternRock", "Bass", "Soul", "Punk", "Space", "Meditative", "Instrumental Pop", "Instrumental Rock", "Ethnic", "Gothic", "Darkwave", "Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance", "Dream", "Southern Rock", "Comedy", "Cult", "Gangsta", "Top 40", "Christian Rap", "Pop/Funk", "Jungle", "Native American", "Cabaret", "New Wave", "Psychadelic", "Rave", "Showtunes", "Trailer", "Lo-Fi", "Tribal", "Acid Punk", "Acid Jazz", "Polka", "Retro", "Musical", "Rock & Roll", "Hard Rock", "Folk", "Folk-Rock", "National Folk", "Swing", "Fast Fusion", "Bebob", "Latin", "Revival", "Celtic", "Bluegrass", "Avantgarde", "Gothic Rock", "Progressive Rock", "Psychedelic Rock", "Symphonic Rock", "Slow Rock", "Big Band", "Chorus", "Easy Listening", "Acoustic", "Humour", "Speech", "Chanson", "Opera", "Chamber Music", "Sonata", "Symphony", "Booty Brass", "Primus", "Porn Groove", "Satire", "Slow Jam", "Club", "Tango", "Samba", "Folklore", "Ballad", "Power Ballad", "Rhythmic Soul", "Freestyle", "Duet", "Punk Rock", "Drum Solo", "A Capela", "Euro-House", "Dance Hall", "Goa", "Drum & Bass", "Club-House", "Hardcore", "Terror", "Indie", "BritPop", "Negerpunk", "Polsk Punk", "Beat", "Christian Gangsta Rap", "Heavy Metal", "Black Metal", "Crossover", "Contemporary Christian", "Christian Rock", "Merengue", "Salsa", "Thrash Metal", "Anime", "JPop", "SynthPop"};
   }
}
