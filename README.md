# ⚠️ This project can be considered abandoned/archived. It is not maintained anymore.

<br>
<br>
<br>

## About

Source code for the Auudio Minecraft library mod.

**The source code for the different versions of Auudio (Forge, Fabric, multiple MC versions) is separated by branches.**<br>
**For example, if you want to see the code for Auudio Forge MC 1.18, use the `forge-1.18` branch.**

## How To Play Sounds

Playing sounds via Auudio is pretty simple.

To play a sound, just create a new [`AudioClip`](https://github.com/Keksuccino/Auudio/blob/forge-1.18/src/main/java/de/keksuccino/auudio/audio/AudioClip.java) instance.

```java

//Create a clip for a sound asset stored in /assets/
AudioClip clip = AudioClip.buildInternalClip(new ResourceLocation("auudio", "test_file.ogg"), SoundSource.MUSIC);
//Create a clip for an audio file stored on the client system (outside of the JAR)
AudioClip clip = AudioClip.buildExternalClip("test_file.ogg", AudioClip.SoundType.EXTERNAL_LOCAL, SoundSource.MUSIC);
//Create a clip for a web audio file
AudioClip clip = AudioClip.buildExternalClip("https://mydomain.com/test_file.ogg", AudioClip.SoundType.EXTERNAL_WEB, SoundSource.MUSIC);

//Set the clip volume
clip.setVolume(30);

//Get the clip volume
clip.getVolume();

//Set the clip to loop/not loop
clip.setLooping(true);

//Check if the clip should loop
clip.isLooping();

//Play the clip
clip.play();

//Check if the clip is currently playing
clip.playing();

//Pause the clip
clip.pause();

//Check if the clip is paused
clip.paused();

//Resume the clip
clip.unpause();

//Stop the clip (this will reset its progress)
clip.stop();

//Destroy the clip when you don't need it anymore
clip.destroy();

```

## Copyright

- Auudio © Copyright 2026 Keksuccino.<br>
