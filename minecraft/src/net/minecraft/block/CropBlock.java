package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.mob.IllagerBeastEntity;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class CropBlock extends PlantBlock implements Fertilizable {
	public static final IntegerProperty field_10835 = Properties.AGE_7;
	private static final VoxelShape[] field_10836 = new VoxelShape[]{
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
	};

	protected CropBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(this.getAgeProperty(), Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_10836[blockState.get(this.getAgeProperty())];
	}

	@Override
	protected boolean canPlantOnTop(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.getBlock() == Blocks.field_10362;
	}

	public IntegerProperty getAgeProperty() {
		return field_10835;
	}

	public int getCropAgeMaximum() {
		return 7;
	}

	protected int getCropAge(BlockState blockState) {
		return (Integer)blockState.get(this.getAgeProperty());
	}

	public BlockState withCropAge(int i) {
		return this.getDefaultState().with(this.getAgeProperty(), Integer.valueOf(i));
	}

	public boolean isValidState(BlockState blockState) {
		return (Integer)blockState.get(this.getAgeProperty()) >= this.getCropAgeMaximum();
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		super.scheduledTick(blockState, world, blockPos, random);
		if (world.getLightLevel(blockPos.up(), 0) >= 9) {
			int i = this.getCropAge(blockState);
			if (i < this.getCropAgeMaximum()) {
				float f = method_9830(this, world, blockPos);
				if (random.nextInt((int)(25.0F / f) + 1) == 0) {
					world.setBlockState(blockPos, this.withCropAge(i + 1), 2);
				}
			}
		}
	}

	public void applyGrowth(World world, BlockPos blockPos, BlockState blockState) {
		int i = this.getCropAge(blockState) + this.getGrowthAmount(world);
		int j = this.getCropAgeMaximum();
		if (i > j) {
			i = j;
		}

		world.setBlockState(blockPos, this.withCropAge(i), 2);
	}

	protected int getGrowthAmount(World world) {
		return MathHelper.nextInt(world.random, 2, 5);
	}

	protected static float method_9830(Block block, BlockView blockView, BlockPos blockPos) {
		float f = 1.0F;
		BlockPos blockPos2 = blockPos.down();

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				float g = 0.0F;
				BlockState blockState = blockView.getBlockState(blockPos2.add(i, 0, j));
				if (blockState.getBlock() == Blocks.field_10362) {
					g = 1.0F;
					if ((Integer)blockState.get(FarmlandBlock.field_11009) > 0) {
						g = 3.0F;
					}
				}

				if (i != 0 || j != 0) {
					g /= 4.0F;
				}

				f += g;
			}
		}

		BlockPos blockPos3 = blockPos.north();
		BlockPos blockPos4 = blockPos.south();
		BlockPos blockPos5 = blockPos.west();
		BlockPos blockPos6 = blockPos.east();
		boolean bl = block == blockView.getBlockState(blockPos5).getBlock() || block == blockView.getBlockState(blockPos6).getBlock();
		boolean bl2 = block == blockView.getBlockState(blockPos3).getBlock() || block == blockView.getBlockState(blockPos4).getBlock();
		if (bl && bl2) {
			f /= 2.0F;
		} else {
			boolean bl3 = block == blockView.getBlockState(blockPos5.north()).getBlock()
				|| block == blockView.getBlockState(blockPos6.north()).getBlock()
				|| block == blockView.getBlockState(blockPos6.south()).getBlock()
				|| block == blockView.getBlockState(blockPos5.south()).getBlock();
			if (bl3) {
				f /= 2.0F;
			}
		}

		return f;
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return (viewableWorld.getLightLevel(blockPos, 0) >= 8 || viewableWorld.isSkyVisible(blockPos)) && super.canPlaceAt(blockState, viewableWorld, blockPos);
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (entity instanceof IllagerBeastEntity && world.getGameRules().getBoolean("mobGriefing")) {
			world.breakBlock(blockPos, true);
		}

		super.onEntityCollision(blockState, world, blockPos, entity);
	}

	@Environment(EnvType.CLIENT)
	protected ItemProvider getCropItem() {
		return Items.field_8317;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(this.getCropItem());
	}

	@Override
	public boolean isFertilizable(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		return !this.isValidState(blockState);
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return true;
	}

	@Override
	public void grow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		this.applyGrowth(world, blockPos, blockState);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_10835);
	}
}
