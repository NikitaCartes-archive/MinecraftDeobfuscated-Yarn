package net.minecraft.item;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class BoatItem extends Item {
	private static final Predicate<Entity> RIDERS = EntityPredicates.EXCEPT_SPECTATOR.and(Entity::canHit);
	private final BoatEntity.Type type;
	private final boolean chest;

	public BoatItem(boolean chest, BoatEntity.Type type, Item.Settings settings) {
		super(settings);
		this.chest = chest;
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
				Vec3d vec3d2 = user.getEyePos();

				for (Entity entity : list) {
					Box box = entity.getBoundingBox().expand((double)entity.getTargetingMargin());
					if (box.contains(vec3d2)) {
						return TypedActionResult.pass(itemStack);
					}
				}
			}

			if (hitResult.getType() == HitResult.Type.BLOCK) {
				BoatEntity boatEntity = this.createEntity(world, hitResult, itemStack, user);
				boatEntity.setVariant(this.type);
				boatEntity.setYaw(user.getYaw());
				if (!world.isSpaceEmpty(boatEntity, boatEntity.getBoundingBox())) {
					return TypedActionResult.fail(itemStack);
				} else {
					if (!world.isClient) {
						world.spawnEntity(boatEntity);
						world.emitGameEvent(user, GameEvent.ENTITY_PLACE, hitResult.getPos());
						itemStack.decrementUnlessCreative(1, user);
					}

					user.incrementStat(Stats.USED.getOrCreateStat(this));
					return TypedActionResult.success(itemStack, world.isClient());
				}
			} else {
				return TypedActionResult.pass(itemStack);
			}
		}
	}

	private BoatEntity createEntity(World world, HitResult hitResult, ItemStack stack, PlayerEntity player) {
		Vec3d vec3d = hitResult.getPos();
		BoatEntity boatEntity = (BoatEntity)(this.chest ? new ChestBoatEntity(world, vec3d.x, vec3d.y, vec3d.z) : new BoatEntity(world, vec3d.x, vec3d.y, vec3d.z));
		if (world instanceof ServerWorld serverWorld) {
			EntityType.copier(serverWorld, stack, player).accept(boatEntity);
		}

		return boatEntity;
	}
}
