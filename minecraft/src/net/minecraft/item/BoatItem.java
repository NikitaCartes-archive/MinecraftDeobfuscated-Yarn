package net.minecraft.item;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class BoatItem extends Item {
	private static final Predicate<Entity> RIDERS = EntityPredicates.EXCEPT_SPECTATOR.and(Entity::collides);
	private final BoatEntity.Type type;

	public BoatItem(BoatEntity.Type type, Item.Settings settings) {
		super(settings);
		this.type = type;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		HitResult hitResult = raycast(world, user, RaycastContext.FluidHandling.ANY);
		if (hitResult.getType() == HitResult.Type.MISS) {
			return TypedActionResult.pass(itemStack);
		} else {
			Vec3d vec3d = user.getRotationVec(1.0F);
			double d = 5.0;
			List<Entity> list = world.getOtherEntities(user, user.getBoundingBox().stretch(vec3d.multiply(5.0)).expand(1.0), RIDERS);
			if (!list.isEmpty()) {
				Vec3d vec3d2 = user.getCameraPosVec(1.0F);

				for (Entity entity : list) {
					Box box = entity.getBoundingBox().expand((double)entity.getTargetingMargin());
					if (box.contains(vec3d2)) {
						return TypedActionResult.pass(itemStack);
					}
				}
			}

			if (hitResult.getType() == HitResult.Type.BLOCK) {
				BoatEntity boatEntity = new BoatEntity(world, hitResult.getPos().x, hitResult.getPos().y, hitResult.getPos().z);
				boatEntity.setBoatType(this.type);
				boatEntity.yaw = user.yaw;
				if (!world.isSpaceEmpty(boatEntity, boatEntity.getBoundingBox().expand(-0.1))) {
					return TypedActionResult.fail(itemStack);
				} else {
					if (!world.isClient) {
						world.spawnEntity(boatEntity);
						if (!user.abilities.creativeMode) {
							itemStack.decrement(1);
						}
					}

					user.incrementStat(Stats.USED.getOrCreateStat(this));
					return TypedActionResult.success(itemStack, world.isClient());
				}
			} else {
				return TypedActionResult.pass(itemStack);
			}
		}
	}
}
