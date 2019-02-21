package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4000;
import net.minecraft.class_4001;
import net.minecraft.class_4002;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BubblePopParticle extends SpriteBillboardParticle {
	private final class_4002 field_17787;

	private BubblePopParticle(World world, double d, double e, double f, double g, double h, double i, class_4002 arg) {
		super(world, d, e, f);
		this.field_17787 = arg;
		this.maxAge = 4;
		this.gravityStrength = 0.008F;
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
		this.method_18142(arg);
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.velocityY = this.velocityY - (double)this.gravityStrength;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			this.method_18142(this.field_17787);
		}
	}

	@Override
	public ParticleTextureSheet getTextureSheet() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17788;

		public Factory(class_4001 arg) {
			this.field_17788 = arg.register(class_4000.field_17838);
		}

		public Particle method_3016(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new BubblePopParticle(world, d, e, f, g, h, i, this.field_17788);
		}
	}
}
