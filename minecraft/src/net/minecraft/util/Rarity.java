package net.minecraft.util;

public enum Rarity {
	field_8906(Formatting.field_1068),
	field_8907(Formatting.field_1054),
	field_8903(Formatting.field_1075),
	field_8904(Formatting.field_1076);

	public final Formatting formatting;

	private Rarity(Formatting formatting) {
		this.formatting = formatting;
	}
}
