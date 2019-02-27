package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4002;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class SuspendParticle extends SpriteBillboardParticle {
	private SuspendParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
		float j = this.random.nextFloat() * 0.1F + 0.2F;
		this.colorRed = j;
		this.colorGreen = j;
		this.colorBlue = j;
		this.setBoundingBoxSpacing(0.02F, 0.02F);
		this.scale = this.scale * (this.random.nextFloat() * 0.6F + 0.5F);
		this.velocityX *= 0.02F;
		this.velocityY *= 0.02F;
		this.velocityZ *= 0.02F;
		this.maxAge = (int)(20.0 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public ParticleTextureSheet getTextureSheet() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void move(double d, double e, double f) {
		this.setBoundingBox(this.getBoundingBox().offset(d, e, f));
		this.repositionFromBoundingBox();
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.maxAge-- <= 0) {
			this.markDead();
		} else {
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.99;
			this.velocityY *= 0.99;
			this.velocityZ *= 0.99;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class DolphinFactory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17881;

		public DolphinFactory(class_4002 arg) {
			this.field_17881 = arg;
		}

		public Particle method_3110(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			SuspendParticle suspendParticle = new SuspendParticle(world, d, e, f, g, h, i);
			suspendParticle.setColor(0.3F, 0.5F, 1.0F);
			suspendParticle.method_18140(this.field_17881);
			suspendParticle.setColorAlpha(1.0F - world.random.nextFloat() * 0.7F);
			suspendParticle.setMaxAge(suspendParticle.getMaxAge() / 2);
			return suspendParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class HappyVillagerFactory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17882;

		public HappyVillagerFactory(class_4002 arg) {
			this.field_17882 = arg;
		}

		public Particle method_3111(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			SuspendParticle suspendParticle = new SuspendParticle(world, d, e, f, g, h, i);
			suspendParticle.method_18140(this.field_17882);
			suspendParticle.setColor(1.0F, 1.0F, 1.0F);
			return suspendParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class MyceliumFactory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17883;

		public MyceliumFactory(class_4002 arg) {
			this.field_17883 = arg;
		}

		public Particle method_3112(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			SuspendParticle suspendParticle = new SuspendParticle(world, d, e, f, g, h, i);
			suspendParticle.method_18140(this.field_17883);
			return suspendParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_3991 implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17880;

		public class_3991(class_4002 arg) {
			this.field_17880 = arg;
		}

		public Particle method_18044(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			SuspendParticle suspendParticle = new SuspendParticle(world, d, e, f, g, h, i);
			suspendParticle.method_18140(this.field_17880);
			suspendParticle.setColor(1.0F, 1.0F, 1.0F);
			suspendParticle.setMaxAge(3 + world.getRandom().nextInt(5));
			return suspendParticle;
		}
	}
}
