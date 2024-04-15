package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class ChiseledBookshelfBlock extends BlockWithEntity {
	public static final MapCodec<ChiseledBookshelfBlock> CODEC = createCodec(ChiseledBookshelfBlock::new);
	private static final int MAX_BOOK_COUNT = 6;
	public static final int BOOK_HEIGHT = 3;
	public static final List<BooleanProperty> SLOT_OCCUPIED_PROPERTIES = List.of(
		Properties.SLOT_0_OCCUPIED,
		Properties.SLOT_1_OCCUPIED,
		Properties.SLOT_2_OCCUPIED,
		Properties.SLOT_3_OCCUPIED,
		Properties.SLOT_4_OCCUPIED,
		Properties.SLOT_5_OCCUPIED
	);

	@Override
	public MapCodec<ChiseledBookshelfBlock> getCodec() {
		return CODEC;
	}

	public ChiseledBookshelfBlock(AbstractBlock.Settings settings) {
		super(settings);
		BlockState blockState = this.stateManager.getDefaultState().with(HorizontalFacingBlock.FACING, Direction.NORTH);

		for (BooleanProperty booleanProperty : SLOT_OCCUPIED_PROPERTIES) {
			blockState = blockState.with(booleanProperty, Boolean.valueOf(false));
		}

		this.setDefaultState(blockState);
	}

	@Override
	protected BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.getBlockEntity(pos) instanceof ChiseledBookshelfBlockEntity chiseledBookshelfBlockEntity) {
			if (!stack.isIn(ItemTags.BOOKSHELF_BOOKS)) {
				return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
			} else {
				OptionalInt optionalInt = this.getSlotForHitPos(hit, state);
				if (optionalInt.isEmpty()) {
					return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
				} else if ((Boolean)state.get((Property)SLOT_OCCUPIED_PROPERTIES.get(optionalInt.getAsInt()))) {
					return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
				} else {
					tryAddBook(world, pos, player, chiseledBookshelfBlockEntity, stack, optionalInt.getAsInt());
					return ItemActionResult.success(world.isClient);
				}
			}
		} else {
			return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
		}
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (world.getBlockEntity(pos) instanceof ChiseledBookshelfBlockEntity chiseledBookshelfBlockEntity) {
			OptionalInt optionalInt = this.getSlotForHitPos(hit, state);
			if (optionalInt.isEmpty()) {
				return ActionResult.PASS;
			} else if (!(Boolean)state.get((Property)SLOT_OCCUPIED_PROPERTIES.get(optionalInt.getAsInt()))) {
				return ActionResult.CONSUME;
			} else {
				tryRemoveBook(world, pos, player, chiseledBookshelfBlockEntity, optionalInt.getAsInt());
				return ActionResult.success(world.isClient);
			}
		} else {
			return ActionResult.PASS;
		}
	}

	private OptionalInt getSlotForHitPos(BlockHitResult hit, BlockState state) {
		return (OptionalInt)getHitPos(hit, state.get(HorizontalFacingBlock.FACING)).map(hitPos -> {
			int i = hitPos.y >= 0.5F ? 0 : 1;
			int j = getColumn(hitPos.x);
			return OptionalInt.of(j + i * 3);
		}).orElseGet(OptionalInt::empty);
	}

	private static Optional<Vec2f> getHitPos(BlockHitResult hit, Direction facing) {
		Direction direction = hit.getSide();
		if (facing != direction) {
			return Optional.empty();
		} else {
			BlockPos blockPos = hit.getBlockPos().offset(direction);
			Vec3d vec3d = hit.getPos().subtract((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
			double d = vec3d.getX();
			double e = vec3d.getY();
			double f = vec3d.getZ();

			return switch (direction) {
				case NORTH -> Optional.of(new Vec2f((float)(1.0 - d), (float)e));
				case SOUTH -> Optional.of(new Vec2f((float)d, (float)e));
				case WEST -> Optional.of(new Vec2f((float)f, (float)e));
				case EAST -> Optional.of(new Vec2f((float)(1.0 - f), (float)e));
				case DOWN, UP -> Optional.empty();
				default -> throw new MatchException(null, null);
			};
		}
	}

	private static int getColumn(float x) {
		float f = 0.0625F;
		float g = 0.375F;
		if (x < 0.375F) {
			return 0;
		} else {
			float h = 0.6875F;
			return x < 0.6875F ? 1 : 2;
		}
	}

	private static void tryAddBook(World world, BlockPos pos, PlayerEntity player, ChiseledBookshelfBlockEntity blockEntity, ItemStack stack, int slot) {
		if (!world.isClient) {
			player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
			SoundEvent soundEvent = stack.isOf(Items.ENCHANTED_BOOK)
				? SoundEvents.BLOCK_CHISELED_BOOKSHELF_INSERT_ENCHANTED
				: SoundEvents.BLOCK_CHISELED_BOOKSHELF_INSERT;
			blockEntity.setStack(slot, stack.split(1));
			world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
			if (player.isCreative()) {
				stack.increment(1);
			}
		}
	}

	private static void tryRemoveBook(World world, BlockPos pos, PlayerEntity player, ChiseledBookshelfBlockEntity blockEntity, int slot) {
		if (!world.isClient) {
			ItemStack itemStack = blockEntity.removeStack(slot, 1);
			SoundEvent soundEvent = itemStack.isOf(Items.ENCHANTED_BOOK)
				? SoundEvents.BLOCK_CHISELED_BOOKSHELF_PICKUP_ENCHANTED
				: SoundEvents.BLOCK_CHISELED_BOOKSHELF_PICKUP;
			world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
			if (!player.getInventory().insertStack(itemStack)) {
				player.dropItem(itemStack, false);
			}

			world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
		}
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new ChiseledBookshelfBlockEntity(pos, state);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(HorizontalFacingBlock.FACING);
		SLOT_OCCUPIED_PROPERTIES.forEach(property -> builder.add(property));
	}

	@Override
	protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			if (world.getBlockEntity(pos) instanceof ChiseledBookshelfBlockEntity chiseledBookshelfBlockEntity && !chiseledBookshelfBlockEntity.isEmpty()) {
				for (int i = 0; i < 6; i++) {
					ItemStack itemStack = chiseledBookshelfBlockEntity.getStack(i);
					if (!itemStack.isEmpty()) {
						ItemScatterer.spawn(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), itemStack);
					}
				}

				chiseledBookshelfBlockEntity.clear();
				world.updateComparators(pos, this);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(HorizontalFacingBlock.FACING, ctx.getHorizontalPlayerFacing().getOpposite());
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(HorizontalFacingBlock.FACING, rotation.rotate(state.get(HorizontalFacingBlock.FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(HorizontalFacingBlock.FACING)));
	}

	@Override
	protected boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		if (world.isClient()) {
			return 0;
		} else {
			return world.getBlockEntity(pos) instanceof ChiseledBookshelfBlockEntity chiseledBookshelfBlockEntity
				? chiseledBookshelfBlockEntity.getLastInteractedSlot() + 1
				: 0;
		}
	}
}
