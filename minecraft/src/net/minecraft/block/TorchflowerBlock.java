package net.minecraft.block;

import com.mojang.serialization.MapCodec;
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
	public static final MapCodec<TorchflowerBlock> CODEC = createCodec(TorchflowerBlock::new);
	public static final int field_42775 = 2;
	public static final IntProperty AGE = Properties.AGE_1;
	private static final float field_42777 = 3.0F;
	private static final VoxelShape[] SHAPES = new VoxelShape[]{
		Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0), Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 10.0, 11.0)
	};
	private static final int field_44479 = 1;

	@Override
	public MapCodec<TorchflowerBlock> getCodec() {
		return CODEC;
	}

	public TorchflowerBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPES[this.getAge(state)];
	}

	@Override
	protected IntProperty getAgeProperty() {
		return AGE;
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
	public BlockState withAge(int age) {
		return age == 2 ? Blocks.TORCHFLOWER.getDefaultState() : super.withAge(age);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (random.nextInt(3) != 0) {
			super.randomTick(state, world, pos, random);
		}
	}

	@Override
	protected int getGrowthAmount(World world) {
		return 1;
	}
}
