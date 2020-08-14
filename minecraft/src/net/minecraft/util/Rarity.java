package net.minecraft.util;

public enum Rarity {
	COMMON(Formatting.WHITE),
	UNCOMMON(Formatting.YELLOW),
	RARE(Formatting.AQUA),
	EPIC(Formatting.LIGHT_PURPLE);

	public final Formatting formatting;

	private Rarity(Formatting formatting) {
		this.formatting = formatting;
	}
}
