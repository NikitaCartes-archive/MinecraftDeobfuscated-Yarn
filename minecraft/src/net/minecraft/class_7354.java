package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class class_7354 extends Particle {
	private final Sprite field_38662;
	private final float field_38663;

	protected class_7354(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, SpriteProvider spriteProvider) {
		super(clientWorld, d, e, f);
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
		this.field_38663 = (float)g;
		this.maxAge = 200;
		this.gravityStrength = 0.0F;
		this.collidesWithWorld = false;
		this.field_38662 = spriteProvider.getSprite(this.random);
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		float f = ((float)this.age + tickDelta) / (float)this.maxAge;
		f *= f;
		float g = 2.0F - f * 2.0F;
		g *= 0.2F;
		float h = 0.125F;
		Vec3d vec3d = camera.getPos();
		float i = (float)(this.x - vec3d.x);
		float j = (float)(this.y - vec3d.y);
		float k = (float)(this.z - vec3d.z);
		int l = this.getBrightness(tickDelta);
		float m = this.field_38662.getMinU();
		float n = this.field_38662.getMaxU();
		float o = this.field_38662.getMinV();
		float p = this.field_38662.getMaxV();
		Quaternion quaternion = Vec3f.POSITIVE_Y.getDegreesQuaternion(this.field_38663);
		Matrix4f matrix4f = Matrix4f.translate(i, j, k);
		matrix4f.multiply(quaternion);
		vertexConsumer.vertex(matrix4f, -0.125F, 0.0F, 0.125F).texture(m, p).color(this.red, this.green, this.blue, g).light(l).next();
		vertexConsumer.vertex(matrix4f, 0.125F, 0.0F, 0.125F).texture(n, p).color(this.red, this.green, this.blue, g).light(l).next();
		vertexConsumer.vertex(matrix4f, 0.125F, 0.0F, -0.125F).texture(n, o).color(this.red, this.green, this.blue, g).light(l).next();
		vertexConsumer.vertex(matrix4f, -0.125F, 0.0F, -0.125F).texture(m, o).color(this.red, this.green, this.blue, g).light(l).next();
	}

	@Environment(EnvType.CLIENT)
	public static class class_7355 implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_38664;

		public class_7355(SpriteProvider spriteProvider) {
			this.field_38664 = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new class_7354(clientWorld, d, e, f, g, h, i, this.field_38664);
		}
	}
}
