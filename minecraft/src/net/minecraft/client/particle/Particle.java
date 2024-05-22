package net.minecraft.client.particle;

import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

/**
 * A client-side visual effect with position, velocity, collision, and additional render properties.
 * 
 * <p>
 * Each particle is typically rendered as a camera-facing texture quad.
 * {@link net.minecraft.client.particle.SpriteBillboardParticle} provides this behavior, and most vanilla particles inherit from it.
 * 
 * <p>
 * If you would like a particle with no direct rendering effects, inherit from {@link NoRenderParticle}.
 */
@Environment(EnvType.CLIENT)
public abstract class Particle {
	private static final Box EMPTY_BOUNDING_BOX = new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	private static final double MAX_SQUARED_COLLISION_CHECK_DISTANCE = MathHelper.square(100.0);
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
	private boolean stopped;
	protected boolean dead;
	protected float spacingXZ = 0.6F;
	protected float spacingY = 1.8F;
	protected final Random random = Random.create();
	protected int age;
	protected int maxAge;
	protected float gravityStrength;
	protected float red = 1.0F;
	protected float green = 1.0F;
	protected float blue = 1.0F;
	protected float alpha = 1.0F;
	protected float angle;
	protected float prevAngle;
	protected float velocityMultiplier = 0.98F;
	protected boolean ascending = false;

	protected Particle(ClientWorld world, double x, double y, double z) {
		this.world = world;
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
		double d = (Math.random() + Math.random() + 1.0) * 0.15F;
		double e = Math.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
		this.velocityX = this.velocityX / e * d * 0.4F;
		this.velocityY = this.velocityY / e * d * 0.4F + 0.1F;
		this.velocityZ = this.velocityZ / e * d * 0.4F;
	}

	/**
	 * Multiplies this particle's current velocity by the target {@code speed} amount.
	 * 
	 * @param speed the velocity multiplier to apply to this particle
	 */
	public Particle move(float speed) {
		this.velocityX *= (double)speed;
		this.velocityY = (this.velocityY - 0.1F) * (double)speed + 0.1F;
		this.velocityZ *= (double)speed;
		return this;
	}

	/**
	 * Updates this particle's velocity to the target X, Y, and Z values.
	 * 
	 * @param velocityX the new x-velocity of this particle
	 * @param velocityY the new y-velocity of this particle
	 * @param velocityZ the new z-velocity of this particle
	 */
	public void setVelocity(double velocityX, double velocityY, double velocityZ) {
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.velocityZ = velocityZ;
	}

	/**
	 * Scales the size of this particle by the given {@code scale} amount.
	 * 
	 * @return this particle
	 * 
	 * @param scale the amount to scale this particle's size by
	 */
	public Particle scale(float scale) {
		this.setBoundingBoxSpacing(0.2F * scale, 0.2F * scale);
		return this;
	}

	/**
	 * Updates the rendering color of this particle.
	 * Each value should be between 0.0 (no channel color) and 1.0 (full channel color).
	 * 
	 * @param red the target red color to use while rendering
	 * @param green the target green color to use while rendering
	 * @param blue the target blue color to use while rendering
	 */
	public void setColor(float red, float green, float blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	/**
	 * Updates the alpha value of this particle to use while rendering.
	 * 
	 * <p>
	 * Note that a particle cannot render with transparency unless {@link Particle#getType()} is
	 * {@link ParticleTextureSheet#PARTICLE_SHEET_TRANSLUCENT}, or another sheet that supports transparency.
	 * 
	 * <p>
	 * Also note that the default particle shader (core/particle.fsh) will discard all transparent pixels below 0.1 alpha.
	 * 
	 * @param alpha the new alpha value of this particle
	 */
	protected void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	/**
	 * Sets the maximum age, in ticks, that this particle can exist for.
	 * 
	 * @param maxAge the new maximum age of this particle, in ticks
	 */
	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	/**
	 * {@return the maximum age, in ticks, of this particle}
	 * If this particle's age exceeds this value, it will be removed from the world.
	 */
	public int getMaxAge() {
		return this.maxAge;
	}

	/**
	 * Called each game tick (20 times per second), and should be used to do core particle logic, such as movement and collision.
	 */
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.velocityY = this.velocityY - 0.04 * (double)this.gravityStrength;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			if (this.ascending && this.y == this.prevPosY) {
				this.velocityX *= 1.1;
				this.velocityZ *= 1.1;
			}

			this.velocityX = this.velocityX * (double)this.velocityMultiplier;
			this.velocityY = this.velocityY * (double)this.velocityMultiplier;
			this.velocityZ = this.velocityZ * (double)this.velocityMultiplier;
			if (this.onGround) {
				this.velocityX *= 0.7F;
				this.velocityZ *= 0.7F;
			}
		}
	}

	/**
	 * Renders this particle to the given {@link VertexConsumer} buffer.
	 * 
	 * @param vertexConsumer the buffer to render to
	 * @param camera the current active game {@link Camera}
	 * @param tickDelta frame tick delta amount
	 */
	public abstract void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta);

	/**
	 * {@return the rendering category this particle is rendered under}
	 * 
	 * <p>
	 * For more information on the properties and types available to each {@code Particle}, visit {@link ParticleTextureSheet}.
	 */
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
			+ this.red
			+ ","
			+ this.green
			+ ","
			+ this.blue
			+ ","
			+ this.alpha
			+ "), Age "
			+ this.age;
	}

	/**
	 * Marks this particle as ready to be removed from the containing {@link ClientWorld}.
	 */
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

	/**
	 * Updates the position and bounding box of this particle to the target {@code x}, {@code y}, {@code z} position.
	 * 
	 * @param y the y position to move this particle to
	 * @param x the x position to move this particle to
	 * @param z the z position to move this particle to
	 */
	public void setPos(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		float f = this.spacingXZ / 2.0F;
		float g = this.spacingY;
		this.setBoundingBox(new Box(x - (double)f, y, z - (double)f, x + (double)f, y + (double)g, z + (double)f));
	}

	/**
	 * Moves this particle by the specified delta amounts, re-positioning bounding boxes and adjusting movement for collision with the world.
	 * 
	 * @param dx the delta x to move this particle by
	 * @param dz the delta z to move this particle by
	 * @param dy the delta y to move this particle by
	 */
	public void move(double dx, double dy, double dz) {
		if (!this.stopped) {
			double d = dx;
			double e = dy;
			double f = dz;
			if (this.collidesWithWorld && (dx != 0.0 || dy != 0.0 || dz != 0.0) && dx * dx + dy * dy + dz * dz < MAX_SQUARED_COLLISION_CHECK_DISTANCE) {
				Vec3d vec3d = Entity.adjustMovementForCollisions(null, new Vec3d(dx, dy, dz), this.getBoundingBox(), this.world, List.of());
				dx = vec3d.x;
				dy = vec3d.y;
				dz = vec3d.z;
			}

			if (dx != 0.0 || dy != 0.0 || dz != 0.0) {
				this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
				this.repositionFromBoundingBox();
			}

			if (Math.abs(e) >= 1.0E-5F && Math.abs(dy) < 1.0E-5F) {
				this.stopped = true;
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

	/**
	 * {@return the packed light level this particle should render at}
	 * 
	 * @see net.minecraft.client.render.LightmapTextureManager
	 */
	protected int getBrightness(float tint) {
		BlockPos blockPos = BlockPos.ofFloored(this.x, this.y, this.z);
		return this.world.isChunkLoaded(blockPos) ? WorldRenderer.getLightmapCoordinates(this.world, blockPos) : 0;
	}

	/**
	 * {@return {@code false} if this particle is finished and should be removed from the parent {@link ParticleManager}, otherwise {@code true} if the particle is still alive}
	 */
	public boolean isAlive() {
		return !this.dead;
	}

	/**
	 * {@return the bounding {@link Box} of this particle used for collision and movement logic}
	 * 
	 * <p>
	 * By default, this bounding box is automatically repositioned when a particle moves in {@link Particle#tick()}.
	 * To adjust the size of the returned box, visit {@link Particle#setBoundingBoxSpacing(float, float)}.
	 * To directly update the current bounding box, visit {@link Particle#setBoundingBox(Box)};
	 */
	public Box getBoundingBox() {
		return this.boundingBox;
	}

	public void setBoundingBox(Box boundingBox) {
		this.boundingBox = boundingBox;
	}

	/**
	 * {@return the optional group that this particle belongs to}
	 * 
	 * <p>A particle group restricts the number of particles from the group that
	 * can be rendered in a client world. If the particle does not have a group,
	 * it is not restricted.
	 */
	public Optional<ParticleGroup> getGroup() {
		return Optional.empty();
	}

	@Environment(EnvType.CLIENT)
	public static record DynamicAlpha(float startAlpha, float endAlpha, float startAtNormalizedAge, float endAtNormalizedAge) {
		public static final Particle.DynamicAlpha OPAQUE = new Particle.DynamicAlpha(1.0F, 1.0F, 0.0F, 1.0F);

		public boolean isOpaque() {
			return this.startAlpha >= 1.0F && this.endAlpha >= 1.0F;
		}

		public float getAlpha(int age, int maxAge, float tickDelta) {
			if (MathHelper.approximatelyEquals(this.startAlpha, this.endAlpha)) {
				return this.startAlpha;
			} else {
				float f = MathHelper.getLerpProgress(((float)age + tickDelta) / (float)maxAge, this.startAtNormalizedAge, this.endAtNormalizedAge);
				return MathHelper.clampedLerp(this.startAlpha, this.endAlpha, f);
			}
		}
	}
}
