package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class PotatoesBlock extends CropBlock {
	private static final VoxelShape[] field_11357 = new VoxelShape[]{
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 5.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 7.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 9.0, 16.0)
	};

	public PotatoesBlock(Block.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	protected ItemProvider getCropItem() {
		return Items.field_8567;
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_11357[blockState.method_11654(this.method_9824())];
	}
}
