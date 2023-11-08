package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class GustDustParticle extends SpriteBillboardParticle {
	private final Vector3f START_COLOR = new Vector3f(0.5F, 0.5F, 0.5F);
	private final Vector3f END_COLOR = new Vector3f(1.0F, 1.0F, 1.0F);

	GustDustParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
		super(clientWorld, d, e, f);
		this.collidesWithWorld = false;
		this.velocityX = g + (double)MathHelper.nextBetween(this.random, -0.4F, 0.4F);
		this.velocityZ = i + (double)MathHelper.nextBetween(this.random, -0.4F, 0.4F);
		double j = Math.random() * 2.0;
		double k = Math.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
		this.velocityX = this.velocityX / k * j * 0.4F;
		this.velocityZ = this.velocityZ / k * j * 0.4F;
		this.scale *= 2.5F;
		this.velocityX *= 0.08F;
		this.velocityZ *= 0.08F;
		this.maxAge = 18 + this.random.nextInt(4);
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		this.lerpColor(tickDelta);
		super.buildGeometry(vertexConsumer, camera, tickDelta);
	}

	private void lerpColor(float tickDelta) {
		float f = ((float)this.age + tickDelta) / (float)(this.maxAge + 1);
		Vector3f vector3f = new Vector3f(this.START_COLOR).lerp(this.END_COLOR, f);
		this.red = vector3f.x();
		this.green = vector3f.y();
		this.blue = vector3f.z();
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.prevPosX = this.x;
			this.prevPosZ = this.z;
			this.move(this.velocityX, 0.0, this.velocityZ);
			this.velocityX *= 0.99;
			this.velocityZ *= 0.99;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			GustDustParticle gustDustParticle = new GustDustParticle(clientWorld, d, e, f, g, h, i);
			gustDustParticle.setSprite(this.spriteProvider);
			return gustDustParticle;
		}
	}
}
