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
import net.minecraft.world.Difficulty;

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
	public static final Predicate<LivingEntity> VALID_LIVING_ENTITY = LivingEntity::isAlive;
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
	public static final Predicate<Entity> EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL = entity -> !(entity instanceof PlayerEntity)
			|| !entity.isSpectator() && !((PlayerEntity)entity).isCreative() && entity.world.getDifficulty() != Difficulty.field_5801;
	public static final Predicate<Entity> EXCEPT_SPECTATOR = entity -> !entity.isSpectator();

	public static Predicate<Entity> maxDistance(double x, double y, double z, double d) {
		double e = d * d;
		return entity -> entity != null && entity.squaredDistanceTo(x, y, z) <= e;
	}

	public static Predicate<Entity> canBePushedBy(Entity entity) {
		AbstractTeam abstractTeam = entity.getScoreboardTeam();
		AbstractTeam.CollisionRule collisionRule = abstractTeam == null ? AbstractTeam.CollisionRule.field_1437 : abstractTeam.getCollisionRule();
		return (Predicate<Entity>)(collisionRule == AbstractTeam.CollisionRule.field_1435
			? Predicates.alwaysFalse()
			: EXCEPT_SPECTATOR.and(
				entity2 -> {
					if (!entity2.isPushable()) {
						return false;
					} else if (!entity.world.isClient || entity2 instanceof PlayerEntity && ((PlayerEntity)entity2).isMainPlayer()) {
						AbstractTeam abstractTeam2 = entity2.getScoreboardTeam();
						AbstractTeam.CollisionRule collisionRule2 = abstractTeam2 == null ? AbstractTeam.CollisionRule.field_1437 : abstractTeam2.getCollisionRule();
						if (collisionRule2 == AbstractTeam.CollisionRule.field_1435) {
							return false;
						} else {
							boolean bl = abstractTeam != null && abstractTeam.isEqual(abstractTeam2);
							return (collisionRule == AbstractTeam.CollisionRule.field_1440 || collisionRule2 == AbstractTeam.CollisionRule.field_1440) && bl
								? false
								: collisionRule != AbstractTeam.CollisionRule.field_1434 && collisionRule2 != AbstractTeam.CollisionRule.field_1434 || bl;
						}
					} else {
						return false;
					}
				}
			));
	}

	public static Predicate<Entity> rides(Entity entity) {
		return entity2 -> {
			while (entity2.hasVehicle()) {
				entity2 = entity2.getVehicle();
				if (entity2 == entity) {
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

		public boolean method_5916(@Nullable Entity entity) {
			if (!entity.isAlive()) {
				return false;
			} else if (!(entity instanceof LivingEntity)) {
				return false;
			} else {
				LivingEntity livingEntity = (LivingEntity)entity;
				return livingEntity.canEquip(this.stack);
			}
		}
	}
}
