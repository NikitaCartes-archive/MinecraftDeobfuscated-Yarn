package net.minecraft.client.particle;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3538;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class Particle {
	private static final BoundingBox EMPTY_BOUNDING_BOX = new BoundingBox(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	protected World world;
	protected double prevPosX;
	protected double prevPosY;
	protected double prevPosZ;
	protected double posX;
	protected double posY;
	protected double posZ;
	protected double velocityX;
	protected double velocityY;
	protected double velocityZ;
	private BoundingBox boundingBox = EMPTY_BOUNDING_BOX;
	protected boolean onGround;
	protected boolean collidesWithWorld;
	protected boolean dead;
	protected float spacingXZ = 0.6F;
	protected float spacingY = 1.8F;
	protected Random random = new Random();
	protected int tileU;
	protected int tileV;
	protected float field_3865;
	protected float field_3846;
	protected int age;
	protected int maxAge;
	protected float size;
	protected float gravityStrength;
	protected float colorRed;
	protected float colorGreen;
	protected float colorBlue;
	protected float colorAlpha = 1.0F;
	protected Sprite sprite;
	protected float field_3839;
	protected float field_3857;
	public static double lerpX;
	public static double lerpY;
	public static double lerpZ;
	public static Vec3d field_3864;

	protected Particle(World world, double d, double e, double f) {
		this.world = world;
		this.setBoundingBoxSpacing(0.2F, 0.2F);
		this.setPos(d, e, f);
		this.prevPosX = d;
		this.prevPosY = e;
		this.prevPosZ = f;
		this.colorRed = 1.0F;
		this.colorGreen = 1.0F;
		this.colorBlue = 1.0F;
		this.field_3865 = this.random.nextFloat() * 3.0F;
		this.field_3846 = this.random.nextFloat() * 3.0F;
		this.size = (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
		this.maxAge = (int)(4.0F / (this.random.nextFloat() * 0.9F + 0.1F));
		this.age = 0;
		this.collidesWithWorld = true;
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

	public Particle method_3075(float f) {
		this.velocityX *= (double)f;
		this.velocityY = (this.velocityY - 0.1F) * (double)f + 0.1F;
		this.velocityZ *= (double)f;
		return this;
	}

	public Particle method_3087(float f) {
		this.setBoundingBoxSpacing(0.2F * f, 0.2F * f);
		this.size *= f;
		return this;
	}

	public void setColor(float f, float g, float h) {
		this.colorRed = f;
		this.colorGreen = g;
		this.colorBlue = h;
	}

	public void setColorAlpha(float f) {
		this.colorAlpha = f;
	}

	public boolean hasAlpha() {
		return false;
	}

	public float getColorRed() {
		return this.colorRed;
	}

	public float getColorGreen() {
		return this.colorGreen;
	}

	public float getColorBlue() {
		return this.colorBlue;
	}

	public void setMaxAge(int i) {
		this.maxAge = i;
	}

	public int method_3082() {
		return this.maxAge;
	}

	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		}

		this.velocityY = this.velocityY - 0.04 * (double)this.gravityStrength;
		this.addPos(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.98F;
		this.velocityY *= 0.98F;
		this.velocityZ *= 0.98F;
		if (this.onGround) {
			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}
	}

	public void buildGeometry(VertexBuffer vertexBuffer, Entity entity, float f, float g, float h, float i, float j, float k) {
		float l = (float)this.tileU / 32.0F;
		float m = l + 0.03121875F;
		float n = (float)this.tileV / 32.0F;
		float o = n + 0.03121875F;
		float p = 0.1F * this.size;
		if (this.sprite != null) {
			l = this.sprite.getMinU();
			m = this.sprite.getMaxU();
			n = this.sprite.getMinV();
			o = this.sprite.getMaxV();
		}

		float q = (float)(MathHelper.lerp((double)f, this.prevPosX, this.posX) - lerpX);
		float r = (float)(MathHelper.lerp((double)f, this.prevPosY, this.posY) - lerpY);
		float s = (float)(MathHelper.lerp((double)f, this.prevPosZ, this.posZ) - lerpZ);
		int t = this.getColorMultiplier(f);
		int u = t >> 16 & 65535;
		int v = t & 65535;
		Vec3d[] vec3ds = new Vec3d[]{
			new Vec3d((double)(-g * p - j * p), (double)(-h * p), (double)(-i * p - k * p)),
			new Vec3d((double)(-g * p + j * p), (double)(h * p), (double)(-i * p + k * p)),
			new Vec3d((double)(g * p + j * p), (double)(h * p), (double)(i * p + k * p)),
			new Vec3d((double)(g * p - j * p), (double)(-h * p), (double)(i * p - k * p))
		};
		if (this.field_3839 != 0.0F) {
			float w = MathHelper.lerp(f, this.field_3857, this.field_3839);
			float x = MathHelper.cos(w * 0.5F);
			float y = MathHelper.sin(w * 0.5F) * (float)field_3864.x;
			float z = MathHelper.sin(w * 0.5F) * (float)field_3864.y;
			float aa = MathHelper.sin(w * 0.5F) * (float)field_3864.z;
			Vec3d vec3d = new Vec3d((double)y, (double)z, (double)aa);

			for (int ab = 0; ab < 4; ab++) {
				vec3ds[ab] = vec3d.multiply(2.0 * vec3ds[ab].dotProduct(vec3d))
					.add(vec3ds[ab].multiply((double)(x * x) - vec3d.dotProduct(vec3d)))
					.add(vec3d.crossProduct(vec3ds[ab]).multiply((double)(2.0F * x)));
			}
		}

		vertexBuffer.vertex((double)q + vec3ds[0].x, (double)r + vec3ds[0].y, (double)s + vec3ds[0].z)
			.texture((double)m, (double)o)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.texture(u, v)
			.next();
		vertexBuffer.vertex((double)q + vec3ds[1].x, (double)r + vec3ds[1].y, (double)s + vec3ds[1].z)
			.texture((double)m, (double)n)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.texture(u, v)
			.next();
		vertexBuffer.vertex((double)q + vec3ds[2].x, (double)r + vec3ds[2].y, (double)s + vec3ds[2].z)
			.texture((double)l, (double)n)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.texture(u, v)
			.next();
		vertexBuffer.vertex((double)q + vec3ds[3].x, (double)r + vec3ds[3].y, (double)s + vec3ds[3].z)
			.texture((double)l, (double)o)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.texture(u, v)
			.next();
	}

	public int getParticleGroup() {
		return 0;
	}

	public void setSprite(Sprite sprite) {
		int i = this.getParticleGroup();
		if (i == 1) {
			this.sprite = sprite;
		} else {
			throw new RuntimeException("Invalid call to Particle.setTex, use coordinate methods");
		}
	}

	public void setSpriteIndex(int i) {
		if (this.getParticleGroup() != 0) {
			throw new RuntimeException("Invalid call to Particle.setMiscTex");
		} else {
			this.tileU = i % 16;
			this.tileV = i / 16;
		}
	}

	public void incSpriteIndex() {
		this.tileU++;
	}

	public String toString() {
		return this.getClass().getSimpleName()
			+ ", Pos ("
			+ this.posX
			+ ","
			+ this.posY
			+ ","
			+ this.posZ
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
			BoundingBox boundingBox = this.getBoundingBox();
			double d = (boundingBox.minX + boundingBox.maxX - (double)f) / 2.0;
			double e = (boundingBox.minZ + boundingBox.maxZ - (double)f) / 2.0;
			this.setBoundingBox(
				new BoundingBox(d, boundingBox.minY, e, d + (double)this.spacingXZ, boundingBox.minY + (double)this.spacingY, e + (double)this.spacingXZ)
			);
		}
	}

	public void setPos(double d, double e, double f) {
		this.posX = d;
		this.posY = e;
		this.posZ = f;
		float g = this.spacingXZ / 2.0F;
		float h = this.spacingY;
		this.setBoundingBox(new BoundingBox(d - (double)g, e, f - (double)g, d + (double)g, e + (double)h, f + (double)g));
	}

	public void addPos(double d, double e, double f) {
		double g = d;
		double h = e;
		double i = f;
		if (this.collidesWithWorld && (d != 0.0 || e != 0.0 || f != 0.0)) {
			class_3538<VoxelShape> lv = new class_3538<>(this.world.method_8609(null, this.getBoundingBox(), d, e, f));
			e = VoxelShapes.method_1085(Direction.Axis.Y, this.getBoundingBox(), lv.method_15418(), e);
			this.setBoundingBox(this.getBoundingBox().offset(0.0, e, 0.0));
			d = VoxelShapes.method_1085(Direction.Axis.X, this.getBoundingBox(), lv.method_15418(), d);
			if (d != 0.0) {
				this.setBoundingBox(this.getBoundingBox().offset(d, 0.0, 0.0));
			}

			f = VoxelShapes.method_1085(Direction.Axis.Z, this.getBoundingBox(), lv.method_15418(), f);
			if (f != 0.0) {
				this.setBoundingBox(this.getBoundingBox().offset(0.0, 0.0, f));
			}
		} else {
			this.setBoundingBox(this.getBoundingBox().offset(d, e, f));
		}

		this.repositionFromBoundingBox();
		this.onGround = h != e && h < 0.0;
		if (g != d) {
			this.velocityX = 0.0;
		}

		if (i != f) {
			this.velocityZ = 0.0;
		}
	}

	protected void repositionFromBoundingBox() {
		BoundingBox boundingBox = this.getBoundingBox();
		this.posX = (boundingBox.minX + boundingBox.maxX) / 2.0;
		this.posY = boundingBox.minY;
		this.posZ = (boundingBox.minZ + boundingBox.maxZ) / 2.0;
	}

	public int getColorMultiplier(float f) {
		BlockPos blockPos = new BlockPos(this.posX, this.posY, this.posZ);
		return this.world.isBlockLoaded(blockPos) ? this.world.getLightmapIndex(blockPos, 0) : 0;
	}

	public boolean isAlive() {
		return !this.dead;
	}

	public BoundingBox getBoundingBox() {
		return this.boundingBox;
	}

	public void setBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}
}
