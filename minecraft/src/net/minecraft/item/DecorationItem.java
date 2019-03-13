package net.minecraft.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DecorationItem extends Item {
	private final EntityType<? extends AbstractDecorationEntity> entityType;

	public DecorationItem(EntityType<? extends AbstractDecorationEntity> entityType, Item.Settings settings) {
		super(settings);
		this.entityType = entityType;
	}

	@Override
	public ActionResult method_7884(ItemUsageContext itemUsageContext) {
		BlockPos blockPos = itemUsageContext.method_8037();
		Direction direction = itemUsageContext.method_8038();
		BlockPos blockPos2 = blockPos.method_10093(direction);
		PlayerEntity playerEntity = itemUsageContext.getPlayer();
		ItemStack itemStack = itemUsageContext.getItemStack();
		if (playerEntity != null && !this.method_7834(playerEntity, direction, itemStack, blockPos2)) {
			return ActionResult.field_5814;
		} else {
			World world = itemUsageContext.method_8045();
			AbstractDecorationEntity abstractDecorationEntity;
			if (this.entityType == EntityType.PAINTING) {
				abstractDecorationEntity = new PaintingEntity(world, blockPos2, direction);
			} else {
				if (this.entityType != EntityType.ITEM_FRAME) {
					return ActionResult.field_5812;
				}

				abstractDecorationEntity = new ItemFrameEntity(world, blockPos2, direction);
			}

			CompoundTag compoundTag = itemStack.method_7969();
			if (compoundTag != null) {
				EntityType.method_5881(world, playerEntity, abstractDecorationEntity, compoundTag);
			}

			if (abstractDecorationEntity.method_6888()) {
				if (!world.isClient) {
					abstractDecorationEntity.method_6894();
					world.spawnEntity(abstractDecorationEntity);
				}

				itemStack.subtractAmount(1);
			}

			return ActionResult.field_5812;
		}
	}

	protected boolean method_7834(PlayerEntity playerEntity, Direction direction, ItemStack itemStack, BlockPos blockPos) {
		return !direction.getAxis().isVertical() && playerEntity.method_7343(blockPos, direction, itemStack);
	}
}
