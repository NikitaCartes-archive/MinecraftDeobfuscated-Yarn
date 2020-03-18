package net.minecraft.entity.projectile;

import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class ProjectileEntity extends Entity {
	private UUID ownerUuid;

	public ProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	public void setOwner(@Nullable Entity entity) {
		this.ownerUuid = entity == null ? null : entity.getUuid();
	}

	@Nullable
	public Entity getOwner() {
		return this.ownerUuid != null && this.world instanceof ServerWorld ? ((ServerWorld)this.world).getEntity(this.ownerUuid) : null;
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag tag) {
		if (this.ownerUuid != null) {
			tag.putUuidNew("Owner", this.ownerUuid);
		}
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag tag) {
		if (tag.containsUuidNew("Owner")) {
			this.ownerUuid = tag.getUuidNew("Owner");
		}
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
			this.method_24920((BlockHitResult)hitResult);
		}
	}

	protected void onEntityHit(EntityHitResult entityHitResult) {
	}

	protected void method_24920(BlockHitResult blockHitResult) {
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
}
