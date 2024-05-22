package net.minecraft.item;

import java.util.List;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.GlowItemFrameEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class DecorationItem extends Item {
	private static final Text RANDOM_TEXT = Text.translatable("painting.random").formatted(Formatting.GRAY);
	private final EntityType<? extends AbstractDecorationEntity> entityType;

	public DecorationItem(EntityType<? extends AbstractDecorationEntity> type, Item.Settings settings) {
		super(settings);
		this.entityType = type;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		BlockPos blockPos = context.getBlockPos();
		Direction direction = context.getSide();
		BlockPos blockPos2 = blockPos.offset(direction);
		PlayerEntity playerEntity = context.getPlayer();
		ItemStack itemStack = context.getStack();
		if (playerEntity != null && !this.canPlaceOn(playerEntity, direction, itemStack, blockPos2)) {
			return ActionResult.FAIL;
		} else {
			World world = context.getWorld();
			AbstractDecorationEntity abstractDecorationEntity;
			if (this.entityType == EntityType.PAINTING) {
				Optional<PaintingEntity> optional = PaintingEntity.placePainting(world, blockPos2, direction);
				if (optional.isEmpty()) {
					return ActionResult.CONSUME;
				}

				abstractDecorationEntity = (AbstractDecorationEntity)optional.get();
			} else if (this.entityType == EntityType.ITEM_FRAME) {
				abstractDecorationEntity = new ItemFrameEntity(world, blockPos2, direction);
			} else {
				if (this.entityType != EntityType.GLOW_ITEM_FRAME) {
					return ActionResult.success(world.isClient);
				}

				abstractDecorationEntity = new GlowItemFrameEntity(world, blockPos2, direction);
			}

			NbtComponent nbtComponent = itemStack.getOrDefault(DataComponentTypes.ENTITY_DATA, NbtComponent.DEFAULT);
			if (!nbtComponent.isEmpty()) {
				EntityType.loadFromEntityNbt(world, playerEntity, abstractDecorationEntity, nbtComponent);
			}

			if (abstractDecorationEntity.canStayAttached()) {
				if (!world.isClient) {
					abstractDecorationEntity.onPlace();
					world.emitGameEvent(playerEntity, GameEvent.ENTITY_PLACE, abstractDecorationEntity.getPos());
					world.spawnEntity(abstractDecorationEntity);
				}

				itemStack.decrement(1);
				return ActionResult.success(world.isClient);
			} else {
				return ActionResult.CONSUME;
			}
		}
	}

	protected boolean canPlaceOn(PlayerEntity player, Direction side, ItemStack stack, BlockPos pos) {
		return !side.getAxis().isVertical() && player.canPlaceOn(pos, side, stack);
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
		super.appendTooltip(stack, context, tooltip, type);
		RegistryWrapper.WrapperLookup wrapperLookup = context.getRegistryLookup();
		if (wrapperLookup != null && this.entityType == EntityType.PAINTING) {
			NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.ENTITY_DATA, NbtComponent.DEFAULT);
			if (!nbtComponent.isEmpty()) {
				nbtComponent.get(wrapperLookup.getOps(NbtOps.INSTANCE), PaintingEntity.VARIANT_MAP_CODEC).result().ifPresentOrElse(variant -> {
					variant.getKey().ifPresent(key -> {
						tooltip.add(Text.translatable(key.getValue().toTranslationKey("painting", "title")).formatted(Formatting.YELLOW));
						tooltip.add(Text.translatable(key.getValue().toTranslationKey("painting", "author")).formatted(Formatting.GRAY));
					});
					tooltip.add(Text.translatable("painting.dimensions", ((PaintingVariant)variant.value()).width(), ((PaintingVariant)variant.value()).height()));
				}, () -> tooltip.add(RANDOM_TEXT));
			} else if (type.isCreative()) {
				tooltip.add(RANDOM_TEXT);
			}
		}
	}
}
