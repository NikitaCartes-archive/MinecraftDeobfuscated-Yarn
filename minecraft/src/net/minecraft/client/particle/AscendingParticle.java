package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class AscendingParticle extends SpriteBillboardParticle {
	private final SpriteProvider spriteProvider;
	private final double ascendingAcceleration;

	protected AscendingParticle(
		World world,
		double x,
		double y,
		double z,
		float randomVelocityXMultiplier,
		float randomVelocityYMultiplier,
		float randomVelocityZMultiplier,
		double velocityX,
		double velocityY,
		double velocityZ,
		float scaleMultiplier,
		SpriteProvider spriteProvider,
		float colorMultiplier,
		int baseMaxAge,
		double ascendingAcceleration,
		boolean bl
	) {
		super(world, x, y, z, 0.0, 0.0, 0.0);
		this.ascendingAcceleration = ascendingAcceleration;
		this.spriteProvider = spriteProvider;
		this.velocityX *= (double)randomVelocityXMultiplier;
		this.velocityY *= (double)randomVelocityYMultiplier;
		this.velocityZ *= (double)randomVelocityZMultiplier;
		this.velocityX += velocityX;
		this.velocityY += velocityY;
		this.velocityZ += velocityZ;
		float f = world.random.nextFloat() * colorMultiplier;
		this.colorRed = f;
		this.colorGreen = f;
		this.colorBlue = f;
		this.scale *= 0.75F * scaleMultiplier;
		this.maxAge = (int)((double)baseMaxAge / ((double)world.random.nextFloat() * 0.8 + 0.2));
		this.maxAge = (int)((float)this.maxAge * scaleMultiplier);
		this.maxAge = Math.max(this.maxAge, 1);
		this.setSpriteForAge(spriteProvider);
		this.collidesWithWorld = bl;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public float getSize(float tickDelta) {
		return this.scale * MathHelper.clamp(((float)this.age + tickDelta) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
	}

	@Override
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.setSpriteForAge(this.spriteProvider);
			this.velocityY = this.velocityY + this.ascendingAcceleration;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			if (this.y == this.prevPosY) {
				this.velocityX *= 1.1;
				this.velocityZ *= 1.1;
			}

			this.velocityX *= 0.96F;
			this.velocityY *= 0.96F;
			this.velocityZ *= 0.96F;
			if (this.onGround) {
				this.velocityX *= 0.7F;
				this.velocityZ *= 0.7F;
			}
		}
	}
}
