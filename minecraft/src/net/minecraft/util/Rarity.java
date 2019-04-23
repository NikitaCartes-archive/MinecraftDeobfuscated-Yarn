package net.minecraft.util;

import net.minecraft.ChatFormat;

public enum Rarity {
	field_8906(ChatFormat.field_1068),
	field_8907(ChatFormat.field_1054),
	field_8903(ChatFormat.field_1075),
	field_8904(ChatFormat.field_1076);

	public final ChatFormat formatting;

	private Rarity(ChatFormat chatFormat) {
		this.formatting = chatFormat;
	}
}
