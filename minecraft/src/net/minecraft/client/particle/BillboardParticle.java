package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4588;
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
	public void buildGeometry(class_4588 arg, Camera camera, float f, float g, float h, float i, float j, float k) {
		float l = this.getSize(f);
		float m = this.getMinU();
		float n = this.getMaxU();
		float o = this.getMinV();
		float p = this.getMaxV();
		float q = (float)(MathHelper.lerp((double)f, this.prevPosX, this.x) - cameraX);
		float r = (float)(MathHelper.lerp((double)f, this.prevPosY, this.y) - cameraY);
		float s = (float)(MathHelper.lerp((double)f, this.prevPosZ, this.z) - cameraZ);
		int t = this.getColorMultiplier(f);
		Vec3d[] vec3ds = new Vec3d[]{
			new Vec3d((double)(-g * l - j * l), (double)(-h * l), (double)(-i * l - k * l)),
			new Vec3d((double)(-g * l + j * l), (double)(h * l), (double)(-i * l + k * l)),
			new Vec3d((double)(g * l + j * l), (double)(h * l), (double)(i * l + k * l)),
			new Vec3d((double)(g * l - j * l), (double)(-h * l), (double)(i * l - k * l))
		};
		if (this.angle != 0.0F) {
			float u = MathHelper.lerp(f, this.prevAngle, this.angle);
			float v = MathHelper.cos(u * 0.5F);
			float w = (float)((double)MathHelper.sin(u * 0.5F) * camera.getHorizontalPlane().x);
			float x = (float)((double)MathHelper.sin(u * 0.5F) * camera.getHorizontalPlane().y);
			float y = (float)((double)MathHelper.sin(u * 0.5F) * camera.getHorizontalPlane().z);
			Vec3d vec3d = new Vec3d((double)w, (double)x, (double)y);

			for (int z = 0; z < 4; z++) {
				vec3ds[z] = vec3d.multiply(2.0 * vec3ds[z].dotProduct(vec3d))
					.add(vec3ds[z].multiply((double)(v * v) - vec3d.dotProduct(vec3d)))
					.add(vec3d.crossProduct(vec3ds[z]).multiply((double)(2.0F * v)));
			}
		}

		arg.vertex((double)q + vec3ds[0].x, (double)r + vec3ds[0].y, (double)s + vec3ds[0].z)
			.texture(n, p)
			.method_22915(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.method_22916(t)
			.next();
		arg.vertex((double)q + vec3ds[1].x, (double)r + vec3ds[1].y, (double)s + vec3ds[1].z)
			.texture(n, o)
			.method_22915(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.method_22916(t)
			.next();
		arg.vertex((double)q + vec3ds[2].x, (double)r + vec3ds[2].y, (double)s + vec3ds[2].z)
			.texture(m, o)
			.method_22915(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.method_22916(t)
			.next();
		arg.vertex((double)q + vec3ds[3].x, (double)r + vec3ds[3].y, (double)s + vec3ds[3].z)
			.texture(m, p)
			.method_22915(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.method_22916(t)
			.next();
	}

	public float getSize(float f) {
		return this.scale;
	}

	@Override
	public Particle scale(float f) {
		this.scale *= f;
		return super.scale(f);
	}

	protected abstract float getMinU();

	protected abstract float getMaxU();

	protected abstract float getMinV();

	protected abstract float getMaxV();
}
