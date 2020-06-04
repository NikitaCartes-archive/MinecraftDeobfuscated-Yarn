package net.minecraft;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public interface class_5354 {
	int method_29507();

	void method_29514(int i);

	@Nullable
	UUID method_29508();

	void method_29513(@Nullable UUID uUID);

	void method_29509();

	default void method_29517(CompoundTag compoundTag) {
		compoundTag.putInt("AngerTime", this.method_29507());
		if (this.method_29508() != null) {
			compoundTag.putUuid("AngryAt", this.method_29508());
		}
	}

	default void method_29512(World world, CompoundTag compoundTag) {
		this.method_29514(compoundTag.getInt("AngerTime"));
		if (compoundTag.containsUuid("AngryAt")) {
			this.method_29513(compoundTag.getUuid("AngryAt"));
			UUID uUID = this.method_29508();
			PlayerEntity playerEntity = uUID == null ? null : world.getPlayerByUuid(uUID);
			if (playerEntity != null) {
				this.setAttacker(playerEntity);
				this.method_29505(playerEntity);
			}
		}
	}

	default void method_29510() {
		LivingEntity livingEntity = this.getTarget();
		if (livingEntity != null && livingEntity.getType() == EntityType.PLAYER) {
			this.method_29513(livingEntity.getUuid());
			if (this.method_29507() <= 0) {
				this.method_29509();
			}
		} else {
			int i = this.method_29507();
			if (i > 0) {
				this.method_29514(i - 1);
				if (this.method_29507() == 0) {
					this.method_29513(null);
				}
			}
		}
	}

	default boolean method_29515(LivingEntity livingEntity) {
		if (livingEntity instanceof PlayerEntity && EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(livingEntity)) {
			boolean bl = livingEntity.world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER);
			return bl ? this.method_29511() : livingEntity.getUuid().equals(this.method_29508());
		} else {
			return false;
		}
	}

	default boolean method_29511() {
		return this.method_29507() > 0;
	}

	default void method_29516(PlayerEntity playerEntity) {
		if (!playerEntity.world.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS)) {
		}

		if (playerEntity.getUuid().equals(this.method_29508())) {
			this.setAttacker(null);
			this.method_29513(null);
			this.setTarget(null);
			this.method_29514(0);
		}
	}

	void setAttacker(@Nullable LivingEntity livingEntity);

	void method_29505(@Nullable PlayerEntity playerEntity);

	void setTarget(@Nullable LivingEntity livingEntity);

	@Nullable
	LivingEntity getTarget();
}
