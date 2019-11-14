package net.minecraft.entity.projectile;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Material;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class LlamaSpitEntity extends Entity implements Projectile {
	public LlamaEntity owner;
	private CompoundTag tag;

	public LlamaSpitEntity(EntityType<? extends LlamaSpitEntity> entityType, World world) {
		super(entityType, world);
	}

	public LlamaSpitEntity(World world, LlamaEntity owner) {
		this(EntityType.LLAMA_SPIT, world);
		this.owner = owner;
		this.setPosition(
			owner.getX() - (double)(owner.getWidth() + 1.0F) * 0.5 * (double)MathHelper.sin(owner.bodyYaw * (float) (Math.PI / 180.0)),
			owner.getEyeY() - 0.1F,
			owner.getZ() + (double)(owner.getWidth() + 1.0F) * 0.5 * (double)MathHelper.cos(owner.bodyYaw * (float) (Math.PI / 180.0))
		);
	}

	@Environment(EnvType.CLIENT)
	public LlamaSpitEntity(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		this(EntityType.LLAMA_SPIT, world);
		this.setPosition(x, y, z);

		for (int i = 0; i < 7; i++) {
			double d = 0.4 + 0.1 * (double)i;
			world.addParticle(ParticleTypes.SPIT, x, y, z, velocityX * d, velocityY, velocityZ * d);
		}

		this.setVelocity(velocityX, velocityY, velocityZ);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.tag != null) {
			this.readTag();
		}

		Vec3d vec3d = this.getVelocity();
		HitResult hitResult = ProjectileUtil.getCollision(
			this, this.getBoundingBox().stretch(vec3d).expand(1.0), entity -> !entity.isSpectator() && entity != this.owner, RayTraceContext.ShapeType.OUTLINE, true
		);
		if (hitResult != null) {
			this.method_7481(hitResult);
		}

		double d = this.getX() + vec3d.x;
		double e = this.getY() + vec3d.y;
		double f = this.getZ() + vec3d.z;
		float g = MathHelper.sqrt(squaredHorizontalLength(vec3d));
		this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(vec3d.y, (double)g) * 180.0F / (float)Math.PI);

		while (this.pitch - this.prevPitch < -180.0F) {
			this.prevPitch -= 360.0F;
		}

		while (this.pitch - this.prevPitch >= 180.0F) {
			this.prevPitch += 360.0F;
		}

		while (this.yaw - this.prevYaw < -180.0F) {
			this.prevYaw -= 360.0F;
		}

		while (this.yaw - this.prevYaw >= 180.0F) {
			this.prevYaw += 360.0F;
		}

		this.pitch = MathHelper.lerp(0.2F, this.prevPitch, this.pitch);
		this.yaw = MathHelper.lerp(0.2F, this.prevYaw, this.yaw);
		float h = 0.99F;
		float i = 0.06F;
		if (!this.world.containsBlockWithMaterial(this.getBoundingBox(), Material.AIR)) {
			this.remove();
		} else if (this.isInsideWaterOrBubbleColumn()) {
			this.remove();
		} else {
			this.setVelocity(vec3d.multiply(0.99F));
			if (!this.hasNoGravity()) {
				this.setVelocity(this.getVelocity().add(0.0, -0.06F, 0.0));
			}

			this.setPosition(d, e, f);
		}
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
			this.setPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch);
		}
	}

	@Override
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
		this.yaw = (float)(MathHelper.atan2(vec3d.x, z) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * 180.0F / (float)Math.PI);
		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
	}

	public void method_7481(HitResult hitResult) {
		HitResult.Type type = hitResult.getType();
		if (type == HitResult.Type.ENTITY && this.owner != null) {
			((EntityHitResult)hitResult).getEntity().damage(DamageSource.mobProjectile(this, this.owner).setProjectile(), 1.0F);
		} else if (type == HitResult.Type.BLOCK && !this.world.isClient) {
			this.remove();
		}
	}

	@Override
	protected void initDataTracker() {
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag tag) {
		if (tag.contains("Owner", 10)) {
			this.tag = tag.getCompound("Owner");
		}
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag tag) {
		if (this.owner != null) {
			CompoundTag compoundTag = new CompoundTag();
			UUID uUID = this.owner.getUuid();
			compoundTag.putUuid("OwnerUUID", uUID);
			tag.put("Owner", compoundTag);
		}
	}

	private void readTag() {
		if (this.tag != null && this.tag.containsUuid("OwnerUUID")) {
			UUID uUID = this.tag.getUuid("OwnerUUID");

			for (LlamaEntity llamaEntity : this.world.getNonSpectatingEntities(LlamaEntity.class, this.getBoundingBox().expand(15.0))) {
				if (llamaEntity.getUuid().equals(uUID)) {
					this.owner = llamaEntity;
					break;
				}
			}
		}

		this.tag = null;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}
}
