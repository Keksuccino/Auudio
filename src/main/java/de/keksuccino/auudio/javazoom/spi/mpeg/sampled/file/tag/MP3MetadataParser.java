package de.keksuccino.auudio.javazoom.spi.mpeg.sampled.file.tag;

public interface MP3MetadataParser {

   void addTagParseListener(TagParseListener var1);

   void removeTagParseListener(TagParseListener var1);

   MP3Tag[] getTags();
}
