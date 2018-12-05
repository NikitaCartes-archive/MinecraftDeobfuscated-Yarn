package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.FactoryParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.particle.TexturedParticle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class class_661 extends Particle {
	protected class_661(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.colorRed = 1.0F;
		this.colorGreen = 1.0F;
		this.colorBlue = 1.0F;
		this.setSpriteIndex(256);
		this.maxAge = 4;
		this.gravityStrength = 0.008F;
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.velocityY = this.velocityY - (double)this.gravityStrength;
		this.addPos(this.velocityX, this.velocityY, this.velocityZ);
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			int i = this.age * 5 / this.maxAge;
			if (i <= 4) {
				this.setSpriteIndex(256 + i);
			}
		}
	}

	@Override
	public void buildGeometry(VertexBuffer vertexBuffer, Entity entity, float f, float g, float h, float i, float j, float k) {
		float l = (float)this.tileU / 32.0F;
		float m = l + 0.0624375F;
		float n = (float)this.tileV / 32.0F;
		float o = n + 0.0624375F;
		float p = 0.1F * this.size;
		if (this.sprite != null) {
			l = this.sprite.getMinU();
			m = this.sprite.getMaxU();
			n = this.sprite.getMinV();
			o = this.sprite.getMaxV();
		}

		float q = (float)(MathHelper.lerp((double)f, this.prevPosX, this.posX) - lerpX);
		float r = (float)(MathHelper.lerp((double)f, this.prevPosY, this.posY) - lerpY);
		float s = (float)(MathHelper.lerp((double)f, this.prevPosZ, this.posZ) - lerpZ);
		int t = this.getColorMultiplier(f);
		int u = t >> 16 & 65535;
		int v = t & 65535;
		Vec3d[] vec3ds = new Vec3d[]{
			new Vec3d((double)(-g * p - j * p), (double)(-h * p), (double)(-i * p - k * p)),
			new Vec3d((double)(-g * p + j * p), (double)(h * p), (double)(-i * p + k * p)),
			new Vec3d((double)(g * p + j * p), (double)(h * p), (double)(i * p + k * p)),
			new Vec3d((double)(g * p - j * p), (double)(-h * p), (double)(i * p - k * p))
		};
		vertexBuffer.vertex((double)q + vec3ds[0].x, (double)r + vec3ds[0].y, (double)s + vec3ds[0].z)
			.texture((double)m, (double)o)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.texture(u, v)
			.next();
		vertexBuffer.vertex((double)q + vec3ds[1].x, (double)r + vec3ds[1].y, (double)s + vec3ds[1].z)
			.texture((double)m, (double)n)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.texture(u, v)
			.next();
		vertexBuffer.vertex((double)q + vec3ds[2].x, (double)r + vec3ds[2].y, (double)s + vec3ds[2].z)
			.texture((double)l, (double)n)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.texture(u, v)
			.next();
		vertexBuffer.vertex((double)q + vec3ds[3].x, (double)r + vec3ds[3].y, (double)s + vec3ds[3].z)
			.texture((double)l, (double)o)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.texture(u, v)
			.next();
	}

	@Override
	public void setSpriteIndex(int i) {
		if (this.getParticleGroup() != 0) {
			throw new RuntimeException("Invalid call to Particle.setMiscTex");
		} else {
			this.tileU = 2 * i % 16;
			this.tileV = i / 16;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_662 implements FactoryParticle<TexturedParticle> {
		@Nullable
		public Particle createParticle(TexturedParticle texturedParticle, World world, double d, double e, double f, double g, double h, double i) {
			return new class_661(world, d, e, f, g, h, i);
		}
	}
}
