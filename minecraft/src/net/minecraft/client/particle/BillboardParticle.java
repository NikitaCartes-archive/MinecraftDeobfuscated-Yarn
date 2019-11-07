package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
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
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		Vec3d vec3d = camera.getPos();
		float f = (float)(MathHelper.lerp((double)tickDelta, this.prevPosX, this.x) - vec3d.getX());
		float g = (float)(MathHelper.lerp((double)tickDelta, this.prevPosY, this.y) - vec3d.getY());
		float h = (float)(MathHelper.lerp((double)tickDelta, this.prevPosZ, this.z) - vec3d.getZ());
		Quaternion quaternion;
		if (this.angle == 0.0F) {
			quaternion = camera.method_23767();
		} else {
			quaternion = new Quaternion(camera.method_23767());
			float i = MathHelper.lerp(tickDelta, this.prevAngle, this.angle);
			quaternion.hamiltonProduct(Vector3f.POSITIVE_Z.method_23626(i));
		}

		Vector3f vector3f = new Vector3f(-1.0F, -1.0F, 0.0F);
		vector3f.method_19262(quaternion);
		Vector3f[] vector3fs = new Vector3f[]{
			new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
		};
		float j = this.getSize(tickDelta);

		for (int k = 0; k < 4; k++) {
			Vector3f vector3f2 = vector3fs[k];
			vector3f2.method_19262(quaternion);
			vector3f2.scale(j);
			vector3f2.add(f, g, h);
		}

		float l = this.getMinU();
		float m = this.getMaxU();
		float n = this.getMinV();
		float o = this.getMaxV();
		int p = this.getColorMultiplier(tickDelta);
		vertexConsumer.vertex((double)vector3fs[0].getX(), (double)vector3fs[0].getY(), (double)vector3fs[0].getZ())
			.texture(m, o)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.light(p)
			.next();
		vertexConsumer.vertex((double)vector3fs[1].getX(), (double)vector3fs[1].getY(), (double)vector3fs[1].getZ())
			.texture(m, n)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.light(p)
			.next();
		vertexConsumer.vertex((double)vector3fs[2].getX(), (double)vector3fs[2].getY(), (double)vector3fs[2].getZ())
			.texture(l, n)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.light(p)
			.next();
		vertexConsumer.vertex((double)vector3fs[3].getX(), (double)vector3fs[3].getY(), (double)vector3fs[3].getZ())
			.texture(l, o)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.light(p)
			.next();
	}

	public float getSize(float tickDelta) {
		return this.scale;
	}

	@Override
	public Particle scale(float scale) {
		this.scale *= scale;
		return super.scale(scale);
	}

	protected abstract float getMinU();

	protected abstract float getMaxU();

	protected abstract float getMinV();

	protected abstract float getMaxV();
}
