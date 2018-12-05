package net.minecraft.entity.projectile;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1675;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.HitResult;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class ShulkerBulletEntity extends Entity {
	private LivingEntity field_7630;
	private Entity field_7626;
	@Nullable
	private Direction field_7628;
	private int field_7627;
	private double field_7635;
	private double field_7633;
	private double field_7625;
	@Nullable
	private UUID field_7629;
	private BlockPos field_7634;
	@Nullable
	private UUID field_7632;
	private BlockPos field_7631;

	public ShulkerBulletEntity(World world) {
		super(EntityType.SHULKER_BULLET, world);
		this.setSize(0.3125F, 0.3125F);
		this.noClip = true;
	}

	@Environment(EnvType.CLIENT)
	public ShulkerBulletEntity(World world, double d, double e, double f, double g, double h, double i) {
		this(world);
		this.setPositionAndAngles(d, e, f, this.yaw, this.pitch);
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
	}

	public ShulkerBulletEntity(World world, LivingEntity livingEntity, Entity entity, Direction.Axis axis) {
		this(world);
		this.field_7630 = livingEntity;
		BlockPos blockPos = new BlockPos(livingEntity);
		double d = (double)blockPos.getX() + 0.5;
		double e = (double)blockPos.getY() + 0.5;
		double f = (double)blockPos.getZ() + 0.5;
		this.setPositionAndAngles(d, e, f, this.yaw, this.pitch);
		this.field_7626 = entity;
		this.field_7628 = Direction.UP;
		this.method_7486(axis);
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.field_15251;
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag compoundTag) {
		if (this.field_7630 != null) {
			BlockPos blockPos = new BlockPos(this.field_7630);
			CompoundTag compoundTag2 = TagHelper.serializeUuid(this.field_7630.getUuid());
			compoundTag2.putInt("X", blockPos.getX());
			compoundTag2.putInt("Y", blockPos.getY());
			compoundTag2.putInt("Z", blockPos.getZ());
			compoundTag.put("Owner", compoundTag2);
		}

		if (this.field_7626 != null) {
			BlockPos blockPos = new BlockPos(this.field_7626);
			CompoundTag compoundTag2 = TagHelper.serializeUuid(this.field_7626.getUuid());
			compoundTag2.putInt("X", blockPos.getX());
			compoundTag2.putInt("Y", blockPos.getY());
			compoundTag2.putInt("Z", blockPos.getZ());
			compoundTag.put("Target", compoundTag2);
		}

		if (this.field_7628 != null) {
			compoundTag.putInt("Dir", this.field_7628.getId());
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
			this.field_7628 = Direction.byId(compoundTag.getInt("Dir"));
		}

		if (compoundTag.containsKey("Owner", 10)) {
			CompoundTag compoundTag2 = compoundTag.getCompound("Owner");
			this.field_7629 = TagHelper.deserializeUuid(compoundTag2);
			this.field_7634 = new BlockPos(compoundTag2.getInt("X"), compoundTag2.getInt("Y"), compoundTag2.getInt("Z"));
		}

		if (compoundTag.containsKey("Target", 10)) {
			CompoundTag compoundTag2 = compoundTag.getCompound("Target");
			this.field_7632 = TagHelper.deserializeUuid(compoundTag2);
			this.field_7631 = new BlockPos(compoundTag2.getInt("X"), compoundTag2.getInt("Y"), compoundTag2.getInt("Z"));
		}
	}

	@Override
	protected void initDataTracker() {
	}

	private void method_7487(@Nullable Direction direction) {
		this.field_7628 = direction;
	}

	private void method_7486(@Nullable Direction.Axis axis) {
		double d = 0.5;
		BlockPos blockPos;
		if (this.field_7626 == null) {
			blockPos = new BlockPos(this).down();
		} else {
			d = (double)this.field_7626.height * 0.5;
			blockPos = new BlockPos(this.field_7626.x, this.field_7626.y + d, this.field_7626.z);
		}

		double e = (double)blockPos.getX() + 0.5;
		double f = (double)blockPos.getY() + d;
		double g = (double)blockPos.getZ() + 0.5;
		Direction direction = null;
		if (blockPos.squaredDistanceToCenter(this.x, this.y, this.z) >= 4.0) {
			BlockPos blockPos2 = new BlockPos(this);
			List<Direction> list = Lists.<Direction>newArrayList();
			if (axis != Direction.Axis.X) {
				if (blockPos2.getX() < blockPos.getX() && this.world.isAir(blockPos2.east())) {
					list.add(Direction.EAST);
				} else if (blockPos2.getX() > blockPos.getX() && this.world.isAir(blockPos2.west())) {
					list.add(Direction.WEST);
				}
			}

			if (axis != Direction.Axis.Y) {
				if (blockPos2.getY() < blockPos.getY() && this.world.isAir(blockPos2.up())) {
					list.add(Direction.UP);
				} else if (blockPos2.getY() > blockPos.getY() && this.world.isAir(blockPos2.down())) {
					list.add(Direction.DOWN);
				}
			}

			if (axis != Direction.Axis.Z) {
				if (blockPos2.getZ() < blockPos.getZ() && this.world.isAir(blockPos2.south())) {
					list.add(Direction.SOUTH);
				} else if (blockPos2.getZ() > blockPos.getZ() && this.world.isAir(blockPos2.north())) {
					list.add(Direction.NORTH);
				}
			}

			direction = Direction.random(this.random);
			if (list.isEmpty()) {
				for (int i = 5; !this.world.isAir(blockPos2.method_10093(direction)) && i > 0; i--) {
					direction = Direction.random(this.random);
				}
			} else {
				direction = (Direction)list.get(this.random.nextInt(list.size()));
			}

			e = this.x + (double)direction.getOffsetX();
			f = this.y + (double)direction.getOffsetY();
			g = this.z + (double)direction.getOffsetZ();
		}

		this.method_7487(direction);
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
	public void update() {
		if (!this.world.isRemote && this.world.getDifficulty() == Difficulty.PEACEFUL) {
			this.invalidate();
		} else {
			super.update();
			if (!this.world.isRemote) {
				if (this.field_7626 == null && this.field_7632 != null) {
					for (LivingEntity livingEntity : this.world
						.getVisibleEntities(LivingEntity.class, new BoundingBox(this.field_7631.add(-2, -2, -2), this.field_7631.add(2, 2, 2)))) {
						if (livingEntity.getUuid().equals(this.field_7632)) {
							this.field_7626 = livingEntity;
							break;
						}
					}

					this.field_7632 = null;
				}

				if (this.field_7630 == null && this.field_7629 != null) {
					for (LivingEntity livingEntityx : this.world
						.getVisibleEntities(LivingEntity.class, new BoundingBox(this.field_7634.add(-2, -2, -2), this.field_7634.add(2, 2, 2)))) {
						if (livingEntityx.getUuid().equals(this.field_7629)) {
							this.field_7630 = livingEntityx;
							break;
						}
					}

					this.field_7629 = null;
				}

				if (this.field_7626 == null || !this.field_7626.isValid() || this.field_7626 instanceof PlayerEntity && ((PlayerEntity)this.field_7626).isSpectator()) {
					if (!this.isUnaffectedByGravity()) {
						this.velocityY -= 0.04;
					}
				} else {
					this.field_7635 = MathHelper.clamp(this.field_7635 * 1.025, -1.0, 1.0);
					this.field_7633 = MathHelper.clamp(this.field_7633 * 1.025, -1.0, 1.0);
					this.field_7625 = MathHelper.clamp(this.field_7625 * 1.025, -1.0, 1.0);
					this.velocityX = this.velocityX + (this.field_7635 - this.velocityX) * 0.2;
					this.velocityY = this.velocityY + (this.field_7633 - this.velocityY) * 0.2;
					this.velocityZ = this.velocityZ + (this.field_7625 - this.velocityZ) * 0.2;
				}

				HitResult hitResult = class_1675.method_7482(this, true, false, this.field_7630);
				if (hitResult != null) {
					this.method_7488(hitResult);
				}
			}

			this.setPosition(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
			class_1675.method_7484(this, 0.5F);
			if (this.world.isRemote) {
				this.world.method_8406(ParticleTypes.field_11207, this.x - this.velocityX, this.y - this.velocityY + 0.15, this.z - this.velocityZ, 0.0, 0.0, 0.0);
			} else if (this.field_7626 != null && !this.field_7626.invalid) {
				if (this.field_7627 > 0) {
					this.field_7627--;
					if (this.field_7627 == 0) {
						this.method_7486(this.field_7628 == null ? null : this.field_7628.getAxis());
					}
				}

				if (this.field_7628 != null) {
					BlockPos blockPos = new BlockPos(this);
					Direction.Axis axis = this.field_7628.getAxis();
					if (this.world.method_8515(blockPos.method_10093(this.field_7628))) {
						this.method_7486(axis);
					} else {
						BlockPos blockPos2 = new BlockPos(this.field_7626);
						if (axis == Direction.Axis.X && blockPos.getX() == blockPos2.getX()
							|| axis == Direction.Axis.Z && blockPos.getZ() == blockPos2.getZ()
							|| axis == Direction.Axis.Y && blockPos.getY() == blockPos2.getY()) {
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
	public float method_5718() {
		return 1.0F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getLightmapCoordinates() {
		return 15728880;
	}

	protected void method_7488(HitResult hitResult) {
		if (hitResult.entity == null) {
			((ServerWorld)this.world).method_14199(ParticleTypes.field_11236, this.x, this.y, this.z, 2, 0.2, 0.2, 0.2, 0.0);
			this.playSoundAtEntity(SoundEvents.field_14895, 1.0F, 1.0F);
		} else {
			boolean bl = hitResult.entity.damage(DamageSource.mobProjectile(this, this.field_7630).setProjectile(), 4.0F);
			if (bl) {
				this.method_5723(this.field_7630, hitResult.entity);
				if (hitResult.entity instanceof LivingEntity) {
					((LivingEntity)hitResult.entity).addPotionEffect(new StatusEffectInstance(StatusEffects.field_5902, 200));
				}
			}
		}

		this.invalidate();
	}

	@Override
	public boolean doesCollide() {
		return true;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (!this.world.isRemote) {
			this.playSoundAtEntity(SoundEvents.field_14977, 1.0F, 1.0F);
			((ServerWorld)this.world).method_14199(ParticleTypes.field_11205, this.x, this.y, this.z, 15, 0.2, 0.2, 0.2, 0.0);
			this.invalidate();
		}

		return true;
	}
}
