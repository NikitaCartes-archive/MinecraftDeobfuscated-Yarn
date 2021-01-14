package net.minecraft.entity.projectile;

import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class ProjectileEntity extends Entity {
	private UUID ownerUuid;
	private int ownerEntityId;
	private boolean leftOwner;

	public ProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	public void setOwner(@Nullable Entity entity) {
		if (entity != null) {
			this.ownerUuid = entity.getUuid();
			this.ownerEntityId = entity.getEntityId();
		}
	}

	@Nullable
	public Entity getOwner() {
		if (this.ownerUuid != null && this.world instanceof ServerWorld) {
			return ((ServerWorld)this.world).getEntity(this.ownerUuid);
		} else {
			return this.ownerEntityId != 0 ? this.world.getEntityById(this.ownerEntityId) : null;
		}
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		if (this.ownerUuid != null) {
			nbt.putUuid("Owner", this.ownerUuid);
		}

		if (this.leftOwner) {
			nbt.putBoolean("LeftOwner", true);
		}
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		if (nbt.containsUuid("Owner")) {
			this.ownerUuid = nbt.getUuid("Owner");
		}

		this.leftOwner = nbt.getBoolean("LeftOwner");
	}

	@Override
	public void tick() {
		if (!this.leftOwner) {
			this.leftOwner = this.method_26961();
		}

		super.tick();
	}

	private boolean method_26961() {
		Entity entity = this.getOwner();
		if (entity != null) {
			for (Entity entity2 : this.world
				.getOtherEntities(this, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), entityx -> !entityx.isSpectator() && entityx.collides())) {
				if (entity2.getRootVehicle() == entity.getRootVehicle()) {
					return false;
				}
			}
		}

		return true;
	}

	public void setVelocity(double x, double y, double z, float speed, float divergence) {
		Vec3d vec3d = new Vec3d(x, y, z)
			.normalize()
			.add(
				this.random.nextGaussian() * 0.0075F * (double)divergence,
				this.random.nextGaussian() * 0.0075F * (double)divergence,
				this.random.nextGaussian() * 0.0075F * (double)divergence
			)
			.multiply((double)speed);
		this.setVelocity(vec3d);
		float f = MathHelper.sqrt(squaredHorizontalLength(vec3d));
		this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * 180.0F / (float)Math.PI);
		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
	}

	public void setProperties(Entity user, float pitch, float yaw, float roll, float modifierZ, float modifierXYZ) {
		float f = -MathHelper.sin(yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(pitch * (float) (Math.PI / 180.0));
		float g = -MathHelper.sin((pitch + roll) * (float) (Math.PI / 180.0));
		float h = MathHelper.cos(yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(pitch * (float) (Math.PI / 180.0));
		this.setVelocity((double)f, (double)g, (double)h, modifierZ, modifierXYZ);
		Vec3d vec3d = user.getVelocity();
		this.setVelocity(this.getVelocity().add(vec3d.x, user.isOnGround() ? 0.0 : vec3d.y, vec3d.z));
	}

	protected void onCollision(HitResult hitResult) {
		HitResult.Type type = hitResult.getType();
		if (type == HitResult.Type.ENTITY) {
			this.onEntityHit((EntityHitResult)hitResult);
		} else if (type == HitResult.Type.BLOCK) {
			this.onBlockHit((BlockHitResult)hitResult);
		}
	}

	protected void onEntityHit(EntityHitResult entityHitResult) {
	}

	protected void onBlockHit(BlockHitResult blockHitResult) {
		BlockState blockState = this.world.getBlockState(blockHitResult.getBlockPos());
		blockState.onProjectileHit(this.world, blockState, blockHitResult, this);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setVelocityClient(double x, double y, double z) {
		this.setVelocity(x, y, z);
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			float f = MathHelper.sqrt(x * x + z * z);
			this.pitch = (float)(MathHelper.atan2(y, (double)f) * 180.0F / (float)Math.PI);
			this.yaw = (float)(MathHelper.atan2(x, z) * 180.0F / (float)Math.PI);
			this.prevPitch = this.pitch;
			this.prevYaw = this.yaw;
			this.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch);
		}
	}

	protected boolean method_26958(Entity entity) {
		if (!entity.isSpectator() && entity.isAlive() && entity.collides()) {
			Entity entity2 = this.getOwner();
			return entity2 == null || this.leftOwner || !entity2.isConnectedThroughVehicle(entity);
		} else {
			return false;
		}
	}

	protected void method_26962() {
		Vec3d vec3d = this.getVelocity();
		float f = MathHelper.sqrt(squaredHorizontalLength(vec3d));
		this.pitch = updateRotation(this.prevPitch, (float)(MathHelper.atan2(vec3d.y, (double)f) * 180.0F / (float)Math.PI));
		this.yaw = updateRotation(this.prevYaw, (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI));
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
}
