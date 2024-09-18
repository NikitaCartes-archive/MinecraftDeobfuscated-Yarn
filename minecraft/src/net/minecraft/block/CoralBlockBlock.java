package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class CoralBlockBlock extends Block {
	public static final MapCodec<Block> DEAD_FIELD = Registries.BLOCK.getCodec().fieldOf("dead");
	public static final MapCodec<CoralBlockBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(DEAD_FIELD.forGetter(block -> block.deadCoralBlock), createSettingsCodec()).apply(instance, CoralBlockBlock::new)
	);
	private final Block deadCoralBlock;

	public CoralBlockBlock(Block deadCoralBlock, AbstractBlock.Settings settings) {
		super(settings);
		this.deadCoralBlock = deadCoralBlock;
	}

	@Override
	public MapCodec<CoralBlockBlock> getCodec() {
		return CODEC;
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!this.isInWater(world, pos)) {
			world.setBlockState(pos, this.deadCoralBlock.getDefaultState(), Block.NOTIFY_LISTENERS);
		}
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state,
		WorldView world,
		ScheduledTickView tickView,
		BlockPos pos,
		Direction direction,
		BlockPos neighborPos,
		BlockState neighborState,
		Random random
	) {
		if (!this.isInWater(world, pos)) {
			tickView.scheduleBlockTick(pos, this, 60 + random.nextInt(40));
		}

		return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
	}

	protected boolean isInWater(BlockView world, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			FluidState fluidState = world.getFluidState(pos.offset(direction));
			if (fluidState.isIn(FluidTags.WATER)) {
				return true;
			}
		}

		return false;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		if (!this.isInWater(ctx.getWorld(), ctx.getBlockPos())) {
			ctx.getWorld().scheduleBlockTick(ctx.getBlockPos(), this, 60 + ctx.getWorld().getRandom().nextInt(40));
		}

		return this.getDefaultState();
	}
}
