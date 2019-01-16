package net.minecraft.block;

public abstract class GourdBlock extends Block {
	public GourdBlock(Block.Settings settings) {
		super(settings);
	}

	public abstract StemBlock getStem();

	public abstract AttachedStemBlock getAttachedStem();
}
