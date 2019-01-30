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
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class BoatItem extends Item {
	private static final Predicate<Entity> field_17497 = EntityPredicates.EXCEPT_SPECTATOR.and(Entity::doesCollide);
	private final BoatEntity.Type type;

	public BoatItem(BoatEntity.Type type, Item.Settings settings) {
		super(settings);
		this.type = type;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		HitResult hitResult = getHitResult(world, playerEntity, RayTraceContext.FluidHandling.field_1347);
		if (hitResult.getType() == HitResult.Type.NONE) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else {
			Vec3d vec3d = playerEntity.getRotationVec(1.0F);
			double d = 5.0;
			List<Entity> list = world.getEntities(
				playerEntity, playerEntity.getBoundingBox().stretch(vec3d.x * 5.0, vec3d.y * 5.0, vec3d.z * 5.0).expand(1.0), field_17497
			);
			if (!list.isEmpty()) {
				Vec3d vec3d2 = playerEntity.getCameraPosVec(1.0F);

				for (Entity entity : list) {
					BoundingBox boundingBox = entity.getBoundingBox().expand((double)entity.getBoundingBoxMarginForTargeting());
					if (boundingBox.contains(vec3d2)) {
						return new TypedActionResult<>(ActionResult.PASS, itemStack);
					}
				}
			}

			if (hitResult.getType() == HitResult.Type.BLOCK) {
				BoatEntity boatEntity = new BoatEntity(world, hitResult.getPos().x, hitResult.getPos().y, hitResult.getPos().z);
				boatEntity.setBoatType(this.type);
				boatEntity.yaw = playerEntity.yaw;
				if (!world.isEntityColliding(boatEntity, boatEntity.getBoundingBox().expand(-0.1))) {
					return new TypedActionResult<>(ActionResult.FAILURE, itemStack);
				} else {
					if (!world.isClient) {
						world.spawnEntity(boatEntity);
					}

					if (!playerEntity.abilities.creativeMode) {
						itemStack.subtractAmount(1);
					}

					playerEntity.incrementStat(Stats.field_15372.getOrCreateStat(this));
					return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
				}
			} else {
				return new TypedActionResult<>(ActionResult.PASS, itemStack);
			}
		}
	}
}
