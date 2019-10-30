package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class CloudParticle extends SpriteBillboardParticle {
	private final SpriteProvider field_17862;

	private CloudParticle(World world, double x, double y, double z, double d, double e, double f, SpriteProvider spriteProvider) {
		super(world, x, y, z, 0.0, 0.0, 0.0);
		this.field_17862 = spriteProvider;
		float g = 2.5F;
		this.velocityX *= 0.1F;
		this.velocityY *= 0.1F;
		this.velocityZ *= 0.1F;
		this.velocityX += d;
		this.velocityY += e;
		this.velocityZ += f;
		float h = 1.0F - (float)(Math.random() * 0.3F);
		this.colorRed = h;
		this.colorGreen = h;
		this.colorBlue = h;
		this.scale *= 1.875F;
		int i = (int)(8.0 / (Math.random() * 0.8 + 0.3));
		this.maxAge = (int)Math.max((float)i * 2.5F, 1.0F);
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
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.setSpriteForAge(this.field_17862);
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.96F;
			this.velocityY *= 0.96F;
			this.velocityZ *= 0.96F;
			PlayerEntity playerEntity = this.world.getClosestPlayer(this.x, this.y, this.z, 2.0, false);
			if (playerEntity != null) {
				double d = playerEntity.getY();
				if (this.y > d) {
					this.y = this.y + (d - this.y) * 0.2;
					this.velocityY = this.velocityY + (playerEntity.getVelocity().y - this.velocityY) * 0.2;
					this.setPos(this.x, this.y, this.z);
				}
			}

			if (this.onGround) {
				this.velocityX *= 0.7F;
				this.velocityZ *= 0.7F;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class CloudFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_17863;

		public CloudFactory(SpriteProvider spriteProvider) {
			this.field_17863 = spriteProvider;
		}

		public Particle method_3088(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new CloudParticle(world, d, e, f, g, h, i, this.field_17863);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class SneezeFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_17864;

		public SneezeFactory(SpriteProvider spriteProvider) {
			this.field_17864 = spriteProvider;
		}

		public Particle method_3089(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			Particle particle = new CloudParticle(world, d, e, f, g, h, i, this.field_17864);
			particle.setColor(200.0F, 50.0F, 120.0F);
			particle.setColorAlpha(0.4F);
			return particle;
		}
	}
}
