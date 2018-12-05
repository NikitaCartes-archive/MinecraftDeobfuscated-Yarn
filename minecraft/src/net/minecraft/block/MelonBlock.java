package net.minecraft.block;

public class MelonBlock extends GourdBlock {
	protected MelonBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public StemBlock method_10679() {
		return (StemBlock)Blocks.field_10168;
	}

	@Override
	public StemAttachedBlock method_10680() {
		return (StemAttachedBlock)Blocks.field_10150;
	}
}
