package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public record EntityFlagsPredicate(
	Optional<Boolean> isOnGround,
	Optional<Boolean> isOnFire,
	Optional<Boolean> isSneaking,
	Optional<Boolean> isSprinting,
	Optional<Boolean> isSwimming,
	Optional<Boolean> isFlying,
	Optional<Boolean> isBaby
) {
	public static final Codec<EntityFlagsPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.BOOL.optionalFieldOf("is_on_ground").forGetter(EntityFlagsPredicate::isOnGround),
					Codec.BOOL.optionalFieldOf("is_on_fire").forGetter(EntityFlagsPredicate::isOnFire),
					Codec.BOOL.optionalFieldOf("is_sneaking").forGetter(EntityFlagsPredicate::isSneaking),
					Codec.BOOL.optionalFieldOf("is_sprinting").forGetter(EntityFlagsPredicate::isSprinting),
					Codec.BOOL.optionalFieldOf("is_swimming").forGetter(EntityFlagsPredicate::isSwimming),
					Codec.BOOL.optionalFieldOf("is_flying").forGetter(EntityFlagsPredicate::isFlying),
					Codec.BOOL.optionalFieldOf("is_baby").forGetter(EntityFlagsPredicate::isBaby)
				)
				.apply(instance, EntityFlagsPredicate::new)
	);

	public boolean test(Entity entity) {
		if (this.isOnGround.isPresent() && entity.isOnGround() != (Boolean)this.isOnGround.get()) {
			return false;
		} else if (this.isOnFire.isPresent() && entity.isOnFire() != (Boolean)this.isOnFire.get()) {
			return false;
		} else if (this.isSneaking.isPresent() && entity.isInSneakingPose() != (Boolean)this.isSneaking.get()) {
			return false;
		} else if (this.isSprinting.isPresent() && entity.isSprinting() != (Boolean)this.isSprinting.get()) {
			return false;
		} else if (this.isSwimming.isPresent() && entity.isSwimming() != (Boolean)this.isSwimming.get()) {
			return false;
		} else {
			if (this.isFlying.isPresent()) {
				boolean var10000;
				label53: {
					if (entity instanceof LivingEntity livingEntity
						&& (livingEntity.isFallFlying() || livingEntity instanceof PlayerEntity playerEntity && playerEntity.getAbilities().flying)) {
						var10000 = true;
						break label53;
					}

					var10000 = false;
				}

				boolean bl = var10000;
				if (bl != (Boolean)this.isFlying.get()) {
					return false;
				}
			}

			if (this.isBaby.isPresent() && entity instanceof LivingEntity livingEntity2 && livingEntity2.isBaby() != (Boolean)this.isBaby.get()) {
				return false;
			}

			return true;
		}
	}

	public static class Builder {
		private Optional<Boolean> isOnGround = Optional.empty();
		private Optional<Boolean> isOnFire = Optional.empty();
		private Optional<Boolean> isSneaking = Optional.empty();
		private Optional<Boolean> isSprinting = Optional.empty();
		private Optional<Boolean> isSwimming = Optional.empty();
		private Optional<Boolean> isFlying = Optional.empty();
		private Optional<Boolean> isBaby = Optional.empty();

		public static EntityFlagsPredicate.Builder create() {
			return new EntityFlagsPredicate.Builder();
		}

		public EntityFlagsPredicate.Builder onGround(Boolean onGround) {
			this.isOnGround = Optional.of(onGround);
			return this;
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

		public EntityFlagsPredicate.Builder flying(Boolean flying) {
			this.isFlying = Optional.of(flying);
			return this;
		}

		public EntityFlagsPredicate.Builder isBaby(Boolean isBaby) {
			this.isBaby = Optional.of(isBaby);
			return this;
		}

		public EntityFlagsPredicate build() {
			return new EntityFlagsPredicate(this.isOnGround, this.isOnFire, this.isSneaking, this.isSprinting, this.isSwimming, this.isFlying, this.isBaby);
		}
	}
}
