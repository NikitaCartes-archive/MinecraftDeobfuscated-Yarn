package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.Wearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class AbstractSkullBlock extends BlockWithEntity implements Wearable {
	private final SkullBlock.SkullType type;

	public AbstractSkullBlock(SkullBlock.SkullType type, AbstractBlock.Settings settings) {
		super(settings);
		this.type = type;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new SkullBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return !world.isClient || !state.isOf(Blocks.DRAGON_HEAD) && !state.isOf(Blocks.DRAGON_WALL_HEAD)
			? null
			: checkType(type, BlockEntityType.SKULL, SkullBlockEntity::tick);
	}

	public SkullBlock.SkullType getSkullType() {
		return this.type;
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}
}
