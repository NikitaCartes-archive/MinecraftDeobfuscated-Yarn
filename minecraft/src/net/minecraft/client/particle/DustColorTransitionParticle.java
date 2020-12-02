package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class DustColorTransitionParticle extends AbstractDustParticle<DustColorTransitionParticleEffect> {
	private final Vec3d field_28244;
	private final Vec3d field_28245;

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

	private Vec3d method_33073(Vec3d vec3d, float f) {
		return new Vec3d((double)this.method_33076((float)vec3d.x, f), (double)this.method_33076((float)vec3d.y, f), (double)this.method_33076((float)vec3d.z, f));
	}

	private void method_33074(float f) {
		float g = ((float)this.age + f) / ((float)this.maxAge + 1.0F);
		Vec3d vec3d = this.field_28244.method_33068(this.field_28245, (double)g);
		this.colorRed = (float)vec3d.x;
		this.colorGreen = (float)vec3d.y;
		this.colorBlue = (float)vec3d.z;
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		this.method_33074(tickDelta);
		super.buildGeometry(vertexConsumer, camera, tickDelta);
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DustColorTransitionParticleEffect> {
		private final SpriteProvider field_28246;

		public Factory(SpriteProvider spriteProvider) {
			this.field_28246 = spriteProvider;
		}

		public Particle createParticle(
			DustColorTransitionParticleEffect dustColorTransitionParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i
		) {
			return new DustColorTransitionParticle(clientWorld, d, e, f, g, h, i, dustColorTransitionParticleEffect, this.field_28246);
		}
	}
}
