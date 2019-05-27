package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BeetrootsBlock extends CropBlock {
	public static final IntProperty field_9962 = Properties.field_12497;
	private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0)
	};

	public BeetrootsBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public IntProperty method_9824() {
		return field_9962;
	}

	@Override
	public int getMaxAge() {
		return 3;
	}

	@Environment(EnvType.CLIENT)
	@Override
	protected ItemConvertible getSeedsItem() {
		return Items.field_8309;
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (random.nextInt(3) != 0) {
			super.onScheduledTick(blockState, world, blockPos, random);
		}
	}

	@Override
	protected int getGrowthAmount(World world) {
		return super.getGrowthAmount(world) / 3;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(field_9962);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return AGE_TO_SHAPE[blockState.get(this.method_9824())];
	}
}
