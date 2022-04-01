package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

public class IronGolemLookGoal extends Goal {
	private static final TargetPredicate CLOSE_VILLAGER_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(6.0);
	public static final int MAX_LOOK_COOLDOWN = 400;
	private static final List<String> field_38515 = List.of("maria", "alva", "neo", "hidetaka", "miyazaki");
	private final IronGolemEntity golem;
	private LivingEntity field_38516;
	private int lookCountdown;

	public IronGolemLookGoal(IronGolemEntity golem) {
		this.golem = golem;
		this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
	}

	@Override
	public boolean canStart() {
		if (!this.golem.world.isDay()) {
			return false;
		} else if (this.golem.method_42814()) {
			if (this.golem.getRandom().nextInt(300) != 0) {
				return false;
			} else {
				Box box = this.golem.getBoundingBox().expand(6.0, 2.0, 6.0);
				List<PlayerEntity> list = this.golem.world.getEntitiesByType(EntityType.PLAYER, box, entity -> {
					if (entity instanceof PlayerEntity playerEntity && this.method_42813(playerEntity)) {
						return true;
					}

					return false;
				});
				if (list.isEmpty()) {
					return false;
				} else {
					this.field_38516 = (LivingEntity)list.get(0);
					return true;
				}
			}
		} else if (this.golem.getRandom().nextInt(8000) != 0) {
			return false;
		} else {
			this.field_38516 = this.golem
				.world
				.getClosestEntity(
					VillagerEntity.class,
					CLOSE_VILLAGER_PREDICATE,
					this.golem,
					this.golem.getX(),
					this.golem.getY(),
					this.golem.getZ(),
					this.golem.getBoundingBox().expand(6.0, 2.0, 6.0)
				);
			return this.field_38516 != null;
		}
	}

	private boolean method_42813(PlayerEntity playerEntity) {
		return field_38515.contains(playerEntity.getName().getString().toLowerCase(Locale.ROOT));
	}

	@Override
	public boolean shouldContinue() {
		return this.lookCountdown > 0;
	}

	@Override
	public void start() {
		this.lookCountdown = this.getTickCount(400);
		this.golem.setLookingAtVillager(true);
	}

	@Override
	public void stop() {
		this.golem.setLookingAtVillager(false);
		this.field_38516 = null;
	}

	@Override
	public void tick() {
		this.golem.getLookControl().lookAt(this.field_38516, 30.0F, 30.0F);
		this.lookCountdown--;
	}
}
