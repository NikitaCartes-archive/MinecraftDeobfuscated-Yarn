package net.minecraft.world;

import java.util.Arrays;
import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum Difficulty {
	field_5801(0, "peaceful"),
	field_5805(1, "easy"),
	field_5802(2, "normal"),
	field_5807(3, "hard");

	private static final Difficulty[] DIFFICULTIES = (Difficulty[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(Difficulty::getId))
		.toArray(Difficulty[]::new);
	private final int id;
	private final String translationKey;

	private Difficulty(int j, String string2) {
		this.id = j;
		this.translationKey = string2;
	}

	public int getId() {
		return this.id;
	}

	public Component toTextComponent() {
		return new TranslatableComponent("options.difficulty." + this.translationKey);
	}

	public static Difficulty getDifficulty(int i) {
		return DIFFICULTIES[i % DIFFICULTIES.length];
	}

	@Nullable
	public static Difficulty getDifficulty(String string) {
		for (Difficulty difficulty : values()) {
			if (difficulty.translationKey.equals(string)) {
				return difficulty;
			}
		}

		return null;
	}

	public String getTranslationKey() {
		return this.translationKey;
	}
}
