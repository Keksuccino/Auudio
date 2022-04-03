package de.keksuccino.auudio.javazoom.spi.mpeg.sampled.file.tag;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.StringTokenizer;

public class IcyInputStream extends BufferedInputStream implements MP3MetadataParser {

   public static boolean DEBUG = false;
   MP3TagParseSupport tagParseSupport = new MP3TagParseSupport();
   protected static final String INLINE_TAG_SEPARATORS = "; ";
   HashMap tags = new HashMap();
   protected byte[] crlfBuffer = new byte[1024];
   protected int metaint = -1;
   protected int bytesUntilNextMetadata = -1;


   public IcyInputStream(InputStream in) throws IOException {
      super(in);
      this.readInitialHeaders();
      IcyTag metaIntTag = (IcyTag)this.getTag("icy-metaint");
      if(DEBUG) {
         System.out.println("METATAG:" + metaIntTag);
      }

      if(metaIntTag != null) {
         String metaIntString = metaIntTag.getValueAsString();

         try {
            this.metaint = Integer.parseInt(metaIntString.trim());
            if(DEBUG) {
               System.out.println("METAINT:" + this.metaint);
            }

            this.bytesUntilNextMetadata = this.metaint;
         } catch (NumberFormatException var5) {
            ;
         }
      }

   }

   public IcyInputStream(InputStream in, String metaIntString) throws IOException {
      super(in);

      try {
         this.metaint = Integer.parseInt(metaIntString.trim());
         if(DEBUG) {
            System.out.println("METAINT:" + this.metaint);
         }

         this.bytesUntilNextMetadata = this.metaint;
      } catch (NumberFormatException var4) {
         ;
      }

   }

   protected void readInitialHeaders() throws IOException {
      String line = null;

      while(!(line = this.readCRLFLine()).equals("")) {
         int colonIndex = line.indexOf(58);
         if(colonIndex != -1) {
            IcyTag tag = new IcyTag(line.substring(0, colonIndex), line.substring(colonIndex + 1));
            this.addTag(tag);
         }
      }

   }

   protected String readCRLFLine() throws IOException {
      int i;
      for(i = 0; i < this.crlfBuffer.length; ++i) {
         byte aByte = (byte)this.read();
         if(aByte == 13) {
            byte anotherByte = (byte)this.read();
            ++i;
            if(anotherByte == 10) {
               break;
            }

            this.crlfBuffer[i - 1] = aByte;
            this.crlfBuffer[i] = anotherByte;
         } else {
            this.crlfBuffer[i] = aByte;
         }
      }

      return new String(this.crlfBuffer, 0, i - 1);
   }

   public int read() throws IOException {
      if(this.bytesUntilNextMetadata > 0) {
         --this.bytesUntilNextMetadata;
         return super.read();
      } else if(this.bytesUntilNextMetadata == 0) {
         this.readMetadata();
         this.bytesUntilNextMetadata = this.metaint - 1;
         return super.read();
      } else {
         return super.read();
      }
   }

   public int read(byte[] buf, int offset, int length) throws IOException {
      int adjLength;
      int got;
      if(this.bytesUntilNextMetadata > 0) {
         adjLength = Math.min(length, this.bytesUntilNextMetadata);
         got = super.read(buf, offset, adjLength);
         this.bytesUntilNextMetadata -= got;
         return got;
      } else if(this.bytesUntilNextMetadata == 0) {
         this.readMetadata();
         this.bytesUntilNextMetadata = this.metaint;
         adjLength = Math.min(length, this.bytesUntilNextMetadata);
         got = super.read(buf, offset, adjLength);
         this.bytesUntilNextMetadata -= got;
         return got;
      } else {
         return super.read(buf, offset, length);
      }
   }

   public int read(byte[] buf) throws IOException {
      return this.read(buf, 0, buf.length);
   }

   protected void readMetadata() throws IOException {
      int blockCount = super.read();
      if(DEBUG) {
         System.out.println("BLOCKCOUNT:" + blockCount);
      }

      int byteCount = blockCount * 16;
      if(byteCount >= 0) {
         byte[] metadataBlock = new byte[byteCount];

         int bytesRead;
         for(int index = 0; byteCount > 0; byteCount -= bytesRead) {
            bytesRead = super.read(metadataBlock, index, byteCount);
            index += bytesRead;
         }

         if(blockCount > 0) {
            this.parseInlineIcyTags(metadataBlock);
         }

      }
   }

   protected void parseInlineIcyTags(byte[] tagBlock) {
      String blockString = null;

      try {
         blockString = new String(tagBlock, "ISO-8859-1");
      } catch (UnsupportedEncodingException var12) {
         blockString = new String(tagBlock);
      }

      if(DEBUG) {
         System.out.println("BLOCKSTR:" + blockString);
      }

      StringTokenizer izer = new StringTokenizer(blockString, "; ");
      boolean i = false;

      while(izer.hasMoreTokens()) {
         String tagString = izer.nextToken();
         int separatorIdx = tagString.indexOf(61);
         if(separatorIdx != -1) {
            int valueStartIdx = tagString.charAt(separatorIdx + 1) == 39?separatorIdx + 2:separatorIdx + 1;
            int valueEndIdx = tagString.charAt(tagString.length() - 1) == 39?tagString.length() - 1:tagString.length();
            String name = tagString.substring(0, separatorIdx);
            String value = tagString.substring(valueStartIdx, valueEndIdx);
            IcyTag tag = new IcyTag(name, value);
            this.addTag(tag);
         }
      }

   }

   protected void addTag(IcyTag tag) {
      this.tags.put(tag.getName(), tag);
      this.tagParseSupport.fireTagParsed(this, tag);
   }

   public MP3Tag getTag(String tagName) {
      return (MP3Tag)this.tags.get(tagName);
   }

   public MP3Tag[] getTags() {
      return (MP3Tag[])((MP3Tag[])this.tags.values().toArray(new MP3Tag[0]));
   }

   public HashMap getTagHash() {
      return this.tags;
   }

   public void addTagParseListener(TagParseListener tpl) {
      this.tagParseSupport.addTagParseListener(tpl);
   }

   public void removeTagParseListener(TagParseListener tpl) {
      this.tagParseSupport.removeTagParseListener(tpl);
   }

   public static void main(String[] args) {
      byte[] chow = new byte[200];
      if(args.length == 1) {
         try {
            URL e = new URL(args[0]);
            URLConnection conn = e.openConnection();
            conn.setRequestProperty("Icy-Metadata", "1");
            IcyInputStream icy = new IcyInputStream(new BufferedInputStream(conn.getInputStream()));

            while(icy.available() > -1) {
               icy.read(chow, 0, chow.length);
            }
         } catch (Exception var5) {
            var5.printStackTrace();
         }

      }
   }

}
