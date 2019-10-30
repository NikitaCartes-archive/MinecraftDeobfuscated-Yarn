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
	public static final Predicate<Entity> VALID_ENTITY = Entity::isAlive;
	public static final Predicate<LivingEntity> VALID_ENTITY_LIVING = LivingEntity::isAlive;
	public static final Predicate<Entity> NOT_MOUNTED = entity -> entity.isAlive() && !entity.hasPassengers() && !entity.hasVehicle();
	public static final Predicate<Entity> VALID_INVENTORIES = entity -> entity instanceof Inventory && entity.isAlive();
	public static final Predicate<Entity> EXCEPT_CREATIVE_OR_SPECTATOR = entity -> !(entity instanceof PlayerEntity)
			|| !entity.isSpectator() && !((PlayerEntity)entity).isCreative();
	public static final Predicate<Entity> EXCEPT_SPECTATOR = entity -> !entity.isSpectator();

	public static Predicate<Entity> maximumDistance(double x, double d, double e, double f) {
		double g = f * f;
		return entity -> entity != null && entity.squaredDistanceTo(x, d, e) <= g;
	}

	public static Predicate<Entity> canBePushedBy(Entity entity) {
		AbstractTeam abstractTeam = entity.getScoreboardTeam();
		AbstractTeam.CollisionRule collisionRule = abstractTeam == null ? AbstractTeam.CollisionRule.ALWAYS : abstractTeam.getCollisionRule();
		return (Predicate<Entity>)(collisionRule == AbstractTeam.CollisionRule.NEVER
			? Predicates.alwaysFalse()
			: EXCEPT_SPECTATOR.and(
				entity2 -> {
					if (!entity2.isPushable()) {
						return false;
					} else if (!entity.world.isClient || entity2 instanceof PlayerEntity && ((PlayerEntity)entity2).isMainPlayer()) {
						AbstractTeam abstractTeam2 = entity2.getScoreboardTeam();
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

	public static class CanPickup implements Predicate<Entity> {
		private final ItemStack itemstack;

		public CanPickup(ItemStack itemStack) {
			this.itemstack = itemStack;
		}

		public boolean method_5916(@Nullable Entity entity) {
			if (!entity.isAlive()) {
				return false;
			} else if (!(entity instanceof LivingEntity)) {
				return false;
			} else {
				LivingEntity livingEntity = (LivingEntity)entity;
				return livingEntity.canPickUp(this.itemstack);
			}
		}
	}
}
