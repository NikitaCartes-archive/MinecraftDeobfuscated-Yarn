package net.minecraft.entity.mob;

import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class EvokerFangsEntity extends Entity {
	private int warmup;
	private boolean field_7610;
	private int field_7607 = 22;
	private boolean field_7608;
	private LivingEntity owner;
	private UUID ownerUuid;

	public EvokerFangsEntity(EntityType<? extends EvokerFangsEntity> entityType, World world) {
		super(entityType, world);
	}

	public EvokerFangsEntity(World world, double d, double e, double f, float g, int i, LivingEntity livingEntity) {
		this(EntityType.EVOKER_FANGS, world);
		this.warmup = i;
		this.setOwner(livingEntity);
		this.yaw = g * (180.0F / (float)Math.PI);
		this.setPosition(d, e, f);
	}

	@Override
	protected void initDataTracker() {
	}

	public void setOwner(@Nullable LivingEntity livingEntity) {
		this.owner = livingEntity;
		this.ownerUuid = livingEntity == null ? null : livingEntity.getUuid();
	}

	@Nullable
	public LivingEntity getOwner() {
		if (this.owner == null && this.ownerUuid != null && this.world instanceof ServerWorld) {
			Entity entity = ((ServerWorld)this.world).getEntity(this.ownerUuid);
			if (entity instanceof LivingEntity) {
				this.owner = (LivingEntity)entity;
			}
		}

		return this.owner;
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag compoundTag) {
		this.warmup = compoundTag.getInt("Warmup");
		if (compoundTag.hasUuid("OwnerUUID")) {
			this.ownerUuid = compoundTag.getUuid("OwnerUUID");
		}
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag compoundTag) {
		compoundTag.putInt("Warmup", this.warmup);
		if (this.ownerUuid != null) {
			compoundTag.putUuid("OwnerUUID", this.ownerUuid);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.world.isClient) {
			if (this.field_7608) {
				this.field_7607--;
				if (this.field_7607 == 14) {
					for (int i = 0; i < 12; i++) {
						double d = this.x + (this.random.nextDouble() * 2.0 - 1.0) * (double)this.getWidth() * 0.5;
						double e = this.y + 0.05 + this.random.nextDouble();
						double f = this.z + (this.random.nextDouble() * 2.0 - 1.0) * (double)this.getWidth() * 0.5;
						double g = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
						double h = 0.3 + this.random.nextDouble() * 0.3;
						double j = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
						this.world.addParticle(ParticleTypes.field_11205, d, e + 1.0, f, g, h, j);
					}
				}
			}
		} else if (--this.warmup < 0) {
			if (this.warmup == -8) {
				for (LivingEntity livingEntity : this.world.method_18467(LivingEntity.class, this.getBoundingBox().expand(0.2, 0.0, 0.2))) {
					this.method_7471(livingEntity);
				}
			}

			if (!this.field_7610) {
				this.world.summonParticle(this, (byte)4);
				this.field_7610 = true;
			}

			if (--this.field_7607 < 0) {
				this.invalidate();
			}
		}
	}

	private void method_7471(LivingEntity livingEntity) {
		LivingEntity livingEntity2 = this.getOwner();
		if (livingEntity.isValid() && !livingEntity.isInvulnerable() && livingEntity != livingEntity2) {
			if (livingEntity2 == null) {
				livingEntity.damage(DamageSource.MAGIC, 6.0F);
			} else {
				if (livingEntity2.isTeammate(livingEntity)) {
					return;
				}

				livingEntity.damage(DamageSource.magic(this, livingEntity2), 6.0F);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		super.method_5711(b);
		if (b == 4) {
			this.field_7608 = true;
			if (!this.isSilent()) {
				this.world.playSound(this.x, this.y, this.z, SoundEvents.field_14692, this.getSoundCategory(), 1.0F, this.random.nextFloat() * 0.2F + 0.85F, false);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public float method_7472(float f) {
		if (!this.field_7608) {
			return 0.0F;
		} else {
			int i = this.field_7607 - 2;
			return i <= 0 ? 1.0F : 1.0F - ((float)i - f) / 20.0F;
		}
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}
}
