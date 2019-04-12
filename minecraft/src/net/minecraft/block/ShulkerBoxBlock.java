package net.minecraft.block;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.container.Container;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameters;

public class ShulkerBoxBlock extends BlockWithEntity {
	public static final EnumProperty<Direction> FACING = FacingBlock.FACING;
	public static final Identifier field_11495 = new Identifier("contents");
	@Nullable
	private final DyeColor color;

	public ShulkerBoxBlock(@Nullable DyeColor dyeColor, Block.Settings settings) {
		super(settings);
		this.color = dyeColor;
		this.setDefaultState(this.stateFactory.getDefaultState().with(FACING, Direction.UP));
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new ShulkerBoxBlockEntity(this.color);
	}

	@Override
	public boolean canSuffocate(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean hasBlockEntityBreakingRender(BlockState blockState) {
		return true;
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.field_11456;
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (world.isClient) {
			return true;
		} else if (playerEntity.isSpectator()) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof ShulkerBoxBlockEntity) {
				Direction direction = blockState.get(FACING);
				ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)blockEntity;
				boolean bl;
				if (shulkerBoxBlockEntity.getAnimationStage() == ShulkerBoxBlockEntity.AnimationStage.CLOSED) {
					BoundingBox boundingBox = VoxelShapes.fullCube()
						.getBoundingBox()
						.stretch((double)(0.5F * (float)direction.getOffsetX()), (double)(0.5F * (float)direction.getOffsetY()), (double)(0.5F * (float)direction.getOffsetZ()))
						.shrink((double)direction.getOffsetX(), (double)direction.getOffsetY(), (double)direction.getOffsetZ());
					bl = world.doesNotCollide(boundingBox.offset(blockPos.offset(direction)));
				} else {
					bl = true;
				}

				if (bl) {
					playerEntity.openContainer(shulkerBoxBlockEntity);
					playerEntity.incrementStat(Stats.field_15418);
				}

				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(FACING, itemPlacementContext.getFacing());
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(FACING);
	}

	@Override
	public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof ShulkerBoxBlockEntity) {
			ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)blockEntity;
			if (!world.isClient && playerEntity.isCreative()) {
				ItemStack itemStack = getItemStack(this.getColor());
				CompoundTag compoundTag = shulkerBoxBlockEntity.method_11317(new CompoundTag());
				if (!compoundTag.isEmpty()) {
					itemStack.setChildTag("BlockEntityTag", compoundTag);
				}

				ItemEntity itemEntity = new ItemEntity(world, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), itemStack);
				itemEntity.setToDefaultPickupDelay();
				world.spawnEntity(itemEntity);
			} else {
				shulkerBoxBlockEntity.checkLootInteraction(playerEntity);
			}
		}

		super.onBreak(world, blockPos, blockState, playerEntity);
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState blockState, LootContext.Builder builder) {
		BlockEntity blockEntity = builder.getNullable(LootContextParameters.field_1228);
		if (blockEntity instanceof ShulkerBoxBlockEntity) {
			ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)blockEntity;
			builder = builder.putDrop(field_11495, (lootContext, consumer) -> {
				for (int i = 0; i < shulkerBoxBlockEntity.getInvSize(); i++) {
					consumer.accept(shulkerBoxBlockEntity.getInvStack(i));
				}
			});
		}

		return super.getDroppedStacks(blockState, builder);
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		if (itemStack.hasDisplayName()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof ShulkerBoxBlockEntity) {
				((ShulkerBoxBlockEntity)blockEntity).setCustomName(itemStack.getDisplayName());
			}
		}
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof ShulkerBoxBlockEntity) {
				world.updateHorizontalAdjacent(blockPos, blockState.getBlock());
			}

			super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void buildTooltip(ItemStack itemStack, @Nullable BlockView blockView, List<TextComponent> list, TooltipContext tooltipContext) {
		super.buildTooltip(itemStack, blockView, list, tooltipContext);
		CompoundTag compoundTag = itemStack.getSubCompoundTag("BlockEntityTag");
		if (compoundTag != null) {
			if (compoundTag.containsKey("LootTable", 8)) {
				list.add(new StringTextComponent("???????"));
			}

			if (compoundTag.containsKey("Items", 9)) {
				DefaultedList<ItemStack> defaultedList = DefaultedList.create(27, ItemStack.EMPTY);
				Inventories.fromTag(compoundTag, defaultedList);
				int i = 0;
				int j = 0;

				for (ItemStack itemStack2 : defaultedList) {
					if (!itemStack2.isEmpty()) {
						j++;
						if (i <= 4) {
							i++;
							TextComponent textComponent = itemStack2.getDisplayName().copy();
							textComponent.append(" x").append(String.valueOf(itemStack2.getAmount()));
							list.add(textComponent);
						}
					}
				}

				if (j - i > 0) {
					list.add(new TranslatableTextComponent("container.shulkerBox.more", j - i).applyFormat(TextFormat.field_1056));
				}
			}
		}
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState blockState) {
		return PistonBehavior.field_15971;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		BlockEntity blockEntity = blockView.getBlockEntity(blockPos);
		return blockEntity instanceof ShulkerBoxBlockEntity
			? VoxelShapes.cuboid(((ShulkerBoxBlockEntity)blockEntity).getBoundingBox(blockState))
			: VoxelShapes.fullCube();
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public boolean hasComparatorOutput(BlockState blockState) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
		return Container.calculateComparatorOutput((Inventory)world.getBlockEntity(blockPos));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		ItemStack itemStack = super.getPickStack(blockView, blockPos, blockState);
		ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)blockView.getBlockEntity(blockPos);
		CompoundTag compoundTag = shulkerBoxBlockEntity.method_11317(new CompoundTag());
		if (!compoundTag.isEmpty()) {
			itemStack.setChildTag("BlockEntityTag", compoundTag);
		}

		return itemStack;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static DyeColor getColor(Item item) {
		return getColor(Block.getBlockFromItem(item));
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static DyeColor getColor(Block block) {
		return block instanceof ShulkerBoxBlock ? ((ShulkerBoxBlock)block).getColor() : null;
	}

	public static Block get(@Nullable DyeColor dyeColor) {
		if (dyeColor == null) {
			return Blocks.field_10603;
		} else {
			switch (dyeColor) {
				case field_7952:
					return Blocks.field_10199;
				case field_7946:
					return Blocks.field_10407;
				case field_7958:
					return Blocks.field_10063;
				case field_7951:
					return Blocks.field_10203;
				case field_7947:
					return Blocks.field_10600;
				case field_7961:
					return Blocks.field_10275;
				case field_7954:
					return Blocks.field_10051;
				case field_7944:
					return Blocks.field_10140;
				case field_7967:
					return Blocks.field_10320;
				case field_7955:
					return Blocks.field_10532;
				case field_7945:
				default:
					return Blocks.field_10268;
				case field_7966:
					return Blocks.field_10605;
				case field_7957:
					return Blocks.field_10373;
				case field_7942:
					return Blocks.field_10055;
				case field_7964:
					return Blocks.field_10068;
				case BLACK:
					return Blocks.field_10371;
			}
		}
	}

	@Nullable
	public DyeColor getColor() {
		return this.color;
	}

	public static ItemStack getItemStack(@Nullable DyeColor dyeColor) {
		return new ItemStack(get(dyeColor));
	}

	@Override
	public BlockState rotate(BlockState blockState, Rotation rotation) {
		return blockState.with(FACING, rotation.rotate(blockState.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState blockState, Mirror mirror) {
		return blockState.rotate(mirror.getRotation(blockState.get(FACING)));
	}
}
