package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class WaterBubbleParticle extends SpriteBillboardParticle {
	private WaterBubbleParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f);
		this.setBoundingBoxSpacing(0.02F, 0.02F);
		this.scale = this.scale * (this.random.nextFloat() * 0.6F + 0.2F);
		this.velocityX = g * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
		this.velocityY = h * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
		this.velocityZ = i * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
		this.maxAge = (int)(40.0 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		this.velocityY += 0.005;
		if (this.maxAge-- <= 0) {
			this.markDead();
		} else {
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.85F;
			this.velocityY *= 0.85F;
			this.velocityZ *= 0.85F;
			if (!this.world.getFluidState(new BlockPos(this.x, this.y, this.z)).matches(FluidTags.field_15517)) {
				this.markDead();
			}
		}
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle method_3011(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			WaterBubbleParticle waterBubbleParticle = new WaterBubbleParticle(world, d, e, f, g, h, i);
			waterBubbleParticle.setSprite(this.spriteProvider);
			return waterBubbleParticle;
		}
	}
}
