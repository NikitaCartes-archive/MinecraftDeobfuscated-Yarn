package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BeetrootsBlock extends CropBlock {
	public static final IntegerProperty field_9962 = Properties.AGE_3;
	private static final VoxelShape[] field_9961 = new VoxelShape[]{
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0)
	};

	public BeetrootsBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public IntegerProperty getAgeProperty() {
		return field_9962;
	}

	@Override
	public int getCropAgeMaximum() {
		return 3;
	}

	@Environment(EnvType.CLIENT)
	@Override
	protected ItemProvider getCropItem() {
		return Items.field_8309;
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (random.nextInt(3) != 0) {
			super.scheduledTick(blockState, world, blockPos, random);
		}
	}

	@Override
	protected int getGrowthAmount(World world) {
		return super.getGrowthAmount(world) / 3;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_9962);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_9961[blockState.get(this.getAgeProperty())];
	}
}
