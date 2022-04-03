package de.keksuccino.auudio.tritonus.share.sampled;

public interface FloatSampleInput {

   void read(FloatSampleBuffer var1);

   void read(FloatSampleBuffer var1, int var2, int var3);

   boolean isDone();

   int getChannels();

   float getSampleRate();
}
