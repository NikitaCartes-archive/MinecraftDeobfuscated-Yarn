package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class FireSmokeParticle extends SpriteBillboardParticle {
	private final SpriteProvider field_17868;

	protected FireSmokeParticle(World world, double x, double y, double z, double d, double e, double f, float g, SpriteProvider spriteProvider) {
		super(world, x, y, z, 0.0, 0.0, 0.0);
		this.field_17868 = spriteProvider;
		this.velocityX *= 0.1F;
		this.velocityY *= 0.1F;
		this.velocityZ *= 0.1F;
		this.velocityX += d;
		this.velocityY += e;
		this.velocityZ += f;
		float h = (float)(Math.random() * 0.3F);
		this.colorRed = h;
		this.colorGreen = h;
		this.colorBlue = h;
		this.scale *= 0.75F * g;
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.maxAge = (int)((float)this.maxAge * g);
		this.maxAge = Math.max(this.maxAge, 1);
		this.setSpriteForAge(spriteProvider);
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
			this.setSpriteForAge(this.field_17868);
			this.velocityY += 0.004;
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

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_17869;

		public Factory(SpriteProvider spriteProvider) {
			this.field_17869 = spriteProvider;
		}

		public Particle method_3101(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new FireSmokeParticle(world, d, e, f, g, h, i, 1.0F, this.field_17869);
		}
	}
}
