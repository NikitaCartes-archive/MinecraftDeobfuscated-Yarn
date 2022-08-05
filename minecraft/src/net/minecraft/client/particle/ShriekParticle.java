package net.minecraft.client.particle;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ShriekParticleEffect;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class ShriekParticle extends SpriteBillboardParticle {
	private static final Vec3f field_38334 = Util.make(new Vec3f(0.5F, 0.5F, 0.5F), Vec3f::normalize);
	private static final Vec3f field_38335 = new Vec3f(-1.0F, -1.0F, 0.0F);
	private static final float X_ROTATION = 1.0472F;
	private int delay;

	ShriekParticle(ClientWorld world, double x, double y, double z, int delay) {
		super(world, x, y, z, 0.0, 0.0, 0.0);
		this.scale = 0.85F;
		this.delay = delay;
		this.maxAge = 30;
		this.gravityStrength = 0.0F;
		this.velocityX = 0.0;
		this.velocityY = 0.1;
		this.velocityZ = 0.0;
	}

	@Override
	public float getSize(float tickDelta) {
		return this.scale * MathHelper.clamp(((float)this.age + tickDelta) / (float)this.maxAge * 0.75F, 0.0F, 1.0F);
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		if (this.delay <= 0) {
			this.alpha = 1.0F - MathHelper.clamp(((float)this.age + tickDelta) / (float)this.maxAge, 0.0F, 1.0F);
			this.buildGeometry(vertexConsumer, camera, tickDelta, quaternion -> {
				quaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion(0.0F));
				quaternion.hamiltonProduct(Vec3f.POSITIVE_X.getRadialQuaternion(-1.0472F));
			});
			this.buildGeometry(vertexConsumer, camera, tickDelta, quaternion -> {
				quaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion((float) -Math.PI));
				quaternion.hamiltonProduct(Vec3f.POSITIVE_X.getRadialQuaternion(1.0472F));
			});
		}
	}

	private void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta, Consumer<Quaternion> rotator) {
		Vec3d vec3d = camera.getPos();
		float f = (float)(MathHelper.lerp((double)tickDelta, this.prevPosX, this.x) - vec3d.getX());
		float g = (float)(MathHelper.lerp((double)tickDelta, this.prevPosY, this.y) - vec3d.getY());
		float h = (float)(MathHelper.lerp((double)tickDelta, this.prevPosZ, this.z) - vec3d.getZ());
		Quaternion quaternion = new Quaternion(field_38334, 0.0F, true);
		rotator.accept(quaternion);
		field_38335.rotate(quaternion);
		Vec3f[] vec3fs = new Vec3f[]{new Vec3f(-1.0F, -1.0F, 0.0F), new Vec3f(-1.0F, 1.0F, 0.0F), new Vec3f(1.0F, 1.0F, 0.0F), new Vec3f(1.0F, -1.0F, 0.0F)};
		float i = this.getSize(tickDelta);

		for (int j = 0; j < 4; j++) {
			Vec3f vec3f = vec3fs[j];
			vec3f.rotate(quaternion);
			vec3f.scale(i);
			vec3f.add(f, g, h);
		}

		int j = this.getBrightness(tickDelta);
		this.vertex(vertexConsumer, vec3fs[0], this.getMaxU(), this.getMaxV(), j);
		this.vertex(vertexConsumer, vec3fs[1], this.getMaxU(), this.getMinV(), j);
		this.vertex(vertexConsumer, vec3fs[2], this.getMinU(), this.getMinV(), j);
		this.vertex(vertexConsumer, vec3fs[3], this.getMinU(), this.getMaxV(), j);
	}

	private void vertex(VertexConsumer vertexConsumer, Vec3f pos, float u, float v, int light) {
		vertexConsumer.vertex((double)pos.getX(), (double)pos.getY(), (double)pos.getZ())
			.texture(u, v)
			.color(this.red, this.green, this.blue, this.alpha)
			.light(light)
			.next();
	}

	@Override
	public int getBrightness(float tint) {
		return 240;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		if (this.delay > 0) {
			this.delay--;
		} else {
			super.tick();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<ShriekParticleEffect> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(ShriekParticleEffect shriekParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			ShriekParticle shriekParticle = new ShriekParticle(clientWorld, d, e, f, shriekParticleEffect.getDelay());
			shriekParticle.setSprite(this.spriteProvider);
			shriekParticle.setAlpha(1.0F);
			return shriekParticle;
		}
	}
}
