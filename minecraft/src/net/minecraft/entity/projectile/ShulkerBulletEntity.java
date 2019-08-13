package net.minecraft.entity.projectile;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TagHelper;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class ShulkerBulletEntity extends Entity {
	private LivingEntity owner;
	private Entity target;
	@Nullable
	private Direction direction;
	private int field_7627;
	private double field_7635;
	private double field_7633;
	private double field_7625;
	@Nullable
	private UUID ownerUuid;
	private BlockPos ownerPos;
	@Nullable
	private UUID targetUuid;
	private BlockPos targetPos;

	public ShulkerBulletEntity(EntityType<? extends ShulkerBulletEntity> entityType, World world) {
		super(entityType, world);
		this.noClip = true;
	}

	@Environment(EnvType.CLIENT)
	public ShulkerBulletEntity(World world, double d, double e, double f, double g, double h, double i) {
		this(EntityType.field_6100, world);
		this.setPositionAndAngles(d, e, f, this.yaw, this.pitch);
		this.setVelocity(g, h, i);
	}

	public ShulkerBulletEntity(World world, LivingEntity livingEntity, Entity entity, Direction.Axis axis) {
		this(EntityType.field_6100, world);
		this.owner = livingEntity;
		BlockPos blockPos = new BlockPos(livingEntity);
		double d = (double)blockPos.getX() + 0.5;
		double e = (double)blockPos.getY() + 0.5;
		double f = (double)blockPos.getZ() + 0.5;
		this.setPositionAndAngles(d, e, f, this.yaw, this.pitch);
		this.target = entity;
		this.direction = Direction.field_11036;
		this.method_7486(axis);
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.field_15251;
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag compoundTag) {
		if (this.owner != null) {
			BlockPos blockPos = new BlockPos(this.owner);
			CompoundTag compoundTag2 = TagHelper.serializeUuid(this.owner.getUuid());
			compoundTag2.putInt("X", blockPos.getX());
			compoundTag2.putInt("Y", blockPos.getY());
			compoundTag2.putInt("Z", blockPos.getZ());
			compoundTag.put("Owner", compoundTag2);
		}

		if (this.target != null) {
			BlockPos blockPos = new BlockPos(this.target);
			CompoundTag compoundTag2 = TagHelper.serializeUuid(this.target.getUuid());
			compoundTag2.putInt("X", blockPos.getX());
			compoundTag2.putInt("Y", blockPos.getY());
			compoundTag2.putInt("Z", blockPos.getZ());
			compoundTag.put("Target", compoundTag2);
		}

		if (this.direction != null) {
			compoundTag.putInt("Dir", this.direction.getId());
		}

		compoundTag.putInt("Steps", this.field_7627);
		compoundTag.putDouble("TXD", this.field_7635);
		compoundTag.putDouble("TYD", this.field_7633);
		compoundTag.putDouble("TZD", this.field_7625);
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag compoundTag) {
		this.field_7627 = compoundTag.getInt("Steps");
		this.field_7635 = compoundTag.getDouble("TXD");
		this.field_7633 = compoundTag.getDouble("TYD");
		this.field_7625 = compoundTag.getDouble("TZD");
		if (compoundTag.containsKey("Dir", 99)) {
			this.direction = Direction.byId(compoundTag.getInt("Dir"));
		}

		if (compoundTag.containsKey("Owner", 10)) {
			CompoundTag compoundTag2 = compoundTag.getCompound("Owner");
			this.ownerUuid = TagHelper.deserializeUuid(compoundTag2);
			this.ownerPos = new BlockPos(compoundTag2.getInt("X"), compoundTag2.getInt("Y"), compoundTag2.getInt("Z"));
		}

		if (compoundTag.containsKey("Target", 10)) {
			CompoundTag compoundTag2 = compoundTag.getCompound("Target");
			this.targetUuid = TagHelper.deserializeUuid(compoundTag2);
			this.targetPos = new BlockPos(compoundTag2.getInt("X"), compoundTag2.getInt("Y"), compoundTag2.getInt("Z"));
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
			blockPos = new BlockPos(this).down();
		} else {
			d = (double)this.target.getHeight() * 0.5;
			blockPos = new BlockPos(this.target.x, this.target.y + d, this.target.z);
		}

		double e = (double)blockPos.getX() + 0.5;
		double f = (double)blockPos.getY() + d;
		double g = (double)blockPos.getZ() + 0.5;
		Direction direction = null;
		if (!blockPos.isWithinDistance(this.getPos(), 2.0)) {
			BlockPos blockPos2 = new BlockPos(this);
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
				} else if (blockPos2.getY() > blockPos.getY() && this.world.isAir(blockPos2.down())) {
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

			e = this.x + (double)direction.getOffsetX();
			f = this.y + (double)direction.getOffsetY();
			g = this.z + (double)direction.getOffsetZ();
		}

		this.setDirection(direction);
		double h = e - this.x;
		double j = f - this.y;
		double k = g - this.z;
		double l = (double)MathHelper.sqrt(h * h + j * j + k * k);
		if (l == 0.0) {
			this.field_7635 = 0.0;
			this.field_7633 = 0.0;
			this.field_7625 = 0.0;
		} else {
			this.field_7635 = h / l * 0.15;
			this.field_7633 = j / l * 0.15;
			this.field_7625 = k / l * 0.15;
		}

		this.velocityDirty = true;
		this.field_7627 = 10 + this.random.nextInt(5) * 10;
	}

	@Override
	public void tick() {
		if (!this.world.isClient && this.world.getDifficulty() == Difficulty.field_5801) {
			this.remove();
		} else {
			super.tick();
			if (!this.world.isClient) {
				if (this.target == null && this.targetUuid != null) {
					for (LivingEntity livingEntity : this.world.getEntities(LivingEntity.class, new Box(this.targetPos.add(-2, -2, -2), this.targetPos.add(2, 2, 2)))) {
						if (livingEntity.getUuid().equals(this.targetUuid)) {
							this.target = livingEntity;
							break;
						}
					}

					this.targetUuid = null;
				}

				if (this.owner == null && this.ownerUuid != null) {
					for (LivingEntity livingEntityx : this.world.getEntities(LivingEntity.class, new Box(this.ownerPos.add(-2, -2, -2), this.ownerPos.add(2, 2, 2)))) {
						if (livingEntityx.getUuid().equals(this.ownerUuid)) {
							this.owner = livingEntityx;
							break;
						}
					}

					this.ownerUuid = null;
				}

				if (this.target == null || !this.target.isAlive() || this.target instanceof PlayerEntity && ((PlayerEntity)this.target).isSpectator()) {
					if (!this.hasNoGravity()) {
						this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
					}
				} else {
					this.field_7635 = MathHelper.clamp(this.field_7635 * 1.025, -1.0, 1.0);
					this.field_7633 = MathHelper.clamp(this.field_7633 * 1.025, -1.0, 1.0);
					this.field_7625 = MathHelper.clamp(this.field_7625 * 1.025, -1.0, 1.0);
					Vec3d vec3d = this.getVelocity();
					this.setVelocity(vec3d.add((this.field_7635 - vec3d.x) * 0.2, (this.field_7633 - vec3d.y) * 0.2, (this.field_7625 - vec3d.z) * 0.2));
				}

				HitResult hitResult = ProjectileUtil.getCollision(this, true, false, this.owner, RayTraceContext.ShapeType.field_17558);
				if (hitResult.getType() != HitResult.Type.field_1333) {
					this.onHit(hitResult);
				}
			}

			Vec3d vec3d = this.getVelocity();
			this.setPosition(this.x + vec3d.x, this.y + vec3d.y, this.z + vec3d.z);
			ProjectileUtil.method_7484(this, 0.5F);
			if (this.world.isClient) {
				this.world.addParticle(ParticleTypes.field_11207, this.x - vec3d.x, this.y - vec3d.y + 0.15, this.z - vec3d.z, 0.0, 0.0, 0.0);
			} else if (this.target != null && !this.target.removed) {
				if (this.field_7627 > 0) {
					this.field_7627--;
					if (this.field_7627 == 0) {
						this.method_7486(this.direction == null ? null : this.direction.getAxis());
					}
				}

				if (this.direction != null) {
					BlockPos blockPos = new BlockPos(this);
					Direction.Axis axis = this.direction.getAxis();
					if (this.world.doesBlockHaveSolidTopSurface(blockPos.offset(this.direction), this)) {
						this.method_7486(axis);
					} else {
						BlockPos blockPos2 = new BlockPos(this.target);
						if (axis == Direction.Axis.field_11048 && blockPos.getX() == blockPos2.getX()
							|| axis == Direction.Axis.field_11051 && blockPos.getZ() == blockPos2.getZ()
							|| axis == Direction.Axis.field_11052 && blockPos.getY() == blockPos2.getY()) {
							this.method_7486(axis);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean isOnFire() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderAtDistance(double d) {
		return d < 16384.0;
	}

	@Override
	public float getBrightnessAtEyes() {
		return 1.0F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getLightmapCoordinates() {
		return 15728880;
	}

	protected void onHit(HitResult hitResult) {
		if (hitResult.getType() == HitResult.Type.field_1331) {
			Entity entity = ((EntityHitResult)hitResult).getEntity();
			boolean bl = entity.damage(DamageSource.mobProjectile(this, this.owner).setProjectile(), 4.0F);
			if (bl) {
				this.dealDamage(this.owner, entity);
				if (entity instanceof LivingEntity) {
					((LivingEntity)entity).addPotionEffect(new StatusEffectInstance(StatusEffects.field_5902, 200));
				}
			}
		} else {
			((ServerWorld)this.world).spawnParticles(ParticleTypes.field_11236, this.x, this.y, this.z, 2, 0.2, 0.2, 0.2, 0.0);
			this.playSound(SoundEvents.field_14895, 1.0F, 1.0F);
		}

		this.remove();
	}

	@Override
	public boolean collides() {
		return true;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (!this.world.isClient) {
			this.playSound(SoundEvents.field_14977, 1.0F, 1.0F);
			((ServerWorld)this.world).spawnParticles(ParticleTypes.field_11205, this.x, this.y, this.z, 15, 0.2, 0.2, 0.2, 0.0);
			this.remove();
		}

		return true;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}
}
