package net.minecraft.block;

import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class SkullBlock extends AbstractSkullBlock {
	public static final IntProperty field_11505 = Properties.field_12532;
	protected static final VoxelShape field_11506 = Block.method_9541(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);

	protected SkullBlock(SkullBlock.SkullType skullType, Block.Settings settings) {
		super(skullType, settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11505, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return field_11506;
	}

	@Override
	public VoxelShape method_9571(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return VoxelShapes.method_1073();
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_9564()
			.method_11657(field_11505, Integer.valueOf(MathHelper.floor((double)(itemPlacementContext.getPlayerYaw() * 16.0F / 360.0F) + 0.5) & 15));
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		return blockState.method_11657(field_11505, Integer.valueOf(blockRotation.rotate((Integer)blockState.method_11654(field_11505), 16)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		return blockState.method_11657(field_11505, Integer.valueOf(blockMirror.mirror((Integer)blockState.method_11654(field_11505), 16)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11505);
	}

	public interface SkullType {
	}

	public static enum Type implements SkullBlock.SkullType {
		field_11512,
		field_11513,
		field_11510,
		field_11508,
		field_11507,
		field_11511;
	}
}
