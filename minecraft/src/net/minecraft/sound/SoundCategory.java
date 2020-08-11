package net.minecraft.sound;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

	private static final Map<String, SoundCategory> BY_NAME = (Map<String, SoundCategory>)Arrays.stream(values())
		.collect(Collectors.toMap(SoundCategory::getName, Function.identity()));
	private final String name;

	private SoundCategory(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
