package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CloudParticle extends SpriteBillboardParticle {
	private final SpriteProvider spriteProvider;

	CloudParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, SpriteProvider spriteProvider) {
		super(clientWorld, d, e, f, 0.0, 0.0, 0.0);
		this.field_28786 = 0.96F;
		this.spriteProvider = spriteProvider;
		float j = 2.5F;
		this.velocityX *= 0.1F;
		this.velocityY *= 0.1F;
		this.velocityZ *= 0.1F;
		this.velocityX += g;
		this.velocityY += h;
		this.velocityZ += i;
		float k = 1.0F - (float)(Math.random() * 0.3F);
		this.colorRed = k;
		this.colorGreen = k;
		this.colorBlue = k;
		this.scale *= 1.875F;
		int l = (int)(8.0 / (Math.random() * 0.8 + 0.3));
		this.maxAge = (int)Math.max((float)l * 2.5F, 1.0F);
		this.collidesWithWorld = false;
		this.setSpriteForAge(spriteProvider);
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public float getSize(float tickDelta) {
		return this.scale * MathHelper.clamp(((float)this.age + tickDelta) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.dead) {
			this.setSpriteForAge(this.spriteProvider);
			PlayerEntity playerEntity = this.world.getClosestPlayer(this.x, this.y, this.z, 2.0, false);
			if (playerEntity != null) {
				double d = playerEntity.getY();
				if (this.y > d) {
					this.y = this.y + (d - this.y) * 0.2;
					this.velocityY = this.velocityY + (playerEntity.getVelocity().y - this.velocityY) * 0.2;
					this.setPos(this.x, this.y, this.z);
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class CloudFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public CloudFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new CloudParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class SneezeFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public SneezeFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			Particle particle = new CloudParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
			particle.setColor(200.0F, 50.0F, 120.0F);
			particle.setColorAlpha(0.4F);
			return particle;
		}
	}
}
