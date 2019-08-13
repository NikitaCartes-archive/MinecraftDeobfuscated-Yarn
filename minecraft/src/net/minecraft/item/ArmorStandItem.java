package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ArmorStandItem extends Item {
	public ArmorStandItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		Direction direction = itemUsageContext.getSide();
		if (direction == Direction.field_11033) {
			return ActionResult.field_5814;
		} else {
			World world = itemUsageContext.getWorld();
			ItemPlacementContext itemPlacementContext = new ItemPlacementContext(itemUsageContext);
			BlockPos blockPos = itemPlacementContext.getBlockPos();
			BlockPos blockPos2 = blockPos.up();
			if (itemPlacementContext.canPlace() && world.getBlockState(blockPos2).canReplace(itemPlacementContext)) {
				double d = (double)blockPos.getX();
				double e = (double)blockPos.getY();
				double f = (double)blockPos.getZ();
				List<Entity> list = world.getEntities(null, new Box(d, e, f, d + 1.0, e + 2.0, f + 1.0));
				if (!list.isEmpty()) {
					return ActionResult.field_5814;
				} else {
					ItemStack itemStack = itemUsageContext.getStack();
					if (!world.isClient) {
						world.clearBlockState(blockPos, false);
						world.clearBlockState(blockPos2, false);
						ArmorStandEntity armorStandEntity = new ArmorStandEntity(world, d + 0.5, e, f + 0.5);
						float g = (float)MathHelper.floor((MathHelper.wrapDegrees(itemUsageContext.getPlayerYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
						armorStandEntity.setPositionAndAngles(d + 0.5, e, f + 0.5, g, 0.0F);
						this.setRotations(armorStandEntity, world.random);
						EntityType.loadFromEntityTag(world, itemUsageContext.getPlayer(), armorStandEntity, itemStack.getTag());
						world.spawnEntity(armorStandEntity);
						world.playSound(null, armorStandEntity.x, armorStandEntity.y, armorStandEntity.z, SoundEvents.field_14969, SoundCategory.field_15245, 0.75F, 0.8F);
					}

					itemStack.decrement(1);
					return ActionResult.field_5812;
				}
			} else {
				return ActionResult.field_5814;
			}
		}
	}

	private void setRotations(ArmorStandEntity armorStandEntity, Random random) {
		EulerAngle eulerAngle = armorStandEntity.getHeadRotation();
		float f = random.nextFloat() * 5.0F;
		float g = random.nextFloat() * 20.0F - 10.0F;
		EulerAngle eulerAngle2 = new EulerAngle(eulerAngle.getPitch() + f, eulerAngle.getYaw() + g, eulerAngle.getRoll());
		armorStandEntity.setHeadRotation(eulerAngle2);
		eulerAngle = armorStandEntity.getBodyRotation();
		f = random.nextFloat() * 10.0F - 5.0F;
		eulerAngle2 = new EulerAngle(eulerAngle.getPitch(), eulerAngle.getYaw() + f, eulerAngle.getRoll());
		armorStandEntity.setBodyRotation(eulerAngle2);
	}
}
