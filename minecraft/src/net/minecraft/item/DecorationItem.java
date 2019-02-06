package net.minecraft.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
		Direction direction = itemUsageContext.getFacing();
		BlockPos blockPos2 = blockPos.offset(direction);
		PlayerEntity playerEntity = itemUsageContext.getPlayer();
		if (playerEntity != null && !this.method_7834(playerEntity, direction, itemUsageContext.getItemStack(), blockPos2)) {
			return ActionResult.FAILURE;
		} else {
			World world = itemUsageContext.getWorld();
			AbstractDecorationEntity abstractDecorationEntity;
			if (this.entityType == EntityType.PAINTING) {
				abstractDecorationEntity = new PaintingEntity(world, blockPos2, direction);
			} else {
				if (this.entityType != EntityType.ITEM_FRAME) {
					return ActionResult.SUCCESS;
				}

				abstractDecorationEntity = new ItemFrameEntity(world, blockPos2, direction);
			}

			if (abstractDecorationEntity.method_6888()) {
				if (!world.isClient) {
					abstractDecorationEntity.method_6894();
					world.spawnEntity(abstractDecorationEntity);
				}

				itemUsageContext.getItemStack().subtractAmount(1);
			}

			return ActionResult.SUCCESS;
		}
	}

	protected boolean method_7834(PlayerEntity playerEntity, Direction direction, ItemStack itemStack, BlockPos blockPos) {
		return !direction.getAxis().isVertical() && playerEntity.canPlaceBlock(blockPos, direction, itemStack);
	}
}
