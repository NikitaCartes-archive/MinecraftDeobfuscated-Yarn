package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3940;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BubblePopParticle extends class_3940 {
	protected BubblePopParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.colorRed = 1.0F;
		this.colorGreen = 1.0F;
		this.colorBlue = 1.0F;
		this.setSpriteIndex(256);
		this.maxAge = 4;
		this.gravityStrength = 0.008F;
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
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			int i = this.age * 5 / this.maxAge;
			if (i <= 4) {
				this.setSpriteIndex(256 + i);
			}
		}
	}

	@Override
	public void setSpriteIndex(int i) {
		if (this.getParticleGroup() != 0) {
			throw new RuntimeException("Invalid call to Particle.setMiscTex");
		} else {
			this.tileU = 2 * i % 16;
			this.tileV = i / 16;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		@Nullable
		public Particle method_3016(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new BubblePopParticle(world, d, e, f, g, h, i);
		}
	}
}
