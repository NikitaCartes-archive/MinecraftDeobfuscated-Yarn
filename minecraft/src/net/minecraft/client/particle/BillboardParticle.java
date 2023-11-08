package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * A {@link Particle} which renders a camera-facing sprite with a target texture scale.
 */
@Environment(EnvType.CLIENT)
public abstract class BillboardParticle extends Particle {
	protected float scale;
	private final Quaternionf rotation = new Quaternionf();

	protected BillboardParticle(ClientWorld clientWorld, double d, double e, double f) {
		super(clientWorld, d, e, f);
		this.scale = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
	}

	protected BillboardParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
		super(clientWorld, d, e, f, g, h, i);
		this.scale = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
	}

	public BillboardParticle.Rotator getRotator() {
		return BillboardParticle.Rotator.ALL_AXIS;
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		Vec3d vec3d = camera.getPos();
		float f = (float)(MathHelper.lerp((double)tickDelta, this.prevPosX, this.x) - vec3d.getX());
		float g = (float)(MathHelper.lerp((double)tickDelta, this.prevPosY, this.y) - vec3d.getY());
		float h = (float)(MathHelper.lerp((double)tickDelta, this.prevPosZ, this.z) - vec3d.getZ());
		this.getRotator().setRotation(this.rotation, camera, tickDelta);
		if (this.angle != 0.0F) {
			this.rotation.rotateZ(MathHelper.lerp(tickDelta, this.prevAngle, this.angle));
		}

		Vector3f[] vector3fs = new Vector3f[]{
			new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
		};
		float i = this.getSize(tickDelta);

		for (int j = 0; j < 4; j++) {
			Vector3f vector3f = vector3fs[j];
			vector3f.rotate(this.rotation);
			vector3f.mul(i);
			vector3f.add(f, g, h);
		}

		float k = this.getMinU();
		float l = this.getMaxU();
		float m = this.getMinV();
		float n = this.getMaxV();
		int o = this.getBrightness(tickDelta);
		vertexConsumer.vertex((double)vector3fs[0].x(), (double)vector3fs[0].y(), (double)vector3fs[0].z())
			.texture(l, n)
			.color(this.red, this.green, this.blue, this.alpha)
			.light(o)
			.next();
		vertexConsumer.vertex((double)vector3fs[1].x(), (double)vector3fs[1].y(), (double)vector3fs[1].z())
			.texture(l, m)
			.color(this.red, this.green, this.blue, this.alpha)
			.light(o)
			.next();
		vertexConsumer.vertex((double)vector3fs[2].x(), (double)vector3fs[2].y(), (double)vector3fs[2].z())
			.texture(k, m)
			.color(this.red, this.green, this.blue, this.alpha)
			.light(o)
			.next();
		vertexConsumer.vertex((double)vector3fs[3].x(), (double)vector3fs[3].y(), (double)vector3fs[3].z())
			.texture(k, n)
			.color(this.red, this.green, this.blue, this.alpha)
			.light(o)
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

	@Environment(EnvType.CLIENT)
	public interface Rotator {
		BillboardParticle.Rotator ALL_AXIS = (quaternion, camera, tickDelta) -> quaternion.set(camera.getRotation());
		BillboardParticle.Rotator Y_AND_W_ONLY = (quaternion, camera, tickDelta) -> quaternion.set(0.0F, camera.getRotation().y, 0.0F, camera.getRotation().w);

		void setRotation(Quaternionf quaternion, Camera camera, float tickDelta);
	}
}
