package net.minecraft.util;

import net.minecraft.text.TextFormat;

public enum Rarity {
	field_8906(TextFormat.WHITE),
	field_8907(TextFormat.YELLOW),
	field_8903(TextFormat.AQUA),
	field_8904(TextFormat.LIGHT_PURPLE);

	public final TextFormat formatting;

	private Rarity(TextFormat textFormat) {
		this.formatting = textFormat;
	}
}
