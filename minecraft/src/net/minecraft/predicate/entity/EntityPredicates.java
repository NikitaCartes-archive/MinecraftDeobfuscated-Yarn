package net.minecraft.predicate.entity;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.AbstractScoreboardTeam;

public final class EntityPredicates {
	public static final Predicate<Entity> VALID_ENTITY = Entity::isValid;
	public static final Predicate<LivingEntity> VALID_ENTITY_LIVING = LivingEntity::isValid;
	public static final Predicate<Entity> NOT_MOUNTED = entity -> entity.isValid() && !entity.hasPassengers() && !entity.hasVehicle();
	public static final Predicate<Entity> VALID_INVENTORIES = entity -> entity instanceof Inventory && entity.isValid();
	public static final Predicate<Entity> EXCEPT_CREATIVE_OR_SPECTATOR = entity -> !(entity instanceof PlayerEntity)
			|| !entity.isSpectator() && !((PlayerEntity)entity).isCreative();
	public static final Predicate<Entity> EXCEPT_SPECTATOR = entity -> !entity.isSpectator();

	public static Predicate<Entity> maximumDistance(double d, double e, double f, double g) {
		double h = g * g;
		return entity -> entity != null && entity.squaredDistanceTo(d, e, f) <= h;
	}

	public static Predicate<Entity> method_5911(Entity entity) {
		AbstractScoreboardTeam abstractScoreboardTeam = entity.getScoreboardTeam();
		AbstractScoreboardTeam.CollisionRule collisionRule = abstractScoreboardTeam == null
			? AbstractScoreboardTeam.CollisionRule.field_1437
			: abstractScoreboardTeam.getCollisionRule();
		return (Predicate<Entity>)(collisionRule == AbstractScoreboardTeam.CollisionRule.field_1435
			? Predicates.alwaysFalse()
			: EXCEPT_SPECTATOR.and(
				entity2 -> {
					if (!entity2.isPushable()) {
						return false;
					} else if (!entity.world.isClient || entity2 instanceof PlayerEntity && ((PlayerEntity)entity2).method_7340()) {
						AbstractScoreboardTeam abstractScoreboardTeam2 = entity2.getScoreboardTeam();
						AbstractScoreboardTeam.CollisionRule collisionRule2 = abstractScoreboardTeam2 == null
							? AbstractScoreboardTeam.CollisionRule.field_1437
							: abstractScoreboardTeam2.getCollisionRule();
						if (collisionRule2 == AbstractScoreboardTeam.CollisionRule.field_1435) {
							return false;
						} else {
							boolean bl = abstractScoreboardTeam != null && abstractScoreboardTeam.isEqual(abstractScoreboardTeam2);
							return (collisionRule == AbstractScoreboardTeam.CollisionRule.field_1440 || collisionRule2 == AbstractScoreboardTeam.CollisionRule.field_1440) && bl
								? false
								: collisionRule != AbstractScoreboardTeam.CollisionRule.field_1434 && collisionRule2 != AbstractScoreboardTeam.CollisionRule.field_1434 || bl;
						}
					} else {
						return false;
					}
				}
			));
	}

	public static Predicate<Entity> method_5913(Entity entity) {
		return entity2 -> {
			while (entity2.hasVehicle()) {
				entity2 = entity2.getRiddenEntity();
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
			if (!entity.isValid()) {
				return false;
			} else if (!(entity instanceof LivingEntity)) {
				return false;
			} else {
				LivingEntity livingEntity = (LivingEntity)entity;
				EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(this.itemstack);
				if (!livingEntity.getEquippedStack(equipmentSlot).isEmpty()) {
					return false;
				} else if (livingEntity instanceof MobEntity) {
					return ((MobEntity)livingEntity).canPickUpLoot();
				} else {
					return livingEntity instanceof ArmorStandEntity ? !((ArmorStandEntity)livingEntity).method_6915(equipmentSlot) : livingEntity instanceof PlayerEntity;
				}
			}
		}
	}
}
