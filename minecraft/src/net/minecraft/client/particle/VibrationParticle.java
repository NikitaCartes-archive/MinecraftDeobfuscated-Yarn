package net.minecraft.client.particle;

import java.util.Optional;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.event.PositionSource;

@Environment(EnvType.CLIENT)
public class VibrationParticle extends SpriteBillboardParticle {
	private final PositionSource vibration;
	private float field_28250;
	private float field_28248;
	private float field_40507;
	private float field_40508;

	VibrationParticle(ClientWorld world, double x, double y, double z, PositionSource vibration, int maxAge) {
		super(world, x, y, z, 0.0, 0.0, 0.0);
		this.scale = 0.3F;
		this.vibration = vibration;
		this.maxAge = maxAge;
		Optional<Vec3d> optional = vibration.getPos(world);
		if (optional.isPresent()) {
			Vec3d vec3d = (Vec3d)optional.get();
			double d = x - vec3d.getX();
			double e = y - vec3d.getY();
			double f = z - vec3d.getZ();
			this.field_28248 = this.field_28250 = (float)MathHelper.atan2(d, f);
			this.field_40508 = this.field_40507 = (float)MathHelper.atan2(e, Math.sqrt(d * d + f * f));
		}
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		float f = MathHelper.sin(((float)this.age + tickDelta - (float) (Math.PI * 2)) * 0.05F) * 2.0F;
		float g = MathHelper.lerp(tickDelta, this.field_28248, this.field_28250);
		float h = MathHelper.lerp(tickDelta, this.field_40508, this.field_40507) + (float) (Math.PI / 2);
		this.render(vertexConsumer, camera, tickDelta, rotationQuaternion -> {
			rotationQuaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion(g));
			rotationQuaternion.hamiltonProduct(Vec3f.POSITIVE_X.getRadialQuaternion(-h));
			rotationQuaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion(f));
		});
		this.render(vertexConsumer, camera, tickDelta, rotationQuaternion -> {
			rotationQuaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion((float) -Math.PI + g));
			rotationQuaternion.hamiltonProduct(Vec3f.POSITIVE_X.getRadialQuaternion(h));
			rotationQuaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion(f));
		});
	}

	private void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta, Consumer<Quaternion> transforms) {
		Vec3d vec3d = camera.getPos();
		float f = (float)(MathHelper.lerp((double)tickDelta, this.prevPosX, this.x) - vec3d.getX());
		float g = (float)(MathHelper.lerp((double)tickDelta, this.prevPosY, this.y) - vec3d.getY());
		float h = (float)(MathHelper.lerp((double)tickDelta, this.prevPosZ, this.z) - vec3d.getZ());
		Vec3f vec3f = new Vec3f(0.5F, 0.5F, 0.5F);
		vec3f.normalize();
		Quaternion quaternion = new Quaternion(vec3f, 0.0F, true);
		transforms.accept(quaternion);
		Vec3f vec3f2 = new Vec3f(-1.0F, -1.0F, 0.0F);
		vec3f2.rotate(quaternion);
		Vec3f[] vec3fs = new Vec3f[]{new Vec3f(-1.0F, -1.0F, 0.0F), new Vec3f(-1.0F, 1.0F, 0.0F), new Vec3f(1.0F, 1.0F, 0.0F), new Vec3f(1.0F, -1.0F, 0.0F)};
		float i = this.getSize(tickDelta);

		for (int j = 0; j < 4; j++) {
			Vec3f vec3f3 = vec3fs[j];
			vec3f3.rotate(quaternion);
			vec3f3.scale(i);
			vec3f3.add(f, g, h);
		}

		float k = this.getMinU();
		float l = this.getMaxU();
		float m = this.getMinV();
		float n = this.getMaxV();
		int o = this.getBrightness(tickDelta);
		vertexConsumer.vertex((double)vec3fs[0].getX(), (double)vec3fs[0].getY(), (double)vec3fs[0].getZ())
			.texture(l, n)
			.color(this.red, this.green, this.blue, this.alpha)
			.light(o)
			.next();
		vertexConsumer.vertex((double)vec3fs[1].getX(), (double)vec3fs[1].getY(), (double)vec3fs[1].getZ())
			.texture(l, m)
			.color(this.red, this.green, this.blue, this.alpha)
			.light(o)
			.next();
		vertexConsumer.vertex((double)vec3fs[2].getX(), (double)vec3fs[2].getY(), (double)vec3fs[2].getZ())
			.texture(k, m)
			.color(this.red, this.green, this.blue, this.alpha)
			.light(o)
			.next();
		vertexConsumer.vertex((double)vec3fs[3].getX(), (double)vec3fs[3].getY(), (double)vec3fs[3].getZ())
			.texture(k, n)
			.color(this.red, this.green, this.blue, this.alpha)
			.light(o)
			.next();
	}

	@Override
	public int getBrightness(float tint) {
		return 240;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			Optional<Vec3d> optional = this.vibration.getPos(this.world);
			if (optional.isEmpty()) {
				this.markDead();
			} else {
				int i = this.maxAge - this.age;
				double d = 1.0 / (double)i;
				Vec3d vec3d = (Vec3d)optional.get();
				this.x = MathHelper.lerp(d, this.x, vec3d.getX());
				this.y = MathHelper.lerp(d, this.y, vec3d.getY());
				this.z = MathHelper.lerp(d, this.z, vec3d.getZ());
				double e = this.x - vec3d.getX();
				double f = this.y - vec3d.getY();
				double g = this.z - vec3d.getZ();
				this.field_28248 = this.field_28250;
				this.field_28250 = (float)MathHelper.atan2(e, g);
				this.field_40508 = this.field_40507;
				this.field_40507 = (float)MathHelper.atan2(f, Math.sqrt(e * e + g * g));
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<VibrationParticleEffect> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(
			VibrationParticleEffect vibrationParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i
		) {
			VibrationParticle vibrationParticle = new VibrationParticle(
				clientWorld, d, e, f, vibrationParticleEffect.getVibration(), vibrationParticleEffect.getArrivalInTicks()
			);
			vibrationParticle.setSprite(this.spriteProvider);
			vibrationParticle.setAlpha(1.0F);
			return vibrationParticle;
		}
	}
}
