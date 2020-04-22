package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.Wearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public abstract class AbstractSkullBlock extends BlockWithEntity implements Wearable {
	private final SkullBlock.SkullType type;

	public AbstractSkullBlock(SkullBlock.SkullType type, AbstractBlock.Settings settings) {
		super(settings);
		this.type = type;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new SkullBlockEntity();
	}

	@Environment(EnvType.CLIENT)
	public SkullBlock.SkullType getSkullType() {
		return this.type;
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}
}
