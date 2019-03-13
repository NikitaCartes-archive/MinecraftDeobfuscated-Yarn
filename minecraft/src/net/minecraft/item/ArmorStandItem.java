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
	public ActionResult method_7884(ItemUsageContext itemUsageContext) {
		Direction direction = itemUsageContext.method_8038();
		if (direction == Direction.DOWN) {
			return ActionResult.field_5814;
		} else {
			World world = itemUsageContext.method_8045();
			ItemPlacementContext itemPlacementContext = new ItemPlacementContext(itemUsageContext);
			BlockPos blockPos = itemPlacementContext.method_8037();
			BlockPos blockPos2 = blockPos.up();
			if (itemPlacementContext.canPlace() && world.method_8320(blockPos2).method_11587(itemPlacementContext)) {
				double d = (double)blockPos.getX();
				double e = (double)blockPos.getY();
				double f = (double)blockPos.getZ();
				List<Entity> list = world.method_8335(null, new BoundingBox(d, e, f, d + 1.0, e + 2.0, f + 1.0));
				if (!list.isEmpty()) {
					return ActionResult.field_5814;
				} else {
					ItemStack itemStack = itemUsageContext.getItemStack();
					if (!world.isClient) {
						world.method_8650(blockPos);
						world.method_8650(blockPos2);
						ArmorStandEntity armorStandEntity = new ArmorStandEntity(world, d + 0.5, e, f + 0.5);
						float g = (float)MathHelper.floor((MathHelper.wrapDegrees(itemUsageContext.getPlayerYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
						armorStandEntity.setPositionAndAngles(d + 0.5, e, f + 0.5, g, 0.0F);
						this.setRotations(armorStandEntity, world.random);
						EntityType.method_5881(world, itemUsageContext.getPlayer(), armorStandEntity, itemStack.method_7969());
						world.spawnEntity(armorStandEntity);
						world.method_8465(null, armorStandEntity.x, armorStandEntity.y, armorStandEntity.z, SoundEvents.field_14969, SoundCategory.field_15245, 0.75F, 0.8F);
					}

					itemStack.subtractAmount(1);
					return ActionResult.field_5812;
				}
			} else {
				return ActionResult.field_5814;
			}
		}
	}

	private void setRotations(ArmorStandEntity armorStandEntity, Random random) {
		Rotation rotation = armorStandEntity.method_6921();
		float f = random.nextFloat() * 5.0F;
		float g = random.nextFloat() * 20.0F - 10.0F;
		Rotation rotation2 = new Rotation(rotation.getX() + f, rotation.getY() + g, rotation.getZ());
		armorStandEntity.method_6919(rotation2);
		rotation = armorStandEntity.method_6923();
		f = random.nextFloat() * 10.0F - 5.0F;
		rotation2 = new Rotation(rotation.getX(), rotation.getY() + f, rotation.getZ());
		armorStandEntity.method_6927(rotation2);
	}
}
