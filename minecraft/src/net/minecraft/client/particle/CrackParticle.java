package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticle;
import net.minecraft.particle.TexturedParticle;
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
	public void buildGeometry(VertexBuffer vertexBuffer, Entity entity, float f, float g, float h, float i, float j, float k) {
		float l = 0.1F * this.size;
		float m = this.sprite.getU((double)(this.field_3865 / 4.0F * 16.0F));
		float n = this.sprite.getU((double)((this.field_3865 + 1.0F) / 4.0F * 16.0F));
		float o = this.sprite.getV((double)(this.field_3846 / 4.0F * 16.0F));
		float p = this.sprite.getV((double)((this.field_3846 + 1.0F) / 4.0F * 16.0F));
		float q = (float)(MathHelper.lerp((double)f, this.prevPosX, this.posX) - lerpX);
		float r = (float)(MathHelper.lerp((double)f, this.prevPosY, this.posY) - lerpY);
		float s = (float)(MathHelper.lerp((double)f, this.prevPosZ, this.posZ) - lerpZ);
		int t = this.getColorMultiplier(f);
		int u = t >> 16 & 65535;
		int v = t & 65535;
		vertexBuffer.vertex((double)(q - g * l - j * l), (double)(r - h * l), (double)(s - i * l - k * l))
			.texture((double)m, (double)p)
			.color(this.colorRed, this.colorGreen, this.colorBlue, 1.0F)
			.texture(u, v)
			.next();
		vertexBuffer.vertex((double)(q - g * l + j * l), (double)(r + h * l), (double)(s - i * l + k * l))
			.texture((double)m, (double)o)
			.color(this.colorRed, this.colorGreen, this.colorBlue, 1.0F)
			.texture(u, v)
			.next();
		vertexBuffer.vertex((double)(q + g * l + j * l), (double)(r + h * l), (double)(s + i * l + k * l))
			.texture((double)n, (double)o)
			.color(this.colorRed, this.colorGreen, this.colorBlue, 1.0F)
			.texture(u, v)
			.next();
		vertexBuffer.vertex((double)(q + g * l - j * l), (double)(r - h * l), (double)(s + i * l - k * l))
			.texture((double)n, (double)p)
			.color(this.colorRed, this.colorGreen, this.colorBlue, 1.0F)
			.texture(u, v)
			.next();
	}

	@Environment(EnvType.CLIENT)
	public static class FactoryItem implements FactoryParticle<ItemStackParticle> {
		public Particle createParticle(ItemStackParticle itemStackParticle, World world, double d, double e, double f, double g, double h, double i) {
			return new CrackParticle(world, d, e, f, g, h, i, itemStackParticle.getItemStack());
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_649 implements FactoryParticle<TexturedParticle> {
		public Particle createParticle(TexturedParticle texturedParticle, World world, double d, double e, double f, double g, double h, double i) {
			return new CrackParticle(world, d, e, f, new ItemStack(Items.field_8777));
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_650 implements FactoryParticle<TexturedParticle> {
		public Particle createParticle(TexturedParticle texturedParticle, World world, double d, double e, double f, double g, double h, double i) {
			return new CrackParticle(world, d, e, f, new ItemStack(Items.field_8543));
		}
	}
}
