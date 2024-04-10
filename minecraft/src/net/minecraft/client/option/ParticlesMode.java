package net.minecraft.client.option;

import java.util.function.IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

@Environment(EnvType.CLIENT)
public enum ParticlesMode implements TranslatableOption {
	ALL(0, "options.particles.all"),
	DECREASED(1, "options.particles.decreased"),
	MINIMAL(2, "options.particles.minimal");

	private static final IntFunction<ParticlesMode> BY_ID = ValueLists.createIdToValueFunction(ParticlesMode::getId, values(), ValueLists.OutOfBoundsHandling.WRAP);
	private final int id;
	private final String translationKey;

	private ParticlesMode(final int id, final String translationKey) {
		this.id = id;
		this.translationKey = translationKey;
	}

	@Override
	public String getTranslationKey() {
		return this.translationKey;
	}

	@Override
	public int getId() {
		return this.id;
	}

	public static ParticlesMode byId(int id) {
		return (ParticlesMode)BY_ID.apply(id);
	}
}
