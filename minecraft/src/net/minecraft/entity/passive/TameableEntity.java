package net.minecraft.entity.passive;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public abstract class TameableEntity extends AnimalEntity {
	protected static final TrackedData<Byte> TAMEABLE_FLAGS = DataTracker.registerData(TameableEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(TameableEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	protected SitGoal sitGoal;

	protected TameableEntity(EntityType<? extends TameableEntity> entityType, World world) {
		super(entityType, world);
		this.onTamedChanged();
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(TAMEABLE_FLAGS, (byte)0);
		this.dataTracker.startTracking(OWNER_UUID, Optional.empty());
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		if (this.getOwnerUuid() == null) {
			compoundTag.putString("OwnerUUID", "");
		} else {
			compoundTag.putString("OwnerUUID", this.getOwnerUuid().toString());
		}

		compoundTag.putBoolean("Sitting", this.isSitting());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		String string;
		if (compoundTag.contains("OwnerUUID", 8)) {
			string = compoundTag.getString("OwnerUUID");
		} else {
			String string2 = compoundTag.getString("Owner");
			string = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string2);
		}

		if (!string.isEmpty()) {
			try {
				this.setOwnerUuid(UUID.fromString(string));
				this.setTamed(true);
			} catch (Throwable var4) {
				this.setTamed(false);
			}
		}

		if (this.sitGoal != null) {
			this.sitGoal.setEnabledWithOwner(compoundTag.getBoolean("Sitting"));
		}

		this.setSitting(compoundTag.getBoolean("Sitting"));
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity playerEntity) {
		return !this.isLeashed();
	}

	@Environment(EnvType.CLIENT)
	protected void showEmoteParticle(boolean bl) {
		ParticleEffect particleEffect = ParticleTypes.HEART;
		if (!bl) {
			particleEffect = ParticleTypes.SMOKE;
		}

		for (int i = 0; i < 7; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.world.addParticle(particleEffect, this.method_23322(1.0), this.method_23319() + 0.5, this.method_23325(1.0), d, e, f);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte b) {
		if (b == 7) {
			this.showEmoteParticle(true);
		} else if (b == 6) {
			this.showEmoteParticle(false);
		} else {
			super.handleStatus(b);
		}
	}

	public boolean isTamed() {
		return (this.dataTracker.get(TAMEABLE_FLAGS) & 4) != 0;
	}

	public void setTamed(boolean bl) {
		byte b = this.dataTracker.get(TAMEABLE_FLAGS);
		if (bl) {
			this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b | 4));
		} else {
			this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b & -5));
		}

		this.onTamedChanged();
	}

	protected void onTamedChanged() {
	}

	public boolean isSitting() {
		return (this.dataTracker.get(TAMEABLE_FLAGS) & 1) != 0;
	}

	public void setSitting(boolean bl) {
		byte b = this.dataTracker.get(TAMEABLE_FLAGS);
		if (bl) {
			this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b | 1));
		} else {
			this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b & -2));
		}
	}

	@Nullable
	public UUID getOwnerUuid() {
		return (UUID)this.dataTracker.get(OWNER_UUID).orElse(null);
	}

	public void setOwnerUuid(@Nullable UUID uUID) {
		this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uUID));
	}

	public void setOwner(PlayerEntity playerEntity) {
		this.setTamed(true);
		this.setOwnerUuid(playerEntity.getUuid());
		if (playerEntity instanceof ServerPlayerEntity) {
			Criterions.TAME_ANIMAL.trigger((ServerPlayerEntity)playerEntity, this);
		}
	}

	@Nullable
	public LivingEntity getOwner() {
		try {
			UUID uUID = this.getOwnerUuid();
			return uUID == null ? null : this.world.getPlayerByUuid(uUID);
		} catch (IllegalArgumentException var2) {
			return null;
		}
	}

	@Override
	public boolean canTarget(LivingEntity livingEntity) {
		return this.isOwner(livingEntity) ? false : super.canTarget(livingEntity);
	}

	public boolean isOwner(LivingEntity livingEntity) {
		return livingEntity == this.getOwner();
	}

	public SitGoal getSitGoal() {
		return this.sitGoal;
	}

	public boolean canAttackWithOwner(LivingEntity livingEntity, LivingEntity livingEntity2) {
		return true;
	}

	@Override
	public AbstractTeam getScoreboardTeam() {
		if (this.isTamed()) {
			LivingEntity livingEntity = this.getOwner();
			if (livingEntity != null) {
				return livingEntity.getScoreboardTeam();
			}
		}

		return super.getScoreboardTeam();
	}

	@Override
	public boolean isTeammate(Entity entity) {
		if (this.isTamed()) {
			LivingEntity livingEntity = this.getOwner();
			if (entity == livingEntity) {
				return true;
			}

			if (livingEntity != null) {
				return livingEntity.isTeammate(entity);
			}
		}

		return super.isTeammate(entity);
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		if (!this.world.isClient && this.world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES) && this.getOwner() instanceof ServerPlayerEntity) {
			this.getOwner().sendMessage(this.getDamageTracker().getDeathMessage());
		}

		super.onDeath(damageSource);
	}
}
