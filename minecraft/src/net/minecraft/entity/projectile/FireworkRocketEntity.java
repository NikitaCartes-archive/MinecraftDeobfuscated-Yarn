package net.minecraft.entity.projectile;

import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import java.util.List;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
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
	private static final TrackedData<OptionalInt> SHOOTER_ENTITY_ID = DataTracker.registerData(FireworkRocketEntity.class, TrackedDataHandlerRegistry.OPTIONAL_INT);
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
		this.dataTracker.set(ITEM, stack.copy());
		int i = 1;
		FireworksComponent fireworksComponent = stack.get(DataComponentTypes.FIREWORKS);
		if (fireworksComponent != null) {
			i += fireworksComponent.flightDuration();
		}

		this.setVelocity(this.random.nextTriangular(0.0, 0.002297), 0.05, this.random.nextTriangular(0.0, 0.002297));
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
	protected void initDataTracker(DataTracker.Builder builder) {
		builder.add(ITEM, getDefaultStack());
		builder.add(SHOOTER_ENTITY_ID, OptionalInt.empty());
		builder.add(SHOT_AT_ANGLE, false);
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
					Entity entity = this.getWorld().getEntityById(id);
					if (entity instanceof LivingEntity) {
						this.shooter = (LivingEntity)entity;
					}
				});
			}

			if (this.shooter != null) {
				Vec3d vec3d3;
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
					vec3d3 = this.shooter.getHandPosOffset(Items.FIREWORK_ROCKET);
				} else {
					vec3d3 = Vec3d.ZERO;
				}

				this.setPosition(this.shooter.getX() + vec3d3.x, this.shooter.getY() + vec3d3.y, this.shooter.getZ() + vec3d3.z);
				this.setVelocity(this.shooter.getVelocity());
			}
		} else {
			if (!this.wasShotAtAngle()) {
				double f = this.horizontalCollision ? 1.0 : 1.15;
				this.setVelocity(this.getVelocity().multiply(f, 1.0, f).add(0.0, 0.04, 0.0));
			}

			Vec3d vec3d3 = this.getVelocity();
			this.move(MovementType.SELF, vec3d3);
			if (!this.getWorld().isClient()) {
				this.tickBlockCollision();
			}

			this.setVelocity(vec3d3);
		}

		HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
		if (!this.noClip) {
			this.hitOrDeflect(hitResult);
			this.velocityDirty = true;
		}

		this.updateRotation();
		if (this.life == 0 && !this.isSilent()) {
			this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.AMBIENT, 3.0F, 1.0F);
		}

		this.life++;
		if (this.getWorld().isClient && this.life % 2 < 2) {
			this.getWorld()
				.addParticle(
					ParticleTypes.FIREWORK,
					this.getX(),
					this.getY(),
					this.getZ(),
					this.random.nextGaussian() * 0.05,
					-this.getVelocity().y * 0.5,
					this.random.nextGaussian() * 0.05
				);
		}

		if (!this.getWorld().isClient && this.life > this.lifeTime) {
			this.explodeAndRemove();
		}
	}

	private void explodeAndRemove() {
		this.getWorld().sendEntityStatus(this, EntityStatuses.EXPLODE_FIREWORK_CLIENT);
		this.emitGameEvent(GameEvent.EXPLODE, this.getOwner());
		this.explode();
		this.discard();
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);
		if (!this.getWorld().isClient) {
			this.explodeAndRemove();
		}
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		BlockPos blockPos = new BlockPos(blockHitResult.getBlockPos());
		this.getWorld().getBlockState(blockPos).onEntityCollision(this.getWorld(), blockPos, this);
		if (!this.getWorld().isClient() && this.hasExplosionEffects()) {
			this.explodeAndRemove();
		}

		super.onBlockHit(blockHitResult);
	}

	private boolean hasExplosionEffects() {
		return !this.getExplosions().isEmpty();
	}

	private void explode() {
		float f = 0.0F;
		List<FireworkExplosionComponent> list = this.getExplosions();
		if (!list.isEmpty()) {
			f = 5.0F + (float)(list.size() * 2);
		}

		if (f > 0.0F) {
			if (this.shooter != null) {
				this.shooter.damage(this.getDamageSources().fireworks(this, this.getOwner()), 5.0F + (float)(list.size() * 2));
			}

			double d = 5.0;
			Vec3d vec3d = this.getPos();

			for (LivingEntity livingEntity : this.getWorld().getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox().expand(5.0))) {
				if (livingEntity != this.shooter && !(this.squaredDistanceTo(livingEntity) > 25.0)) {
					boolean bl = false;

					for (int i = 0; i < 2; i++) {
						Vec3d vec3d2 = new Vec3d(livingEntity.getX(), livingEntity.getBodyY(0.5 * (double)i), livingEntity.getZ());
						HitResult hitResult = this.getWorld()
							.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
						if (hitResult.getType() == HitResult.Type.MISS) {
							bl = true;
							break;
						}
					}

					if (bl) {
						float g = f * (float)Math.sqrt((5.0 - (double)this.distanceTo(livingEntity)) / 5.0);
						livingEntity.damage(this.getDamageSources().fireworks(this, this.getOwner()), g);
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
		if (status == EntityStatuses.EXPLODE_FIREWORK_CLIENT && this.getWorld().isClient) {
			Vec3d vec3d = this.getVelocity();
			this.getWorld().addFireworkParticle(this.getX(), this.getY(), this.getZ(), vec3d.x, vec3d.y, vec3d.z, this.getExplosions());
		}

		super.handleStatus(status);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Life", this.life);
		nbt.putInt("LifeTime", this.lifeTime);
		nbt.put("FireworksItem", this.getStack().toNbt(this.getRegistryManager()));
		nbt.putBoolean("ShotAtAngle", this.dataTracker.get(SHOT_AT_ANGLE));
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.life = nbt.getInt("Life");
		this.lifeTime = nbt.getInt("LifeTime");
		if (nbt.contains("FireworksItem", NbtElement.COMPOUND_TYPE)) {
			this.dataTracker
				.set(ITEM, (ItemStack)ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound("FireworksItem")).orElseGet(FireworkRocketEntity::getDefaultStack));
		} else {
			this.dataTracker.set(ITEM, getDefaultStack());
		}

		if (nbt.contains("ShotAtAngle")) {
			this.dataTracker.set(SHOT_AT_ANGLE, nbt.getBoolean("ShotAtAngle"));
		}
	}

	private List<FireworkExplosionComponent> getExplosions() {
		ItemStack itemStack = this.dataTracker.get(ITEM);
		FireworksComponent fireworksComponent = itemStack.get(DataComponentTypes.FIREWORKS);
		return fireworksComponent != null ? fireworksComponent.explosions() : List.of();
	}

	@Override
	public ItemStack getStack() {
		return this.dataTracker.get(ITEM);
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	private static ItemStack getDefaultStack() {
		return new ItemStack(Items.FIREWORK_ROCKET);
	}

	@Override
	public DoubleDoubleImmutablePair getKnockback(LivingEntity target, DamageSource source) {
		double d = target.getPos().x - this.getPos().x;
		double e = target.getPos().z - this.getPos().z;
		return DoubleDoubleImmutablePair.of(d, e);
	}
}
