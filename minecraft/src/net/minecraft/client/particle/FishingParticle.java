package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class FishingParticle extends Particle {
	protected FishingParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.velocityX *= 0.3F;
		this.velocityY = Math.random() * 0.2F + 0.1F;
		this.velocityZ *= 0.3F;
		this.colorRed = 1.0F;
		this.colorGreen = 1.0F;
		this.colorBlue = 1.0F;
		this.setSpriteIndex(19);
		this.setBoundingBoxSpacing(0.01F, 0.01F);
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.gravityStrength = 0.0F;
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.velocityY = this.velocityY - (double)this.gravityStrength;
		this.move(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.98F;
		this.velocityY *= 0.98F;
		this.velocityZ *= 0.98F;
		int i = 60 - this.maxAge;
		float f = (float)i * 0.001F;
		this.setBoundingBoxSpacing(f, f);
		this.setSpriteIndex(19 + i % 4);
		if (this.maxAge-- <= 0) {
			this.markDead();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		public Particle method_3115(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new FishingParticle(world, d, e, f, g, h, i);
		}
	}
}
