package net.minecraft.item;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

/**
 * An item for a block that can be vertically attached as well as horizontally.
 * This is usually a block that is either standing or attached to a wall; however,
 * some blocks are instead hung from the ceiling.
 */
public class VerticallyAttachableBlockItem extends BlockItem {
	protected final Block wallBlock;
	private final Direction verticalAttachmentDirection;

	/**
	 * @param verticalAttachmentDirection the direction of the item's vertical attachment, {@link Direction#UP} for hanging blocks
	 * and {@link Direction#DOWN} for standing blocks
	 */
	public VerticallyAttachableBlockItem(Block standingBlock, Block wallBlock, Direction verticalAttachmentDirection, Item.Settings settings) {
		super(standingBlock, settings);
		this.wallBlock = wallBlock;
		this.verticalAttachmentDirection = verticalAttachmentDirection;
	}

	protected boolean canPlaceAt(WorldView world, BlockState state, BlockPos pos) {
		return state.canPlaceAt(world, pos);
	}

	@Nullable
	@Override
	protected BlockState getPlacementState(ItemPlacementContext context) {
		BlockState blockState = this.wallBlock.getPlacementState(context);
		BlockState blockState2 = null;
		WorldView worldView = context.getWorld();
		BlockPos blockPos = context.getBlockPos();

		for (Direction direction : context.getPlacementDirections()) {
			if (direction != this.verticalAttachmentDirection.getOpposite()) {
				BlockState blockState3 = direction == this.verticalAttachmentDirection ? this.getBlock().getPlacementState(context) : blockState;
				if (blockState3 != null && this.canPlaceAt(worldView, blockState3, blockPos)) {
					blockState2 = blockState3;
					break;
				}
			}
		}

		return blockState2 != null && worldView.canPlace(blockState2, blockPos, ShapeContext.absent()) ? blockState2 : null;
	}

	@Override
	public void appendBlocks(Map<Block, Item> map, Item item) {
		super.appendBlocks(map, item);
		map.put(this.wallBlock, item);
	}
}
