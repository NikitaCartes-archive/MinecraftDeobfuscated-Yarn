package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public abstract class BillboardParticle extends Particle {
	protected float scale = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;

	protected BillboardParticle(World world, double d, double e, double f) {
		super(world, d, e, f);
	}

	protected BillboardParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
	}

	@Override
	public void buildGeometry(BufferBuilder bufferBuilder, Camera camera, float tickDelta, float f, float g, float h, float i, float j) {
		float k = this.getSize(tickDelta);
		float l = this.getMinU();
		float m = this.getMaxU();
		float n = this.getMinV();
		float o = this.getMaxV();
		float p = (float)(MathHelper.lerp((double)tickDelta, this.prevPosX, this.x) - cameraX);
		float q = (float)(MathHelper.lerp((double)tickDelta, this.prevPosY, this.y) - cameraY);
		float r = (float)(MathHelper.lerp((double)tickDelta, this.prevPosZ, this.z) - cameraZ);
		int s = this.getColorMultiplier(tickDelta);
		int t = s >> 16 & 65535;
		int u = s & 65535;
		Vec3d[] vec3ds = new Vec3d[]{
			new Vec3d((double)(-f * k - i * k), (double)(-g * k), (double)(-h * k - j * k)),
			new Vec3d((double)(-f * k + i * k), (double)(g * k), (double)(-h * k + j * k)),
			new Vec3d((double)(f * k + i * k), (double)(g * k), (double)(h * k + j * k)),
			new Vec3d((double)(f * k - i * k), (double)(-g * k), (double)(h * k - j * k))
		};
		if (this.angle != 0.0F) {
			float v = MathHelper.lerp(tickDelta, this.prevAngle, this.angle);
			float w = MathHelper.cos(v * 0.5F);
			float x = (float)((double)MathHelper.sin(v * 0.5F) * camera.getHorizontalPlane().x);
			float y = (float)((double)MathHelper.sin(v * 0.5F) * camera.getHorizontalPlane().y);
			float z = (float)((double)MathHelper.sin(v * 0.5F) * camera.getHorizontalPlane().z);
			Vec3d vec3d = new Vec3d((double)x, (double)y, (double)z);

			for (int aa = 0; aa < 4; aa++) {
				vec3ds[aa] = vec3d.multiply(2.0 * vec3ds[aa].dotProduct(vec3d))
					.add(vec3ds[aa].multiply((double)(w * w) - vec3d.dotProduct(vec3d)))
					.add(vec3d.crossProduct(vec3ds[aa]).multiply((double)(2.0F * w)));
			}
		}

		bufferBuilder.vertex((double)p + vec3ds[0].x, (double)q + vec3ds[0].y, (double)r + vec3ds[0].z)
			.texture((double)m, (double)o)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.texture(t, u)
			.next();
		bufferBuilder.vertex((double)p + vec3ds[1].x, (double)q + vec3ds[1].y, (double)r + vec3ds[1].z)
			.texture((double)m, (double)n)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.texture(t, u)
			.next();
		bufferBuilder.vertex((double)p + vec3ds[2].x, (double)q + vec3ds[2].y, (double)r + vec3ds[2].z)
			.texture((double)l, (double)n)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.texture(t, u)
			.next();
		bufferBuilder.vertex((double)p + vec3ds[3].x, (double)q + vec3ds[3].y, (double)r + vec3ds[3].z)
			.texture((double)l, (double)o)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.texture(t, u)
			.next();
	}

	public float getSize(float tickDelta) {
		return this.scale;
	}

	@Override
	public Particle method_3087(float f) {
		this.scale *= f;
		return super.method_3087(f);
	}

	protected abstract float getMinU();

	protected abstract float getMaxU();

	protected abstract float getMinV();

	protected abstract float getMaxV();
}
