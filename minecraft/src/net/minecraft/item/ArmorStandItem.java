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
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Rotation;
import net.minecraft.world.World;

public class ArmorStandItem extends Item {
	public ArmorStandItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		Direction direction = itemUsageContext.getFacing();
		if (direction == Direction.DOWN) {
			return ActionResult.FAILURE;
		} else {
			World world = itemUsageContext.getWorld();
			ItemPlacementContext itemPlacementContext = new ItemPlacementContext(itemUsageContext);
			BlockPos blockPos = itemPlacementContext.getBlockPos();
			BlockPos blockPos2 = blockPos.up();
			if (itemPlacementContext.canPlace() && world.getBlockState(blockPos2).method_11587(itemPlacementContext)) {
				double d = (double)blockPos.getX();
				double e = (double)blockPos.getY();
				double f = (double)blockPos.getZ();
				List<Entity> list = world.getVisibleEntities(null, new BoundingBox(d, e, f, d + 1.0, e + 2.0, f + 1.0));
				if (!list.isEmpty()) {
					return ActionResult.FAILURE;
				} else {
					ItemStack itemStack = itemUsageContext.getItemStack();
					if (!world.isClient) {
						world.clearBlockState(blockPos);
						world.clearBlockState(blockPos2);
						ArmorStandEntity armorStandEntity = new ArmorStandEntity(world, d + 0.5, e, f + 0.5);
						float g = (float)MathHelper.floor((MathHelper.wrapDegrees(itemUsageContext.getPlayerYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
						armorStandEntity.setPositionAndAngles(d + 0.5, e, f + 0.5, g, 0.0F);
						this.setRotations(armorStandEntity, world.random);
						EntityType.loadFromEntityTag(world, itemUsageContext.getPlayer(), armorStandEntity, itemStack.getTag());
						world.spawnEntity(armorStandEntity);
						world.playSound(null, armorStandEntity.x, armorStandEntity.y, armorStandEntity.z, SoundEvents.field_14969, SoundCategory.field_15245, 0.75F, 0.8F);
					}

					itemStack.subtractAmount(1);
					return ActionResult.SUCCESS;
				}
			} else {
				return ActionResult.FAILURE;
			}
		}
	}

	private void setRotations(ArmorStandEntity armorStandEntity, Random random) {
		Rotation rotation = armorStandEntity.getHeadRotation();
		float f = random.nextFloat() * 5.0F;
		float g = random.nextFloat() * 20.0F - 10.0F;
		Rotation rotation2 = new Rotation(rotation.getX() + f, rotation.getY() + g, rotation.getZ());
		armorStandEntity.setHeadRotation(rotation2);
		rotation = armorStandEntity.getBodyRotation();
		f = random.nextFloat() * 10.0F - 5.0F;
		rotation2 = new Rotation(rotation.getX(), rotation.getY() + f, rotation.getZ());
		armorStandEntity.setBodyRotation(rotation2);
	}
}
