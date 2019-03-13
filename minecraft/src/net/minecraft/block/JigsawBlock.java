package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.structure.Structure;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class JigsawBlock extends FacingBlock implements BlockEntityProvider {
	protected JigsawBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10927, Direction.UP));
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10927);
	}

	@Override
	public BlockState method_9598(BlockState blockState, Rotation rotation) {
		return blockState.method_11657(field_10927, rotation.method_10503(blockState.method_11654(field_10927)));
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_9564().method_11657(field_10927, itemPlacementContext.method_8038());
	}

	@Nullable
	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return new JigsawBlockEntity();
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		BlockEntity blockEntity = world.method_8321(blockPos);
		if (blockEntity instanceof JigsawBlockEntity && playerEntity.isCreativeLevelTwoOp()) {
			playerEntity.method_16354((JigsawBlockEntity)blockEntity);
			return true;
		} else {
			return false;
		}
	}

	public static boolean method_16546(Structure.StructureBlockInfo structureBlockInfo, Structure.StructureBlockInfo structureBlockInfo2) {
		return structureBlockInfo.state.method_11654(field_10927) == ((Direction)structureBlockInfo2.state.method_11654(field_10927)).getOpposite()
			&& structureBlockInfo.field_15595.getString("attachement_type").equals(structureBlockInfo2.field_15595.getString("attachement_type"));
	}
}
