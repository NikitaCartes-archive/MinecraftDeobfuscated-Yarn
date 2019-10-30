package net.minecraft.client.options;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public enum ParticlesOption {
	ALL(0, "options.particles.all"),
	DECREASED(1, "options.particles.decreased"),
	MINIMAL(2, "options.particles.minimal");

	private static final ParticlesOption[] VALUES = (ParticlesOption[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(ParticlesOption::getId))
		.toArray(ParticlesOption[]::new);
	private final int id;
	private final String translationKey;

	private ParticlesOption(int id, String translationKey) {
		this.id = id;
		this.translationKey = translationKey;
	}

	public String getTranslationKey() {
		return this.translationKey;
	}

	public int getId() {
		return this.id;
	}

	public static ParticlesOption byId(int id) {
		return VALUES[MathHelper.floorMod(id, VALUES.length)];
	}
}
