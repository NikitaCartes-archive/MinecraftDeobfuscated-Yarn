package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class SweepAttackParticle extends Particle {
	private static final Identifier TEX = new Identifier("textures/entity/sweep.png");
	private static final VertexFormat field_3783 = new VertexFormat()
		.add(VertexFormats.POSITION_ELEMENT)
		.add(VertexFormats.UV_ELEMENT)
		.add(VertexFormats.COLOR_ELEMENT)
		.add(VertexFormats.LMAP_ELEMENT)
		.add(VertexFormats.NORMAL_ELEMENT)
		.add(VertexFormats.PADDING_ELEMENT);
	private int age_;
	private final int maxAge_;
	private final TextureManager textureManager;
	private final float field_3785;

	protected SweepAttackParticle(TextureManager textureManager, World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.textureManager = textureManager;
		this.maxAge_ = 4;
		float j = this.random.nextFloat() * 0.6F + 0.4F;
		this.colorRed = j;
		this.colorGreen = j;
		this.colorBlue = j;
		this.field_3785 = 1.0F - (float)g * 0.5F;
	}

	@Override
	public void buildGeometry(BufferBuilder bufferBuilder, Entity entity, float f, float g, float h, float i, float j, float k) {
		int l = (int)(((float)this.age_ + f) * 3.0F / (float)this.maxAge_);
		if (l <= 7) {
			this.textureManager.bindTexture(TEX);
			float m = (float)(l % 4) / 4.0F;
			float n = m + 0.24975F;
			float o = (float)(l / 2) / 2.0F;
			float p = o + 0.4995F;
			float q = 1.0F * this.field_3785;
			float r = (float)(MathHelper.lerp((double)f, this.prevPosX, this.posX) - cameraX);
			float s = (float)(MathHelper.lerp((double)f, this.prevPosY, this.posY) - cameraY);
			float t = (float)(MathHelper.lerp((double)f, this.prevPosZ, this.posZ) - cameraZ);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableLighting();
			GuiLighting.disable();
			bufferBuilder.begin(7, field_3783);
			bufferBuilder.vertex((double)(r - g * q - j * q), (double)(s - h * q * 0.5F), (double)(t - i * q - k * q))
				.texture((double)n, (double)p)
				.color(this.colorRed, this.colorGreen, this.colorBlue, 1.0F)
				.texture(0, 240)
				.normal(0.0F, 1.0F, 0.0F)
				.next();
			bufferBuilder.vertex((double)(r - g * q + j * q), (double)(s + h * q * 0.5F), (double)(t - i * q + k * q))
				.texture((double)n, (double)o)
				.color(this.colorRed, this.colorGreen, this.colorBlue, 1.0F)
				.texture(0, 240)
				.normal(0.0F, 1.0F, 0.0F)
				.next();
			bufferBuilder.vertex((double)(r + g * q + j * q), (double)(s + h * q * 0.5F), (double)(t + i * q + k * q))
				.texture((double)m, (double)o)
				.color(this.colorRed, this.colorGreen, this.colorBlue, 1.0F)
				.texture(0, 240)
				.normal(0.0F, 1.0F, 0.0F)
				.next();
			bufferBuilder.vertex((double)(r + g * q - j * q), (double)(s - h * q * 0.5F), (double)(t + i * q - k * q))
				.texture((double)m, (double)p)
				.color(this.colorRed, this.colorGreen, this.colorBlue, 1.0F)
				.texture(0, 240)
				.normal(0.0F, 1.0F, 0.0F)
				.next();
			Tessellator.getInstance().draw();
			GlStateManager.enableLighting();
		}
	}

	@Override
	public int getColorMultiplier(float f) {
		return 61680;
	}

	@Override
	public void update() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.age_++;
		if (this.age_ == this.maxAge_) {
			this.markDead();
		}
	}

	@Override
	public int getParticleGroup() {
		return 3;
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		public Particle method_3006(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new SweepAttackParticle(MinecraftClient.getInstance().getTextureManager(), world, d, e, f, g, h, i);
		}
	}
}
