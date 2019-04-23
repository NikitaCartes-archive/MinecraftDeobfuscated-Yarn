package net.minecraft.entity.effect;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;

public enum StatusEffectType {
	field_18271(ChatFormat.field_1078),
	field_18272(ChatFormat.field_1061),
	field_18273(ChatFormat.field_1078);

	private final ChatFormat formatting;

	private StatusEffectType(ChatFormat chatFormat) {
		this.formatting = chatFormat;
	}

	@Environment(EnvType.CLIENT)
	public ChatFormat getFormatting() {
		return this.formatting;
	}
}
