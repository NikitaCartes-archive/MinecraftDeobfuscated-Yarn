package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class class_3940 extends Particle {
	public class_3940(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
	}

	@Override
	public void buildGeometry(BufferBuilder bufferBuilder, Entity entity, float f, float g, float h, float i, float j, float k) {
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

		float q = (float)(MathHelper.lerp((double)f, this.prevPosX, this.posX) - cameraX);
		float r = (float)(MathHelper.lerp((double)f, this.prevPosY, this.posY) - cameraY);
		float s = (float)(MathHelper.lerp((double)f, this.prevPosZ, this.posZ) - cameraZ);
		int t = this.getColorMultiplier(f);
		int u = t >> 16 & 65535;
		int v = t & 65535;
		Vec3d[] vec3ds = new Vec3d[]{
			new Vec3d((double)(-g * p - j * p), (double)(-h * p), (double)(-i * p - k * p)),
			new Vec3d((double)(-g * p + j * p), (double)(h * p), (double)(-i * p + k * p)),
			new Vec3d((double)(g * p + j * p), (double)(h * p), (double)(i * p + k * p)),
			new Vec3d((double)(g * p - j * p), (double)(-h * p), (double)(i * p - k * p))
		};
		bufferBuilder.vertex((double)q + vec3ds[0].x, (double)r + vec3ds[0].y, (double)s + vec3ds[0].z)
			.texture((double)m, (double)o)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.texture(u, v)
			.next();
		bufferBuilder.vertex((double)q + vec3ds[1].x, (double)r + vec3ds[1].y, (double)s + vec3ds[1].z)
			.texture((double)m, (double)n)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.texture(u, v)
			.next();
		bufferBuilder.vertex((double)q + vec3ds[2].x, (double)r + vec3ds[2].y, (double)s + vec3ds[2].z)
			.texture((double)l, (double)n)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.texture(u, v)
			.next();
		bufferBuilder.vertex((double)q + vec3ds[3].x, (double)r + vec3ds[3].y, (double)s + vec3ds[3].z)
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
}
