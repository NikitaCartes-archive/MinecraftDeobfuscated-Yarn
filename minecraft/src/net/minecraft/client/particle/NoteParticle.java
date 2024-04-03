package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class NoteParticle extends SpriteBillboardParticle {
	NoteParticle(ClientWorld world, double x, double y, double z, double d) {
		super(world, x, y, z, 0.0, 0.0, 0.0);
		this.velocityMultiplier = 0.66F;
		this.ascending = true;
		this.velocityX *= 0.01F;
		this.velocityY *= 0.01F;
		this.velocityZ *= 0.01F;
		this.velocityY += 0.2;
		this.red = Math.max(0.0F, MathHelper.sin(((float)d + 0.0F) * (float) (Math.PI * 2)) * 0.65F + 0.35F);
		this.green = Math.max(0.0F, MathHelper.sin(((float)d + 0.33333334F) * (float) (Math.PI * 2)) * 0.65F + 0.35F);
		this.blue = Math.max(0.0F, MathHelper.sin(((float)d + 0.6666667F) * (float) (Math.PI * 2)) * 0.65F + 0.35F);
		this.scale *= 1.5F;
		this.maxAge = 6;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public float getSize(float tickDelta) {
		return this.scale * MathHelper.clamp(((float)this.age + tickDelta) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<SimpleParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			NoteParticle noteParticle = new NoteParticle(clientWorld, d, e, f, g);
			noteParticle.setSprite(this.spriteProvider);
			return noteParticle;
		}
	}
}
