package net.minecraft.entity.projectile;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1675;
import net.minecraft.block.Material;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.sortme.Projectile;
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
	private CompoundTag field_7623;

	public LlamaSpitEntity(EntityType<? extends LlamaSpitEntity> entityType, World world) {
		super(entityType, world);
	}

	public LlamaSpitEntity(World world, LlamaEntity llamaEntity) {
		this(EntityType.LLAMA_SPIT, world);
		this.owner = llamaEntity;
		this.setPosition(
			llamaEntity.x - (double)(llamaEntity.getWidth() + 1.0F) * 0.5 * (double)MathHelper.sin(llamaEntity.field_6283 * (float) (Math.PI / 180.0)),
			llamaEntity.y + (double)llamaEntity.getStandingEyeHeight() - 0.1F,
			llamaEntity.z + (double)(llamaEntity.getWidth() + 1.0F) * 0.5 * (double)MathHelper.cos(llamaEntity.field_6283 * (float) (Math.PI / 180.0))
		);
	}

	@Environment(EnvType.CLIENT)
	public LlamaSpitEntity(World world, double d, double e, double f, double g, double h, double i) {
		this(EntityType.LLAMA_SPIT, world);
		this.setPosition(d, e, f);

		for (int j = 0; j < 7; j++) {
			double k = 0.4 + 0.1 * (double)j;
			world.method_8406(ParticleTypes.field_11228, d, e, f, g * k, h, i * k);
		}

		this.setVelocity(g, h, i);
	}

	@Override
	public void update() {
		super.update();
		if (this.field_7623 != null) {
			this.method_7479();
		}

		Vec3d vec3d = this.method_18798();
		HitResult hitResult = class_1675.method_18074(
			this,
			this.method_5829().method_18804(vec3d).expand(1.0),
			entity -> !entity.isSpectator() && entity != this.owner,
			RayTraceContext.ShapeType.field_17559,
			true
		);
		if (hitResult != null) {
			this.method_7481(hitResult);
		}

		this.x = this.x + vec3d.x;
		this.y = this.y + vec3d.y;
		this.z = this.z + vec3d.z;
		float f = MathHelper.sqrt(method_17996(vec3d));
		this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * 180.0F / (float)Math.PI);

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
		if (!this.field_6002.method_8422(this.method_5829(), Material.AIR)) {
			this.invalidate();
		} else if (this.isInsideWaterOrBubbleColumn()) {
			this.invalidate();
		} else {
			this.method_18799(vec3d.multiply(0.99F));
			if (!this.isUnaffectedByGravity()) {
				this.method_18799(this.method_18798().add(0.0, -0.06F, 0.0));
			}

			this.setPosition(this.x, this.y, this.z);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setVelocityClient(double d, double e, double f) {
		this.setVelocity(d, e, f);
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			float g = MathHelper.sqrt(d * d + f * f);
			this.pitch = (float)(MathHelper.atan2(e, (double)g) * 180.0F / (float)Math.PI);
			this.yaw = (float)(MathHelper.atan2(d, f) * 180.0F / (float)Math.PI);
			this.prevPitch = this.pitch;
			this.prevYaw = this.yaw;
			this.setPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
		}
	}

	@Override
	public void setVelocity(double d, double e, double f, float g, float h) {
		Vec3d vec3d = new Vec3d(d, e, f)
			.normalize()
			.add(this.random.nextGaussian() * 0.0075F * (double)h, this.random.nextGaussian() * 0.0075F * (double)h, this.random.nextGaussian() * 0.0075F * (double)h)
			.multiply((double)g);
		this.method_18799(vec3d);
		float i = MathHelper.sqrt(method_17996(vec3d));
		this.yaw = (float)(MathHelper.atan2(vec3d.x, f) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(vec3d.y, (double)i) * 180.0F / (float)Math.PI);
		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
	}

	public void method_7481(HitResult hitResult) {
		HitResult.Type type = hitResult.getType();
		if (type == HitResult.Type.ENTITY && this.owner != null) {
			((EntityHitResult)hitResult).getEntity().damage(DamageSource.method_5519(this, this.owner).setProjectile(), 1.0F);
		} else if (type == HitResult.Type.BLOCK && !this.field_6002.isClient) {
			this.invalidate();
		}
	}

	@Override
	protected void initDataTracker() {
	}

	@Override
	protected void method_5749(CompoundTag compoundTag) {
		if (compoundTag.containsKey("Owner", 10)) {
			this.field_7623 = compoundTag.getCompound("Owner");
		}
	}

	@Override
	protected void method_5652(CompoundTag compoundTag) {
		if (this.owner != null) {
			CompoundTag compoundTag2 = new CompoundTag();
			UUID uUID = this.owner.getUuid();
			compoundTag2.putUuid("OwnerUUID", uUID);
			compoundTag.method_10566("Owner", compoundTag2);
		}
	}

	private void method_7479() {
		if (this.field_7623 != null && this.field_7623.hasUuid("OwnerUUID")) {
			UUID uUID = this.field_7623.getUuid("OwnerUUID");

			for (LlamaEntity llamaEntity : this.field_6002.method_18467(LlamaEntity.class, this.method_5829().expand(15.0))) {
				if (llamaEntity.getUuid().equals(uUID)) {
					this.owner = llamaEntity;
					break;
				}
			}
		}

		this.field_7623 = null;
	}

	@Override
	public Packet<?> method_18002() {
		return new EntitySpawnS2CPacket(this);
	}
}
