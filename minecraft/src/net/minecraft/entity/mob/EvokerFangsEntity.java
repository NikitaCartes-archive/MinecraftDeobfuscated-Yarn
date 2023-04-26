package net.minecraft.entity.mob;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class EvokerFangsEntity extends Entity implements Ownable {
	public static final int field_30662 = 20;
	public static final int field_30663 = 2;
	public static final int field_30664 = 14;
	private int warmup;
	private boolean startedAttack;
	private int ticksLeft = 22;
	private boolean playingAnimation;
	@Nullable
	private LivingEntity owner;
	@Nullable
	private UUID ownerUuid;

	public EvokerFangsEntity(EntityType<? extends EvokerFangsEntity> entityType, World world) {
		super(entityType, world);
	}

	public EvokerFangsEntity(World world, double x, double y, double z, float yaw, int warmup, LivingEntity owner) {
		this(EntityType.EVOKER_FANGS, world);
		this.warmup = warmup;
		this.setOwner(owner);
		this.setYaw(yaw * (180.0F / (float)Math.PI));
		this.setPosition(x, y, z);
	}

	@Override
	protected void initDataTracker() {
	}

	public void setOwner(@Nullable LivingEntity owner) {
		this.owner = owner;
		this.ownerUuid = owner == null ? null : owner.getUuid();
	}

	@Nullable
	public LivingEntity getOwner() {
		if (this.owner == null && this.ownerUuid != null && this.getWorld() instanceof ServerWorld) {
			Entity entity = ((ServerWorld)this.getWorld()).getEntity(this.ownerUuid);
			if (entity instanceof LivingEntity) {
				this.owner = (LivingEntity)entity;
			}
		}

		return this.owner;
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		this.warmup = nbt.getInt("Warmup");
		if (nbt.containsUuid("Owner")) {
			this.ownerUuid = nbt.getUuid("Owner");
		}
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putInt("Warmup", this.warmup);
		if (this.ownerUuid != null) {
			nbt.putUuid("Owner", this.ownerUuid);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.getWorld().isClient) {
			if (this.playingAnimation) {
				this.ticksLeft--;
				if (this.ticksLeft == 14) {
					for (int i = 0; i < 12; i++) {
						double d = this.getX() + (this.random.nextDouble() * 2.0 - 1.0) * (double)this.getWidth() * 0.5;
						double e = this.getY() + 0.05 + this.random.nextDouble();
						double f = this.getZ() + (this.random.nextDouble() * 2.0 - 1.0) * (double)this.getWidth() * 0.5;
						double g = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
						double h = 0.3 + this.random.nextDouble() * 0.3;
						double j = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
						this.getWorld().addParticle(ParticleTypes.CRIT, d, e + 1.0, f, g, h, j);
					}
				}
			}
		} else if (--this.warmup < 0) {
			if (this.warmup == -8) {
				for (LivingEntity livingEntity : this.getWorld().getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox().expand(0.2, 0.0, 0.2))) {
					this.damage(livingEntity);
				}
			}

			if (!this.startedAttack) {
				this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND);
				this.startedAttack = true;
			}

			if (--this.ticksLeft < 0) {
				this.discard();
			}
		}
	}

	private void damage(LivingEntity target) {
		LivingEntity livingEntity = this.getOwner();
		if (target.isAlive() && !target.isInvulnerable() && target != livingEntity) {
			if (livingEntity == null) {
				target.damage(this.getDamageSources().magic(), 6.0F);
			} else {
				if (livingEntity.isTeammate(target)) {
					return;
				}

				target.damage(this.getDamageSources().indirectMagic(this, livingEntity), 6.0F);
			}
		}
	}

	@Override
	public void handleStatus(byte status) {
		super.handleStatus(status);
		if (status == EntityStatuses.PLAY_ATTACK_SOUND) {
			this.playingAnimation = true;
			if (!this.isSilent()) {
				this.getWorld()
					.playSound(
						this.getX(),
						this.getY(),
						this.getZ(),
						SoundEvents.ENTITY_EVOKER_FANGS_ATTACK,
						this.getSoundCategory(),
						1.0F,
						this.random.nextFloat() * 0.2F + 0.85F,
						false
					);
			}
		}
	}

	public float getAnimationProgress(float tickDelta) {
		if (!this.playingAnimation) {
			return 0.0F;
		} else {
			int i = this.ticksLeft - 2;
			return i <= 0 ? 1.0F : 1.0F - ((float)i - tickDelta) / 20.0F;
		}
	}
}
