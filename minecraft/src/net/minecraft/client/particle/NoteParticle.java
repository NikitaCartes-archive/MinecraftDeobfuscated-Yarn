package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
		this.colorRed = Math.max(0.0F, MathHelper.sin(((float)g + 0.0F) * (float) (Math.PI * 2)) * 0.65F + 0.35F);
		this.colorGreen = Math.max(0.0F, MathHelper.sin(((float)g + 0.33333334F) * (float) (Math.PI * 2)) * 0.65F + 0.35F);
		this.colorBlue = Math.max(0.0F, MathHelper.sin(((float)g + 0.6666667F) * (float) (Math.PI * 2)) * 0.65F + 0.35F);
		this.scale *= 1.5F;
		this.maxAge = 6;
	}

	@Override
	public ParticleTextureSheet method_18122() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public float getSize(float f) {
		return this.scale * MathHelper.clamp(((float)this.age + f) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
	}

	@Override
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			if (this.y == this.prevPosY) {
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
		private final SpriteProvider field_17819;

		public Factory(SpriteProvider spriteProvider) {
			this.field_17819 = spriteProvider;
		}

		public Particle method_3041(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			NoteParticle noteParticle = new NoteParticle(world, d, e, f, g);
			noteParticle.setSprite(this.field_17819);
			return noteParticle;
		}
	}
}
