package net.minecraft.entity.effect;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Formatting;

public enum StatusEffectType {
	field_18271(Formatting.field_1078),
	field_18272(Formatting.field_1061),
	field_18273(Formatting.field_1078);

	private final Formatting formatting;

	private StatusEffectType(Formatting format) {
		this.formatting = format;
	}

	@Environment(EnvType.CLIENT)
	public Formatting getFormatting() {
		return this.formatting;
	}
}
