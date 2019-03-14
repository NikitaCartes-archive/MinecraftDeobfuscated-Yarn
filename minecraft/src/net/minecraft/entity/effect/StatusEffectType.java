package net.minecraft.entity.effect;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextFormat;

public enum StatusEffectType {
	field_18271(TextFormat.field_1078),
	field_18272(TextFormat.field_1061),
	field_18273(TextFormat.field_1078);

	private final TextFormat formatting;

	private StatusEffectType(TextFormat textFormat) {
		this.formatting = textFormat;
	}

	@Environment(EnvType.CLIENT)
	public TextFormat getFormatting() {
		return this.formatting;
	}
}
