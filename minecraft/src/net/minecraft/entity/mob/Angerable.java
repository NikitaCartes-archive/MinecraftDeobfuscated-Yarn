package net.minecraft.entity.mob;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
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

	default void angerFromTag(World world, CompoundTag tag) {
		this.setAngerTime(tag.getInt("AngerTime"));
		if (tag.containsUuid("AngryAt")) {
			this.setAngryAt(tag.getUuid("AngryAt"));
			UUID uUID = this.getAngryAt();
			PlayerEntity playerEntity = uUID == null ? null : world.getPlayerByUuid(uUID);
			if (playerEntity != null) {
				this.setAttacker(playerEntity);
				this.method_29505(playerEntity);
			}
		}
	}

	default void tickAngerLogic() {
		LivingEntity livingEntity = this.getTarget();
		if (livingEntity != null && livingEntity.getType() == EntityType.PLAYER) {
			this.setAngryAt(livingEntity.getUuid());
			if (this.getAngerTime() <= 0) {
				this.chooseRandomAngerTime();
			}
		} else {
			int i = this.getAngerTime();
			if (i > 0) {
				this.setAngerTime(i - 1);
				if (this.getAngerTime() == 0) {
					this.setAngryAt(null);
				}
			}
		}
	}

	default boolean shouldAngerAt(LivingEntity entity) {
		if (entity instanceof PlayerEntity && EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(entity)) {
			boolean bl = entity.world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER);
			return bl ? this.hasAngerTime() : entity.getUuid().equals(this.getAngryAt());
		} else {
			return false;
		}
	}

	default boolean hasAngerTime() {
		return this.getAngerTime() > 0;
	}

	default void forgive(PlayerEntity player) {
		if (!player.world.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS)) {
		}

		if (player.getUuid().equals(this.getAngryAt())) {
			this.setAttacker(null);
			this.setAngryAt(null);
			this.setTarget(null);
			this.setAngerTime(0);
		}
	}

	void setAttacker(@Nullable LivingEntity attacker);

	void method_29505(@Nullable PlayerEntity playerEntity);

	void setTarget(@Nullable LivingEntity target);

	@Nullable
	LivingEntity getTarget();
}
