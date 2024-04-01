package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class FootstepParticle extends Particle {
	private final Sprite sprite;
	private final float rotation;

	protected FootstepParticle(ClientWorld world, double x, double y, double z, double rotation, double d, double e, SpriteProvider spriteProvider) {
		super(world, x, y, z);
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
		this.rotation = (float)rotation;
		this.maxAge = 200;
		this.gravityStrength = 0.0F;
		this.collidesWithWorld = false;
		this.sprite = spriteProvider.getSprite(this.random);
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
		float m = this.sprite.getMinU();
		float n = this.sprite.getMaxU();
		float o = this.sprite.getMinV();
		float p = this.sprite.getMaxV();
		Matrix4f matrix4f = new Matrix4f().translation(i, j, k);
		matrix4f.rotate((float) (Math.PI / 180.0) * this.rotation, 0.0F, 1.0F, 0.0F);
		vertexConsumer.vertex(matrix4f, -0.125F, 0.0F, 0.125F).texture(m, p).color(this.red, this.green, this.blue, g).light(l).next();
		vertexConsumer.vertex(matrix4f, 0.125F, 0.0F, 0.125F).texture(n, p).color(this.red, this.green, this.blue, g).light(l).next();
		vertexConsumer.vertex(matrix4f, 0.125F, 0.0F, -0.125F).texture(n, o).color(this.red, this.green, this.blue, g).light(l).next();
		vertexConsumer.vertex(matrix4f, -0.125F, 0.0F, -0.125F).texture(m, o).color(this.red, this.green, this.blue, g).light(l).next();
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new FootstepParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		}
	}
}
