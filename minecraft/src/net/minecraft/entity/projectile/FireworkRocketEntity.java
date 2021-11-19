package net.minecraft.entity.projectile;

import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class FireworkRocketEntity extends ProjectileEntity implements FlyingItemEntity {
	private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(FireworkRocketEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private static final TrackedData<OptionalInt> SHOOTER_ENTITY_ID = DataTracker.registerData(
		FireworkRocketEntity.class, TrackedDataHandlerRegistry.FIREWORK_DATA
	);
	private static final TrackedData<Boolean> SHOT_AT_ANGLE = DataTracker.registerData(FireworkRocketEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private int life;
	private int lifeTime;
	@Nullable
	private LivingEntity shooter;

	public FireworkRocketEntity(EntityType<? extends FireworkRocketEntity> entityType, World world) {
		super(entityType, world);
	}

	public FireworkRocketEntity(World world, double x, double y, double z, ItemStack stack) {
		super(EntityType.FIREWORK_ROCKET, world);
		this.life = 0;
		this.setPosition(x, y, z);
		int i = 1;
		if (!stack.isEmpty() && stack.hasNbt()) {
			this.dataTracker.set(ITEM, stack.copy());
			i += stack.getOrCreateSubNbt("Fireworks").getByte("Flight");
		}

		this.setVelocity(this.random.nextGaussian() * 0.001, 0.05, this.random.nextGaussian() * 0.001);
		this.lifeTime = 10 * i + this.random.nextInt(6) + this.random.nextInt(7);
	}

	public FireworkRocketEntity(World world, @Nullable Entity entity, double x, double y, double z, ItemStack stack) {
		this(world, x, y, z, stack);
		this.setOwner(entity);
	}

	public FireworkRocketEntity(World world, ItemStack stack, LivingEntity shooter) {
		this(world, shooter, shooter.getX(), shooter.getY(), shooter.getZ(), stack);
		this.dataTracker.set(SHOOTER_ENTITY_ID, OptionalInt.of(shooter.getId()));
		this.shooter = shooter;
	}

	public FireworkRocketEntity(World world, ItemStack stack, double x, double y, double z, boolean shotAtAngle) {
		this(world, x, y, z, stack);
		this.dataTracker.set(SHOT_AT_ANGLE, shotAtAngle);
	}

	public FireworkRocketEntity(World world, ItemStack stack, Entity entity, double x, double y, double z, boolean shotAtAngle) {
		this(world, stack, x, y, z, shotAtAngle);
		this.setOwner(entity);
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(ITEM, ItemStack.EMPTY);
		this.dataTracker.startTracking(SHOOTER_ENTITY_ID, OptionalInt.empty());
		this.dataTracker.startTracking(SHOT_AT_ANGLE, false);
	}

	@Override
	public boolean shouldRender(double distance) {
		return distance < 4096.0 && !this.wasShotByEntity();
	}

	@Override
	public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
		return super.shouldRender(cameraX, cameraY, cameraZ) && !this.wasShotByEntity();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.wasShotByEntity()) {
			if (this.shooter == null) {
				this.dataTracker.get(SHOOTER_ENTITY_ID).ifPresent(id -> {
					Entity entity = this.world.getEntityById(id);
					if (entity instanceof LivingEntity) {
						this.shooter = (LivingEntity)entity;
					}
				});
			}

			if (this.shooter != null) {
				if (this.shooter.isFallFlying()) {
					Vec3d vec3d = this.shooter.getRotationVector();
					double d = 1.5;
					double e = 0.1;
					Vec3d vec3d2 = this.shooter.getVelocity();
					this.shooter
						.setVelocity(
							vec3d2.add(
								vec3d.x * 0.1 + (vec3d.x * 1.5 - vec3d2.x) * 0.5, vec3d.y * 0.1 + (vec3d.y * 1.5 - vec3d2.y) * 0.5, vec3d.z * 0.1 + (vec3d.z * 1.5 - vec3d2.z) * 0.5
							)
						);
				}

				this.setPosition(this.shooter.getX(), this.shooter.getY(), this.shooter.getZ());
				this.setVelocity(this.shooter.getVelocity());
			}
		} else {
			if (!this.wasShotAtAngle()) {
				double f = this.horizontalCollision ? 1.0 : 1.15;
				this.setVelocity(this.getVelocity().multiply(f, 1.0, f).add(0.0, 0.04, 0.0));
			}

			Vec3d vec3d = this.getVelocity();
			this.move(MovementType.SELF, vec3d);
			this.setVelocity(vec3d);
		}

		HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
		if (!this.noClip) {
			this.onCollision(hitResult);
			this.velocityDirty = true;
		}

		this.updateRotation();
		if (this.life == 0 && !this.isSilent()) {
			this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.AMBIENT, 3.0F, 1.0F);
		}

		this.life++;
		if (this.world.isClient && this.life % 2 < 2) {
			this.world
				.addParticle(
					ParticleTypes.FIREWORK,
					this.getX(),
					this.getY() - 0.3,
					this.getZ(),
					this.random.nextGaussian() * 0.05,
					-this.getVelocity().y * 0.5,
					this.random.nextGaussian() * 0.05
				);
		}

		if (!this.world.isClient && this.life > this.lifeTime) {
			this.explodeAndRemove();
		}
	}

	private void explodeAndRemove() {
		this.world.sendEntityStatus(this, EntityStatuses.EXPLODE_FIREWORK_CLIENT);
		this.emitGameEvent(GameEvent.EXPLODE, this.getOwner());
		this.explode();
		this.discard();
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);
		if (!this.world.isClient) {
			this.explodeAndRemove();
		}
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		BlockPos blockPos = new BlockPos(blockHitResult.getBlockPos());
		this.world.getBlockState(blockPos).onEntityCollision(this.world, blockPos, this);
		if (!this.world.isClient() && this.hasExplosionEffects()) {
			this.explodeAndRemove();
		}

		super.onBlockHit(blockHitResult);
	}

	private boolean hasExplosionEffects() {
		ItemStack itemStack = this.dataTracker.get(ITEM);
		NbtCompound nbtCompound = itemStack.isEmpty() ? null : itemStack.getSubNbt("Fireworks");
		NbtList nbtList = nbtCompound != null ? nbtCompound.getList("Explosions", NbtElement.COMPOUND_TYPE) : null;
		return nbtList != null && !nbtList.isEmpty();
	}

	private void explode() {
		float f = 0.0F;
		ItemStack itemStack = this.dataTracker.get(ITEM);
		NbtCompound nbtCompound = itemStack.isEmpty() ? null : itemStack.getSubNbt("Fireworks");
		NbtList nbtList = nbtCompound != null ? nbtCompound.getList("Explosions", NbtElement.COMPOUND_TYPE) : null;
		if (nbtList != null && !nbtList.isEmpty()) {
			f = 5.0F + (float)(nbtList.size() * 2);
		}

		if (f > 0.0F) {
			if (this.shooter != null) {
				this.shooter.damage(DamageSource.firework(this, this.getOwner()), 5.0F + (float)(nbtList.size() * 2));
			}

			double d = 5.0;
			Vec3d vec3d = this.getPos();

			for (LivingEntity livingEntity : this.world.getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox().expand(5.0))) {
				if (livingEntity != this.shooter && !(this.squaredDistanceTo(livingEntity) > 25.0)) {
					boolean bl = false;

					for (int i = 0; i < 2; i++) {
						Vec3d vec3d2 = new Vec3d(livingEntity.getX(), livingEntity.getBodyY(0.5 * (double)i), livingEntity.getZ());
						HitResult hitResult = this.world.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
						if (hitResult.getType() == HitResult.Type.MISS) {
							bl = true;
							break;
						}
					}

					if (bl) {
						float g = f * (float)Math.sqrt((5.0 - (double)this.distanceTo(livingEntity)) / 5.0);
						livingEntity.damage(DamageSource.firework(this, this.getOwner()), g);
					}
				}
			}
		}
	}

	private boolean wasShotByEntity() {
		return this.dataTracker.get(SHOOTER_ENTITY_ID).isPresent();
	}

	public boolean wasShotAtAngle() {
		return this.dataTracker.get(SHOT_AT_ANGLE);
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.EXPLODE_FIREWORK_CLIENT && this.world.isClient) {
			if (!this.hasExplosionEffects()) {
				for (int i = 0; i < this.random.nextInt(3) + 2; i++) {
					this.world
						.addParticle(ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.05, 0.005, this.random.nextGaussian() * 0.05);
				}
			} else {
				ItemStack itemStack = this.dataTracker.get(ITEM);
				NbtCompound nbtCompound = itemStack.isEmpty() ? null : itemStack.getSubNbt("Fireworks");
				Vec3d vec3d = this.getVelocity();
				this.world.addFireworkParticle(this.getX(), this.getY(), this.getZ(), vec3d.x, vec3d.y, vec3d.z, nbtCompound);
			}
		}

		super.handleStatus(status);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Life", this.life);
		nbt.putInt("LifeTime", this.lifeTime);
		ItemStack itemStack = this.dataTracker.get(ITEM);
		if (!itemStack.isEmpty()) {
			nbt.put("FireworksItem", itemStack.writeNbt(new NbtCompound()));
		}

		nbt.putBoolean("ShotAtAngle", this.dataTracker.get(SHOT_AT_ANGLE));
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.life = nbt.getInt("Life");
		this.lifeTime = nbt.getInt("LifeTime");
		ItemStack itemStack = ItemStack.fromNbt(nbt.getCompound("FireworksItem"));
		if (!itemStack.isEmpty()) {
			this.dataTracker.set(ITEM, itemStack);
		}

		if (nbt.contains("ShotAtAngle")) {
			this.dataTracker.set(SHOT_AT_ANGLE, nbt.getBoolean("ShotAtAngle"));
		}
	}

	@Override
	public ItemStack getStack() {
		ItemStack itemStack = this.dataTracker.get(ITEM);
		return itemStack.isEmpty() ? new ItemStack(Items.FIREWORK_ROCKET) : itemStack;
	}

	@Override
	public boolean isAttackable() {
		return false;
	}
}
