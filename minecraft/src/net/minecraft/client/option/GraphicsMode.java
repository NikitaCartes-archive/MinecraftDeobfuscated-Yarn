package net.minecraft.client.option;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public enum GraphicsMode {
	FAST(0, "options.graphics.fast"),
	FANCY(1, "options.graphics.fancy"),
	FABULOUS(2, "options.graphics.fabulous");

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
			case FAST:
				return "fast";
			case FANCY:
				return "fancy";
			case FABULOUS:
				return "fabulous";
			default:
				throw new IllegalArgumentException();
		}
	}

	public static GraphicsMode byId(int id) {
		return VALUES[MathHelper.floorMod(id, VALUES.length)];
	}
}
