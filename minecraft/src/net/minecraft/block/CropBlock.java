package net.minecraft.block;

import com.mojang.serialization.MapCodec;
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
	public static final MapCodec<CropBlock> CODEC = createCodec(CropBlock::new);
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

	@Override
	public MapCodec<? extends CropBlock> getCodec() {
		return CODEC;
	}

	protected CropBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(this.getAgeProperty(), Integer.valueOf(0)));
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return AGE_TO_SHAPE[this.getAge(state)];
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		return floor.isOf(Blocks.FARMLAND) || floor.isOf(Blocks.POISON_FARMLAND);
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

	public BlockState withAge(int age, BlockState blockState) {
		return this.getDefaultState().with(this.getAgeProperty(), Integer.valueOf(age));
	}

	public final boolean isMature(BlockState state) {
		return this.getAge(state) >= this.getMaxAge();
	}

	@Override
	protected boolean hasRandomTicks(BlockState state) {
		return !this.isMature(state);
	}

	@Override
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (world.getBaseLightLevel(pos, 0) >= 9) {
			int i = this.getAge(state);
			if (i < this.getMaxAge()) {
				float f = getAvailableMoisture(this, world, pos);
				if (state.isOf(Blocks.POTATOES)) {
					if (random.nextInt((int)(6.25F / f) + 1) == 0) {
						world.setBlockState(pos, this.withAge(i + 1, state), Block.NOTIFY_LISTENERS);
					}
				} else if (random.nextInt((int)(25.0F / f) + 1) == 0) {
					world.setBlockState(pos, this.withAge(i + 1, state), Block.NOTIFY_LISTENERS);
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

		world.setBlockState(pos, this.withAge(i, state), Block.NOTIFY_LISTENERS);
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
				BlockPos blockPos2 = blockPos.add(i, 0, j);
				BlockState blockState = world.getBlockState(blockPos2);
				if (block instanceof PlantBlock plantBlock && plantBlock.canPlantOnTop(blockState, world, blockPos2)) {
					g = 1.0F;
					if ((blockState.isOf(Blocks.FARMLAND) || blockState.isOf(Blocks.POISON_FARMLAND)) && (Integer)blockState.get(FarmlandBlock.MOISTURE) > 0) {
						g = 3.0F;
					}

					if (blockState.isOf(Blocks.CORRUPTED_PEELGRASS_BLOCK)) {
						g = 3.0F;
					}
				}

				if (i != 0 || j != 0) {
					g /= 4.0F;
				}

				f += g;
			}
		}

		BlockPos blockPos3 = pos.north();
		BlockPos blockPos4 = pos.south();
		BlockPos blockPos5 = pos.west();
		BlockPos blockPos2x = pos.east();
		boolean bl = world.getBlockState(blockPos5).isOf(block) || world.getBlockState(blockPos2x).isOf(block);
		boolean bl2 = world.getBlockState(blockPos3).isOf(block) || world.getBlockState(blockPos4).isOf(block);
		if (bl && bl2) {
			f /= 2.0F;
		} else {
			boolean bl3 = world.getBlockState(blockPos5.north()).isOf(block)
				|| world.getBlockState(blockPos2x.north()).isOf(block)
				|| world.getBlockState(blockPos2x.south()).isOf(block)
				|| world.getBlockState(blockPos5.south()).isOf(block);
			if (bl3) {
				f /= 2.0F;
			}
		}

		return f;
	}

	@Override
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return hasEnoughLightAt(world, pos) && super.canPlaceAt(state, world, pos);
	}

	protected static boolean hasEnoughLightAt(WorldView world, BlockPos pos) {
		return world.getBaseLightLevel(pos, 0) >= 8;
	}

	@Override
	protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (entity instanceof RavagerEntity && world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
			world.breakBlock(pos, true, entity);
		}

		super.onEntityCollision(state, world, pos, entity);
	}

	protected ItemConvertible getSeedsItem() {
		return Items.WHEAT_SEEDS;
	}

	@Override
	public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
		return new ItemStack(this.getSeedsItem());
	}

	@Override
	public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
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
