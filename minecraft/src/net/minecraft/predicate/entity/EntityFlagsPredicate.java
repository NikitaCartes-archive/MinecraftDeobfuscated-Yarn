package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public record EntityFlagsPredicate(
	Optional<Boolean> isOnFire, Optional<Boolean> isSneaking, Optional<Boolean> isSprinting, Optional<Boolean> isSwimming, Optional<Boolean> isBaby
) {
	public static final Codec<EntityFlagsPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.BOOL.optionalFieldOf("is_on_fire").forGetter(EntityFlagsPredicate::isOnFire),
					Codec.BOOL.optionalFieldOf("is_sneaking").forGetter(EntityFlagsPredicate::isSneaking),
					Codec.BOOL.optionalFieldOf("is_sprinting").forGetter(EntityFlagsPredicate::isSprinting),
					Codec.BOOL.optionalFieldOf("is_swimming").forGetter(EntityFlagsPredicate::isSwimming),
					Codec.BOOL.optionalFieldOf("is_baby").forGetter(EntityFlagsPredicate::isBaby)
				)
				.apply(instance, EntityFlagsPredicate::new)
	);

	public boolean test(Entity entity) {
		if (this.isOnFire.isPresent() && entity.isOnFire() != (Boolean)this.isOnFire.get()) {
			return false;
		} else if (this.isSneaking.isPresent() && entity.isInSneakingPose() != (Boolean)this.isSneaking.get()) {
			return false;
		} else if (this.isSprinting.isPresent() && entity.isSprinting() != (Boolean)this.isSprinting.get()) {
			return false;
		} else if (this.isSwimming.isPresent() && entity.isSwimming() != (Boolean)this.isSwimming.get()) {
			return false;
		} else {
			if (this.isBaby.isPresent() && entity instanceof LivingEntity livingEntity && livingEntity.isBaby() != (Boolean)this.isBaby.get()) {
				return false;
			}

			return true;
		}
	}

	public static class Builder {
		private Optional<Boolean> isOnFire = Optional.empty();
		private Optional<Boolean> isSneaking = Optional.empty();
		private Optional<Boolean> isSprinting = Optional.empty();
		private Optional<Boolean> isSwimming = Optional.empty();
		private Optional<Boolean> isBaby = Optional.empty();

		public static EntityFlagsPredicate.Builder create() {
			return new EntityFlagsPredicate.Builder();
		}

		public EntityFlagsPredicate.Builder onFire(Boolean onFire) {
			this.isOnFire = Optional.of(onFire);
			return this;
		}

		public EntityFlagsPredicate.Builder sneaking(Boolean sneaking) {
			this.isSneaking = Optional.of(sneaking);
			return this;
		}

		public EntityFlagsPredicate.Builder sprinting(Boolean sprinting) {
			this.isSprinting = Optional.of(sprinting);
			return this;
		}

		public EntityFlagsPredicate.Builder swimming(Boolean swimming) {
			this.isSwimming = Optional.of(swimming);
			return this;
		}

		public EntityFlagsPredicate.Builder isBaby(Boolean isBaby) {
			this.isBaby = Optional.of(isBaby);
			return this;
		}

		public EntityFlagsPredicate build() {
			return new EntityFlagsPredicate(this.isOnFire, this.isSneaking, this.isSprinting, this.isSwimming, this.isBaby);
		}
	}
}
