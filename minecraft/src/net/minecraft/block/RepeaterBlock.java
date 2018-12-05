package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticle;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class RepeaterBlock extends AbstractRedstoneGateBlock {
	public static final BooleanProperty field_11452 = Properties.LOCKED;
	public static final IntegerProperty field_11451 = Properties.DELAY;

	protected RepeaterBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(field_11177, Direction.NORTH)
				.with(field_11451, Integer.valueOf(1))
				.with(field_11452, Boolean.valueOf(false))
				.with(field_10911, Boolean.valueOf(false))
		);
	}

	@Override
	public boolean method_9534(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		if (!playerEntity.abilities.allowModifyWorld) {
			return false;
		} else {
			world.setBlockState(blockPos, blockState.method_11572(field_11451), 3);
			return true;
		}
	}

	@Override
	protected int getUpdateDelayInternal(BlockState blockState) {
		return (Integer)blockState.get(field_11451) * 2;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = super.getPlacementState(itemPlacementContext);
		return blockState.with(field_11452, Boolean.valueOf(this.method_9996(itemPlacementContext.getWorld(), itemPlacementContext.getPos(), blockState)));
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return !iWorld.isRemote() && direction.getAxis() != ((Direction)blockState.get(field_11177)).getAxis()
			? blockState.with(field_11452, Boolean.valueOf(this.method_9996(iWorld, blockPos, blockState)))
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean method_9996(ViewableWorld viewableWorld, BlockPos blockPos, BlockState blockState) {
		return this.getMaxInputLevelSides(viewableWorld, blockPos, blockState) > 0;
	}

	@Override
	protected boolean isValidInput(BlockState blockState) {
		return method_9999(blockState);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.get(field_10911)) {
			Direction direction = blockState.get(field_11177);
			double d = (double)((float)blockPos.getX() + 0.5F) + (double)(random.nextFloat() - 0.5F) * 0.2;
			double e = (double)((float)blockPos.getY() + 0.4F) + (double)(random.nextFloat() - 0.5F) * 0.2;
			double f = (double)((float)blockPos.getZ() + 0.5F) + (double)(random.nextFloat() - 0.5F) * 0.2;
			float g = -5.0F;
			if (random.nextBoolean()) {
				g = (float)((Integer)blockState.get(field_11451) * 2 - 1);
			}

			g /= 16.0F;
			double h = (double)(g * (float)direction.getOffsetX());
			double i = (double)(g * (float)direction.getOffsetZ());
			world.method_8406(DustParticle.field_11188, d + h, e, f + i, 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11177, field_11451, field_11452, field_10911);
	}
}
