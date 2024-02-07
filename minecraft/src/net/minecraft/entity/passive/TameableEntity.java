package net.minecraft.entity.passive;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public abstract class TameableEntity extends AnimalEntity implements Tameable {
	/**
	 * The tracked flags of tameable entities. Has the {@code 1} flag for {@linkplain
	 * #isInSittingPose() sitting pose} and the {@code 4} flag for {@linkplain
	 * #isTamed() tamed}.
	 */
	protected static final TrackedData<Byte> TAMEABLE_FLAGS = DataTracker.registerData(TameableEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(TameableEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	private boolean sitting;

	protected TameableEntity(EntityType<? extends TameableEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(TAMEABLE_FLAGS, (byte)0);
		builder.add(OWNER_UUID, Optional.empty());
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		if (this.getOwnerUuid() != null) {
			nbt.putUuid("Owner", this.getOwnerUuid());
		}

		nbt.putBoolean("Sitting", this.sitting);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		UUID uUID;
		if (nbt.containsUuid("Owner")) {
			uUID = nbt.getUuid("Owner");
		} else {
			String string = nbt.getString("Owner");
			uUID = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string);
		}

		if (uUID != null) {
			try {
				this.setOwnerUuid(uUID);
				this.setTamed(true, false);
			} catch (Throwable var4) {
				this.setTamed(false, true);
			}
		}

		this.sitting = nbt.getBoolean("Sitting");
		this.setInSittingPose(this.sitting);
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity player) {
		return !this.isLeashed();
	}

	protected void showEmoteParticle(boolean positive) {
		ParticleEffect particleEffect = ParticleTypes.HEART;
		if (!positive) {
			particleEffect = ParticleTypes.SMOKE;
		}

		for (int i = 0; i < 7; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.getWorld().addParticle(particleEffect, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES) {
			this.showEmoteParticle(true);
		} else if (status == EntityStatuses.ADD_NEGATIVE_PLAYER_REACTION_PARTICLES) {
			this.showEmoteParticle(false);
		} else {
			super.handleStatus(status);
		}
	}

	public boolean isTamed() {
		return (this.dataTracker.get(TAMEABLE_FLAGS) & 4) != 0;
	}

	public void setTamed(boolean tamed, boolean updateAttributes) {
		byte b = this.dataTracker.get(TAMEABLE_FLAGS);
		if (tamed) {
			this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b | 4));
		} else {
			this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b & -5));
		}

		if (updateAttributes) {
			this.updateAttributesForTamed();
		}
	}

	protected void updateAttributesForTamed() {
	}

	public boolean isInSittingPose() {
		return (this.dataTracker.get(TAMEABLE_FLAGS) & 1) != 0;
	}

	public void setInSittingPose(boolean inSittingPose) {
		byte b = this.dataTracker.get(TAMEABLE_FLAGS);
		if (inSittingPose) {
			this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b | 1));
		} else {
			this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b & -2));
		}
	}

	@Nullable
	@Override
	public UUID getOwnerUuid() {
		return (UUID)this.dataTracker.get(OWNER_UUID).orElse(null);
	}

	public void setOwnerUuid(@Nullable UUID uuid) {
		this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uuid));
	}

	public void setOwner(PlayerEntity player) {
		this.setTamed(true, true);
		this.setOwnerUuid(player.getUuid());
		if (player instanceof ServerPlayerEntity) {
			Criteria.TAME_ANIMAL.trigger((ServerPlayerEntity)player, this);
		}
	}

	@Override
	public boolean canTarget(LivingEntity target) {
		return this.isOwner(target) ? false : super.canTarget(target);
	}

	public boolean isOwner(LivingEntity entity) {
		return entity == this.getOwner();
	}

	public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
		return true;
	}

	@Override
	public Team getScoreboardTeam() {
		if (this.isTamed()) {
			LivingEntity livingEntity = this.getOwner();
			if (livingEntity != null) {
				return livingEntity.getScoreboardTeam();
			}
		}

		return super.getScoreboardTeam();
	}

	@Override
	public boolean isTeammate(Entity other) {
		if (this.isTamed()) {
			LivingEntity livingEntity = this.getOwner();
			if (other == livingEntity) {
				return true;
			}

			if (livingEntity != null) {
				return livingEntity.isTeammate(other);
			}
		}

		return super.isTeammate(other);
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		if (!this.getWorld().isClient && this.getWorld().getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES) && this.getOwner() instanceof ServerPlayerEntity) {
			this.getOwner().sendMessage(this.getDamageTracker().getDeathMessage());
		}

		super.onDeath(damageSource);
	}

	public boolean isSitting() {
		return this.sitting;
	}

	public void setSitting(boolean sitting) {
		this.sitting = sitting;
	}
}
