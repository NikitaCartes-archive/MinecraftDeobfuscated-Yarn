package net.minecraft.entity.projectile;

import com.google.common.base.MoreObjects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public abstract class ProjectileEntity extends Entity {
	@Nullable
	private UUID ownerUuid;
	@Nullable
	private Entity owner;
	private boolean leftOwner;
	private boolean shot;

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
	public Entity getOwner() {
		if (this.owner != null && !this.owner.isRemoved()) {
			return this.owner;
		} else if (this.ownerUuid != null && this.world instanceof ServerWorld) {
			this.owner = ((ServerWorld)this.world).getEntity(this.ownerUuid);
			return this.owner;
		} else {
			return null;
		}
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
			this.ownerUuid = nbt.getUuid("Owner");
		}

		this.leftOwner = nbt.getBoolean("LeftOwner");
		this.shot = nbt.getBoolean("HasBeenShot");
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
			for (Entity entity2 : this.world
				.getOtherEntities(this, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), entityx -> !entityx.isSpectator() && entityx.canHit())) {
				if (entity2.getRootVehicle() == entity.getRootVehicle()) {
					return false;
				}
			}
		}

		return true;
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
	 * @param x the X component of the direction vector
	 * @param y the Y component of the direction vector
	 * @param z the Z component of the direction vector
	 * @param speed the speed
	 * @param divergence the fuzziness added to the direction; player usages have 1.0 and other
	 * mobs/tools have higher values; some mobs have difficulty-adjusted
	 * values
	 */
	public void setVelocity(double x, double y, double z, float speed, float divergence) {
		Vec3d vec3d = new Vec3d(x, y, z)
			.normalize()
			.add(
				this.random.nextTriangular(0.0, 0.0172275 * (double)divergence),
				this.random.nextTriangular(0.0, 0.0172275 * (double)divergence),
				this.random.nextTriangular(0.0, 0.0172275 * (double)divergence)
			)
			.multiply((double)speed);
		this.setVelocity(vec3d);
		double d = vec3d.horizontalLength();
		this.setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI));
		this.setPitch((float)(MathHelper.atan2(vec3d.y, d) * 180.0F / (float)Math.PI));
		this.prevYaw = this.getYaw();
		this.prevPitch = this.getPitch();
	}

	/**
	 * Sets velocity and updates rotation accordingly.
	 * 
	 * @param shooter the entity who shot this projectile; used to add the shooter's velocity
	 * to this projectile
	 * @param pitch the pitch
	 * @param yaw the yaw
	 * @param roll the roll
	 * @param speed the speed
	 * @param divergence the fuzziness added to the direction; player usages have 1.0 and other
	 * mobs/tools have higher values; some mobs have difficulty-adjusted
	 * values
	 */
	public void setVelocity(Entity shooter, float pitch, float yaw, float roll, float speed, float divergence) {
		float f = -MathHelper.sin(yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(pitch * (float) (Math.PI / 180.0));
		float g = -MathHelper.sin((pitch + roll) * (float) (Math.PI / 180.0));
		float h = MathHelper.cos(yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(pitch * (float) (Math.PI / 180.0));
		this.setVelocity((double)f, (double)g, (double)h, speed, divergence);
		Vec3d vec3d = shooter.getVelocity();
		this.setVelocity(this.getVelocity().add(vec3d.x, shooter.isOnGround() ? 0.0 : vec3d.y, vec3d.z));
	}

	protected void onCollision(HitResult hitResult) {
		HitResult.Type type = hitResult.getType();
		if (type == HitResult.Type.ENTITY) {
			this.onEntityHit((EntityHitResult)hitResult);
			this.world.emitGameEvent(GameEvent.PROJECTILE_LAND, hitResult.getPos(), GameEvent.Emitter.of(this, null));
		} else if (type == HitResult.Type.BLOCK) {
			BlockHitResult blockHitResult = (BlockHitResult)hitResult;
			this.onBlockHit(blockHitResult);
			BlockPos blockPos = blockHitResult.getBlockPos();
			this.world.emitGameEvent(GameEvent.PROJECTILE_LAND, blockPos, GameEvent.Emitter.of(this, this.world.getBlockState(blockPos)));
		}
	}

	protected void onEntityHit(EntityHitResult entityHitResult) {
	}

	protected void onBlockHit(BlockHitResult blockHitResult) {
		BlockState blockState = this.world.getBlockState(blockHitResult.getBlockPos());
		blockState.onProjectileHit(this.world, blockState, blockHitResult, this);
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
		if (!entity.isSpectator() && entity.isAlive() && entity.canHit()) {
			Entity entity2 = this.getOwner();
			return entity2 == null || this.leftOwner || !entity2.isConnectedThroughVehicle(entity);
		} else {
			return false;
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
	public Packet<ClientPlayPacketListener> createSpawnPacket() {
		Entity entity = this.getOwner();
		return new EntitySpawnS2CPacket(this, entity == null ? 0 : entity.getId());
	}

	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet) {
		super.onSpawnPacket(packet);
		Entity entity = this.world.getEntityById(packet.getEntityData());
		if (entity != null) {
			this.setOwner(entity);
		}
	}

	@Override
	public boolean canModifyAt(World world, BlockPos pos) {
		Entity entity = this.getOwner();
		return entity instanceof PlayerEntity ? entity.canModifyAt(world, pos) : entity == null || world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
	}
}
