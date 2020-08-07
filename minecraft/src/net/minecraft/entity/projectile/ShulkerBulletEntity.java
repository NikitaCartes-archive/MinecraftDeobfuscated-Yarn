package net.minecraft.entity.projectile;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class ShulkerBulletEntity extends ProjectileEntity {
	private Entity target;
	@Nullable
	private Direction direction;
	private int stepCount;
	private double targetX;
	private double targetY;
	private double targetZ;
	@Nullable
	private UUID targetUuid;

	public ShulkerBulletEntity(EntityType<? extends ShulkerBulletEntity> entityType, World world) {
		super(entityType, world);
		this.noClip = true;
	}

	@Environment(EnvType.CLIENT)
	public ShulkerBulletEntity(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		this(EntityType.field_6100, world);
		this.refreshPositionAndAngles(x, y, z, this.yaw, this.pitch);
		this.setVelocity(velocityX, velocityY, velocityZ);
	}

	public ShulkerBulletEntity(World world, LivingEntity owner, Entity target, Direction.Axis axis) {
		this(EntityType.field_6100, world);
		this.setOwner(owner);
		BlockPos blockPos = owner.getBlockPos();
		double d = (double)blockPos.getX() + 0.5;
		double e = (double)blockPos.getY() + 0.5;
		double f = (double)blockPos.getZ() + 0.5;
		this.refreshPositionAndAngles(d, e, f, this.yaw, this.pitch);
		this.target = target;
		this.direction = Direction.field_11036;
		this.method_7486(axis);
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.field_15251;
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		if (this.target != null) {
			tag.putUuid("Target", this.target.getUuid());
		}

		if (this.direction != null) {
			tag.putInt("Dir", this.direction.getId());
		}

		tag.putInt("Steps", this.stepCount);
		tag.putDouble("TXD", this.targetX);
		tag.putDouble("TYD", this.targetY);
		tag.putDouble("TZD", this.targetZ);
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.stepCount = tag.getInt("Steps");
		this.targetX = tag.getDouble("TXD");
		this.targetY = tag.getDouble("TYD");
		this.targetZ = tag.getDouble("TZD");
		if (tag.contains("Dir", 99)) {
			this.direction = Direction.byId(tag.getInt("Dir"));
		}

		if (tag.containsUuid("Target")) {
			this.targetUuid = tag.getUuid("Target");
		}
	}

	@Override
	protected void initDataTracker() {
	}

	private void setDirection(@Nullable Direction direction) {
		this.direction = direction;
	}

	private void method_7486(@Nullable Direction.Axis axis) {
		double d = 0.5;
		BlockPos blockPos;
		if (this.target == null) {
			blockPos = this.getBlockPos().method_10074();
		} else {
			d = (double)this.target.getHeight() * 0.5;
			blockPos = new BlockPos(this.target.getX(), this.target.getY() + d, this.target.getZ());
		}

		double e = (double)blockPos.getX() + 0.5;
		double f = (double)blockPos.getY() + d;
		double g = (double)blockPos.getZ() + 0.5;
		Direction direction = null;
		if (!blockPos.isWithinDistance(this.getPos(), 2.0)) {
			BlockPos blockPos2 = this.getBlockPos();
			List<Direction> list = Lists.<Direction>newArrayList();
			if (axis != Direction.Axis.field_11048) {
				if (blockPos2.getX() < blockPos.getX() && this.world.isAir(blockPos2.east())) {
					list.add(Direction.field_11034);
				} else if (blockPos2.getX() > blockPos.getX() && this.world.isAir(blockPos2.west())) {
					list.add(Direction.field_11039);
				}
			}

			if (axis != Direction.Axis.field_11052) {
				if (blockPos2.getY() < blockPos.getY() && this.world.isAir(blockPos2.up())) {
					list.add(Direction.field_11036);
				} else if (blockPos2.getY() > blockPos.getY() && this.world.isAir(blockPos2.method_10074())) {
					list.add(Direction.field_11033);
				}
			}

			if (axis != Direction.Axis.field_11051) {
				if (blockPos2.getZ() < blockPos.getZ() && this.world.isAir(blockPos2.south())) {
					list.add(Direction.field_11035);
				} else if (blockPos2.getZ() > blockPos.getZ() && this.world.isAir(blockPos2.north())) {
					list.add(Direction.field_11043);
				}
			}

			direction = Direction.random(this.random);
			if (list.isEmpty()) {
				for (int i = 5; !this.world.isAir(blockPos2.offset(direction)) && i > 0; i--) {
					direction = Direction.random(this.random);
				}
			} else {
				direction = (Direction)list.get(this.random.nextInt(list.size()));
			}

			e = this.getX() + (double)direction.getOffsetX();
			f = this.getY() + (double)direction.getOffsetY();
			g = this.getZ() + (double)direction.getOffsetZ();
		}

		this.setDirection(direction);
		double h = e - this.getX();
		double j = f - this.getY();
		double k = g - this.getZ();
		double l = (double)MathHelper.sqrt(h * h + j * j + k * k);
		if (l == 0.0) {
			this.targetX = 0.0;
			this.targetY = 0.0;
			this.targetZ = 0.0;
		} else {
			this.targetX = h / l * 0.15;
			this.targetY = j / l * 0.15;
			this.targetZ = k / l * 0.15;
		}

		this.velocityDirty = true;
		this.stepCount = 10 + this.random.nextInt(5) * 10;
	}

	@Override
	public void checkDespawn() {
		if (this.world.getDifficulty() == Difficulty.field_5801) {
			this.remove();
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.world.isClient) {
			if (this.target == null && this.targetUuid != null) {
				this.target = ((ServerWorld)this.world).getEntity(this.targetUuid);
				if (this.target == null) {
					this.targetUuid = null;
				}
			}

			if (this.target == null || !this.target.isAlive() || this.target instanceof PlayerEntity && ((PlayerEntity)this.target).isSpectator()) {
				if (!this.hasNoGravity()) {
					this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
				}
			} else {
				this.targetX = MathHelper.clamp(this.targetX * 1.025, -1.0, 1.0);
				this.targetY = MathHelper.clamp(this.targetY * 1.025, -1.0, 1.0);
				this.targetZ = MathHelper.clamp(this.targetZ * 1.025, -1.0, 1.0);
				Vec3d vec3d = this.getVelocity();
				this.setVelocity(vec3d.add((this.targetX - vec3d.x) * 0.2, (this.targetY - vec3d.y) * 0.2, (this.targetZ - vec3d.z) * 0.2));
			}

			HitResult hitResult = ProjectileUtil.getCollision(this, this::method_26958);
			if (hitResult.getType() != HitResult.Type.field_1333) {
				this.onCollision(hitResult);
			}
		}

		this.checkBlockCollision();
		Vec3d vec3d = this.getVelocity();
		this.updatePosition(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z);
		ProjectileUtil.method_7484(this, 0.5F);
		if (this.world.isClient) {
			this.world.addParticle(ParticleTypes.field_11207, this.getX() - vec3d.x, this.getY() - vec3d.y + 0.15, this.getZ() - vec3d.z, 0.0, 0.0, 0.0);
		} else if (this.target != null && !this.target.removed) {
			if (this.stepCount > 0) {
				this.stepCount--;
				if (this.stepCount == 0) {
					this.method_7486(this.direction == null ? null : this.direction.getAxis());
				}
			}

			if (this.direction != null) {
				BlockPos blockPos = this.getBlockPos();
				Direction.Axis axis = this.direction.getAxis();
				if (this.world.isTopSolid(blockPos.offset(this.direction), this)) {
					this.method_7486(axis);
				} else {
					BlockPos blockPos2 = this.target.getBlockPos();
					if (axis == Direction.Axis.field_11048 && blockPos.getX() == blockPos2.getX()
						|| axis == Direction.Axis.field_11051 && blockPos.getZ() == blockPos2.getZ()
						|| axis == Direction.Axis.field_11052 && blockPos.getY() == blockPos2.getY()) {
						this.method_7486(axis);
					}
				}
			}
		}
	}

	@Override
	protected boolean method_26958(Entity entity) {
		return super.method_26958(entity) && !entity.noClip;
	}

	@Override
	public boolean isOnFire() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRender(double distance) {
		return distance < 16384.0;
	}

	@Override
	public float getBrightnessAtEyes() {
		return 1.0F;
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);
		Entity entity = entityHitResult.getEntity();
		Entity entity2 = this.getOwner();
		LivingEntity livingEntity = entity2 instanceof LivingEntity ? (LivingEntity)entity2 : null;
		boolean bl = entity.damage(DamageSource.mobProjectile(this, livingEntity).setProjectile(), 4.0F);
		if (bl) {
			this.dealDamage(livingEntity, entity);
			if (entity instanceof LivingEntity) {
				((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.field_5902, 200));
			}
		}
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		super.onBlockHit(blockHitResult);
		((ServerWorld)this.world).spawnParticles(ParticleTypes.field_11236, this.getX(), this.getY(), this.getZ(), 2, 0.2, 0.2, 0.2, 0.0);
		this.playSound(SoundEvents.field_14895, 1.0F, 1.0F);
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		this.remove();
	}

	@Override
	public boolean collides() {
		return true;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (!this.world.isClient) {
			this.playSound(SoundEvents.field_14977, 1.0F, 1.0F);
			((ServerWorld)this.world).spawnParticles(ParticleTypes.field_11205, this.getX(), this.getY(), this.getZ(), 15, 0.2, 0.2, 0.2, 0.0);
			this.remove();
		}

		return true;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}
}
