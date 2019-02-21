package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4000;
import net.minecraft.class_4001;
import net.minecraft.class_4002;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class NoteParticle extends SpriteBillboardParticle {
	private NoteParticle(World world, double d, double e, double f, double g) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.velocityX *= 0.01F;
		this.velocityY *= 0.01F;
		this.velocityZ *= 0.01F;
		this.velocityY += 0.2;
		this.colorRed = MathHelper.sin(((float)g + 0.0F) * (float) (Math.PI * 2)) * 0.65F + 0.35F;
		this.colorGreen = MathHelper.sin(((float)g + 0.33333334F) * (float) (Math.PI * 2)) * 0.65F + 0.35F;
		this.colorBlue = MathHelper.sin(((float)g + 0.6666667F) * (float) (Math.PI * 2)) * 0.65F + 0.35F;
		this.field_17867 *= 1.5F;
		this.maxAge = 6;
	}

	@Override
	public ParticleTextureSheet getTextureSheet() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public float method_18132(float f) {
		return this.field_17867 * MathHelper.clamp(((float)this.age + f) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			if (this.posY == this.prevPosY) {
				this.velocityX *= 1.1;
				this.velocityZ *= 1.1;
			}

			this.velocityX *= 0.66F;
			this.velocityY *= 0.66F;
			this.velocityZ *= 0.66F;
			if (this.onGround) {
				this.velocityX *= 0.7F;
				this.velocityZ *= 0.7F;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17819;

		public Factory(class_4001 arg) {
			this.field_17819 = arg.method_18137(class_4000.field_17858);
		}

		public Particle method_3041(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			NoteParticle noteParticle = new NoteParticle(world, d, e, f, g);
			noteParticle.method_18140(this.field_17819);
			return noteParticle;
		}
	}
}
