package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class WaterSuspendParticle extends SpriteBillboardParticle {
	private WaterSuspendParticle(World world, double d, double e, double f) {
		super(world, d, e - 0.125, f);
		this.colorRed = 0.4F;
		this.colorGreen = 0.4F;
		this.colorBlue = 0.7F;
		this.setBoundingBoxSpacing(0.01F, 0.01F);
		this.scale = this.scale * (this.random.nextFloat() * 0.6F + 0.2F);
		this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public ParticleTextureSheet getTextureSheet() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void update() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.maxAge-- <= 0) {
			this.markDead();
		} else {
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			if (!this.world.getFluidState(new BlockPos(this.x, this.y, this.z)).matches(FluidTags.field_15517)) {
				this.markDead();
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class UnderwaterFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider field_17879;

		public UnderwaterFactory(SpriteProvider spriteProvider) {
			this.field_17879 = spriteProvider;
		}

		public Particle method_3104(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			WaterSuspendParticle waterSuspendParticle = new WaterSuspendParticle(world, d, e, f);
			waterSuspendParticle.setSprite(this.field_17879);
			return waterSuspendParticle;
		}
	}
}
