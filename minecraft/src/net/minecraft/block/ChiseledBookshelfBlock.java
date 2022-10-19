package net.minecraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class ChiseledBookshelfBlock extends BlockWithEntity {
	public static final IntProperty BOOKS_STORED = Properties.BOOKS_STORED;
	public static final IntProperty LAST_INTERACTION_BOOK_SLOT = Properties.LAST_INTERACTION_BOOK_SLOT;

	public ChiseledBookshelfBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(BOOKS_STORED, Integer.valueOf(0))
				.with(HorizontalFacingBlock.FACING, Direction.NORTH)
				.with(LAST_INTERACTION_BOOK_SLOT, Integer.valueOf(0))
		);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.getBlockEntity(pos) instanceof ChiseledBookshelfBlockEntity chiseledBookshelfBlockEntity) {
			if (world.isClient()) {
				return ActionResult.SUCCESS;
			} else {
				ItemStack itemStack = player.getStackInHand(hand);
				return itemStack.isIn(ItemTags.BOOKSHELF_BOOKS)
					? tryAddBook(state, world, pos, player, chiseledBookshelfBlockEntity, itemStack)
					: tryRemoveBook(state, world, pos, player, chiseledBookshelfBlockEntity);
			}
		} else {
			return ActionResult.PASS;
		}
	}

	private static ActionResult tryRemoveBook(BlockState state, World world, BlockPos pos, PlayerEntity player, ChiseledBookshelfBlockEntity blockEntity) {
		if (!blockEntity.isEmpty()) {
			ItemStack itemStack = blockEntity.getLastBook();
			world.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
			int i = blockEntity.getBookCount();
			world.setBlockState(pos, state.with(BOOKS_STORED, Integer.valueOf(i)).with(LAST_INTERACTION_BOOK_SLOT, Integer.valueOf(i + 1)), Block.NOTIFY_ALL);
			world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
			if (!player.getInventory().insertStack(itemStack)) {
				player.dropItem(itemStack, false);
			}
		}

		return ActionResult.CONSUME;
	}

	private static ActionResult tryAddBook(
		BlockState state, World world, BlockPos pos, PlayerEntity player, ChiseledBookshelfBlockEntity blockEntity, ItemStack stack
	) {
		if (!blockEntity.isFull()) {
			blockEntity.addBook(stack.split(1));
			world.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
			if (player.isCreative()) {
				stack.increment(1);
			}

			int i = blockEntity.getBookCount();
			world.setBlockState(pos, state.with(BOOKS_STORED, Integer.valueOf(i)).with(LAST_INTERACTION_BOOK_SLOT, Integer.valueOf(i)), Block.NOTIFY_ALL);
			world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
		}

		return ActionResult.CONSUME;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new ChiseledBookshelfBlockEntity(pos, state);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(BOOKS_STORED).add(LAST_INTERACTION_BOOK_SLOT).add(HorizontalFacingBlock.FACING);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			if (world.getBlockEntity(pos) instanceof ChiseledBookshelfBlockEntity chiseledBookshelfBlockEntity) {
				for (ItemStack itemStack = chiseledBookshelfBlockEntity.getLastBook(); !itemStack.isEmpty(); itemStack = chiseledBookshelfBlockEntity.getLastBook()) {
					ItemScatterer.spawn(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), itemStack);
				}

				world.updateComparators(pos, this);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(HorizontalFacingBlock.FACING, ctx.getPlayerFacing().getOpposite());
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return (Integer)state.get(LAST_INTERACTION_BOOK_SLOT);
	}
}
