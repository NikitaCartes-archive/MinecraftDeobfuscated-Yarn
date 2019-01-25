package net.minecraft.world;

import java.util.Arrays;
import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public enum Difficulty {
	PEACEFUL(0, "peaceful"),
	EASY(1, "easy"),
	NORMAL(2, "normal"),
	HARD(3, "hard");

	private static final Difficulty[] difficultyId = (Difficulty[])Arrays.stream(values())
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

	public TextComponent method_5463() {
		return new TranslatableTextComponent("options.difficulty." + this.translationKey);
	}

	public static Difficulty byId(int i) {
		return difficultyId[i % difficultyId.length];
	}

	@Nullable
	public static Difficulty method_16691(String string) {
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
