package net.minecraft.entity.projectile;

import com.google.common.base.MoreObjects;
import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public abstract class ProjectileEntity extends Entity implements Ownable {
	@Nullable
	private UUID ownerUuid;
	@Nullable
	private Entity owner;
	private boolean leftOwner;
	private boolean shot;
	@Nullable
	private Entity lastDeflectedEntity;

	public ProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	public void setOwner(@Nullable Entity entity) {
		if (entity != null) {
			this.ownerUuid = entity.getUuid();
			this.owner = entity;
		}
	}

	@Nullable
	@Override
	public Entity getOwner() {
		if (this.owner != null && !this.owner.isRemoved()) {
			return this.owner;
		} else if (this.ownerUuid != null) {
			this.owner = this.getEntity(this.ownerUuid);
			return this.owner;
		} else {
			return null;
		}
	}

	@Nullable
	protected Entity getEntity(UUID uuid) {
		return this.getWorld() instanceof ServerWorld serverWorld ? serverWorld.getEntity(uuid) : null;
	}

	/**
	 * {@return the cause entity of any effect applied by this projectile} If this
	 * projectile has an owner, the effect is attributed to the owner; otherwise, it
	 * is attributed to this projectile itself.
	 */
	public Entity getEffectCause() {
		return MoreObjects.firstNonNull(this.getOwner(), this);
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		if (this.ownerUuid != null) {
			nbt.putUuid("Owner", this.ownerUuid);
		}

		if (this.leftOwner) {
			nbt.putBoolean("LeftOwner", true);
		}

		nbt.putBoolean("HasBeenShot", this.shot);
	}

	protected boolean isOwner(Entity entity) {
		return entity.getUuid().equals(this.ownerUuid);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		if (nbt.containsUuid("Owner")) {
			this.setOwner(nbt.getUuid("Owner"));
		}

		this.leftOwner = nbt.getBoolean("LeftOwner");
		this.shot = nbt.getBoolean("HasBeenShot");
	}

	protected void setOwner(UUID uuid) {
		if (this.ownerUuid != uuid) {
			this.ownerUuid = uuid;
			this.owner = this.getEntity(uuid);
		}
	}

	@Override
	public void copyFrom(Entity original) {
		super.copyFrom(original);
		if (original instanceof ProjectileEntity projectileEntity) {
			this.ownerUuid = projectileEntity.ownerUuid;
			this.owner = projectileEntity.owner;
		}
	}

	@Override
	public void tick() {
		if (!this.shot) {
			this.emitGameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner());
			this.shot = true;
		}

		if (!this.leftOwner) {
			this.leftOwner = this.shouldLeaveOwner();
		}

		super.tick();
	}

	private boolean shouldLeaveOwner() {
		Entity entity = this.getOwner();
		if (entity != null) {
			Box box = this.getBoundingBox().stretch(this.getVelocity()).expand(1.0);
			return entity.getRootVehicle().streamSelfAndPassengers().filter(EntityPredicates.CAN_HIT).noneMatch(entityx -> box.intersects(entityx.getBoundingBox()));
		} else {
			return true;
		}
	}

	public Vec3d calculateVelocity(double x, double y, double z, float power, float uncertainty) {
		return new Vec3d(x, y, z)
			.normalize()
			.add(
				this.random.nextTriangular(0.0, 0.0172275 * (double)uncertainty),
				this.random.nextTriangular(0.0, 0.0172275 * (double)uncertainty),
				this.random.nextTriangular(0.0, 0.0172275 * (double)uncertainty)
			)
			.multiply((double)power);
	}

	/**
	 * Sets velocity and updates rotation accordingly.
	 * 
	 * <p>The velocity and rotation will be set to the same direction.
	 * 
	 * <p>The direction is calculated as follows: Based on the direction vector
	 * {@code (x, y, z)}, a random vector is added, then multiplied by the
	 * {@code speed}.
	 * 
	 * @param z the Z component of the direction vector
	 * @param uncertainty the fuzziness added to the direction; player usages have 1.0 and other
	 * mobs/tools have higher values; some mobs have difficulty-adjusted
	 * values
	 * @param power the speed
	 * @param x the X component of the direction vector
	 * @param y the Y component of the direction vector
	 */
	public void setVelocity(double x, double y, double z, float power, float uncertainty) {
		Vec3d vec3d = this.calculateVelocity(x, y, z, power, uncertainty);
		this.setVelocity(vec3d);
		this.velocityDirty = true;
		double d = vec3d.horizontalLength();
		this.setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI));
		this.setPitch((float)(MathHelper.atan2(vec3d.y, d) * 180.0F / (float)Math.PI));
		this.prevYaw = this.getYaw();
		this.prevPitch = this.getPitch();
	}

	/**
	 * Sets velocity and updates rotation accordingly.
	 * 
	 * @param divergence the fuzziness added to the direction; player usages have 1.0 and other
	 * mobs/tools have higher values; some mobs have difficulty-adjusted
	 * values
	 * @param speed the speed
	 * @param pitch the pitch
	 * @param shooter the entity who shot this projectile; used to add the shooter's velocity
	 * to this projectile
	 * @param roll the roll
	 * @param yaw the yaw
	 */
	public void setVelocity(Entity shooter, float pitch, float yaw, float roll, float speed, float divergence) {
		float f = -MathHelper.sin(yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(pitch * (float) (Math.PI / 180.0));
		float g = -MathHelper.sin((pitch + roll) * (float) (Math.PI / 180.0));
		float h = MathHelper.cos(yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(pitch * (float) (Math.PI / 180.0));
		this.setVelocity((double)f, (double)g, (double)h, speed, divergence);
		Vec3d vec3d = shooter.getMovement();
		this.setVelocity(this.getVelocity().add(vec3d.x, shooter.isOnGround() ? 0.0 : vec3d.y, vec3d.z));
	}

	public static <T extends ProjectileEntity> T spawnWithVelocity(
		ProjectileEntity.ProjectileCreator<T> creator, ServerWorld world, ItemStack projectileStack, LivingEntity shooter, float roll, float power, float divergence
	) {
		return spawn(
			creator.create(world, shooter, projectileStack),
			world,
			projectileStack,
			entity -> entity.setVelocity(shooter, shooter.getPitch(), shooter.getYaw(), roll, power, divergence)
		);
	}

	public static <T extends ProjectileEntity> T spawnWithVelocity(
		ProjectileEntity.ProjectileCreator<T> creator,
		ServerWorld world,
		ItemStack projectileStack,
		LivingEntity shooter,
		double velocityX,
		double velocityY,
		double velocityZ,
		float power,
		float divergence
	) {
		return spawn(
			creator.create(world, shooter, projectileStack), world, projectileStack, entity -> entity.setVelocity(velocityX, velocityY, velocityZ, power, divergence)
		);
	}

	public static <T extends ProjectileEntity> T spawnWithVelocity(
		T projectile, ServerWorld world, ItemStack projectileStack, double velocityX, double velocityY, double velocityZ, float power, float divergence
	) {
		return spawn(projectile, world, projectileStack, entity -> projectile.setVelocity(velocityX, velocityY, velocityZ, power, divergence));
	}

	public static <T extends ProjectileEntity> T spawn(T projectile, ServerWorld world, ItemStack projectileStack) {
		return spawn(projectile, world, projectileStack, entity -> {
		});
	}

	public static <T extends ProjectileEntity> T spawn(T projectile, ServerWorld world, ItemStack projectileStack, Consumer<T> beforeSpawn) {
		beforeSpawn.accept(projectile);
		world.spawnEntity(projectile);
		projectile.triggerProjectileSpawned(world, projectileStack);
		return projectile;
	}

	public void triggerProjectileSpawned(ServerWorld world, ItemStack projectileStack) {
		EnchantmentHelper.onProjectileSpawned(world, projectileStack, this, item -> {
		});
		if (this instanceof PersistentProjectileEntity persistentProjectileEntity) {
			ItemStack itemStack = persistentProjectileEntity.getWeaponStack();
			if (itemStack != null && !itemStack.isEmpty() && !projectileStack.getItem().equals(itemStack.getItem())) {
				EnchantmentHelper.onProjectileSpawned(world, itemStack, this, persistentProjectileEntity::onBroken);
			}
		}
	}

	protected ProjectileDeflection hitOrDeflect(HitResult hitResult) {
		if (hitResult.getType() == HitResult.Type.ENTITY) {
			EntityHitResult entityHitResult = (EntityHitResult)hitResult;
			Entity entity = entityHitResult.getEntity();
			ProjectileDeflection projectileDeflection = entity.getProjectileDeflection(this);
			if (projectileDeflection != ProjectileDeflection.NONE) {
				if (entity != this.lastDeflectedEntity && this.deflect(projectileDeflection, entity, this.getOwner(), false)) {
					this.lastDeflectedEntity = entity;
				}

				return projectileDeflection;
			}
		} else if (this.deflectsAgainstWorldBorder() && hitResult instanceof BlockHitResult blockHitResult && blockHitResult.isAgainstWorldBorder()) {
			ProjectileDeflection projectileDeflection2 = ProjectileDeflection.SIMPLE;
			if (this.deflect(projectileDeflection2, null, this.getOwner(), false)) {
				this.setVelocity(this.getVelocity().multiply(0.2));
				return projectileDeflection2;
			}
		}

		this.onCollision(hitResult);
		return ProjectileDeflection.NONE;
	}

	protected boolean deflectsAgainstWorldBorder() {
		return false;
	}

	public boolean deflect(ProjectileDeflection deflection, @Nullable Entity deflector, @Nullable Entity owner, boolean fromAttack) {
		deflection.deflect(this, deflector, this.random);
		if (!this.getWorld().isClient) {
			this.setOwner(owner);
			this.onDeflected(deflector, fromAttack);
		}

		return true;
	}

	protected void onDeflected(@Nullable Entity deflector, boolean fromAttack) {
	}

	protected void onBroken(Item item) {
	}

	protected void onCollision(HitResult hitResult) {
		HitResult.Type type = hitResult.getType();
		if (type == HitResult.Type.ENTITY) {
			EntityHitResult entityHitResult = (EntityHitResult)hitResult;
			Entity entity = entityHitResult.getEntity();
			if (entity.getType().isIn(EntityTypeTags.REDIRECTABLE_PROJECTILE) && entity instanceof ProjectileEntity projectileEntity) {
				projectileEntity.deflect(ProjectileDeflection.REDIRECTED, this.getOwner(), this.getOwner(), true);
			}

			this.onEntityHit(entityHitResult);
			this.getWorld().emitGameEvent(GameEvent.PROJECTILE_LAND, hitResult.getPos(), GameEvent.Emitter.of(this, null));
		} else if (type == HitResult.Type.BLOCK) {
			BlockHitResult blockHitResult = (BlockHitResult)hitResult;
			this.onBlockHit(blockHitResult);
			BlockPos blockPos = blockHitResult.getBlockPos();
			this.getWorld().emitGameEvent(GameEvent.PROJECTILE_LAND, blockPos, GameEvent.Emitter.of(this, this.getWorld().getBlockState(blockPos)));
		}
	}

	protected void onEntityHit(EntityHitResult entityHitResult) {
	}

	protected void onBlockHit(BlockHitResult blockHitResult) {
		BlockState blockState = this.getWorld().getBlockState(blockHitResult.getBlockPos());
		blockState.onProjectileHit(this.getWorld(), blockState, blockHitResult, this);
	}

	@Override
	public void setVelocityClient(double x, double y, double z) {
		this.setVelocity(x, y, z);
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			double d = Math.sqrt(x * x + z * z);
			this.setPitch((float)(MathHelper.atan2(y, d) * 180.0F / (float)Math.PI));
			this.setYaw((float)(MathHelper.atan2(x, z) * 180.0F / (float)Math.PI));
			this.prevPitch = this.getPitch();
			this.prevYaw = this.getYaw();
			this.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
		}
	}

	protected boolean canHit(Entity entity) {
		if (!entity.canBeHitByProjectile()) {
			return false;
		} else {
			Entity entity2 = this.getOwner();
			return entity2 == null || this.leftOwner || !entity2.isConnectedThroughVehicle(entity);
		}
	}

	protected void updateRotation() {
		Vec3d vec3d = this.getVelocity();
		double d = vec3d.horizontalLength();
		this.setPitch(updateRotation(this.prevPitch, (float)(MathHelper.atan2(vec3d.y, d) * 180.0F / (float)Math.PI)));
		this.setYaw(updateRotation(this.prevYaw, (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI)));
	}

	public static float updateRotation(float prevRot, float newRot) {
		while (newRot - prevRot < -180.0F) {
			prevRot -= 360.0F;
		}

		while (newRot - prevRot >= 180.0F) {
			prevRot += 360.0F;
		}

		return MathHelper.lerp(0.2F, prevRot, newRot);
	}

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
		Entity entity = this.getOwner();
		return new EntitySpawnS2CPacket(this, entityTrackerEntry, entity == null ? 0 : entity.getId());
	}

	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet) {
		super.onSpawnPacket(packet);
		Entity entity = this.getWorld().getEntityById(packet.getEntityData());
		if (entity != null) {
			this.setOwner(entity);
		}
	}

	@Override
	public boolean canModifyAt(World world, BlockPos pos) {
		Entity entity = this.getOwner();
		return entity instanceof PlayerEntity ? entity.canModifyAt(world, pos) : entity == null || world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
	}

	public boolean canBreakBlocks(World world) {
		return this.getType().isIn(EntityTypeTags.IMPACT_PROJECTILES) && world.getGameRules().getBoolean(GameRules.PROJECTILES_CAN_BREAK_BLOCKS);
	}

	@Override
	public boolean canHit() {
		return this.getType().isIn(EntityTypeTags.REDIRECTABLE_PROJECTILE);
	}

	@Override
	public float getTargetingMargin() {
		return this.canHit() ? 1.0F : 0.0F;
	}

	public DoubleDoubleImmutablePair getKnockback(LivingEntity target, DamageSource source) {
		double d = this.getVelocity().x;
		double e = this.getVelocity().z;
		return DoubleDoubleImmutablePair.of(d, e);
	}

	@Override
	public int getDefaultPortalCooldown() {
		return 2;
	}

	@FunctionalInterface
	public interface ProjectileCreator<T extends ProjectileEntity> {
		T create(ServerWorld world, LivingEntity shooter, ItemStack stack);
	}
}
