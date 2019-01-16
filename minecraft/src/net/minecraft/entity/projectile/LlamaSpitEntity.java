package net.minecraft.entity.projectile;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.sortme.Projectile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.EntityHitResult;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class LlamaSpitEntity extends Entity implements Projectile {
	public LlamaEntity owner;
	private CompoundTag field_7623;

	public LlamaSpitEntity(World world) {
		super(EntityType.LLAMA_SPIT, world);
	}

	public LlamaSpitEntity(World world, LlamaEntity llamaEntity) {
		this(world);
		this.owner = llamaEntity;
		this.setPosition(
			llamaEntity.x - (double)(llamaEntity.getWidth() + 1.0F) * 0.5 * (double)MathHelper.sin(llamaEntity.field_6283 * (float) (Math.PI / 180.0)),
			llamaEntity.y + (double)llamaEntity.getEyeHeight() - 0.1F,
			llamaEntity.z + (double)(llamaEntity.getWidth() + 1.0F) * 0.5 * (double)MathHelper.cos(llamaEntity.field_6283 * (float) (Math.PI / 180.0))
		);
	}

	@Environment(EnvType.CLIENT)
	public LlamaSpitEntity(World world, double d, double e, double f, double g, double h, double i) {
		this(world);
		this.setPosition(d, e, f);

		for (int j = 0; j < 7; j++) {
			double k = 0.4 + 0.1 * (double)j;
			world.addParticle(ParticleTypes.field_11228, d, e, f, g * k, h, i * k);
		}

		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
	}

	@Override
	public void update() {
		super.update();
		if (this.field_7623 != null) {
			this.method_7479();
		}

		Vec3d vec3d = new Vec3d(this.x, this.y, this.z);
		Vec3d vec3d2 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
		HitResult hitResult = this.world
			.rayTrace(new RayTraceContext(vec3d, vec3d2, RayTraceContext.ShapeType.field_17559, RayTraceContext.FluidHandling.NONE, this));
		if (hitResult.getType() != HitResult.Type.NONE) {
			vec3d2 = hitResult.getPos();
		}

		Entity entity = this.method_7480(vec3d, vec3d2);
		if (entity != null) {
			hitResult = new EntityHitResult(entity);
		}

		if (hitResult != null) {
			this.method_7481(hitResult);
		}

		this.x = this.x + this.velocityX;
		this.y = this.y + this.velocityY;
		this.z = this.z + this.velocityZ;
		float f = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
		this.yaw = (float)(MathHelper.atan2(this.velocityX, this.velocityZ) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(this.velocityY, (double)f) * 180.0F / (float)Math.PI);

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
		float g = 0.99F;
		float h = 0.06F;
		if (!this.world.method_8422(this.getBoundingBox(), Material.AIR)) {
			this.invalidate();
		} else if (this.isInsideWaterOrBubbleColumn()) {
			this.invalidate();
		} else {
			this.velocityX *= 0.99F;
			this.velocityY *= 0.99F;
			this.velocityZ *= 0.99F;
			if (!this.isUnaffectedByGravity()) {
				this.velocityY -= 0.06F;
			}

			this.setPosition(this.x, this.y, this.z);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setVelocityClient(double d, double e, double f) {
		this.velocityX = d;
		this.velocityY = e;
		this.velocityZ = f;
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			float g = MathHelper.sqrt(d * d + f * f);
			this.pitch = (float)(MathHelper.atan2(e, (double)g) * 180.0F / (float)Math.PI);
			this.yaw = (float)(MathHelper.atan2(d, f) * 180.0F / (float)Math.PI);
			this.prevPitch = this.pitch;
			this.prevYaw = this.yaw;
			this.setPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
		}
	}

	@Nullable
	private Entity method_7480(Vec3d vec3d, Vec3d vec3d2) {
		Entity entity = null;
		List<Entity> list = this.world
			.getEntities(
				this,
				this.getBoundingBox().stretch(this.velocityX, this.velocityY, this.velocityZ).expand(1.0),
				EntityPredicates.EXCEPT_SPECTATOR.and(entityx -> entityx != this.owner)
			);
		double d = 0.0;

		for (Entity entity2 : list) {
			BoundingBox boundingBox = entity2.getBoundingBox().expand(0.3F);
			Vec3d vec3d3 = boundingBox.rayTrace(vec3d, vec3d2);
			if (vec3d3 != null) {
				double e = vec3d.squaredDistanceTo(vec3d3);
				if (e < d || d == 0.0) {
					entity = entity2;
					d = e;
				}
			}
		}

		return entity;
	}

	@Override
	public void setVelocity(double d, double e, double f, float g, float h) {
		float i = MathHelper.sqrt(d * d + e * e + f * f);
		d /= (double)i;
		e /= (double)i;
		f /= (double)i;
		d += this.random.nextGaussian() * 0.0075F * (double)h;
		e += this.random.nextGaussian() * 0.0075F * (double)h;
		f += this.random.nextGaussian() * 0.0075F * (double)h;
		d *= (double)g;
		e *= (double)g;
		f *= (double)g;
		this.velocityX = d;
		this.velocityY = e;
		this.velocityZ = f;
		float j = MathHelper.sqrt(d * d + f * f);
		this.yaw = (float)(MathHelper.atan2(d, f) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(e, (double)j) * 180.0F / (float)Math.PI);
		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
	}

	public void method_7481(HitResult hitResult) {
		if (hitResult.getType() == HitResult.Type.ENTITY && this.owner != null) {
			((EntityHitResult)hitResult).getEntity().damage(DamageSource.mobProjectile(this, this.owner).setProjectile(), 1.0F);
		}

		if (!this.world.isClient) {
			this.invalidate();
		}
	}

	@Override
	protected void initDataTracker() {
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag compoundTag) {
		if (compoundTag.containsKey("Owner", 10)) {
			this.field_7623 = compoundTag.getCompound("Owner");
		}
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag compoundTag) {
		if (this.owner != null) {
			CompoundTag compoundTag2 = new CompoundTag();
			UUID uUID = this.owner.getUuid();
			compoundTag2.putUuid("OwnerUUID", uUID);
			compoundTag.put("Owner", compoundTag2);
		}
	}

	private void method_7479() {
		if (this.field_7623 != null && this.field_7623.hasUuid("OwnerUUID")) {
			UUID uUID = this.field_7623.getUuid("OwnerUUID");

			for (LlamaEntity llamaEntity : this.world.getVisibleEntities(LlamaEntity.class, this.getBoundingBox().expand(15.0))) {
				if (llamaEntity.getUuid().equals(uUID)) {
					this.owner = llamaEntity;
					break;
				}
			}
		}

		this.field_7623 = null;
	}
}
