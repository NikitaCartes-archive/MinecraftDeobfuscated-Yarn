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
	public ActionResult useOnBlock(ItemUsageContext context) {
		Direction direction = context.getSide();
		if (direction == Direction.DOWN) {
			return ActionResult.FAIL;
		} else {
			World world = context.getWorld();
			ItemPlacementContext itemPlacementContext = new ItemPlacementContext(context);
			BlockPos blockPos = itemPlacementContext.getBlockPos();
			BlockPos blockPos2 = blockPos.up();
			if (itemPlacementContext.canPlace() && world.getBlockState(blockPos2).canReplace(itemPlacementContext)) {
				double d = (double)blockPos.getX();
				double e = (double)blockPos.getY();
				double f = (double)blockPos.getZ();
				List<Entity> list = world.getEntities(null, new Box(d, e, f, d + 1.0, e + 2.0, f + 1.0));
				if (!list.isEmpty()) {
					return ActionResult.FAIL;
				} else {
					ItemStack itemStack = context.getStack();
					if (!world.isClient) {
						world.removeBlock(blockPos, false);
						world.removeBlock(blockPos2, false);
						ArmorStandEntity armorStandEntity = new ArmorStandEntity(world, d + 0.5, e, f + 0.5);
						float g = (float)MathHelper.floor((MathHelper.wrapDegrees(context.getPlayerYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
						armorStandEntity.refreshPositionAndAngles(d + 0.5, e, f + 0.5, g, 0.0F);
						this.setRotations(armorStandEntity, world.random);
						EntityType.loadFromEntityTag(world, context.getPlayer(), armorStandEntity, itemStack.getTag());
						world.spawnEntity(armorStandEntity);
						world.playSound(
							null, armorStandEntity.getX(), armorStandEntity.getY(), armorStandEntity.getZ(), SoundEvents.ENTITY_ARMOR_STAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F
						);
					}

					itemStack.decrement(1);
					return ActionResult.SUCCESS;
				}
			} else {
				return ActionResult.FAIL;
			}
		}
	}

	private void setRotations(ArmorStandEntity stand, Random random) {
		EulerAngle eulerAngle = stand.getHeadRotation();
		float f = random.nextFloat() * 5.0F;
		float g = random.nextFloat() * 20.0F - 10.0F;
		EulerAngle eulerAngle2 = new EulerAngle(eulerAngle.getPitch() + f, eulerAngle.getYaw() + g, eulerAngle.getRoll());
		stand.setHeadRotation(eulerAngle2);
		eulerAngle = stand.getBodyRotation();
		f = random.nextFloat() * 10.0F - 5.0F;
		eulerAngle2 = new EulerAngle(eulerAngle.getPitch(), eulerAngle.getYaw() + f, eulerAngle.getRoll());
		stand.setBodyRotation(eulerAngle2);
	}
}
