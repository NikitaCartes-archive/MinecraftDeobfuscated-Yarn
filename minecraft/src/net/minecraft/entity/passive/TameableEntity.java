package net.minecraft.entity.passive;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1386;
import net.minecraft.class_3321;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.AbstractScoreboardTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public abstract class TameableEntity extends AnimalEntity implements OwnableEntity {
	protected static final TrackedData<Byte> TAMEABLE_FLAGS = DataTracker.registerData(TameableEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(TameableEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	protected class_1386 field_6321;

	protected TameableEntity(EntityType<?> entityType, World world) {
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
		if (compoundTag.containsKey("OwnerUUID", 8)) {
			string = compoundTag.getString("OwnerUUID");
		} else {
			String string2 = compoundTag.getString("Owner");
			string = class_3321.method_14546(this.getServer(), string2);
		}

		if (!string.isEmpty()) {
			try {
				this.setOwnerUuid(UUID.fromString(string));
				this.setTamed(true);
			} catch (Throwable var4) {
				this.setTamed(false);
			}
		}

		if (this.field_6321 != null) {
			this.field_6321.method_6311(compoundTag.getBoolean("Sitting"));
		}

		this.setSitting(compoundTag.getBoolean("Sitting"));
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity playerEntity) {
		return !this.isLeashed();
	}

	protected void method_6180(boolean bl) {
		ParticleParameters particleParameters = ParticleTypes.field_11201;
		if (!bl) {
			particleParameters = ParticleTypes.field_11251;
		}

		for (int i = 0; i < 7; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.world
				.method_8406(
					particleParameters,
					this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
					this.y + 0.5 + (double)(this.random.nextFloat() * this.height),
					this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
					d,
					e,
					f
				);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 7) {
			this.method_6180(true);
		} else if (b == 6) {
			this.method_6180(false);
		} else {
			super.method_5711(b);
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
	@Override
	public UUID getOwnerUuid() {
		return (UUID)this.dataTracker.get(OWNER_UUID).orElse(null);
	}

	public void setOwnerUuid(@Nullable UUID uUID) {
		this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uUID));
	}

	public void method_6170(PlayerEntity playerEntity) {
		this.setTamed(true);
		this.setOwnerUuid(playerEntity.getUuid());
		if (playerEntity instanceof ServerPlayerEntity) {
			Criterions.TAME_ANIMAL.handle((ServerPlayerEntity)playerEntity, this);
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

	public boolean isOwner(LivingEntity livingEntity) {
		return livingEntity == this.getOwner();
	}

	public class_1386 method_6176() {
		return this.field_6321;
	}

	public boolean method_6178(LivingEntity livingEntity, LivingEntity livingEntity2) {
		return true;
	}

	@Override
	public AbstractScoreboardTeam getScoreboardTeam() {
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
		if (!this.world.isClient && this.world.getGameRules().getBoolean("showDeathMessages") && this.getOwner() instanceof ServerPlayerEntity) {
			this.getOwner().appendCommandFeedback(this.getDamageTracker().getDeathMessage());
		}

		super.onDeath(damageSource);
	}
}
