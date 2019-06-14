package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BubblePopParticle extends SpriteBillboardParticle {
	private final SpriteProvider field_17787;

	private BubblePopParticle(World world, double d, double e, double f, double g, double h, double i, SpriteProvider spriteProvider) {
		super(world, d, e, f);
		this.field_17787 = spriteProvider;
		this.maxAge = 4;
		this.gravityStrength = 0.008F;
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
		this.setSpriteForAge(spriteProvider);
	}

	@Override
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.velocityY = this.velocityY - (double)this.gravityStrength;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			this.setSpriteForAge(this.field_17787);
		}
	}

	@Override
	public ParticleTextureSheet method_18122() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_17788;

		public Factory(SpriteProvider spriteProvider) {
			this.field_17788 = spriteProvider;
		}

		public Particle method_3016(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new BubblePopParticle(world, d, e, f, g, h, i, this.field_17788);
		}
	}
}
