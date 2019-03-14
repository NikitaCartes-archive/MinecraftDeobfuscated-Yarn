package net.minecraft.util;

import net.minecraft.text.TextFormat;

public enum Rarity {
	field_8906(TextFormat.field_1068),
	field_8907(TextFormat.field_1054),
	field_8903(TextFormat.field_1075),
	field_8904(TextFormat.field_1076);

	public final TextFormat formatting;

	private Rarity(TextFormat textFormat) {
		this.formatting = textFormat;
	}
}
