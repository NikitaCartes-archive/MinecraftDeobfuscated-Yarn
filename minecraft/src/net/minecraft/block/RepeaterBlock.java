package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class RepeaterBlock extends AbstractRedstoneGateBlock {
	public static final BooleanProperty LOCKED = Properties.LOCKED;
	public static final IntProperty field_11451 = Properties.field_12494;

	protected RepeaterBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(FACING, Direction.field_11043)
				.with(field_11451, Integer.valueOf(1))
				.with(LOCKED, Boolean.valueOf(false))
				.with(POWERED, Boolean.valueOf(false))
		);
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (!playerEntity.abilities.allowModifyWorld) {
			return false;
		} else {
			world.setBlockState(blockPos, blockState.cycle(field_11451), 3);
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
		return blockState.with(LOCKED, Boolean.valueOf(this.isLocked(itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos(), blockState)));
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return !iWorld.isClient() && direction.getAxis() != ((Direction)blockState.get(FACING)).getAxis()
			? blockState.with(LOCKED, Boolean.valueOf(this.isLocked(iWorld, blockPos, blockState)))
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean isLocked(ViewableWorld viewableWorld, BlockPos blockPos, BlockState blockState) {
		return this.getMaxInputLevelSides(viewableWorld, blockPos, blockState) > 0;
	}

	@Override
	protected boolean isValidInput(BlockState blockState) {
		return isRedstoneGate(blockState);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.get(POWERED)) {
			Direction direction = blockState.get(FACING);
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
			world.addParticle(DustParticleEffect.RED, d + h, e, f + i, 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(FACING, field_11451, LOCKED, POWERED);
	}
}
