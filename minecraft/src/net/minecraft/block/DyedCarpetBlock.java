package net.minecraft.block;

import net.minecraft.util.DyeColor;

public class DyedCarpetBlock extends CarpetBlock {
	private final DyeColor dyeColor;

	protected DyedCarpetBlock(DyeColor dyeColor, AbstractBlock.Settings settings) {
		super(settings);
		this.dyeColor = dyeColor;
	}

	public DyeColor getDyeColor() {
		return this.dyeColor;
	}
}
