package net.minecraft.client.option;

import java.util.function.IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

@Environment(EnvType.CLIENT)
public enum GraphicsMode implements TranslatableOption {
	FAST(0, "options.graphics.fast"),
	FANCY(1, "options.graphics.fancy"),
	FABULOUS(2, "options.graphics.fabulous");

	private static final IntFunction<GraphicsMode> BY_ID = ValueLists.createIdToValueFunction(GraphicsMode::getId, values(), ValueLists.OutOfBoundsHandling.WRAP);
	private final int id;
	private final String translationKey;

	private GraphicsMode(final int id, final String translationKey) {
		this.id = id;
		this.translationKey = translationKey;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public String getTranslationKey() {
		return this.translationKey;
	}

	public String toString() {
		return switch (this) {
			case FAST -> "fast";
			case FANCY -> "fancy";
			case FABULOUS -> "fabulous";
		};
	}

	public static GraphicsMode byId(int id) {
		return (GraphicsMode)BY_ID.apply(id);
	}
}
