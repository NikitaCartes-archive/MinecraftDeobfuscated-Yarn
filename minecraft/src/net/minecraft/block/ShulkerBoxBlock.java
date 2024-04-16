package net.minecraft.block;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.item.TooltipType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class ShulkerBoxBlock extends BlockWithEntity {
	public static final MapCodec<ShulkerBoxBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(DyeColor.CODEC.optionalFieldOf("color").forGetter(block -> Optional.ofNullable(block.color)), createSettingsCodec())
				.apply(instance, (color, settings) -> new ShulkerBoxBlock((DyeColor)color.orElse(null), settings))
	);
	private static final Text UNKNOWN_CONTENTS_TEXT = Text.translatable("container.shulkerBox.unknownContents");
	private static final float field_41075 = 1.0F;
	private static final VoxelShape UP_SHAPE = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape DOWN_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
	private static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
	private static final VoxelShape EAST_SHAPE = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
	private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
	private static final Map<Direction, VoxelShape> SIDES_SHAPES = Util.make(Maps.newEnumMap(Direction.class), map -> {
		map.put(Direction.NORTH, NORTH_SHAPE);
		map.put(Direction.EAST, EAST_SHAPE);
		map.put(Direction.SOUTH, SOUTH_SHAPE);
		map.put(Direction.WEST, WEST_SHAPE);
		map.put(Direction.UP, UP_SHAPE);
		map.put(Direction.DOWN, DOWN_SHAPE);
	});
	public static final EnumProperty<Direction> FACING = FacingBlock.FACING;
	public static final Identifier CONTENTS_DYNAMIC_DROP_ID = new Identifier("contents");
	@Nullable
	private final DyeColor color;

	@Override
	public MapCodec<ShulkerBoxBlock> getCodec() {
		return CODEC;
	}

	public ShulkerBoxBlock(@Nullable DyeColor color, AbstractBlock.Settings settings) {
		super(settings);
		this.color = color;
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.UP));
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new ShulkerBoxBlockEntity(this.color, pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return validateTicker(type, BlockEntityType.SHULKER_BOX, ShulkerBoxBlockEntity::tick);
	}

	@Override
	protected BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else if (player.isSpectator()) {
			return ActionResult.CONSUME;
		} else if (world.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity) {
			if (canOpen(state, world, pos, shulkerBoxBlockEntity)) {
				player.openHandledScreen(shulkerBoxBlockEntity);
				player.incrementStat(Stats.OPEN_SHULKER_BOX);
				PiglinBrain.onGuardedBlockInteracted(player, true);
			}

			return ActionResult.CONSUME;
		} else {
			return ActionResult.PASS;
		}
	}

	private static boolean canOpen(BlockState state, World world, BlockPos pos, ShulkerBoxBlockEntity entity) {
		if (entity.getAnimationStage() != ShulkerBoxBlockEntity.AnimationStage.CLOSED) {
			return true;
		} else {
			Box box = ShulkerEntity.calculateBoundingBox(1.0F, state.get(FACING), 0.0F, 0.5F).offset(pos).contract(1.0E-6);
			return world.isSpaceEmpty(box);
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getSide());
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity) {
			if (!world.isClient && player.isCreative() && !shulkerBoxBlockEntity.isEmpty()) {
				ItemStack itemStack = getItemStack(this.getColor());
				itemStack.applyComponentsFrom(blockEntity.createComponentMap());
				ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, itemStack);
				itemEntity.setToDefaultPickupDelay();
				world.spawnEntity(itemEntity);
			} else {
				shulkerBoxBlockEntity.generateLoot(player);
			}
		}

		return super.onBreak(world, pos, state, player);
	}

	@Override
	protected List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
		BlockEntity blockEntity = builder.getOptional(LootContextParameters.BLOCK_ENTITY);
		if (blockEntity instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity) {
			builder = builder.addDynamicDrop(CONTENTS_DYNAMIC_DROP_ID, lootConsumer -> {
				for (int i = 0; i < shulkerBoxBlockEntity.size(); i++) {
					lootConsumer.accept(shulkerBoxBlockEntity.getStack(i));
				}
			});
		}

		return super.getDroppedStacks(state, builder);
	}

	@Override
	protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof ShulkerBoxBlockEntity) {
				world.updateComparators(pos, state.getBlock());
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
		super.appendTooltip(stack, context, tooltip, options);
		if (stack.contains(DataComponentTypes.CONTAINER_LOOT)) {
			tooltip.add(UNKNOWN_CONTENTS_TEXT);
		}

		int i = 0;
		int j = 0;

		for (ItemStack itemStack : stack.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).iterateNonEmpty()) {
			j++;
			if (i <= 4) {
				i++;
				tooltip.add(Text.translatable("container.shulkerBox.itemCount", itemStack.getName(), itemStack.getCount()));
			}
		}

		if (j - i > 0) {
			tooltip.add(Text.translatable("container.shulkerBox.more", j - i).formatted(Formatting.ITALIC));
		}
	}

	@Override
	protected VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
		if (world.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity && !shulkerBoxBlockEntity.suffocates()) {
			return (VoxelShape)SIDES_SHAPES.get(((Direction)state.get(FACING)).getOpposite());
		}

		return VoxelShapes.fullCube();
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity instanceof ShulkerBoxBlockEntity ? VoxelShapes.cuboid(((ShulkerBoxBlockEntity)blockEntity).getBoundingBox(state)) : VoxelShapes.fullCube();
	}

	@Override
	protected boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
		return false;
	}

	@Override
	protected boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
	}

	@Override
	public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
		ItemStack itemStack = super.getPickStack(world, pos, state);
		world.getBlockEntity(pos, BlockEntityType.SHULKER_BOX).ifPresent(blockEntity -> blockEntity.setStackNbt(itemStack, world.getRegistryManager()));
		return itemStack;
	}

	@Nullable
	public static DyeColor getColor(Item item) {
		return getColor(Block.getBlockFromItem(item));
	}

	@Nullable
	public static DyeColor getColor(Block block) {
		return block instanceof ShulkerBoxBlock ? ((ShulkerBoxBlock)block).getColor() : null;
	}

	public static Block get(@Nullable DyeColor dyeColor) {
		if (dyeColor == null) {
			return Blocks.SHULKER_BOX;
		} else {
			return switch (dyeColor) {
				case WHITE -> Blocks.WHITE_SHULKER_BOX;
				case ORANGE -> Blocks.ORANGE_SHULKER_BOX;
				case MAGENTA -> Blocks.MAGENTA_SHULKER_BOX;
				case LIGHT_BLUE -> Blocks.LIGHT_BLUE_SHULKER_BOX;
				case YELLOW -> Blocks.YELLOW_SHULKER_BOX;
				case LIME -> Blocks.LIME_SHULKER_BOX;
				case PINK -> Blocks.PINK_SHULKER_BOX;
				case GRAY -> Blocks.GRAY_SHULKER_BOX;
				case LIGHT_GRAY -> Blocks.LIGHT_GRAY_SHULKER_BOX;
				case CYAN -> Blocks.CYAN_SHULKER_BOX;
				case BLUE -> Blocks.BLUE_SHULKER_BOX;
				case BROWN -> Blocks.BROWN_SHULKER_BOX;
				case GREEN -> Blocks.GREEN_SHULKER_BOX;
				case RED -> Blocks.RED_SHULKER_BOX;
				case BLACK -> Blocks.BLACK_SHULKER_BOX;
				case PURPLE -> Blocks.PURPLE_SHULKER_BOX;
			};
		}
	}

	@Nullable
	public DyeColor getColor() {
		return this.color;
	}

	public static ItemStack getItemStack(@Nullable DyeColor color) {
		return new ItemStack(get(color));
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}
}
