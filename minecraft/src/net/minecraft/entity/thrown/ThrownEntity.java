package net.minecraft.entity.thrown;

import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1675;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.sortme.Projectile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TagHelper;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public abstract class ThrownEntity extends Entity implements Projectile {
	private int blockX = -1;
	private int blockY = -1;
	private int blockZ = -1;
	protected boolean inGround;
	public int shake;
	protected LivingEntity owner;
	private UUID field_7644;
	private Entity field_7637;
	private int field_7638;

	protected ThrownEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	protected ThrownEntity(EntityType<?> entityType, double d, double e, double f, World world) {
		this(entityType, world);
		this.setPosition(d, e, f);
	}

	protected ThrownEntity(EntityType<?> entityType, LivingEntity livingEntity, World world) {
		this(entityType, livingEntity.x, livingEntity.y + (double)livingEntity.getEyeHeight() - 0.1F, livingEntity.z, world);
		this.owner = livingEntity;
		this.field_7644 = livingEntity.getUuid();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderAtDistance(double d) {
		double e = this.getBoundingBox().averageDimension() * 4.0;
		if (Double.isNaN(e)) {
			e = 4.0;
		}

		e *= 64.0;
		return d < e * e;
	}

	public void calculateVelocity(Entity entity, float f, float g, float h, float i, float j) {
		float k = -MathHelper.sin(g * (float) (Math.PI / 180.0)) * MathHelper.cos(f * (float) (Math.PI / 180.0));
		float l = -MathHelper.sin((f + h) * (float) (Math.PI / 180.0));
		float m = MathHelper.cos(g * (float) (Math.PI / 180.0)) * MathHelper.cos(f * (float) (Math.PI / 180.0));
		this.setVelocity((double)k, (double)l, (double)m, i, j);
		this.velocityX = this.velocityX + entity.velocityX;
		this.velocityZ = this.velocityZ + entity.velocityZ;
		if (!entity.onGround) {
			this.velocityY = this.velocityY + entity.velocityY;
		}
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

	@Environment(EnvType.CLIENT)
	@Override
	public void setVelocityClient(double d, double e, double f) {
		this.velocityX = d;
		this.velocityY = e;
		this.velocityZ = f;
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			float g = MathHelper.sqrt(d * d + f * f);
			this.yaw = (float)(MathHelper.atan2(d, f) * 180.0F / (float)Math.PI);
			this.pitch = (float)(MathHelper.atan2(e, (double)g) * 180.0F / (float)Math.PI);
			this.prevYaw = this.yaw;
			this.prevPitch = this.pitch;
		}
	}

	@Override
	public void update() {
		this.prevRenderX = this.x;
		this.prevRenderY = this.y;
		this.prevRenderZ = this.z;
		super.update();
		if (this.shake > 0) {
			this.shake--;
		}

		if (this.inGround) {
			this.inGround = false;
			this.velocityX = this.velocityX * (double)(this.random.nextFloat() * 0.2F);
			this.velocityY = this.velocityY * (double)(this.random.nextFloat() * 0.2F);
			this.velocityZ = this.velocityZ * (double)(this.random.nextFloat() * 0.2F);
		}

		BoundingBox boundingBox = this.getBoundingBox().stretch(this.velocityX, this.velocityY, this.velocityZ).expand(1.0);

		for (Entity entity : this.world.getEntities(this, boundingBox, entityx -> !entityx.isSpectator() && entityx.doesCollide())) {
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

		HitResult hitResult = class_1675.method_18074(
			this, boundingBox, entity -> !entity.isSpectator() && entity.doesCollide() && entity != this.field_7637, RayTraceContext.ShapeType.field_17559, true
		);
		if (this.field_7637 != null && this.field_7638-- <= 0) {
			this.field_7637 = null;
		}

		if (hitResult.getType() != HitResult.Type.NONE) {
			if (hitResult.getType() == HitResult.Type.BLOCK && this.world.getBlockState(((BlockHitResult)hitResult).getBlockPos()).getBlock() == Blocks.field_10316) {
				this.setInPortal(((BlockHitResult)hitResult).getBlockPos());
			} else {
				this.onCollision(hitResult);
			}
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
		float h = this.getGravity();
		if (this.isInsideWater()) {
			for (int i = 0; i < 4; i++) {
				float j = 0.25F;
				this.world
					.addParticle(
						ParticleTypes.field_11247,
						this.x - this.velocityX * 0.25,
						this.y - this.velocityY * 0.25,
						this.z - this.velocityZ * 0.25,
						this.velocityX,
						this.velocityY,
						this.velocityZ
					);
			}

			g = 0.8F;
		}

		this.velocityX *= (double)g;
		this.velocityY *= (double)g;
		this.velocityZ *= (double)g;
		if (!this.isUnaffectedByGravity()) {
			this.velocityY -= (double)h;
		}

		this.setPosition(this.x, this.y, this.z);
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
		compoundTag.putByte("inGround", (byte)(this.inGround ? 1 : 0));
		if (this.field_7644 != null) {
			compoundTag.put("owner", TagHelper.serializeUuid(this.field_7644));
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		this.blockX = compoundTag.getInt("xTile");
		this.blockY = compoundTag.getInt("yTile");
		this.blockZ = compoundTag.getInt("zTile");
		this.shake = compoundTag.getByte("shake") & 255;
		this.inGround = compoundTag.getByte("inGround") == 1;
		this.owner = null;
		if (compoundTag.containsKey("owner", 10)) {
			this.field_7644 = TagHelper.deserializeUuid(compoundTag.getCompound("owner"));
		}
	}

	@Nullable
	public LivingEntity getOwner() {
		if (this.owner == null && this.field_7644 != null && this.world instanceof ServerWorld) {
			Entity entity = ((ServerWorld)this.world).getEntity(this.field_7644);
			if (entity instanceof LivingEntity) {
				this.owner = (LivingEntity)entity;
			} else {
				this.field_7644 = null;
			}
		}

		return this.owner;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}
}
