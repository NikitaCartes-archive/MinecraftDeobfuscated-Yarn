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
	public static final EnumProperty<Direction> field_11496 = FacingBlock.field_10927;
	public static final Identifier field_11495 = new Identifier("contents");
	@Nullable
	private final DyeColor color;

	public ShulkerBoxBlock(@Nullable DyeColor dyeColor, Block.Settings settings) {
		super(settings);
		this.color = dyeColor;
		this.method_9590(this.field_10647.method_11664().method_11657(field_11496, Direction.UP));
	}

	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return new ShulkerBoxBlockEntity(this.color);
	}

	@Override
	public boolean method_16362(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_9589(BlockState blockState) {
		return true;
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11456;
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (world.isClient) {
			return true;
		} else if (playerEntity.isSpectator()) {
			return true;
		} else {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof ShulkerBoxBlockEntity) {
				Direction direction = blockState.method_11654(field_11496);
				ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)blockEntity;
				boolean bl;
				if (shulkerBoxBlockEntity.getAnimationStage() == ShulkerBoxBlockEntity.AnimationStage.CLOSED) {
					BoundingBox boundingBox = VoxelShapes.method_1077()
						.getBoundingBox()
						.stretch((double)(0.5F * (float)direction.getOffsetX()), (double)(0.5F * (float)direction.getOffsetY()), (double)(0.5F * (float)direction.getOffsetZ()))
						.shrink((double)direction.getOffsetX(), (double)direction.getOffsetY(), (double)direction.getOffsetZ());
					bl = world.method_18026(boundingBox.method_996(blockPos.method_10093(direction)));
				} else {
					bl = true;
				}

				if (bl) {
					playerEntity.openContainer(shulkerBoxBlockEntity);
					playerEntity.method_7281(Stats.field_15418);
				}

				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_9564().method_11657(field_11496, itemPlacementContext.method_8038());
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11496);
	}

	@Override
	public void method_9576(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		BlockEntity blockEntity = world.method_8321(blockPos);
		if (blockEntity instanceof ShulkerBoxBlockEntity) {
			((ShulkerBoxBlockEntity)blockEntity).checkLootInteraction(playerEntity);
		}

		super.method_9576(world, blockPos, blockState, playerEntity);
	}

	@Override
	public List<ItemStack> method_9560(BlockState blockState, LootContext.Builder builder) {
		BlockEntity blockEntity = builder.method_305(LootContextParameters.field_1228);
		if (blockEntity instanceof ShulkerBoxBlockEntity) {
			ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)blockEntity;
			builder = builder.method_307(field_11495, (lootContext, consumer) -> {
				for (int i = 0; i < shulkerBoxBlockEntity.getInvSize(); i++) {
					consumer.accept(shulkerBoxBlockEntity.method_5438(i));
				}
			});
		}

		return super.method_9560(blockState, builder);
	}

	@Override
	public void method_9567(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		if (itemStack.hasDisplayName()) {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof ShulkerBoxBlockEntity) {
				((ShulkerBoxBlockEntity)blockEntity).method_17488(itemStack.method_7964());
			}
		}
	}

	@Override
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof ShulkerBoxBlockEntity) {
				world.method_8455(blockPos, blockState.getBlock());
			}

			super.method_9536(blockState, world, blockPos, blockState2, bl);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void buildTooltip(ItemStack itemStack, @Nullable BlockView blockView, List<TextComponent> list, TooltipContext tooltipContext) {
		super.buildTooltip(itemStack, blockView, list, tooltipContext);
		CompoundTag compoundTag = itemStack.method_7941("BlockEntityTag");
		if (compoundTag != null) {
			if (compoundTag.containsKey("LootTable", 8)) {
				list.add(new StringTextComponent("???????"));
			}

			if (compoundTag.containsKey("Items", 9)) {
				DefaultedList<ItemStack> defaultedList = DefaultedList.create(27, ItemStack.EMPTY);
				Inventories.method_5429(compoundTag, defaultedList);
				int i = 0;
				int j = 0;

				for (ItemStack itemStack2 : defaultedList) {
					if (!itemStack2.isEmpty()) {
						j++;
						if (i <= 4) {
							i++;
							TextComponent textComponent = itemStack2.method_7964().copy();
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
	public PistonBehavior method_9527(BlockState blockState) {
		return PistonBehavior.field_15971;
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		BlockEntity blockEntity = blockView.method_8321(blockPos);
		return blockEntity instanceof ShulkerBoxBlockEntity
			? VoxelShapes.method_1078(((ShulkerBoxBlockEntity)blockEntity).method_11314(blockState))
			: VoxelShapes.method_1077();
	}

	@Override
	public boolean method_9601(BlockState blockState) {
		return false;
	}

	@Override
	public boolean method_9498(BlockState blockState) {
		return true;
	}

	@Override
	public int method_9572(BlockState blockState, World world, BlockPos blockPos) {
		return Container.calculateComparatorOutput((Inventory)world.method_8321(blockPos));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack method_9574(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		ItemStack itemStack = super.method_9574(blockView, blockPos, blockState);
		ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)blockView.method_8321(blockPos);
		CompoundTag compoundTag = shulkerBoxBlockEntity.method_11317(new CompoundTag());
		if (!compoundTag.isEmpty()) {
			itemStack.method_7959("BlockEntityTag", compoundTag);
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

	public static Block get(DyeColor dyeColor) {
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
	@Environment(EnvType.CLIENT)
	public DyeColor getColor() {
		return this.color;
	}

	public static ItemStack getItemStack(DyeColor dyeColor) {
		return new ItemStack(get(dyeColor));
	}

	@Override
	public BlockState method_9598(BlockState blockState, Rotation rotation) {
		return blockState.method_11657(field_11496, rotation.method_10503(blockState.method_11654(field_11496)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, Mirror mirror) {
		return blockState.rotate(mirror.method_10345(blockState.method_11654(field_11496)));
	}
}
