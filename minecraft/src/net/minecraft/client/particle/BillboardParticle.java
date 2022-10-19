package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

/**
 * A {@link Particle} which renders a camera-facing sprite with a target texture scale.
 */
@Environment(EnvType.CLIENT)
public abstract class BillboardParticle extends Particle {
	protected float scale = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;

	protected BillboardParticle(ClientWorld clientWorld, double d, double e, double f) {
		super(clientWorld, d, e, f);
	}

	protected BillboardParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
		super(clientWorld, d, e, f, g, h, i);
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		Vec3d vec3d = camera.getPos();
		float f = (float)(MathHelper.lerp((double)tickDelta, this.prevPosX, this.x) - vec3d.getX());
		float g = (float)(MathHelper.lerp((double)tickDelta, this.prevPosY, this.y) - vec3d.getY());
		float h = (float)(MathHelper.lerp((double)tickDelta, this.prevPosZ, this.z) - vec3d.getZ());
		Quaternion quaternion;
		if (this.angle == 0.0F) {
			quaternion = camera.getRotation();
		} else {
			quaternion = new Quaternion(camera.getRotation());
			float i = MathHelper.lerp(tickDelta, this.prevAngle, this.angle);
			quaternion.hamiltonProduct(Vec3f.POSITIVE_Z.getRadialQuaternion(i));
		}

		Vec3f vec3f = new Vec3f(-1.0F, -1.0F, 0.0F);
		vec3f.rotate(quaternion);
		Vec3f[] vec3fs = new Vec3f[]{new Vec3f(-1.0F, -1.0F, 0.0F), new Vec3f(-1.0F, 1.0F, 0.0F), new Vec3f(1.0F, 1.0F, 0.0F), new Vec3f(1.0F, -1.0F, 0.0F)};
		float j = this.getSize(tickDelta);

		for (int k = 0; k < 4; k++) {
			Vec3f vec3f2 = vec3fs[k];
			vec3f2.rotate(quaternion);
			vec3f2.scale(j);
			vec3f2.add(f, g, h);
		}

		float l = this.getMinU();
		float m = this.getMaxU();
		float n = this.getMinV();
		float o = this.getMaxV();
		int p = this.getBrightness(tickDelta);
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

	/**
	 * {@return the draw scale of this particle, which is used while rendering in {@link #buildGeometry}}
	 */
	public float getSize(float tickDelta) {
		return this.scale;
	}

	@Override
	public Particle scale(float scale) {
		this.scale *= scale;
		return super.scale(scale);
	}

	/**
	 * {@return the lower U coordinate of the UV coordinates used to draw this particle}
	 */
	protected abstract float getMinU();

	/**
	 * {@return the upper U coordinate of the UV coordinates used to draw this particle}
	 */
	protected abstract float getMaxU();

	/**
	 * {@return the lower V coordinate of the UV coordinates used to draw this particle}
	 */
	protected abstract float getMinV();

	/**
	 * {@return the upper V coordinate of the UV coordinates used to draw this particle}
	 */
	protected abstract float getMaxV();
}
