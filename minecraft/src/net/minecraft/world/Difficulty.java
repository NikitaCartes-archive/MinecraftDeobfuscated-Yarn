package net.minecraft.world;

import java.util.Arrays;
import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum Difficulty {
	field_5801(0, "peaceful"),
	field_5805(1, "easy"),
	field_5802(2, "normal"),
	field_5807(3, "hard");

	private static final Difficulty[] BY_NAME = (Difficulty[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(Difficulty::getId))
		.toArray(Difficulty[]::new);
	private final int id;
	private final String name;

	private Difficulty(int j, String string2) {
		this.id = j;
		this.name = string2;
	}

	public int getId() {
		return this.id;
	}

	public Text getTranslatableName() {
		return new TranslatableText("options.difficulty." + this.name);
	}

	public static Difficulty byOrdinal(int i) {
		return BY_NAME[i % BY_NAME.length];
	}

	@Nullable
	public static Difficulty byName(String string) {
		for (Difficulty difficulty : values()) {
			if (difficulty.name.equals(string)) {
				return difficulty;
			}
		}

		return null;
	}

	public String getName() {
		return this.name;
	}
}
