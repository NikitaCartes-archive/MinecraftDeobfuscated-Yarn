package net.minecraft.block;

public abstract class GourdBlock extends Block {
	public GourdBlock(Block.Settings settings) {
		super(settings);
	}

	public abstract StemBlock method_10679();

	public abstract StemAttachedBlock method_10680();
}
