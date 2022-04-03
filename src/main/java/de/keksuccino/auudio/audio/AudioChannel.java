package de.keksuccino.auudio.audio;

public enum AudioChannel {

    MASTER("master"),
    MUSIC("music"),
    RECORD("record"),
    WEATHER("weather"),
    BLOCK("block"),
    HOSTILE("hostile"),
    NEUTRAL("neutral"),
    PLAYER("player"),
    AMBIENT("ambient"),
    VOICE("voice");

    private String name;

    AudioChannel(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static AudioChannel getForName(String name) {
        for (AudioChannel c : AudioChannel.values()) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

}
