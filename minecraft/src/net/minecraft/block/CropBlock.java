package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class CropBlock extends PlantBlock implements Fertilizable {
	public static final int MAX_AGE = 7;
	public static final IntProperty AGE = Properties.AGE_7;
	private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
	};

	protected CropBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(this.getAgeProperty(), Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return AGE_TO_SHAPE[this.getAge(state)];
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		return floor.isOf(Blocks.FARMLAND);
	}

	protected IntProperty getAgeProperty() {
		return AGE;
	}

	public int getMaxAge() {
		return 7;
	}

	public int getAge(BlockState state) {
		return (Integer)state.get(this.getAgeProperty());
	}

	public BlockState withAge(int age) {
		return this.getDefaultState().with(this.getAgeProperty(), Integer.valueOf(age));
	}

	public final boolean isMature(BlockState blockState) {
		return this.getAge(blockState) >= this.getMaxAge();
	}

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return !this.isMature(state);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (world.getBaseLightLevel(pos, 0) >= 9) {
			int i = this.getAge(state);
			if (i < this.getMaxAge()) {
				float f = getAvailableMoisture(this, world, pos);
				if (random.nextInt((int)(25.0F / f) + 1) == 0) {
					world.setBlockState(pos, this.withAge(i + 1), Block.NOTIFY_LISTENERS);
				}
			}
		}
	}

	public void applyGrowth(World world, BlockPos pos, BlockState state) {
		int i = this.getAge(state) + this.getGrowthAmount(world);
		int j = this.getMaxAge();
		if (i > j) {
			i = j;
		}

		world.setBlockState(pos, this.withAge(i), Block.NOTIFY_LISTENERS);
	}

	protected int getGrowthAmount(World world) {
		return MathHelper.nextInt(world.random, 2, 5);
	}

	protected static float getAvailableMoisture(Block block, BlockView world, BlockPos pos) {
		float f = 1.0F;
		BlockPos blockPos = pos.down();

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				float g = 0.0F;
				BlockState blockState = world.getBlockState(blockPos.add(i, 0, j));
				if (blockState.isOf(Blocks.FARMLAND)) {
					g = 1.0F;
					if ((Integer)blockState.get(FarmlandBlock.MOISTURE) > 0) {
						g = 3.0F;
					}
				}

				if (i != 0 || j != 0) {
					g /= 4.0F;
				}

				f += g;
			}
		}

		BlockPos blockPos2 = pos.north();
		BlockPos blockPos3 = pos.south();
		BlockPos blockPos4 = pos.west();
		BlockPos blockPos5 = pos.east();
		boolean bl = world.getBlockState(blockPos4).isOf(block) || world.getBlockState(blockPos5).isOf(block);
		boolean bl2 = world.getBlockState(blockPos2).isOf(block) || world.getBlockState(blockPos3).isOf(block);
		if (bl && bl2) {
			f /= 2.0F;
		} else {
			boolean bl3 = world.getBlockState(blockPos4.north()).isOf(block)
				|| world.getBlockState(blockPos5.north()).isOf(block)
				|| world.getBlockState(blockPos5.south()).isOf(block)
				|| world.getBlockState(blockPos4.south()).isOf(block);
			if (bl3) {
				f /= 2.0F;
			}
		}

		return f;
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return (world.getBaseLightLevel(pos, 0) >= 8 || world.isSkyVisible(pos)) && super.canPlaceAt(state, world, pos);
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (entity instanceof RavagerEntity && world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
			world.breakBlock(pos, true, entity);
		}

		super.onEntityCollision(state, world, pos, entity);
	}

	protected ItemConvertible getSeedsItem() {
		return Items.WHEAT_SEEDS;
	}

	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(this.getSeedsItem());
	}

	@Override
	public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) {
		return !this.isMature(state);
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		this.applyGrowth(world, pos, state);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}
}
