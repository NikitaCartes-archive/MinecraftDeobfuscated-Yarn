package net.minecraft.client.particle;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ShriekParticleEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class ShriekParticle extends SpriteBillboardParticle {
	private int field_36990;

	ShriekParticle(ClientWorld world, double x, double y, double z, int i) {
		super(world, x, y, z, 0.0, 0.0, 0.0);
		this.scale = 0.85F;
		this.field_36990 = i;
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
		if (this.field_36990 <= 0) {
			this.alpha = 1.0F - MathHelper.clamp(((float)this.age + tickDelta) / (float)this.maxAge, 0.0F, 1.0F);
			float f = 1.0472F;
			this.method_40928(vertexConsumer, camera, tickDelta, quaternion -> {
				quaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion(0.0F));
				quaternion.hamiltonProduct(Vec3f.POSITIVE_X.getRadialQuaternion(-1.0472F));
			});
			this.method_40928(vertexConsumer, camera, tickDelta, quaternion -> {
				quaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion((float) -Math.PI));
				quaternion.hamiltonProduct(Vec3f.POSITIVE_X.getRadialQuaternion(1.0472F));
			});
		}
	}

	private void method_40928(VertexConsumer vertexConsumer, Camera camera, float f, Consumer<Quaternion> consumer) {
		Vec3d vec3d = camera.getPos();
		float g = (float)(MathHelper.lerp((double)f, this.prevPosX, this.x) - vec3d.getX());
		float h = (float)(MathHelper.lerp((double)f, this.prevPosY, this.y) - vec3d.getY());
		float i = (float)(MathHelper.lerp((double)f, this.prevPosZ, this.z) - vec3d.getZ());
		Vec3f vec3f = new Vec3f(0.5F, 0.5F, 0.5F);
		vec3f.normalize();
		Quaternion quaternion = new Quaternion(vec3f, 0.0F, true);
		consumer.accept(quaternion);
		Vec3f vec3f2 = new Vec3f(-1.0F, -1.0F, 0.0F);
		vec3f2.rotate(quaternion);
		Vec3f[] vec3fs = new Vec3f[]{new Vec3f(-1.0F, -1.0F, 0.0F), new Vec3f(-1.0F, 1.0F, 0.0F), new Vec3f(1.0F, 1.0F, 0.0F), new Vec3f(1.0F, -1.0F, 0.0F)};
		float j = this.getSize(f);

		for (int k = 0; k < 4; k++) {
			Vec3f vec3f3 = vec3fs[k];
			vec3f3.rotate(quaternion);
			vec3f3.scale(j);
			vec3f3.add(g, h, i);
		}

		float l = this.getMinU();
		float m = this.getMaxU();
		float n = this.getMinV();
		float o = this.getMaxV();
		int p = this.getBrightness(f);
		vertexConsumer.vertex((double)vec3fs[0].getX(), (double)vec3fs[0].getY(), (double)vec3fs[0].getZ())
			.texture(m, o)
			.color(this.red, this.green, this.blue, this.alpha)
			.light(p)
			.next();
		vertexConsumer.vertex((double)vec3fs[1].getX(), (double)vec3fs[1].getY(), (double)vec3fs[1].getZ())
			.texture(m, n)
			.color(this.red, this.green, this.blue, this.alpha)
			.light(p)
			.next();
		vertexConsumer.vertex((double)vec3fs[2].getX(), (double)vec3fs[2].getY(), (double)vec3fs[2].getZ())
			.texture(l, n)
			.color(this.red, this.green, this.blue, this.alpha)
			.light(p)
			.next();
		vertexConsumer.vertex((double)vec3fs[3].getX(), (double)vec3fs[3].getY(), (double)vec3fs[3].getZ())
			.texture(l, o)
			.color(this.red, this.green, this.blue, this.alpha)
			.light(p)
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
		if (this.field_36990 > 0) {
			this.field_36990--;
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
