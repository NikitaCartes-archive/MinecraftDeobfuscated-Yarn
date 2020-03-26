package net.minecraft.entity.projectile;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class LlamaSpitEntity extends ProjectileEntity {
	public LlamaSpitEntity(EntityType<? extends LlamaSpitEntity> entityType, World world) {
		super(entityType, world);
	}

	public LlamaSpitEntity(World world, LlamaEntity owner) {
		this(EntityType.LLAMA_SPIT, world);
		super.setOwner(owner);
		this.updatePosition(
			owner.getX() - (double)(owner.getWidth() + 1.0F) * 0.5 * (double)MathHelper.sin(owner.bodyYaw * (float) (Math.PI / 180.0)),
			owner.getEyeY() - 0.1F,
			owner.getZ() + (double)(owner.getWidth() + 1.0F) * 0.5 * (double)MathHelper.cos(owner.bodyYaw * (float) (Math.PI / 180.0))
		);
	}

	@Environment(EnvType.CLIENT)
	public LlamaSpitEntity(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		this(EntityType.LLAMA_SPIT, world);
		this.updatePosition(x, y, z);

		for (int i = 0; i < 7; i++) {
			double d = 0.4 + 0.1 * (double)i;
			world.addParticle(ParticleTypes.SPIT, x, y, z, velocityX * d, velocityY, velocityZ * d);
		}

		this.setVelocity(velocityX, velocityY, velocityZ);
	}

	@Override
	public void tick() {
		super.tick();
		Vec3d vec3d = this.getVelocity();
		HitResult hitResult = ProjectileUtil.getCollision(
			this,
			this.getBoundingBox().stretch(vec3d).expand(1.0),
			entity -> !entity.isSpectator() && entity != this.getOwner(),
			RayTraceContext.ShapeType.OUTLINE,
			true
		);
		if (hitResult != null) {
			this.onCollision(hitResult);
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

			this.updatePosition(d, e, f);
		}
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);
		Entity entity = this.getOwner();
		if (entity instanceof LivingEntity) {
			entityHitResult.getEntity().damage(DamageSource.mobProjectile(this, (LivingEntity)entity).setProjectile(), 1.0F);
		}
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		super.onBlockHit(blockHitResult);
		if (!this.world.isClient) {
			this.remove();
		}
	}

	@Override
	protected void initDataTracker() {
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}
}
