package net.minecraft.block;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TorchflowerBlock extends CropBlock {
	public static final int field_42775 = 2;
	public static final IntProperty field_42776 = Properties.AGE_2;
	private static final float field_42777 = 4.0F;
	private static final VoxelShape[] SHAPES = new VoxelShape[]{
		Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 10.0, 12.0),
		Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 10.0, 12.0),
		Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 10.0, 12.0)
	};

	public TorchflowerBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(field_42776);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPES[state.get(this.getAgeProperty())];
	}

	@Override
	public IntProperty getAgeProperty() {
		return field_42776;
	}

	@Override
	public int getMaxAge() {
		return 2;
	}

	@Override
	protected ItemConvertible getSeedsItem() {
		return Items.TORCHFLOWER_SEEDS;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (random.nextInt(3) != 0) {
			super.randomTick(state, world, pos, random);
		}
	}

	@Override
	protected int getGrowthAmount(World world) {
		return super.getGrowthAmount(world) / 3;
	}
}
