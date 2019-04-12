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
	public void buildGeometry(BufferBuilder bufferBuilder, Camera camera, float f, float g, float h, float i, float j, float k) {
		float l = this.getSize(f);
		float m = this.getMinU();
		float n = this.getMaxU();
		float o = this.getMinV();
		float p = this.getMaxV();
		float q = (float)(MathHelper.lerp((double)f, this.prevPosX, this.x) - cameraX);
		float r = (float)(MathHelper.lerp((double)f, this.prevPosY, this.y) - cameraY);
		float s = (float)(MathHelper.lerp((double)f, this.prevPosZ, this.z) - cameraZ);
		int t = this.getColorMultiplier(f);
		int u = t >> 16 & 65535;
		int v = t & 65535;
		Vec3d[] vec3ds = new Vec3d[]{
			new Vec3d((double)(-g * l - j * l), (double)(-h * l), (double)(-i * l - k * l)),
			new Vec3d((double)(-g * l + j * l), (double)(h * l), (double)(-i * l + k * l)),
			new Vec3d((double)(g * l + j * l), (double)(h * l), (double)(i * l + k * l)),
			new Vec3d((double)(g * l - j * l), (double)(-h * l), (double)(i * l - k * l))
		};
		if (this.angle != 0.0F) {
			float w = MathHelper.lerp(f, this.prevAngle, this.angle);
			float x = MathHelper.cos(w * 0.5F);
			float y = (float)((double)MathHelper.sin(w * 0.5F) * camera.method_19335().x);
			float z = (float)((double)MathHelper.sin(w * 0.5F) * camera.method_19335().y);
			float aa = (float)((double)MathHelper.sin(w * 0.5F) * camera.method_19335().z);
			Vec3d vec3d = new Vec3d((double)y, (double)z, (double)aa);

			for (int ab = 0; ab < 4; ab++) {
				vec3ds[ab] = vec3d.multiply(2.0 * vec3ds[ab].dotProduct(vec3d))
					.add(vec3ds[ab].multiply((double)(x * x) - vec3d.dotProduct(vec3d)))
					.add(vec3d.crossProduct(vec3ds[ab]).multiply((double)(2.0F * x)));
			}
		}

		bufferBuilder.vertex((double)q + vec3ds[0].x, (double)r + vec3ds[0].y, (double)s + vec3ds[0].z)
			.texture((double)n, (double)p)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.texture(u, v)
			.next();
		bufferBuilder.vertex((double)q + vec3ds[1].x, (double)r + vec3ds[1].y, (double)s + vec3ds[1].z)
			.texture((double)n, (double)o)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.texture(u, v)
			.next();
		bufferBuilder.vertex((double)q + vec3ds[2].x, (double)r + vec3ds[2].y, (double)s + vec3ds[2].z)
			.texture((double)m, (double)o)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.texture(u, v)
			.next();
		bufferBuilder.vertex((double)q + vec3ds[3].x, (double)r + vec3ds[3].y, (double)s + vec3ds[3].z)
			.texture((double)m, (double)p)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.texture(u, v)
			.next();
	}

	public float getSize(float f) {
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
