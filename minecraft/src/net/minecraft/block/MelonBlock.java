package net.minecraft.block;

public class MelonBlock extends GourdBlock {
	protected MelonBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public StemBlock getStem() {
		return (StemBlock)Blocks.field_10168;
	}

	@Override
	public AttachedStemBlock getAttachedStem() {
		return (AttachedStemBlock)Blocks.field_10150;
	}
}
