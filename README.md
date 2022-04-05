<p style="text-align: center;">
<a href="https://discord.gg/UzmeWkD"><img src="https://discordapp.com/api/guilds/704163135787106365/widget.png?style=banner2" /></a> 
<a href="https://twitter.com/keksuccino"><img src="https://user-images.githubusercontent.com/35544624/132924153-df28357d-6816-48a2-96a8-594333d3b075.png" /></a> 
<a href="https://www.patreon.com/keksuccino"><img src="https://user-images.githubusercontent.com/35544624/132924155-25fe4269-5936-4cac-88cf-5d6069e0443a.png" /></a> 
<a href="https://paypal.me/TimSchroeter"><img src="https://user-images.githubusercontent.com/35544624/132924156-ec4300ea-7e10-40de-a271-8effb8fbf5cf.png" /></a>
</p>

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

//Set the clip to loop/not loop
clip.setLooping(true);

//Play the clip
clip.play();

//Pause the clip
clip.pause();

//Resume the clip
clip.unpause();

//Stop the clip (this will reset its progress)
clip.stop();
```

## Download

Auudio is available on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/auudio-forge)!

## Licensing

Auudio is licensed under GPLv3.<br>
See `LICENSE` for more information.

## Copyright

- Auudio © Copyright 2022 Keksuccino.<br>
