package net.minecraft.block;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.MapCodec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TadpoleEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class FrogspawnBlock extends Block {
	public static final MapCodec<FrogspawnBlock> CODEC = createCodec(FrogspawnBlock::new);
	private static final int MIN_TADPOLES = 2;
	private static final int MAX_TADPOLES = 5;
	private static final int MIN_HATCH_TIME = 3600;
	private static final int MAX_HATCH_TIME = 12000;
	protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.5, 16.0);
	private static int minHatchTime = 3600;
	private static int maxHatchTime = 12000;

	@Override
	public MapCodec<FrogspawnBlock> getCodec() {
		return CODEC;
	}

	public FrogspawnBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return canLayAt(world, pos.down());
	}

	@Override
	protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		world.scheduleBlockTick(pos, this, getHatchTime(world.getRandom()));
	}

	private static int getHatchTime(Random random) {
		return random.nextBetweenExclusive(minHatchTime, maxHatchTime);
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		return !this.canPlaceAt(state, world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!this.canPlaceAt(state, world, pos)) {
			this.breakWithoutDrop(world, pos);
		} else {
			this.hatch(world, pos, random);
		}
	}

	@Override
	protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (entity.getType().equals(EntityType.FALLING_BLOCK)) {
			this.breakWithoutDrop(world, pos);
		}
	}

	private static boolean canLayAt(BlockView world, BlockPos pos) {
		FluidState fluidState = world.getFluidState(pos);
		FluidState fluidState2 = world.getFluidState(pos.up());
		return fluidState.getFluid() == Fluids.WATER && fluidState2.getFluid() == Fluids.EMPTY;
	}

	private void hatch(ServerWorld world, BlockPos pos, Random random) {
		this.breakWithoutDrop(world, pos);
		world.playSound(null, pos, SoundEvents.BLOCK_FROGSPAWN_HATCH, SoundCategory.BLOCKS, 1.0F, 1.0F);
		this.spawnTadpoles(world, pos, random);
	}

	private void breakWithoutDrop(World world, BlockPos pos) {
		world.breakBlock(pos, false);
	}

	private void spawnTadpoles(ServerWorld world, BlockPos pos, Random random) {
		int i = random.nextBetweenExclusive(2, 6);

		for (int j = 1; j <= i; j++) {
			TadpoleEntity tadpoleEntity = EntityType.TADPOLE.create(world);
			if (tadpoleEntity != null) {
				double d = (double)pos.getX() + this.getSpawnOffset(random);
				double e = (double)pos.getZ() + this.getSpawnOffset(random);
				int k = random.nextBetweenExclusive(1, 361);
				tadpoleEntity.refreshPositionAndAngles(d, (double)pos.getY() - 0.5, e, (float)k, 0.0F);
				tadpoleEntity.setPersistent();
				world.spawnEntity(tadpoleEntity);
			}
		}
	}

	private double getSpawnOffset(Random random) {
		double d = 0.2F;
		return MathHelper.clamp(random.nextDouble(), 0.2F, 0.7999999970197678);
	}

	@VisibleForTesting
	public static void setHatchTimeRange(int min, int max) {
		minHatchTime = min;
		maxHatchTime = max;
	}

	@VisibleForTesting
	public static void resetHatchTimeRange() {
		minHatchTime = 3600;
		maxHatchTime = 12000;
	}
}
