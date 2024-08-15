package net.minecraft.predicate.entity;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.AbstractTeam;

public final class EntityPredicates {
	/**
	 * Tests if an entity is valid.
	 * 
	 * <p>An entity is valid when the entity is alive.
	 * 
	 * @see net.minecraft.entity.Entity#isAlive()
	 */
	public static final Predicate<Entity> VALID_ENTITY = Entity::isAlive;
	/**
	 * Tests if a living entity is valid.
	 * 
	 * <p>A living entity is valid when the entity is alive.
	 * 
	 * @see net.minecraft.entity.LivingEntity#isAlive()
	 */
	public static final Predicate<Entity> VALID_LIVING_ENTITY = entity -> entity.isAlive() && entity instanceof LivingEntity;
	/**
	 * Tests if an entity is not mounted.
	 * 
	 * <p>An entity is not mounted when:
	 * 
	 * <ul><li>The entity is alive
	 * <li>The entity has no passengers
	 * <li>The entity is not in a vehicle
	 * </ul>
	 */
	public static final Predicate<Entity> NOT_MOUNTED = entity -> entity.isAlive() && !entity.hasPassengers() && !entity.hasVehicle();
	/**
	 * Tests if an entity has a valid inventory.
	 * 
	 * <p>An entity has a valid inventory when:
	 * 
	 * <ul><li>The entity is alive
	 * <li>The entity implements {@link net.minecraft.inventory.Inventory}
	 * </ul>
	 * 
	 * @see net.minecraft.entity.vehicle.StorageMinecartEntity
	 */
	public static final Predicate<Entity> VALID_INVENTORIES = entity -> entity instanceof Inventory && entity.isAlive();
	public static final Predicate<Entity> EXCEPT_CREATIVE_OR_SPECTATOR = entity -> !(entity instanceof PlayerEntity)
			|| !entity.isSpectator() && !((PlayerEntity)entity).isCreative();
	public static final Predicate<Entity> EXCEPT_SPECTATOR = entity -> !entity.isSpectator();
	public static final Predicate<Entity> CAN_COLLIDE = EXCEPT_SPECTATOR.and(Entity::isCollidable);
	public static final Predicate<Entity> CAN_HIT = EXCEPT_SPECTATOR.and(Entity::canHit);

	private EntityPredicates() {
	}

	public static Predicate<Entity> maxDistance(double x, double y, double z, double max) {
		double d = max * max;
		return entity -> entity != null && entity.squaredDistanceTo(x, y, z) <= d;
	}

	public static Predicate<Entity> canBePushedBy(Entity entity) {
		AbstractTeam abstractTeam = entity.getScoreboardTeam();
		AbstractTeam.CollisionRule collisionRule = abstractTeam == null ? AbstractTeam.CollisionRule.ALWAYS : abstractTeam.getCollisionRule();
		return (Predicate<Entity>)(collisionRule == AbstractTeam.CollisionRule.NEVER
			? Predicates.alwaysFalse()
			: EXCEPT_SPECTATOR.and(
				entityxx -> {
					if (!entityxx.isPushable()) {
						return false;
					} else if (!entity.getWorld().isClient || entityxx instanceof PlayerEntity && ((PlayerEntity)entityxx).isMainPlayer()) {
						AbstractTeam abstractTeam2 = entityxx.getScoreboardTeam();
						AbstractTeam.CollisionRule collisionRule2 = abstractTeam2 == null ? AbstractTeam.CollisionRule.ALWAYS : abstractTeam2.getCollisionRule();
						if (collisionRule2 == AbstractTeam.CollisionRule.NEVER) {
							return false;
						} else {
							boolean bl = abstractTeam != null && abstractTeam.isEqual(abstractTeam2);
							return (collisionRule == AbstractTeam.CollisionRule.PUSH_OWN_TEAM || collisionRule2 == AbstractTeam.CollisionRule.PUSH_OWN_TEAM) && bl
								? false
								: collisionRule != AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS && collisionRule2 != AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS || bl;
						}
					} else {
						return false;
					}
				}
			));
	}

	public static Predicate<Entity> rides(Entity entity) {
		return testedEntity -> {
			while (testedEntity.hasVehicle()) {
				testedEntity = testedEntity.getVehicle();
				if (testedEntity == entity) {
					return false;
				}
			}

			return true;
		};
	}

	public static class Equipable implements Predicate<Entity> {
		private final ItemStack stack;

		public Equipable(ItemStack stack) {
			this.stack = stack;
		}

		public boolean test(@Nullable Entity entity) {
			if (!entity.isAlive()) {
				return false;
			} else {
				return !(entity instanceof LivingEntity livingEntity) ? false : livingEntity.canEquip(this.stack);
			}
		}
	}
}
