package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class DustColorTransitionParticle extends AbstractDustParticle<DustColorTransitionParticleEffect> {
	private final Vec3f field_28244;
	private final Vec3f field_28245;

	protected DustColorTransitionParticle(
		ClientWorld world,
		double d,
		double e,
		double f,
		double g,
		double h,
		double i,
		DustColorTransitionParticleEffect dustColorTransitionParticleEffect,
		SpriteProvider spriteProvider
	) {
		super(world, d, e, f, g, h, i, dustColorTransitionParticleEffect, spriteProvider);
		float j = this.random.nextFloat() * 0.4F + 0.6F;
		this.field_28244 = this.method_33073(dustColorTransitionParticleEffect.getFromColor(), j);
		this.field_28245 = this.method_33073(dustColorTransitionParticleEffect.getToColor(), j);
	}

	private Vec3f method_33073(Vec3f vec3f, float f) {
		return new Vec3f(this.method_33076(vec3f.getX(), f), this.method_33076(vec3f.getY(), f), this.method_33076(vec3f.getZ(), f));
	}

	private void method_33074(float f) {
		float g = ((float)this.age + f) / ((float)this.maxAge + 1.0F);
		Vec3f vec3f = this.field_28244.copy();
		vec3f.lerp(this.field_28245, g);
		this.colorRed = vec3f.getX();
		this.colorGreen = vec3f.getY();
		this.colorBlue = vec3f.getZ();
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		this.method_33074(tickDelta);
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
