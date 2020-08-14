package net.minecraft.entity.effect;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Formatting;

public enum StatusEffectType {
	BENEFICIAL(Formatting.BLUE),
	HARMFUL(Formatting.RED),
	NEUTRAL(Formatting.BLUE);

	private final Formatting formatting;

	private StatusEffectType(Formatting format) {
		this.formatting = format;
	}

	@Environment(EnvType.CLIENT)
	public Formatting getFormatting() {
		return this.formatting;
	}
}
