package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DecorationItem extends Item {
	private final Class<? extends AbstractDecorationEntity> entityType;

	public DecorationItem(Class<? extends AbstractDecorationEntity> class_, Item.Settings settings) {
		super(settings);
		this.entityType = class_;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		BlockPos blockPos = itemUsageContext.getPos();
		Direction direction = itemUsageContext.method_8038();
		BlockPos blockPos2 = blockPos.method_10093(direction);
		PlayerEntity playerEntity = itemUsageContext.getPlayer();
		if (playerEntity != null && !this.method_7834(playerEntity, direction, itemUsageContext.getItemStack(), blockPos2)) {
			return ActionResult.FAILURE;
		} else {
			World world = itemUsageContext.getWorld();
			AbstractDecorationEntity abstractDecorationEntity = this.method_7835(world, blockPos2, direction);
			if (abstractDecorationEntity != null && abstractDecorationEntity.method_6888()) {
				if (!world.isRemote) {
					abstractDecorationEntity.onDecorationPlaced();
					world.spawnEntity(abstractDecorationEntity);
				}

				itemUsageContext.getItemStack().subtractAmount(1);
			}

			return ActionResult.SUCCESS;
		}
	}

	protected boolean method_7834(PlayerEntity playerEntity, Direction direction, ItemStack itemStack, BlockPos blockPos) {
		return !direction.getAxis().isVertical() && playerEntity.method_7343(blockPos, direction, itemStack);
	}

	@Nullable
	private AbstractDecorationEntity method_7835(World world, BlockPos blockPos, Direction direction) {
		if (this.entityType == PaintingEntity.class) {
			return new PaintingEntity(world, blockPos, direction);
		} else {
			return this.entityType == ItemFrameEntity.class ? new ItemFrameEntity(world, blockPos, direction) : null;
		}
	}
}
