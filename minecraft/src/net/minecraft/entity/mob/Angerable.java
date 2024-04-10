package net.minecraft.entity.mob;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public interface Angerable {
	String ANGER_TIME_KEY = "AngerTime";
	String ANGRY_AT_KEY = "AngryAt";

	int getAngerTime();

	void setAngerTime(int angerTime);

	@Nullable
	UUID getAngryAt();

	void setAngryAt(@Nullable UUID angryAt);

	void chooseRandomAngerTime();

	default void writeAngerToNbt(NbtCompound nbt) {
		nbt.putInt("AngerTime", this.getAngerTime());
		if (this.getAngryAt() != null) {
			nbt.putUuid("AngryAt", this.getAngryAt());
		}
	}

	default void readAngerFromNbt(World world, NbtCompound nbt) {
		this.setAngerTime(nbt.getInt("AngerTime"));
		if (world instanceof ServerWorld) {
			if (!nbt.containsUuid("AngryAt")) {
				this.setAngryAt(null);
			} else {
				UUID uUID = nbt.getUuid("AngryAt");
				this.setAngryAt(uUID);
				Entity entity = ((ServerWorld)world).getEntity(uUID);
				if (entity != null) {
					if (entity instanceof MobEntity mobEntity) {
						this.setTarget(mobEntity);
						this.setAttacker(mobEntity);
					}

					if (entity instanceof PlayerEntity playerEntity) {
						this.setTarget(playerEntity);
						this.setAttacking(playerEntity);
					}
				}
			}
		}
	}

	/**
	 * @param angerPersistent if {@code true}, the anger time will not decrease for a player target
	 */
	default void tickAngerLogic(ServerWorld world, boolean angerPersistent) {
		LivingEntity livingEntity = this.getTarget();
		UUID uUID = this.getAngryAt();
		if ((livingEntity == null || livingEntity.isDead()) && uUID != null && world.getEntity(uUID) instanceof MobEntity) {
			this.stopAnger();
		} else {
			if (livingEntity != null && !Objects.equals(uUID, livingEntity.getUuid())) {
				this.setAngryAt(livingEntity.getUuid());
				this.chooseRandomAngerTime();
			}

			if (this.getAngerTime() > 0 && (livingEntity == null || livingEntity.getType() != EntityType.PLAYER || !angerPersistent)) {
				this.setAngerTime(this.getAngerTime() - 1);
				if (this.getAngerTime() == 0) {
					this.stopAnger();
				}
			}
		}
	}

	default boolean shouldAngerAt(LivingEntity entity) {
		if (!this.canTarget(entity)) {
			return false;
		} else {
			return entity.getType() == EntityType.PLAYER && this.isUniversallyAngry(entity.getWorld()) ? true : entity.getUuid().equals(this.getAngryAt());
		}
	}

	default boolean isUniversallyAngry(World world) {
		return world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER) && this.hasAngerTime() && this.getAngryAt() == null;
	}

	default boolean hasAngerTime() {
		return this.getAngerTime() > 0;
	}

	default void forgive(PlayerEntity player) {
		if (player.getWorld().getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS)) {
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

	@Nullable
	LivingEntity getAttacker();

	void setAttacker(@Nullable LivingEntity attacker);

	void setAttacking(@Nullable PlayerEntity attacking);

	void setTarget(@Nullable LivingEntity target);

	boolean canTarget(LivingEntity target);

	@Nullable
	LivingEntity getTarget();
}
