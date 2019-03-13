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
	public TypedActionResult<ItemStack> method_7836(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		HitResult hitResult = method_7872(world, playerEntity, RayTraceContext.FluidHandling.field_1347);
		if (hitResult.getType() == HitResult.Type.NONE) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else {
			Vec3d vec3d = playerEntity.method_5828(1.0F);
			double d = 5.0;
			List<Entity> list = world.method_8333(playerEntity, playerEntity.method_5829().method_18804(vec3d.multiply(5.0)).expand(1.0), field_17497);
			if (!list.isEmpty()) {
				Vec3d vec3d2 = playerEntity.method_5836(1.0F);

				for (Entity entity : list) {
					BoundingBox boundingBox = entity.method_5829().expand((double)entity.getBoundingBoxMarginForTargeting());
					if (boundingBox.method_1006(vec3d2)) {
						return new TypedActionResult<>(ActionResult.PASS, itemStack);
					}
				}
			}

			if (hitResult.getType() == HitResult.Type.BLOCK) {
				BoatEntity boatEntity = new BoatEntity(world, hitResult.method_17784().x, hitResult.method_17784().y, hitResult.method_17784().z);
				boatEntity.method_7541(this.type);
				boatEntity.yaw = playerEntity.yaw;
				if (!world.method_8587(boatEntity, boatEntity.method_5829().expand(-0.1))) {
					return new TypedActionResult<>(ActionResult.field_5814, itemStack);
				} else {
					if (!world.isClient) {
						world.spawnEntity(boatEntity);
					}

					if (!playerEntity.abilities.creativeMode) {
						itemStack.subtractAmount(1);
					}

					playerEntity.method_7259(Stats.field_15372.getOrCreateStat(this));
					return new TypedActionResult<>(ActionResult.field_5812, itemStack);
				}
			} else {
				return new TypedActionResult<>(ActionResult.PASS, itemStack);
			}
		}
	}
}
