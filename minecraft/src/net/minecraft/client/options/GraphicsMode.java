package net.minecraft.client.options;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public enum GraphicsMode {
	field_25427(0, "options.graphics.fast"),
	field_25428(1, "options.graphics.fancy"),
	field_25429(2, "options.graphics.fabulous");

	private static final GraphicsMode[] VALUES = (GraphicsMode[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(GraphicsMode::getId))
		.toArray(GraphicsMode[]::new);
	private final int id;
	private final String translationKey;

	private GraphicsMode(int id, String translationKey) {
		this.id = id;
		this.translationKey = translationKey;
	}

	public int getId() {
		return this.id;
	}

	public String getTranslationKey() {
		return this.translationKey;
	}

	public GraphicsMode next() {
		return byId(this.getId() + 1);
	}

	public String toString() {
		switch (this) {
			case field_25427:
				return "fast";
			case field_25428:
				return "fancy";
			case field_25429:
				return "fabulous";
			default:
				throw new IllegalArgumentException();
		}
	}

	public static GraphicsMode byId(int id) {
		return VALUES[MathHelper.floorMod(id, VALUES.length)];
	}
}
