package net.minecraft.client.particle;

import java.util.Random;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.collection.ReusableStream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public abstract class Particle {
	private static final Box EMPTY_BOUNDING_BOX = new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	protected final ClientWorld world;
	protected double prevPosX;
	protected double prevPosY;
	protected double prevPosZ;
	protected double x;
	protected double y;
	protected double z;
	protected double velocityX;
	protected double velocityY;
	protected double velocityZ;
	private Box boundingBox = EMPTY_BOUNDING_BOX;
	protected boolean onGround;
	protected boolean collidesWithWorld = true;
	private boolean field_21507;
	protected boolean dead;
	protected float spacingXZ = 0.6F;
	protected float spacingY = 1.8F;
	protected final Random random = new Random();
	protected int age;
	protected int maxAge;
	protected float gravityStrength;
	protected float colorRed = 1.0F;
	protected float colorGreen = 1.0F;
	protected float colorBlue = 1.0F;
	protected float colorAlpha = 1.0F;
	protected float angle;
	protected float prevAngle;
	protected float field_28786 = 0.98F;
	protected boolean field_28787 = false;

	protected Particle(ClientWorld clientWorld, double x, double y, double z) {
		this.world = clientWorld;
		this.setBoundingBoxSpacing(0.2F, 0.2F);
		this.setPos(x, y, z);
		this.prevPosX = x;
		this.prevPosY = y;
		this.prevPosZ = z;
		this.maxAge = (int)(4.0F / (this.random.nextFloat() * 0.9F + 0.1F));
	}

	public Particle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		this(world, x, y, z);
		this.velocityX = velocityX + (Math.random() * 2.0 - 1.0) * 0.4F;
		this.velocityY = velocityY + (Math.random() * 2.0 - 1.0) * 0.4F;
		this.velocityZ = velocityZ + (Math.random() * 2.0 - 1.0) * 0.4F;
		float f = (float)(Math.random() + Math.random() + 1.0) * 0.15F;
		float g = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
		this.velocityX = this.velocityX / (double)g * (double)f * 0.4F;
		this.velocityY = this.velocityY / (double)g * (double)f * 0.4F + 0.1F;
		this.velocityZ = this.velocityZ / (double)g * (double)f * 0.4F;
	}

	public Particle move(float speed) {
		this.velocityX *= (double)speed;
		this.velocityY = (this.velocityY - 0.1F) * (double)speed + 0.1F;
		this.velocityZ *= (double)speed;
		return this;
	}

	public Particle scale(float scale) {
		this.setBoundingBoxSpacing(0.2F * scale, 0.2F * scale);
		return this;
	}

	public void setColor(float red, float green, float blue) {
		this.colorRed = red;
		this.colorGreen = green;
		this.colorBlue = blue;
	}

	protected void setColorAlpha(float alpha) {
		this.colorAlpha = alpha;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	public int getMaxAge() {
		return this.maxAge;
	}

	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.velocityY = this.velocityY - 0.04 * (double)this.gravityStrength;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			if (this.field_28787 && this.y == this.prevPosY) {
				this.velocityX *= 1.1;
				this.velocityZ *= 1.1;
			}

			this.velocityX = this.velocityX * (double)this.field_28786;
			this.velocityY = this.velocityY * (double)this.field_28786;
			this.velocityZ = this.velocityZ * (double)this.field_28786;
			if (this.onGround) {
				this.velocityX *= 0.7F;
				this.velocityZ *= 0.7F;
			}
		}
	}

	public abstract void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta);

	public abstract ParticleTextureSheet getType();

	public String toString() {
		return this.getClass().getSimpleName()
			+ ", Pos ("
			+ this.x
			+ ","
			+ this.y
			+ ","
			+ this.z
			+ "), RGBA ("
			+ this.colorRed
			+ ","
			+ this.colorGreen
			+ ","
			+ this.colorBlue
			+ ","
			+ this.colorAlpha
			+ "), Age "
			+ this.age;
	}

	public void markDead() {
		this.dead = true;
	}

	protected void setBoundingBoxSpacing(float spacingXZ, float spacingY) {
		if (spacingXZ != this.spacingXZ || spacingY != this.spacingY) {
			this.spacingXZ = spacingXZ;
			this.spacingY = spacingY;
			Box box = this.getBoundingBox();
			double d = (box.minX + box.maxX - (double)spacingXZ) / 2.0;
			double e = (box.minZ + box.maxZ - (double)spacingXZ) / 2.0;
			this.setBoundingBox(new Box(d, box.minY, e, d + (double)this.spacingXZ, box.minY + (double)this.spacingY, e + (double)this.spacingXZ));
		}
	}

	public void setPos(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		float f = this.spacingXZ / 2.0F;
		float g = this.spacingY;
		this.setBoundingBox(new Box(x - (double)f, y, z - (double)f, x + (double)f, y + (double)g, z + (double)f));
	}

	public void move(double dx, double dy, double dz) {
		if (!this.field_21507) {
			double d = dx;
			double e = dy;
			double f = dz;
			if (this.collidesWithWorld && (dx != 0.0 || dy != 0.0 || dz != 0.0)) {
				Vec3d vec3d = Entity.adjustMovementForCollisions(
					null, new Vec3d(dx, dy, dz), this.getBoundingBox(), this.world, ShapeContext.absent(), new ReusableStream<>(Stream.empty())
				);
				dx = vec3d.x;
				dy = vec3d.y;
				dz = vec3d.z;
			}

			if (dx != 0.0 || dy != 0.0 || dz != 0.0) {
				this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
				this.repositionFromBoundingBox();
			}

			if (Math.abs(e) >= 1.0E-5F && Math.abs(dy) < 1.0E-5F) {
				this.field_21507 = true;
			}

			this.onGround = e != dy && e < 0.0;
			if (d != dx) {
				this.velocityX = 0.0;
			}

			if (f != dz) {
				this.velocityZ = 0.0;
			}
		}
	}

	protected void repositionFromBoundingBox() {
		Box box = this.getBoundingBox();
		this.x = (box.minX + box.maxX) / 2.0;
		this.y = box.minY;
		this.z = (box.minZ + box.maxZ) / 2.0;
	}

	protected int getBrightness(float tint) {
		BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
		return this.world.isChunkLoaded(blockPos) ? WorldRenderer.getLightmapCoordinates(this.world, blockPos) : 0;
	}

	public boolean isAlive() {
		return !this.dead;
	}

	public Box getBoundingBox() {
		return this.boundingBox;
	}

	public void setBoundingBox(Box boundingBox) {
		this.boundingBox = boundingBox;
	}
}
