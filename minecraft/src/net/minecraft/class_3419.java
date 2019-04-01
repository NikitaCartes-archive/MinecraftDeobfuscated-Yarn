package net.minecraft;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum class_3419 {
	field_15250("master"),
	field_15253("music"),
	field_15247("record"),
	field_15252("weather"),
	field_15245("block"),
	field_15251("hostile"),
	field_15254("neutral"),
	field_15248("player"),
	field_15256("ambient"),
	field_15246("voice");

	private static final Map<String, class_3419> field_15257 = (Map<String, class_3419>)Arrays.stream(values())
		.collect(Collectors.toMap(class_3419::method_14840, Function.identity()));
	private final String field_15249;

	private class_3419(String string2) {
		this.field_15249 = string2;
	}

	public String method_14840() {
		return this.field_15249;
	}
}
