package net.minecraft.client.options;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public enum ParticlesOption {
	field_18197(0, "options.particles.all"),
	field_18198(1, "options.particles.decreased"),
	field_18199(2, "options.particles.minimal");

	private static final ParticlesOption[] VALUES = (ParticlesOption[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(ParticlesOption::getId))
		.toArray(ParticlesOption[]::new);
	private final int id;
	private final String translationKey;

	private ParticlesOption(int j, String string2) {
		this.id = j;
		this.translationKey = string2;
	}

	public String getTranslationKey() {
		return this.translationKey;
	}

	public int getId() {
		return this.id;
	}

	public static ParticlesOption byId(int i) {
		return VALUES[MathHelper.floorMod(i, VALUES.length)];
	}
}
