package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FlowerPotBlock extends Block {
	private static final Map<Block, Block> CONTENT_TO_POTTED = Maps.<Block, Block>newHashMap();
	protected static final VoxelShape field_11102 = Block.method_9541(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);
	private final Block content;

	public FlowerPotBlock(Block block, Block.Settings settings) {
		super(settings);
		this.content = block;
		CONTENT_TO_POTTED.put(block, this);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return field_11102;
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		Item item = itemStack.getItem();
		Block block = item instanceof BlockItem ? (Block)CONTENT_TO_POTTED.getOrDefault(((BlockItem)item).method_7711(), Blocks.field_10124) : Blocks.field_10124;
		boolean bl = block == Blocks.field_10124;
		boolean bl2 = this.content == Blocks.field_10124;
		if (bl != bl2) {
			if (bl2) {
				world.method_8652(blockPos, block.method_9564(), 3);
				playerEntity.incrementStat(Stats.field_15412);
				if (!playerEntity.abilities.creativeMode) {
					itemStack.decrement(1);
				}
			} else {
				ItemStack itemStack2 = new ItemStack(this.content);
				if (itemStack.isEmpty()) {
					playerEntity.setStackInHand(hand, itemStack2);
				} else if (!playerEntity.giveItemStack(itemStack2)) {
					playerEntity.dropItem(itemStack2, false);
				}

				world.method_8652(blockPos, Blocks.field_10495.method_9564(), 3);
			}
		}

		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack method_9574(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return this.content == Blocks.field_10124 ? super.method_9574(blockView, blockPos, blockState) : new ItemStack(this.content);
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return direction == Direction.field_11033 && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.method_9564()
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9174;
	}

	public Block getContent() {
		return this.content;
	}
}
