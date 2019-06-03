package net.minecraft.entity.effect;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Formatting;

public enum StatusEffectType {
	field_18271(Formatting.field_1078),
	field_18272(Formatting.field_1061),
	field_18273(Formatting.field_1078);

	private final Formatting field_18274;

	private StatusEffectType(Formatting formatting) {
		this.field_18274 = formatting;
	}

	@Environment(EnvType.CLIENT)
	public Formatting method_18793() {
		return this.field_18274;
	}
}
