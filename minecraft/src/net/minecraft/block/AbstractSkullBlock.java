package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.world.BlockView;

public abstract class AbstractSkullBlock extends BlockWithEntity {
	private final SkullBlock.SkullType type;

	public AbstractSkullBlock(SkullBlock.SkullType skullType, Block.Settings settings) {
		super(settings);
		this.type = skullType;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean hasBlockEntityBreakingRender(BlockState blockState) {
		return true;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new SkullBlockEntity();
	}

	@Environment(EnvType.CLIENT)
	public SkullBlock.SkullType getSkullType() {
		return this.type;
	}
}
