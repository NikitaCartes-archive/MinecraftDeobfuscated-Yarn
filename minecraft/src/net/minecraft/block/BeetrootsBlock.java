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

public class BeetrootsBlock extends CropBlock {
	public static final MapCodec<BeetrootsBlock> CODEC = createCodec(BeetrootsBlock::new);
	public static final int BEETROOTS_MAX_AGE = 3;
	public static final IntProperty AGE = Properties.AGE_3;
	private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0)
	};

	@Override
	public MapCodec<BeetrootsBlock> getCodec() {
		return CODEC;
	}

	public BeetrootsBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected IntProperty getAgeProperty() {
		return AGE;
	}

	@Override
	public int getMaxAge() {
		return 3;
	}

	@Override
	protected ItemConvertible getSeedsItem() {
		return Items.BEETROOT_SEEDS;
	}

	@Override
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (random.nextInt(3) != 0) {
			super.randomTick(state, world, pos, random);
		}
	}

	@Override
	protected int getGrowthAmount(World world) {
		return super.getGrowthAmount(world) / 3;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return AGE_TO_SHAPE[this.getAge(state)];
	}
}
