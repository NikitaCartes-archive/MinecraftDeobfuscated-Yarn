package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3999;
import net.minecraft.class_4000;
import net.minecraft.class_4001;
import net.minecraft.class_4002;
import net.minecraft.class_4003;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BubbleColumnUpParticle extends class_4003 {
	private BubbleColumnUpParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f);
		this.setBoundingBoxSpacing(0.02F, 0.02F);
		this.field_17867 = this.field_17867 * (this.random.nextFloat() * 0.6F + 0.2F);
		this.velocityX = g * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
		this.velocityY = h * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
		this.velocityZ = i * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
		this.maxAge = (int)(40.0 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.velocityY += 0.005;
		if (this.maxAge-- <= 0) {
			this.markDead();
		} else {
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.85F;
			this.velocityY *= 0.85F;
			this.velocityZ *= 0.85F;
			if (!this.world.getFluidState(new BlockPos(this.posX, this.posY, this.posZ)).matches(FluidTags.field_15517)) {
				this.markDead();
			}
		}
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17828;
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final class_4002 field_17785;

		public Factory(class_4001 arg) {
			this.field_17785 = arg.method_18137(class_4000.field_17837);
		}

		public Particle method_3012(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			BubbleColumnUpParticle bubbleColumnUpParticle = new BubbleColumnUpParticle(world, d, e, f, g, h, i);
			bubbleColumnUpParticle.method_18140(this.field_17785);
			return bubbleColumnUpParticle;
		}
	}
}
