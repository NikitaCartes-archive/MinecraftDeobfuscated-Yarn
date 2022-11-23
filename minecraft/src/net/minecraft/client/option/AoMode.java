package net.minecraft.client.option;

import java.util.function.IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

@Environment(EnvType.CLIENT)
public enum AoMode implements TranslatableOption {
	OFF(0, "options.ao.off"),
	MIN(1, "options.ao.min"),
	MAX(2, "options.ao.max");

	private static final IntFunction<AoMode> BY_ID = ValueLists.createIdToValueFunction(AoMode::getId, values(), ValueLists.OutOfBoundsHandling.WRAP);
	private final int id;
	private final String translationKey;

	private AoMode(int id, String translationKey) {
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

	public static AoMode byId(int id) {
		return (AoMode)BY_ID.apply(id);
	}
}
