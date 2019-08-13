package net.minecraft.sound;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum SoundCategory {
	field_15250("master"),
	field_15253("music"),
	field_15247("record"),
	field_15252("weather"),
	field_15245("block"),
	field_15251("hostile"),
	field_15254("neutral"),
	PLAYERS("player"),
	field_15256("ambient"),
	field_15246("voice");

	private static final Map<String, SoundCategory> NAME_MAP = (Map<String, SoundCategory>)Arrays.stream(values())
		.collect(Collectors.toMap(SoundCategory::getName, Function.identity()));
	private final String name;

	private SoundCategory(String string2) {
		this.name = string2;
	}

	public String getName() {
		return this.name;
	}
}
