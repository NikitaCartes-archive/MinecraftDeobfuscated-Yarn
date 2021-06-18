package net.minecraft.block;

import net.minecraft.util.DyeColor;

/**
 * A carpet that has an associated carpet color for {@linkplain net.minecraft.entity.passive.LlamaEntity llamas}.
 */
public class DyedCarpetBlock extends CarpetBlock {
	private final DyeColor dyeColor;

	/**
	 * @param dyeColor the color of this carpet when worn by a {@linkplain net.minecraft.entity.passive.LlamaEntity llama}
	 */
	protected DyedCarpetBlock(DyeColor dyeColor, AbstractBlock.Settings settings) {
		super(settings);
		this.dyeColor = dyeColor;
	}

	/**
	 * {@return the color of this carpet when worn by a {@linkplain net.minecraft.entity.passive.LlamaEntity llama}}
	 * 
	 * <p>If {@code null}, the llama will not appear to be wearing the carpet.
	 * However, the carpet will remain wearable by the llama.
	 */
	public DyeColor getDyeColor() {
		return this.dyeColor;
	}
}
