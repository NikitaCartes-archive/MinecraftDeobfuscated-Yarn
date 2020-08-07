package net.minecraft.client.options;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public enum AoMode {
	field_18144(0, "options.ao.off"),
	field_18145(1, "options.ao.min"),
	field_18146(2, "options.ao.max");

	private static final AoMode[] VALUES = (AoMode[])Arrays.stream(values()).sorted(Comparator.comparingInt(AoMode::getId)).toArray(AoMode[]::new);
	private final int id;
	private final String translationKey;

	private AoMode(int id, String translationKey) {
		this.id = id;
		this.translationKey = translationKey;
	}

	public int getId() {
		return this.id;
	}

	public String getTranslationKey() {
		return this.translationKey;
	}

	public static AoMode byId(int id) {
		return VALUES[MathHelper.floorMod(id, VALUES.length)];
	}
}
