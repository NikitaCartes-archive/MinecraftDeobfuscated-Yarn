package net.minecraft.entity.mob;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public interface Angerable {
	int getAngerTime();

	void setAngerTime(int ticks);

	@Nullable
	UUID getAngryAt();

	void setAngryAt(@Nullable UUID uuid);

	void chooseRandomAngerTime();

	default void angerToTag(CompoundTag tag) {
		tag.putInt("AngerTime", this.getAngerTime());
		if (this.getAngryAt() != null) {
			tag.putUuid("AngryAt", this.getAngryAt());
		}
	}

	default void angerFromTag(ServerWorld world, CompoundTag tag) {
		this.setAngerTime(tag.getInt("AngerTime"));
		if (!tag.containsUuid("AngryAt")) {
			this.setAngryAt(null);
		} else {
			UUID uUID = tag.getUuid("AngryAt");
			this.setAngryAt(uUID);
			Entity entity = world.getEntity(uUID);
			if (entity != null) {
				if (entity instanceof MobEntity) {
					this.setAttacker((MobEntity)entity);
				}

				if (entity.getType() == EntityType.PLAYER) {
					this.setAttacking((PlayerEntity)entity);
				}
			}
		}
	}

	default void tickAngerLogic(ServerWorld world, boolean bl) {
		LivingEntity livingEntity = this.getTarget();
		UUID uUID = this.getAngryAt();
		if ((livingEntity == null || livingEntity.isDead()) && uUID != null && world.getEntity(uUID) instanceof MobEntity) {
			this.stopAnger();
		} else {
			if (livingEntity != null && !Objects.equals(uUID, livingEntity.getUuid())) {
				this.setAngryAt(livingEntity.getUuid());
				this.chooseRandomAngerTime();
			}

			if (this.getAngerTime() > 0 && (livingEntity == null || livingEntity.getType() != EntityType.PLAYER || !bl)) {
				this.setAngerTime(this.getAngerTime() - 1);
				if (this.getAngerTime() == 0) {
					this.stopAnger();
				}
			}
		}
	}

	default boolean shouldAngerAt(LivingEntity entity) {
		if (!EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(entity)) {
			return false;
		} else {
			return entity.getType() == EntityType.PLAYER && this.isUniversallyAngry(entity.world) ? true : entity.getUuid().equals(this.getAngryAt());
		}
	}

	default boolean isUniversallyAngry(World world) {
		return world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER) && this.hasAngerTime() && this.getAngryAt() == null;
	}

	default boolean hasAngerTime() {
		return this.getAngerTime() > 0;
	}

	default void forgive(PlayerEntity player) {
		if (player.world.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS)) {
			if (player.getUuid().equals(this.getAngryAt())) {
				this.stopAnger();
			}
		}
	}

	default void universallyAnger() {
		this.stopAnger();
		this.chooseRandomAngerTime();
	}

	default void stopAnger() {
		this.setAttacker(null);
		this.setAngryAt(null);
		this.setTarget(null);
		this.setAngerTime(0);
	}

	void setAttacker(@Nullable LivingEntity attacker);

	void setAttacking(@Nullable PlayerEntity attacking);

	void setTarget(@Nullable LivingEntity target);

	@Nullable
	LivingEntity getTarget();
}
