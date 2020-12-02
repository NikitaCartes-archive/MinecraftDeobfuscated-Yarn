package net.minecraft.client.particle;

import java.util.Optional;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.Vibration;

@Environment(EnvType.CLIENT)
public class VibrationParticle extends SpriteBillboardParticle {
	private final Vibration field_28249;
	private float field_28250;
	private float field_28248;

	private VibrationParticle(ClientWorld world, Vibration vibration, int maxAge) {
		super(
			world,
			(double)((float)vibration.getOrigin().getX() + 0.5F),
			(double)((float)vibration.getOrigin().getY() + 0.5F),
			(double)((float)vibration.getOrigin().getZ() + 0.5F),
			0.0,
			0.0,
			0.0
		);
		this.scale = 0.3F;
		this.field_28249 = vibration;
		this.maxAge = maxAge;
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		float f = MathHelper.sin(((float)this.age + tickDelta - (float) (Math.PI * 2)) * 0.05F) * 2.0F;
		float g = MathHelper.lerp(tickDelta, this.field_28248, this.field_28250);
		float h = 1.0472F;
		this.method_33078(vertexConsumer, camera, tickDelta, quaternion -> {
			quaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion(g));
			quaternion.hamiltonProduct(Vec3f.POSITIVE_X.getRadialQuaternion(-1.0472F));
			quaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion(f));
		});
		this.method_33078(vertexConsumer, camera, tickDelta, quaternion -> {
			quaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion((float) -Math.PI + g));
			quaternion.hamiltonProduct(Vec3f.POSITIVE_X.getRadialQuaternion(1.0472F));
			quaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion(f));
		});
	}

	private void method_33078(VertexConsumer vertexConsumer, Camera camera, float f, Consumer<Quaternion> consumer) {
		Vec3d vec3d = camera.getPos();
		float g = (float)(MathHelper.lerp((double)f, this.prevPosX, this.x) - vec3d.getX());
		float h = (float)(MathHelper.lerp((double)f, this.prevPosY, this.y) - vec3d.getY());
		float i = (float)(MathHelper.lerp((double)f, this.prevPosZ, this.z) - vec3d.getZ());
		Vec3f vec3f = new Vec3f(0.5F, 0.5F, 0.5F);
		vec3f.normalize();
		Quaternion quaternion = new Quaternion(vec3f, 0.0F, true);
		consumer.accept(quaternion);
		Vec3f vec3f2 = new Vec3f(-1.0F, -1.0F, 0.0F);
		vec3f2.rotate(quaternion);
		Vec3f[] vec3fs = new Vec3f[]{new Vec3f(-1.0F, -1.0F, 0.0F), new Vec3f(-1.0F, 1.0F, 0.0F), new Vec3f(1.0F, 1.0F, 0.0F), new Vec3f(1.0F, -1.0F, 0.0F)};
		float j = this.getSize(f);

		for (int k = 0; k < 4; k++) {
			Vec3f vec3f3 = vec3fs[k];
			vec3f3.rotate(quaternion);
			vec3f3.scale(j);
			vec3f3.add(g, h, i);
		}

		float l = this.getMinU();
		float m = this.getMaxU();
		float n = this.getMinV();
		float o = this.getMaxV();
		int p = this.getColorMultiplier(f);
		vertexConsumer.vertex((double)vec3fs[0].getX(), (double)vec3fs[0].getY(), (double)vec3fs[0].getZ())
			.texture(m, o)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.light(p)
			.next();
		vertexConsumer.vertex((double)vec3fs[1].getX(), (double)vec3fs[1].getY(), (double)vec3fs[1].getZ())
			.texture(m, n)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.light(p)
			.next();
		vertexConsumer.vertex((double)vec3fs[2].getX(), (double)vec3fs[2].getY(), (double)vec3fs[2].getZ())
			.texture(l, n)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.light(p)
			.next();
		vertexConsumer.vertex((double)vec3fs[3].getX(), (double)vec3fs[3].getY(), (double)vec3fs[3].getZ())
			.texture(l, o)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.light(p)
			.next();
	}

	@Override
	public int getColorMultiplier(float tint) {
		return 240;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		super.tick();
		Optional<BlockPos> optional = this.field_28249.getDestination().getPos(this.world);
		if (!optional.isPresent()) {
			this.markDead();
		} else {
			double d = (double)this.age / (double)this.maxAge;
			BlockPos blockPos = this.field_28249.getOrigin();
			BlockPos blockPos2 = (BlockPos)optional.get();
			this.x = MathHelper.lerp(d, (double)blockPos.getX() + 0.5, (double)blockPos2.getX() + 0.5);
			this.y = MathHelper.lerp(d, (double)blockPos.getY() + 0.5, (double)blockPos2.getY() + 0.5);
			this.z = MathHelper.lerp(d, (double)blockPos.getZ() + 0.5, (double)blockPos2.getZ() + 0.5);
			this.field_28248 = this.field_28250;
			this.field_28250 = (float)MathHelper.atan2(this.x - (double)blockPos2.getX(), this.z - (double)blockPos2.getZ());
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<VibrationParticleEffect> {
		private final SpriteProvider field_28251;

		public Factory(SpriteProvider spriteProvider) {
			this.field_28251 = spriteProvider;
		}

		public Particle createParticle(
			VibrationParticleEffect vibrationParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i
		) {
			VibrationParticle vibrationParticle = new VibrationParticle(
				clientWorld, vibrationParticleEffect.method_33125(), vibrationParticleEffect.method_33125().getArrivalInTicks()
			);
			vibrationParticle.setSprite(this.field_28251);
			vibrationParticle.setColorAlpha(1.0F);
			return vibrationParticle;
		}
	}
}
