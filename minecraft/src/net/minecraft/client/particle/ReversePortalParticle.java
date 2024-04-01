package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class ReversePortalParticle extends PortalParticle {
	ReversePortalParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
		super(clientWorld, d, e, f, g, h, i);
		this.scale *= 1.5F;
		this.maxAge = (int)(Math.random() * 2.0) + 60;
	}

	@Override
	public float getSize(float tickDelta) {
		float f = 1.0F - ((float)this.age + tickDelta) / ((float)this.maxAge * 1.5F);
		return this.scale * f;
	}

	@Override
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			float f = (float)this.age / (float)this.maxAge;
			this.x = this.x + this.velocityX * (double)f;
			this.y = this.y + this.velocityY * (double)f;
			this.z = this.z + this.velocityZ * (double)f;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;
		private final float field_51169;
		private final float field_51170;
		private final float field_51171;

		public Factory(SpriteProvider spriteProvider, float f, float g, float h) {
			this.spriteProvider = spriteProvider;
			this.field_51169 = f;
			this.field_51170 = g;
			this.field_51171 = h;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			ReversePortalParticle reversePortalParticle = new ReversePortalParticle(clientWorld, d, e, f, g, h, i);
			float j = clientWorld.random.nextFloat() * 0.6F + 0.4F;
			reversePortalParticle.setColor(this.field_51169 * j, this.field_51170 * j, this.field_51171 * j);
			reversePortalParticle.setSprite(this.spriteProvider);
			return reversePortalParticle;
		}
	}
}
