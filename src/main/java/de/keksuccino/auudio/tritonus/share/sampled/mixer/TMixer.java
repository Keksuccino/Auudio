package de.keksuccino.auudio.tritonus.share.sampled.mixer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import de.keksuccino.auudio.tritonus.share.ArraySet;
import de.keksuccino.auudio.tritonus.share.sampled.AudioFormats;

public abstract class TMixer extends TLine implements Mixer {

   private static javax.sound.sampled.Line.Info[] EMPTY_LINE_INFO_ARRAY = new javax.sound.sampled.Line.Info[0];
   private static Line[] EMPTY_LINE_ARRAY = new Line[0];
   private Mixer.Info m_mixerInfo;
   private Collection m_supportedSourceFormats;
   private Collection m_supportedTargetFormats;
   private Collection m_supportedSourceLineInfos;
   private Collection m_supportedTargetLineInfos;
   private Set m_openSourceDataLines;
   private Set m_openTargetDataLines;


   protected TMixer(Mixer.Info mixerInfo, javax.sound.sampled.Line.Info lineInfo) {
      this(mixerInfo, lineInfo, new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList());
   }

   protected TMixer(Mixer.Info mixerInfo, javax.sound.sampled.Line.Info lineInfo, Collection supportedSourceFormats, Collection supportedTargetFormats, Collection supportedSourceLineInfos, Collection supportedTargetLineInfos) {
      super((TMixer)null, lineInfo);

      this.m_mixerInfo = mixerInfo;
      this.setSupportInformation(supportedSourceFormats, supportedTargetFormats, supportedSourceLineInfos, supportedTargetLineInfos);
      this.m_openSourceDataLines = new ArraySet();
      this.m_openTargetDataLines = new ArraySet();

   }

   protected void setSupportInformation(Collection supportedSourceFormats, Collection supportedTargetFormats, Collection supportedSourceLineInfos, Collection supportedTargetLineInfos) {

      this.m_supportedSourceFormats = supportedSourceFormats;
      this.m_supportedTargetFormats = supportedTargetFormats;
      this.m_supportedSourceLineInfos = supportedSourceLineInfos;
      this.m_supportedTargetLineInfos = supportedTargetLineInfos;

   }

   public Mixer.Info getMixerInfo() {

      return this.m_mixerInfo;
   }

   public javax.sound.sampled.Line.Info[] getSourceLineInfo() {

      javax.sound.sampled.Line.Info[] infos = (javax.sound.sampled.Line.Info[])((javax.sound.sampled.Line.Info[])this.m_supportedSourceLineInfos.toArray(EMPTY_LINE_INFO_ARRAY));

      return infos;
   }

   public javax.sound.sampled.Line.Info[] getTargetLineInfo() {

      javax.sound.sampled.Line.Info[] infos = (javax.sound.sampled.Line.Info[])((javax.sound.sampled.Line.Info[])this.m_supportedTargetLineInfos.toArray(EMPTY_LINE_INFO_ARRAY));

      return infos;
   }

   public javax.sound.sampled.Line.Info[] getSourceLineInfo(javax.sound.sampled.Line.Info info) {

      return EMPTY_LINE_INFO_ARRAY;
   }

   public javax.sound.sampled.Line.Info[] getTargetLineInfo(javax.sound.sampled.Line.Info info) {

      return EMPTY_LINE_INFO_ARRAY;
   }

   public boolean isLineSupported(javax.sound.sampled.Line.Info info) {

      Class lineClass = info.getLineClass();
      return lineClass.equals(SourceDataLine.class)?isLineSupportedImpl(info, this.m_supportedSourceLineInfos):(lineClass.equals(TargetDataLine.class)?isLineSupportedImpl(info, this.m_supportedTargetLineInfos):(!lineClass.equals(Port.class)?false:isLineSupportedImpl(info, this.m_supportedSourceLineInfos) || isLineSupportedImpl(info, this.m_supportedTargetLineInfos)));
   }

   private static boolean isLineSupportedImpl(javax.sound.sampled.Line.Info info, Collection supportedLineInfos) {
      Iterator iterator = supportedLineInfos.iterator();

      javax.sound.sampled.Line.Info info2;
      do {
         if(!iterator.hasNext()) {
            return false;
         }

         info2 = (javax.sound.sampled.Line.Info)iterator.next();
      } while(!info2.matches(info));

      return true;
   }

   public Line getLine(javax.sound.sampled.Line.Info info) throws LineUnavailableException {

      Class lineClass = info.getLineClass();
      javax.sound.sampled.DataLine.Info dataLineInfo = null;
      javax.sound.sampled.Port.Info portInfo = null;
      AudioFormat[] aFormats = null;
      if(info instanceof javax.sound.sampled.DataLine.Info) {
         dataLineInfo = (javax.sound.sampled.DataLine.Info)info;
         aFormats = dataLineInfo.getFormats();
      } else if(info instanceof javax.sound.sampled.Port.Info) {
         portInfo = (javax.sound.sampled.Port.Info)info;
      }

      AudioFormat format = null;
      Object line = null;
      if(lineClass == SourceDataLine.class) {

         if(dataLineInfo == null) {
            throw new IllegalArgumentException("need DataLine.Info for SourceDataLine");
         }

         format = this.getSupportedSourceFormat(aFormats);
         line = this.getSourceDataLine(format, dataLineInfo.getMaxBufferSize());
      } else if(lineClass == Clip.class) {

         if(dataLineInfo == null) {
            throw new IllegalArgumentException("need DataLine.Info for Clip");
         }

         format = this.getSupportedSourceFormat(aFormats);
         line = this.getClip(format);
      } else if(lineClass == TargetDataLine.class) {

         if(dataLineInfo == null) {
            throw new IllegalArgumentException("need DataLine.Info for TargetDataLine");
         }

         format = this.getSupportedTargetFormat(aFormats);
         line = this.getTargetDataLine(format, dataLineInfo.getMaxBufferSize());
      } else {
         if(lineClass != Port.class) {
            throw new LineUnavailableException("unknown line class: " + lineClass);
         }

         if(portInfo == null) {
            throw new IllegalArgumentException("need Port.Info for Port");
         }

         line = this.getPort(portInfo);
      }

      return (Line)line;
   }

   protected SourceDataLine getSourceDataLine(AudioFormat format, int nBufferSize) throws LineUnavailableException {

      throw new IllegalArgumentException("this mixer does not support SourceDataLines");
   }

   protected Clip getClip(AudioFormat format) throws LineUnavailableException {

      throw new IllegalArgumentException("this mixer does not support Clips");
   }

   protected TargetDataLine getTargetDataLine(AudioFormat format, int nBufferSize) throws LineUnavailableException {

      throw new IllegalArgumentException("this mixer does not support TargetDataLines");
   }

   protected Port getPort(javax.sound.sampled.Port.Info info) throws LineUnavailableException {

      throw new IllegalArgumentException("this mixer does not support Ports");
   }

   private AudioFormat getSupportedSourceFormat(AudioFormat[] aFormats) {

      AudioFormat format = null;

      for(int i = 0; i < aFormats.length; ++i) {

         if(this.isSourceFormatSupported(aFormats[i])) {

            format = aFormats[i];
            break;
         }

      }

      if(format == null) {
         throw new IllegalArgumentException("no line matchine one of the passed formats");
      } else {

         return format;
      }
   }

   private AudioFormat getSupportedTargetFormat(AudioFormat[] aFormats) {

      AudioFormat format = null;

      for(int i = 0; i < aFormats.length; ++i) {

         if(this.isTargetFormatSupported(aFormats[i])) {

            format = aFormats[i];
            break;
         }

      }

      if(format == null) {
         throw new IllegalArgumentException("no line matchine one of the passed formats");
      } else {

         return format;
      }
   }

   public Line[] getSourceLines() {

      return (Line[])((Line[])this.m_openSourceDataLines.toArray(EMPTY_LINE_ARRAY));
   }

   public Line[] getTargetLines() {

      return (Line[])((Line[])this.m_openTargetDataLines.toArray(EMPTY_LINE_ARRAY));
   }

   public void synchronize(Line[] aLines, boolean bMaintainSync) {
      throw new IllegalArgumentException("synchronization not supported");
   }

   public void unsynchronize(Line[] aLines) {
      throw new IllegalArgumentException("synchronization not supported");
   }

   public boolean isSynchronizationSupported(Line[] aLines, boolean bMaintainSync) {
      return false;
   }

   protected boolean isSourceFormatSupported(AudioFormat format) {

      Iterator iterator = this.m_supportedSourceFormats.iterator();

      AudioFormat supportedFormat;
      do {
         if(!iterator.hasNext()) {
            return false;
         }

         supportedFormat = (AudioFormat)iterator.next();
      } while(!AudioFormats.matches(supportedFormat, format));

      return true;
   }

   protected boolean isTargetFormatSupported(AudioFormat format) {

      Iterator iterator = this.m_supportedTargetFormats.iterator();

      AudioFormat supportedFormat;
      do {
         if(!iterator.hasNext()) {
            return false;
         }

         supportedFormat = (AudioFormat)iterator.next();
      } while(!AudioFormats.matches(supportedFormat, format));

      return true;
   }

   void registerOpenLine(Line line) {

      Set var2;
      if(line instanceof SourceDataLine) {
         var2 = this.m_openSourceDataLines;
         synchronized(this.m_openSourceDataLines) {
            this.m_openSourceDataLines.add((SourceDataLine)line);
         }
      } else if(line instanceof TargetDataLine) {
         var2 = this.m_openSourceDataLines;
         synchronized(this.m_openSourceDataLines) {
            this.m_openTargetDataLines.add((TargetDataLine)line);
         }
      }

   }

   void unregisterOpenLine(Line line) {

      Set var2;
      if(line instanceof SourceDataLine) {
         var2 = this.m_openSourceDataLines;
         synchronized(this.m_openSourceDataLines) {
            this.m_openSourceDataLines.remove((SourceDataLine)line);
         }
      } else if(line instanceof TargetDataLine) {
         var2 = this.m_openTargetDataLines;
         synchronized(this.m_openTargetDataLines) {
            this.m_openTargetDataLines.remove((TargetDataLine)line);
         }
      }

   }

}
