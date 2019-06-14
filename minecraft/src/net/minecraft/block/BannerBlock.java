package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

public class BannerBlock extends AbstractBannerBlock {
	public static final IntProperty field_9924 = Properties.field_12532;
	private static final Map<DyeColor, Block> COLORED_BANNERS = Maps.<DyeColor, Block>newHashMap();
	private static final VoxelShape field_9923 = Block.method_9541(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

	public BannerBlock(DyeColor dyeColor, Block.Settings settings) {
		super(dyeColor, settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_9924, Integer.valueOf(0)));
		COLORED_BANNERS.put(dyeColor, this);
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return viewableWorld.method_8320(blockPos.down()).method_11620().isSolid();
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return field_9923;
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_9564()
			.method_11657(field_9924, Integer.valueOf(MathHelper.floor((double)((180.0F + itemPlacementContext.getPlayerYaw()) * 16.0F / 360.0F) + 0.5) & 15));
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return direction == Direction.field_11033 && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.method_9564()
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		return blockState.method_11657(field_9924, Integer.valueOf(blockRotation.rotate((Integer)blockState.method_11654(field_9924), 16)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		return blockState.method_11657(field_9924, Integer.valueOf(blockMirror.mirror((Integer)blockState.method_11654(field_9924), 16)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_9924);
	}

	@Environment(EnvType.CLIENT)
	public static Block method_9398(DyeColor dyeColor) {
		return (Block)COLORED_BANNERS.getOrDefault(dyeColor, Blocks.field_10154);
	}
}
