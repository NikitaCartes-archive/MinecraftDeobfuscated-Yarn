package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class DustColorTransitionParticle extends AbstractDustParticle<DustColorTransitionParticleEffect> {
	private final Vector3f startColor;
	private final Vector3f endColor;

	protected DustColorTransitionParticle(
		ClientWorld world,
		double x,
		double y,
		double z,
		double velocityX,
		double velocityY,
		double velocityZ,
		DustColorTransitionParticleEffect parameters,
		SpriteProvider spriteProvider
	) {
		super(world, x, y, z, velocityX, velocityY, velocityZ, parameters, spriteProvider);
		float f = this.random.nextFloat() * 0.4F + 0.6F;
		this.startColor = this.darken(parameters.getFromColor(), f);
		this.endColor = this.darken(parameters.getToColor(), f);
	}

	private Vector3f darken(Vector3f color, float multiplier) {
		return new Vector3f(this.darken(color.x(), multiplier), this.darken(color.y(), multiplier), this.darken(color.z(), multiplier));
	}

	private void updateColor(float tickDelta) {
		float f = ((float)this.age + tickDelta) / ((float)this.maxAge + 1.0F);
		Vector3f vector3f = new Vector3f(this.startColor).lerp(this.endColor, f);
		this.red = vector3f.x();
		this.green = vector3f.y();
		this.blue = vector3f.z();
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		this.updateColor(tickDelta);
		super.buildGeometry(vertexConsumer, camera, tickDelta);
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DustColorTransitionParticleEffect> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(
			DustColorTransitionParticleEffect dustColorTransitionParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i
		) {
			return new DustColorTransitionParticle(clientWorld, d, e, f, g, h, i, dustColorTransitionParticleEffect, this.spriteProvider);
		}
	}
}
