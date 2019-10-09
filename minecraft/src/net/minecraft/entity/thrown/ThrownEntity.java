package net.minecraft.entity.thrown;

import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public abstract class ThrownEntity extends Entity implements Projectile {
	private int blockX = -1;
	private int blockY = -1;
	private int blockZ = -1;
	protected boolean inGround;
	public int shake;
	protected LivingEntity owner;
	private UUID ownerUuid;
	private Entity field_7637;
	private int field_7638;

	protected ThrownEntity(EntityType<? extends ThrownEntity> entityType, World world) {
		super(entityType, world);
	}

	protected ThrownEntity(EntityType<? extends ThrownEntity> entityType, double d, double e, double f, World world) {
		this(entityType, world);
		this.setPosition(d, e, f);
	}

	protected ThrownEntity(EntityType<? extends ThrownEntity> entityType, LivingEntity livingEntity, World world) {
		this(entityType, livingEntity.getX(), livingEntity.method_23320() - 0.1F, livingEntity.getZ(), world);
		this.owner = livingEntity;
		this.ownerUuid = livingEntity.getUuid();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderAtDistance(double d) {
		double e = this.getBoundingBox().getAverageSideLength() * 4.0;
		if (Double.isNaN(e)) {
			e = 4.0;
		}

		e *= 64.0;
		return d < e * e;
	}

	public void setProperties(Entity entity, float f, float g, float h, float i, float j) {
		float k = -MathHelper.sin(g * (float) (Math.PI / 180.0)) * MathHelper.cos(f * (float) (Math.PI / 180.0));
		float l = -MathHelper.sin((f + h) * (float) (Math.PI / 180.0));
		float m = MathHelper.cos(g * (float) (Math.PI / 180.0)) * MathHelper.cos(f * (float) (Math.PI / 180.0));
		this.setVelocity((double)k, (double)l, (double)m, i, j);
		Vec3d vec3d = entity.getVelocity();
		this.setVelocity(this.getVelocity().add(vec3d.x, entity.onGround ? 0.0 : vec3d.y, vec3d.z));
	}

	@Override
	public void setVelocity(double d, double e, double f, float g, float h) {
		Vec3d vec3d = new Vec3d(d, e, f)
			.normalize()
			.add(this.random.nextGaussian() * 0.0075F * (double)h, this.random.nextGaussian() * 0.0075F * (double)h, this.random.nextGaussian() * 0.0075F * (double)h)
			.multiply((double)g);
		this.setVelocity(vec3d);
		float i = MathHelper.sqrt(squaredHorizontalLength(vec3d));
		this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(vec3d.y, (double)i) * 180.0F / (float)Math.PI);
		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setVelocityClient(double d, double e, double f) {
		this.setVelocity(d, e, f);
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			float g = MathHelper.sqrt(d * d + f * f);
			this.yaw = (float)(MathHelper.atan2(d, f) * 180.0F / (float)Math.PI);
			this.pitch = (float)(MathHelper.atan2(e, (double)g) * 180.0F / (float)Math.PI);
			this.prevYaw = this.yaw;
			this.prevPitch = this.pitch;
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.shake > 0) {
			this.shake--;
		}

		if (this.inGround) {
			this.inGround = false;
			this.setVelocity(
				this.getVelocity().multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F))
			);
		}

		Box box = this.getBoundingBox().stretch(this.getVelocity()).expand(1.0);

		for (Entity entity : this.world.getEntities(this, box, entityx -> !entityx.isSpectator() && entityx.collides())) {
			if (entity == this.field_7637) {
				this.field_7638++;
				break;
			}

			if (this.owner != null && this.age < 2 && this.field_7637 == null) {
				this.field_7637 = entity;
				this.field_7638 = 3;
				break;
			}
		}

		HitResult hitResult = ProjectileUtil.getCollision(
			this, box, entity -> !entity.isSpectator() && entity.collides() && entity != this.field_7637, RayTraceContext.ShapeType.OUTLINE, true
		);
		if (this.field_7637 != null && this.field_7638-- <= 0) {
			this.field_7637 = null;
		}

		if (hitResult.getType() != HitResult.Type.MISS) {
			if (hitResult.getType() == HitResult.Type.BLOCK && this.world.getBlockState(((BlockHitResult)hitResult).getBlockPos()).getBlock() == Blocks.NETHER_PORTAL) {
				this.setInPortal(((BlockHitResult)hitResult).getBlockPos());
			} else {
				this.onCollision(hitResult);
			}
		}

		Vec3d vec3d = this.getVelocity();
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
		float j;
		if (this.isInsideWater()) {
			for (int i = 0; i < 4; i++) {
				float h = 0.25F;
				this.world.addParticle(ParticleTypes.BUBBLE, d - vec3d.x * 0.25, e - vec3d.y * 0.25, f - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
			}

			j = 0.8F;
		} else {
			j = 0.99F;
		}

		this.setVelocity(vec3d.multiply((double)j));
		if (!this.hasNoGravity()) {
			Vec3d vec3d2 = this.getVelocity();
			this.setVelocity(vec3d2.x, vec3d2.y - (double)this.getGravity(), vec3d2.z);
		}

		this.setPosition(d, e, f);
	}

	protected float getGravity() {
		return 0.03F;
	}

	protected abstract void onCollision(HitResult hitResult);

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		compoundTag.putInt("xTile", this.blockX);
		compoundTag.putInt("yTile", this.blockY);
		compoundTag.putInt("zTile", this.blockZ);
		compoundTag.putByte("shake", (byte)this.shake);
		compoundTag.putBoolean("inGround", this.inGround);
		if (this.ownerUuid != null) {
			compoundTag.put("owner", NbtHelper.fromUuid(this.ownerUuid));
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		this.blockX = compoundTag.getInt("xTile");
		this.blockY = compoundTag.getInt("yTile");
		this.blockZ = compoundTag.getInt("zTile");
		this.shake = compoundTag.getByte("shake") & 255;
		this.inGround = compoundTag.getBoolean("inGround");
		this.owner = null;
		if (compoundTag.contains("owner", 10)) {
			this.ownerUuid = NbtHelper.toUuid(compoundTag.getCompound("owner"));
		}
	}

	@Nullable
	public LivingEntity getOwner() {
		if ((this.owner == null || this.owner.removed) && this.ownerUuid != null && this.world instanceof ServerWorld) {
			Entity entity = ((ServerWorld)this.world).getEntity(this.ownerUuid);
			if (entity instanceof LivingEntity) {
				this.owner = (LivingEntity)entity;
			} else {
				this.owner = null;
			}
		}

		return this.owner;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}
}
