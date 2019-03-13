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
	public static final IntegerProperty field_9962 = Properties.field_12497;
	private static final VoxelShape[] field_9961 = new VoxelShape[]{
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 8.0, 16.0)
	};

	public BeetrootsBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public IntegerProperty method_9824() {
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
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (random.nextInt(3) != 0) {
			super.method_9588(blockState, world, blockPos, random);
		}
	}

	@Override
	protected int getGrowthAmount(World world) {
		return super.getGrowthAmount(world) / 3;
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_9962);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_9961[blockState.method_11654(this.method_9824())];
	}
}
