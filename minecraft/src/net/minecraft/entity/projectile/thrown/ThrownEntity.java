package net.minecraft.entity.projectile.thrown;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public abstract class ThrownEntity extends ProjectileEntity {
	protected ThrownEntity(EntityType<? extends ThrownEntity> entityType, World world) {
		super(entityType, world);
	}

	protected ThrownEntity(EntityType<? extends ThrownEntity> type, double x, double y, double z, World world) {
		this(type, world);
		this.updatePosition(x, y, z);
	}

	protected ThrownEntity(EntityType<? extends ThrownEntity> type, LivingEntity owner, World world) {
		this(type, owner.getX(), owner.getEyeY() - 0.1F, owner.getZ(), world);
		this.setOwner(owner);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRender(double distance) {
		double d = this.getBoundingBox().getAverageSideLength() * 4.0;
		if (Double.isNaN(d)) {
			d = 4.0;
		}

		d *= 64.0;
		return distance < d * d;
	}

	@Override
	public void tick() {
		super.tick();
		HitResult hitResult = ProjectileUtil.getCollision(this, this::method_26958, RayTraceContext.ShapeType.OUTLINE);
		if (hitResult.getType() != HitResult.Type.MISS) {
			if (hitResult.getType() == HitResult.Type.BLOCK && this.world.getBlockState(((BlockHitResult)hitResult).getBlockPos()).getBlock() == Blocks.NETHER_PORTAL) {
				this.setInNetherPortal(((BlockHitResult)hitResult).getBlockPos());
			} else {
				this.onCollision(hitResult);
			}
		}

		Vec3d vec3d = this.getVelocity();
		double d = this.getX() + vec3d.x;
		double e = this.getY() + vec3d.y;
		double f = this.getZ() + vec3d.z;
		this.method_26962();
		float h;
		if (this.isTouchingWater()) {
			for (int i = 0; i < 4; i++) {
				float g = 0.25F;
				this.world.addParticle(ParticleTypes.BUBBLE, d - vec3d.x * 0.25, e - vec3d.y * 0.25, f - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
			}

			h = 0.8F;
		} else {
			h = 0.99F;
		}

		this.setVelocity(vec3d.multiply((double)h));
		if (!this.hasNoGravity()) {
			Vec3d vec3d2 = this.getVelocity();
			this.setVelocity(vec3d2.x, vec3d2.y - (double)this.getGravity(), vec3d2.z);
		}

		this.updatePosition(d, e, f);
	}

	protected float getGravity() {
		return 0.03F;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}
}
