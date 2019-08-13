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
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		BlockPos blockPos = itemUsageContext.getBlockPos();
		Direction direction = itemUsageContext.getSide();
		BlockPos blockPos2 = blockPos.offset(direction);
		PlayerEntity playerEntity = itemUsageContext.getPlayer();
		ItemStack itemStack = itemUsageContext.getStack();
		if (playerEntity != null && !this.canPlaceOn(playerEntity, direction, itemStack, blockPos2)) {
			return ActionResult.field_5814;
		} else {
			World world = itemUsageContext.getWorld();
			AbstractDecorationEntity abstractDecorationEntity;
			if (this.entityType == EntityType.field_6120) {
				abstractDecorationEntity = new PaintingEntity(world, blockPos2, direction);
			} else {
				if (this.entityType != EntityType.field_6043) {
					return ActionResult.field_5812;
				}

				abstractDecorationEntity = new ItemFrameEntity(world, blockPos2, direction);
			}

			CompoundTag compoundTag = itemStack.getTag();
			if (compoundTag != null) {
				EntityType.loadFromEntityTag(world, playerEntity, abstractDecorationEntity, compoundTag);
			}

			if (abstractDecorationEntity.method_6888()) {
				if (!world.isClient) {
					abstractDecorationEntity.onPlace();
					world.spawnEntity(abstractDecorationEntity);
				}

				itemStack.decrement(1);
			}

			return ActionResult.field_5812;
		}
	}

	protected boolean canPlaceOn(PlayerEntity playerEntity, Direction direction, ItemStack itemStack, BlockPos blockPos) {
		return !direction.getAxis().isVertical() && playerEntity.canPlaceOn(blockPos, direction, itemStack);
	}
}
