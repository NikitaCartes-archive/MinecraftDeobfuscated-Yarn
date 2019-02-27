package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4002;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class FireSmokeParticle extends SpriteBillboardParticle {
	private final class_4002 field_17868;

	protected FireSmokeParticle(World world, double d, double e, double f, double g, double h, double i, float j, class_4002 arg) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.field_17868 = arg;
		this.velocityX *= 0.1F;
		this.velocityY *= 0.1F;
		this.velocityZ *= 0.1F;
		this.velocityX += g;
		this.velocityY += h;
		this.velocityZ += i;
		float k = (float)(Math.random() * 0.3F);
		this.colorRed = k;
		this.colorGreen = k;
		this.colorBlue = k;
		this.scale *= 0.75F * j;
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.maxAge = (int)((float)this.maxAge * j);
		this.maxAge = Math.max(this.maxAge, 1);
		this.method_18142(arg);
	}

	@Override
	public ParticleTextureSheet getTextureSheet() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public float method_18132(float f) {
		return this.scale * MathHelper.clamp(((float)this.age + f) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.method_18142(this.field_17868);
			this.velocityY += 0.004;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			if (this.posY == this.prevPosY) {
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
		private final class_4002 field_17869;

		public Factory(class_4002 arg) {
			this.field_17869 = arg;
		}

		public Particle method_3101(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new FireSmokeParticle(world, d, e, f, g, h, i, 1.0F, this.field_17869);
		}
	}
}
