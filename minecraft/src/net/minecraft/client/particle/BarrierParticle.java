package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemContainer;
import net.minecraft.particle.TexturedParticle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BarrierParticle extends Particle {
	protected BarrierParticle(World world, double d, double e, double f, ItemContainer itemContainer) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.setSprite(MinecraftClient.getInstance().getItemRenderer().getModelMap().getSprite(itemContainer));
		this.colorRed = 1.0F;
		this.colorGreen = 1.0F;
		this.colorBlue = 1.0F;
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
		this.gravityStrength = 0.0F;
		this.maxAge = 80;
		this.collidesWithWorld = false;
	}

	@Override
	public int getParticleGroup() {
		return 1;
	}

	@Override
	public void buildGeometry(VertexBuffer vertexBuffer, Entity entity, float f, float g, float h, float i, float j, float k) {
		float l = this.sprite.getMinU();
		float m = this.sprite.getMaxU();
		float n = this.sprite.getMinV();
		float o = this.sprite.getMaxV();
		float p = 0.5F;
		float q = (float)(MathHelper.lerp((double)f, this.prevPosX, this.posX) - lerpX);
		float r = (float)(MathHelper.lerp((double)f, this.prevPosY, this.posY) - lerpY);
		float s = (float)(MathHelper.lerp((double)f, this.prevPosZ, this.posZ) - lerpZ);
		int t = this.getColorMultiplier(f);
		int u = t >> 16 & 65535;
		int v = t & 65535;
		vertexBuffer.vertex((double)(q - g * 0.5F - j * 0.5F), (double)(r - h * 0.5F), (double)(s - i * 0.5F - k * 0.5F))
			.texture((double)m, (double)o)
			.color(this.colorRed, this.colorGreen, this.colorBlue, 1.0F)
			.texture(u, v)
			.next();
		vertexBuffer.vertex((double)(q - g * 0.5F + j * 0.5F), (double)(r + h * 0.5F), (double)(s - i * 0.5F + k * 0.5F))
			.texture((double)m, (double)n)
			.color(this.colorRed, this.colorGreen, this.colorBlue, 1.0F)
			.texture(u, v)
			.next();
		vertexBuffer.vertex((double)(q + g * 0.5F + j * 0.5F), (double)(r + h * 0.5F), (double)(s + i * 0.5F + k * 0.5F))
			.texture((double)l, (double)n)
			.color(this.colorRed, this.colorGreen, this.colorBlue, 1.0F)
			.texture(u, v)
			.next();
		vertexBuffer.vertex((double)(q + g * 0.5F - j * 0.5F), (double)(r - h * 0.5F), (double)(s + i * 0.5F - k * 0.5F))
			.texture((double)l, (double)o)
			.color(this.colorRed, this.colorGreen, this.colorBlue, 1.0F)
			.texture(u, v)
			.next();
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements FactoryParticle<TexturedParticle> {
		public Particle createParticle(TexturedParticle texturedParticle, World world, double d, double e, double f, double g, double h, double i) {
			return new BarrierParticle(world, d, e, f, Blocks.field_10499.getItem());
		}
	}
}
