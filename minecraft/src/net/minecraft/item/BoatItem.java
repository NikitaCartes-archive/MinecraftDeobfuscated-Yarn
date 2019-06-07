package net.minecraft.item;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class BoatItem extends Item {
	private static final Predicate<Entity> RIDERS = EntityPredicates.EXCEPT_SPECTATOR.and(Entity::collides);
	private final BoatEntity.Type type;

	public BoatItem(BoatEntity.Type type, Item.Settings settings) {
		super(settings);
		this.type = type;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		HitResult hitResult = rayTrace(world, playerEntity, RayTraceContext.FluidHandling.field_1347);
		if (hitResult.getType() == HitResult.Type.field_1333) {
			return new TypedActionResult<>(ActionResult.field_5811, itemStack);
		} else {
			Vec3d vec3d = playerEntity.getRotationVec(1.0F);
			double d = 5.0;
			List<Entity> list = world.getEntities(playerEntity, playerEntity.getBoundingBox().stretch(vec3d.multiply(5.0)).expand(1.0), RIDERS);
			if (!list.isEmpty()) {
				Vec3d vec3d2 = playerEntity.getCameraPosVec(1.0F);

				for (Entity entity : list) {
					Box box = entity.getBoundingBox().expand((double)entity.getTargetingMargin());
					if (box.contains(vec3d2)) {
						return new TypedActionResult<>(ActionResult.field_5811, itemStack);
					}
				}
			}

			if (hitResult.getType() == HitResult.Type.field_1332) {
				BoatEntity boatEntity = new BoatEntity(world, hitResult.getPos().x, hitResult.getPos().y, hitResult.getPos().z);
				boatEntity.setBoatType(this.type);
				boatEntity.yaw = playerEntity.yaw;
				if (!world.doesNotCollide(boatEntity, boatEntity.getBoundingBox().expand(-0.1))) {
					return new TypedActionResult<>(ActionResult.field_5814, itemStack);
				} else {
					if (!world.isClient) {
						world.spawnEntity(boatEntity);
					}

					if (!playerEntity.abilities.creativeMode) {
						itemStack.decrement(1);
					}

					playerEntity.incrementStat(Stats.field_15372.getOrCreateStat(this));
					return new TypedActionResult<>(ActionResult.field_5812, itemStack);
				}
			} else {
				return new TypedActionResult<>(ActionResult.field_5811, itemStack);
			}
		}
	}
}
