package net.minecraft.client.options;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public enum NarratorOption {
	field_18176(0, "options.narrator.off"),
	field_18177(1, "options.narrator.all"),
	field_18178(2, "options.narrator.chat"),
	field_18179(3, "options.narrator.system");

	private static final NarratorOption[] VALUES = (NarratorOption[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(NarratorOption::getId))
		.toArray(NarratorOption[]::new);
	private final int id;
	private final String translationKey;

	private NarratorOption(int j, String string2) {
		this.id = j;
		this.translationKey = string2;
	}

	public int getId() {
		return this.id;
	}

	public String getTranslationKey() {
		return this.translationKey;
	}

	public static NarratorOption byId(int i) {
		return VALUES[MathHelper.floorMod(i, VALUES.length)];
	}
}
