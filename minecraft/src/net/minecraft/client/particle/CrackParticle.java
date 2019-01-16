package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ItemStackParticleParameters;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class CrackParticle extends Particle {
	protected CrackParticle(World world, double d, double e, double f, double g, double h, double i, ItemStack itemStack) {
		this(world, d, e, f, itemStack);
		this.velocityX *= 0.1F;
		this.velocityY *= 0.1F;
		this.velocityZ *= 0.1F;
		this.velocityX += g;
		this.velocityY += h;
		this.velocityZ += i;
	}

	protected CrackParticle(World world, double d, double e, double f, ItemStack itemStack) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.setSprite(MinecraftClient.getInstance().getItemRenderer().method_4019(itemStack, world, null).getSprite());
		this.colorRed = 1.0F;
		this.colorGreen = 1.0F;
		this.colorBlue = 1.0F;
		this.gravityStrength = 1.0F;
		this.size /= 2.0F;
	}

	@Override
	public int getParticleGroup() {
		return 1;
	}

	@Override
	public void buildGeometry(BufferBuilder bufferBuilder, Entity entity, float f, float g, float h, float i, float j, float k) {
		float l = 0.1F * this.size;
		float m = this.sprite.getU((double)(this.field_3865 / 4.0F * 16.0F));
		float n = this.sprite.getU((double)((this.field_3865 + 1.0F) / 4.0F * 16.0F));
		float o = this.sprite.getV((double)(this.field_3846 / 4.0F * 16.0F));
		float p = this.sprite.getV((double)((this.field_3846 + 1.0F) / 4.0F * 16.0F));
		float q = (float)(MathHelper.lerp((double)f, this.prevPosX, this.posX) - cameraX);
		float r = (float)(MathHelper.lerp((double)f, this.prevPosY, this.posY) - cameraY);
		float s = (float)(MathHelper.lerp((double)f, this.prevPosZ, this.posZ) - cameraZ);
		int t = this.getColorMultiplier(f);
		int u = t >> 16 & 65535;
		int v = t & 65535;
		bufferBuilder.vertex((double)(q - g * l - j * l), (double)(r - h * l), (double)(s - i * l - k * l))
			.texture((double)m, (double)p)
			.color(this.colorRed, this.colorGreen, this.colorBlue, 1.0F)
			.texture(u, v)
			.next();
		bufferBuilder.vertex((double)(q - g * l + j * l), (double)(r + h * l), (double)(s - i * l + k * l))
			.texture((double)m, (double)o)
			.color(this.colorRed, this.colorGreen, this.colorBlue, 1.0F)
			.texture(u, v)
			.next();
		bufferBuilder.vertex((double)(q + g * l + j * l), (double)(r + h * l), (double)(s + i * l + k * l))
			.texture((double)n, (double)o)
			.color(this.colorRed, this.colorGreen, this.colorBlue, 1.0F)
			.texture(u, v)
			.next();
		bufferBuilder.vertex((double)(q + g * l - j * l), (double)(r - h * l), (double)(s + i * l - k * l))
			.texture((double)n, (double)p)
			.color(this.colorRed, this.colorGreen, this.colorBlue, 1.0F)
			.texture(u, v)
			.next();
	}

	@Environment(EnvType.CLIENT)
	public static class ItemFactory implements ParticleFactory<ItemStackParticleParameters> {
		public Particle createParticle(
			ItemStackParticleParameters itemStackParticleParameters, World world, double d, double e, double f, double g, double h, double i
		) {
			return new CrackParticle(world, d, e, f, g, h, i, itemStackParticleParameters.getItemStack());
		}
	}

	@Environment(EnvType.CLIENT)
	public static class SlimeballFactory implements ParticleFactory<DefaultParticleType> {
		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new CrackParticle(world, d, e, f, new ItemStack(Items.field_8777));
		}
	}

	@Environment(EnvType.CLIENT)
	public static class SnowballFactory implements ParticleFactory<DefaultParticleType> {
		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new CrackParticle(world, d, e, f, new ItemStack(Items.field_8543));
		}
	}
}
