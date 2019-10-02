package net.minecraft.client.particle;

import java.util.Random;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.util.ReusableStream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public abstract class Particle {
	private static final Box EMPTY_BOUNDING_BOX = new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	protected final World world;
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
	public static double cameraX;
	public static double cameraY;
	public static double cameraZ;

	protected Particle(World world, double d, double e, double f) {
		this.world = world;
		this.setBoundingBoxSpacing(0.2F, 0.2F);
		this.setPos(d, e, f);
		this.prevPosX = d;
		this.prevPosY = e;
		this.prevPosZ = f;
		this.maxAge = (int)(4.0F / (this.random.nextFloat() * 0.9F + 0.1F));
	}

	public Particle(World world, double d, double e, double f, double g, double h, double i) {
		this(world, d, e, f);
		this.velocityX = g + (Math.random() * 2.0 - 1.0) * 0.4F;
		this.velocityY = h + (Math.random() * 2.0 - 1.0) * 0.4F;
		this.velocityZ = i + (Math.random() * 2.0 - 1.0) * 0.4F;
		float j = (float)(Math.random() + Math.random() + 1.0) * 0.15F;
		float k = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
		this.velocityX = this.velocityX / (double)k * (double)j * 0.4F;
		this.velocityY = this.velocityY / (double)k * (double)j * 0.4F + 0.1F;
		this.velocityZ = this.velocityZ / (double)k * (double)j * 0.4F;
	}

	public Particle move(float f) {
		this.velocityX *= (double)f;
		this.velocityY = (this.velocityY - 0.1F) * (double)f + 0.1F;
		this.velocityZ *= (double)f;
		return this;
	}

	public Particle scale(float f) {
		this.setBoundingBoxSpacing(0.2F * f, 0.2F * f);
		return this;
	}

	public void setColor(float f, float g, float h) {
		this.colorRed = f;
		this.colorGreen = g;
		this.colorBlue = h;
	}

	protected void setColorAlpha(float f) {
		this.colorAlpha = f;
	}

	public void setMaxAge(int i) {
		this.maxAge = i;
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
			this.velocityX *= 0.98F;
			this.velocityY *= 0.98F;
			this.velocityZ *= 0.98F;
			if (this.onGround) {
				this.velocityX *= 0.7F;
				this.velocityZ *= 0.7F;
			}
		}
	}

	public abstract void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float f, float g, float h, float i, float j, float k);

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

	protected void setBoundingBoxSpacing(float f, float g) {
		if (f != this.spacingXZ || g != this.spacingY) {
			this.spacingXZ = f;
			this.spacingY = g;
			Box box = this.getBoundingBox();
			double d = (box.minX + box.maxX - (double)f) / 2.0;
			double e = (box.minZ + box.maxZ - (double)f) / 2.0;
			this.setBoundingBox(new Box(d, box.minY, e, d + (double)this.spacingXZ, box.minY + (double)this.spacingY, e + (double)this.spacingXZ));
		}
	}

	public void setPos(double d, double e, double f) {
		this.x = d;
		this.y = e;
		this.z = f;
		float g = this.spacingXZ / 2.0F;
		float h = this.spacingY;
		this.setBoundingBox(new Box(d - (double)g, e, f - (double)g, d + (double)g, e + (double)h, f + (double)g));
	}

	public void move(double d, double e, double f) {
		double g = d;
		double h = e;
		double i = f;
		if (this.collidesWithWorld && (d != 0.0 || e != 0.0 || f != 0.0)) {
			Vec3d vec3d = Entity.adjustMovementForCollisions(
				null, new Vec3d(d, e, f), this.getBoundingBox(), this.world, EntityContext.absent(), new ReusableStream<>(Stream.empty())
			);
			d = vec3d.x;
			e = vec3d.y;
			f = vec3d.z;
		}

		if (d != 0.0 || e != 0.0 || f != 0.0) {
			this.setBoundingBox(this.getBoundingBox().offset(d, e, f));
			this.repositionFromBoundingBox();
		}

		this.onGround = h != e && h < 0.0;
		if (g != d) {
			this.velocityX = 0.0;
		}

		if (i != f) {
			this.velocityZ = 0.0;
		}
	}

	protected void repositionFromBoundingBox() {
		Box box = this.getBoundingBox();
		this.x = (box.minX + box.maxX) / 2.0;
		this.y = box.minY;
		this.z = (box.minZ + box.maxZ) / 2.0;
	}

	protected int getColorMultiplier(float f) {
		BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
		return this.world.isChunkLoaded(blockPos) ? this.world.getLightmapCoordinates(blockPos) : 0;
	}

	public boolean isAlive() {
		return !this.dead;
	}

	public Box getBoundingBox() {
		return this.boundingBox;
	}

	public void setBoundingBox(Box box) {
		this.boundingBox = box;
	}
}
