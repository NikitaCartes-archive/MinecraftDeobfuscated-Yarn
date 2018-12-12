package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class CarrotsBlock extends CropBlock {
	private static final VoxelShape[] field_10737 = new VoxelShape[]{
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 5.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 7.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 9.0, 16.0)
	};

	public CarrotsBlock(Block.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	protected ItemProvider getCropItem() {
		return Items.field_8179;
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return field_10737[blockState.get(this.getAgeProperty())];
	}
}
