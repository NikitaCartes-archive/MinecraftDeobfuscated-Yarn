package net.minecraft.sound;

public enum SoundCategory {
	MASTER("master"),
	MUSIC("music"),
	RECORDS("record"),
	WEATHER("weather"),
	BLOCKS("block"),
	HOSTILE("hostile"),
	NEUTRAL("neutral"),
	PLAYERS("player"),
	AMBIENT("ambient"),
	VOICE("voice");

	private final String name;

	private SoundCategory(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
