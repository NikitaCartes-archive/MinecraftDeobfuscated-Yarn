package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.structure.Structure;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class JigsawBlock extends FacingBlock implements BlockEntityProvider {
	protected JigsawBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10927, Direction.field_11036));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10927);
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		return blockState.method_11657(field_10927, blockRotation.rotate(blockState.method_11654(field_10927)));
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_9564().method_11657(field_10927, itemPlacementContext.getSide());
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

	public static boolean attachmentMatches(Structure.StructureBlockInfo structureBlockInfo, Structure.StructureBlockInfo structureBlockInfo2) {
		return structureBlockInfo.state.method_11654(field_10927) == ((Direction)structureBlockInfo2.state.method_11654(field_10927)).getOpposite()
			&& structureBlockInfo.tag.getString("attachement_type").equals(structureBlockInfo2.tag.getString("attachement_type"));
	}
}
