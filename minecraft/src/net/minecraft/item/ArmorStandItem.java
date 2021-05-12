package net.minecraft.item;

import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

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
			ItemStack itemStack = context.getStack();
			Vec3d vec3d = Vec3d.ofBottomCenter(blockPos);
			Box box = EntityType.ARMOR_STAND.getDimensions().getBoxAt(vec3d.getX(), vec3d.getY(), vec3d.getZ());
			if (world.isSpaceEmpty(null, box, entity -> true) && world.getOtherEntities(null, box).isEmpty()) {
				if (world instanceof ServerWorld serverWorld) {
					ArmorStandEntity armorStandEntity = EntityType.ARMOR_STAND
						.create(serverWorld, itemStack.getTag(), null, context.getPlayer(), blockPos, SpawnReason.SPAWN_EGG, true, true);
					if (armorStandEntity == null) {
						return ActionResult.FAIL;
					}

					float f = (float)MathHelper.floor((MathHelper.wrapDegrees(context.getPlayerYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
					armorStandEntity.refreshPositionAndAngles(armorStandEntity.getX(), armorStandEntity.getY(), armorStandEntity.getZ(), f, 0.0F);
					this.setRotations(armorStandEntity, world.random);
					serverWorld.spawnEntityAndPassengers(armorStandEntity);
					world.playSound(
						null, armorStandEntity.getX(), armorStandEntity.getY(), armorStandEntity.getZ(), SoundEvents.ENTITY_ARMOR_STAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F
					);
					world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, armorStandEntity);
				}

				itemStack.decrement(1);
				return ActionResult.success(world.isClient);
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
