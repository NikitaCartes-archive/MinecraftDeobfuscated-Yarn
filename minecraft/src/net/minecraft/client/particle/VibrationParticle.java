package net.minecraft.client.particle;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.PositionSource;
import org.joml.Quaternionf;

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
		Quaternionf quaternionf = new Quaternionf();
		quaternionf.rotationY(g).rotateX(-h).rotateY(f);
		this.method_60373(vertexConsumer, camera, quaternionf, tickDelta);
		quaternionf.rotationY((float) -Math.PI + g).rotateX(h).rotateY(f);
		this.method_60373(vertexConsumer, camera, quaternionf, tickDelta);
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
