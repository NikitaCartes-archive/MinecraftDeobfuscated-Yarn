package net.minecraft.entity.passive;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1386;
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
import net.minecraft.server.config.ServerConfigHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public abstract class TameableEntity extends AnimalEntity {
	protected static final TrackedData<Byte> field_6322 = DataTracker.registerData(TameableEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Optional<UUID>> field_6320 = DataTracker.registerData(TameableEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	protected class_1386 field_6321;

	protected TameableEntity(EntityType<? extends TameableEntity> entityType, World world) {
		super(entityType, world);
		this.onTamedChanged();
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_6322, (byte)0);
		this.field_6011.startTracking(field_6320, Optional.empty());
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		if (this.method_6139() == null) {
			compoundTag.putString("OwnerUUID", "");
		} else {
			compoundTag.putString("OwnerUUID", this.method_6139().toString());
		}

		compoundTag.putBoolean("Sitting", this.isSitting());
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		String string;
		if (compoundTag.containsKey("OwnerUUID", 8)) {
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

		if (this.field_6321 != null) {
			this.field_6321.method_6311(compoundTag.getBoolean("Sitting"));
		}

		this.setSitting(compoundTag.getBoolean("Sitting"));
	}

	@Override
	public boolean method_5931(PlayerEntity playerEntity) {
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
			this.field_6002
				.method_8406(
					particleParameters,
					this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					this.y + 0.5 + (double)(this.random.nextFloat() * this.getHeight()),
					this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
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
		return (this.field_6011.get(field_6322) & 4) != 0;
	}

	public void setTamed(boolean bl) {
		byte b = this.field_6011.get(field_6322);
		if (bl) {
			this.field_6011.set(field_6322, (byte)(b | 4));
		} else {
			this.field_6011.set(field_6322, (byte)(b & -5));
		}

		this.onTamedChanged();
	}

	protected void onTamedChanged() {
	}

	public boolean isSitting() {
		return (this.field_6011.get(field_6322) & 1) != 0;
	}

	public void setSitting(boolean bl) {
		byte b = this.field_6011.get(field_6322);
		if (bl) {
			this.field_6011.set(field_6322, (byte)(b | 1));
		} else {
			this.field_6011.set(field_6322, (byte)(b & -2));
		}
	}

	@Nullable
	public UUID method_6139() {
		return (UUID)this.field_6011.get(field_6320).orElse(null);
	}

	public void setOwnerUuid(@Nullable UUID uUID) {
		this.field_6011.set(field_6320, Optional.ofNullable(uUID));
	}

	public void method_6170(PlayerEntity playerEntity) {
		this.setTamed(true);
		this.setOwnerUuid(playerEntity.getUuid());
		if (playerEntity instanceof ServerPlayerEntity) {
			Criterions.TAME_ANIMAL.method_9132((ServerPlayerEntity)playerEntity, this);
		}
	}

	@Nullable
	public LivingEntity getOwner() {
		try {
			UUID uUID = this.method_6139();
			return uUID == null ? null : this.field_6002.method_18470(uUID);
		} catch (IllegalArgumentException var2) {
			return null;
		}
	}

	@Override
	public boolean method_18395(LivingEntity livingEntity) {
		return this.isOwner(livingEntity) ? false : super.method_18395(livingEntity);
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
	public AbstractScoreboardTeam method_5781() {
		if (this.isTamed()) {
			LivingEntity livingEntity = this.getOwner();
			if (livingEntity != null) {
				return livingEntity.method_5781();
			}
		}

		return super.method_5781();
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
		if (!this.field_6002.isClient && this.field_6002.getGameRules().getBoolean("showDeathMessages") && this.getOwner() instanceof ServerPlayerEntity) {
			this.getOwner().method_9203(this.getDamageTracker().method_5548());
		}

		super.onDeath(damageSource);
	}
}
