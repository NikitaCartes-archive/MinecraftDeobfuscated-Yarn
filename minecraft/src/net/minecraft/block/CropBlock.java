package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.mob.RavagerEntity;
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
	public static final IntegerProperty field_10835 = Properties.field_12550;
	private static final VoxelShape[] field_10836 = new VoxelShape[]{
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 14.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
	};

	protected CropBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(this.method_9824(), Integer.valueOf(0)));
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_10836[blockState.method_11654(this.method_9824())];
	}

	@Override
	protected boolean method_9695(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.getBlock() == Blocks.field_10362;
	}

	public IntegerProperty method_9824() {
		return field_10835;
	}

	public int getCropAgeMaximum() {
		return 7;
	}

	protected int method_9829(BlockState blockState) {
		return (Integer)blockState.method_11654(this.method_9824());
	}

	public BlockState method_9828(int i) {
		return this.method_9564().method_11657(this.method_9824(), Integer.valueOf(i));
	}

	public boolean method_9825(BlockState blockState) {
		return (Integer)blockState.method_11654(this.method_9824()) >= this.getCropAgeMaximum();
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		super.method_9588(blockState, world, blockPos, random);
		if (world.method_8624(blockPos, 0) >= 9) {
			int i = this.method_9829(blockState);
			if (i < this.getCropAgeMaximum()) {
				float f = method_9830(this, world, blockPos);
				if (random.nextInt((int)(25.0F / f) + 1) == 0) {
					world.method_8652(blockPos, this.method_9828(i + 1), 2);
				}
			}
		}
	}

	public void method_9826(World world, BlockPos blockPos, BlockState blockState) {
		int i = this.method_9829(blockState) + this.getGrowthAmount(world);
		int j = this.getCropAgeMaximum();
		if (i > j) {
			i = j;
		}

		world.method_8652(blockPos, this.method_9828(i), 2);
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
				BlockState blockState = blockView.method_8320(blockPos2.add(i, 0, j));
				if (blockState.getBlock() == Blocks.field_10362) {
					g = 1.0F;
					if ((Integer)blockState.method_11654(FarmlandBlock.field_11009) > 0) {
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
		boolean bl = block == blockView.method_8320(blockPos5).getBlock() || block == blockView.method_8320(blockPos6).getBlock();
		boolean bl2 = block == blockView.method_8320(blockPos3).getBlock() || block == blockView.method_8320(blockPos4).getBlock();
		if (bl && bl2) {
			f /= 2.0F;
		} else {
			boolean bl3 = block == blockView.method_8320(blockPos5.north()).getBlock()
				|| block == blockView.method_8320(blockPos6.north()).getBlock()
				|| block == blockView.method_8320(blockPos6.south()).getBlock()
				|| block == blockView.method_8320(blockPos5.south()).getBlock();
			if (bl3) {
				f /= 2.0F;
			}
		}

		return f;
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return (viewableWorld.method_8624(blockPos, 0) >= 8 || viewableWorld.method_8311(blockPos)) && super.method_9558(blockState, viewableWorld, blockPos);
	}

	@Override
	public void method_9548(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (entity instanceof RavagerEntity && world.getGameRules().getBoolean("mobGriefing")) {
			world.method_8651(blockPos, true);
		}

		super.method_9548(blockState, world, blockPos, entity);
	}

	@Environment(EnvType.CLIENT)
	protected ItemProvider getCropItem() {
		return Items.field_8317;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack method_9574(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(this.getCropItem());
	}

	@Override
	public boolean method_9651(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		return !this.method_9825(blockState);
	}

	@Override
	public boolean method_9650(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return true;
	}

	@Override
	public void method_9652(World world, Random random, BlockPos blockPos, BlockState blockState) {
		this.method_9826(world, blockPos, blockState);
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10835);
	}
}
