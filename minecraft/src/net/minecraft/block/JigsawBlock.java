package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.class_3499;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class JigsawBlock extends FacingBlock implements BlockEntityProvider {
	protected JigsawBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_10927, Direction.UP));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_10927);
	}

	@Override
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		return blockState.with(field_10927, rotation.method_10503(blockState.get(field_10927)));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(field_10927, itemPlacementContext.method_8038());
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new JigsawBlockEntity();
	}

	@Override
	public boolean method_9534(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof JigsawBlockEntity && playerEntity.method_7338()) {
			playerEntity.method_16354((JigsawBlockEntity)blockEntity);
			return true;
		} else {
			return false;
		}
	}

	public static boolean method_16546(class_3499.class_3501 arg, class_3499.class_3501 arg2) {
		return arg.field_15596.get(field_10927) == ((Direction)arg2.field_15596.get(field_10927)).getOpposite()
			&& arg.field_15595.getString("attachement_type").equals(arg2.field_15595.getString("attachement_type"));
	}
}
