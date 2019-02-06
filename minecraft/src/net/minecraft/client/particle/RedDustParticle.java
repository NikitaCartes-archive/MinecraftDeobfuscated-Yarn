package net.minecraft.client.particle;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3999;
import net.minecraft.class_4000;
import net.minecraft.class_4001;
import net.minecraft.class_4002;
import net.minecraft.class_4003;
import net.minecraft.particle.DustParticleParameters;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class RedDustParticle extends class_4003 {
	private final class_4002 field_17801;

	private RedDustParticle(World world, double d, double e, double f, double g, double h, double i, DustParticleParameters dustParticleParameters, class_4002 arg) {
		super(world, d, e, f, g, h, i);
		this.field_17801 = arg;
		this.velocityX *= 0.1F;
		this.velocityY *= 0.1F;
		this.velocityZ *= 0.1F;
		float j = (float)Math.random() * 0.4F + 0.6F;
		this.colorRed = ((float)(Math.random() * 0.2F) + 0.8F) * dustParticleParameters.getRed() * j;
		this.colorGreen = ((float)(Math.random() * 0.2F) + 0.8F) * dustParticleParameters.getGreen() * j;
		this.colorBlue = ((float)(Math.random() * 0.2F) + 0.8F) * dustParticleParameters.getBlue() * j;
		this.field_17867 = this.field_17867 * 0.75F * dustParticleParameters.getAlpha();
		int k = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.maxAge = (int)Math.max((float)k * dustParticleParameters.getAlpha(), 1.0F);
		this.method_18142(arg);
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17828;
	}

	@Override
	public float method_18132(float f) {
		return this.field_17867 * MathHelper.clamp(((float)this.age + f) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.method_18142(this.field_17801);
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			if (this.posY == this.prevPosY) {
				this.velocityX *= 1.1;
				this.velocityZ *= 1.1;
			}

			this.velocityX *= 0.96F;
			this.velocityY *= 0.96F;
			this.velocityZ *= 0.96F;
			if (this.onGround) {
				this.velocityX *= 0.7F;
				this.velocityZ *= 0.7F;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DustParticleParameters> {
		private final class_4002 field_17802;

		public Factory(class_4001 arg) {
			this.field_17802 = arg.register(Lists.<Identifier>reverse(class_4000.field_17850));
		}

		public Particle method_3022(DustParticleParameters dustParticleParameters, World world, double d, double e, double f, double g, double h, double i) {
			return new RedDustParticle(world, d, e, f, g, h, i, dustParticleParameters, this.field_17802);
		}
	}
}
