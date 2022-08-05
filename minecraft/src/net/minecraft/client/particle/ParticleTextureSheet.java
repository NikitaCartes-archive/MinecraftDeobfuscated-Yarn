package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;

/**
 * Defines rendering setup & draw logic for particles based on their requirements for depth checking, textures, and transparency.
 * 
 * <p>
 * Each {@link net.minecraft.client.particle.Particle} returns a {@link ParticleTextureSheet} in {@link net.minecraft.client.particle.Particle#getType()}.
 * When particles are rendered, each sheet will be drawn once.
 * {@link ParticleTextureSheet#begin(BufferBuilder, TextureManager)} is first called to set up render state, and after each particle has emitted geometry, {@link ParticleTextureSheet#draw(Tessellator)} is called to draw to a target buffer.
 */
@Environment(EnvType.CLIENT)
public interface ParticleTextureSheet {
	ParticleTextureSheet TERRAIN_SHEET = new ParticleTextureSheet() {
		@Override
		public void begin(BufferBuilder builder, TextureManager textureManager) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.depthMask(true);
			RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
			builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
		}

		@Override
		public void draw(Tessellator tessellator) {
			tessellator.draw();
		}

		public String toString() {
			return "TERRAIN_SHEET";
		}
	};
	ParticleTextureSheet PARTICLE_SHEET_OPAQUE = new ParticleTextureSheet() {
		@Override
		public void begin(BufferBuilder builder, TextureManager textureManager) {
			RenderSystem.disableBlend();
			RenderSystem.depthMask(true);
			RenderSystem.setShader(GameRenderer::getParticleShader);
			RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
			builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
		}

		@Override
		public void draw(Tessellator tessellator) {
			tessellator.draw();
		}

		public String toString() {
			return "PARTICLE_SHEET_OPAQUE";
		}
	};
	ParticleTextureSheet PARTICLE_SHEET_TRANSLUCENT = new ParticleTextureSheet() {
		@Override
		public void begin(BufferBuilder builder, TextureManager textureManager) {
			RenderSystem.depthMask(true);
			RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
			builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
		}

		@Override
		public void draw(Tessellator tessellator) {
			tessellator.draw();
		}

		public String toString() {
			return "PARTICLE_SHEET_TRANSLUCENT";
		}
	};
	ParticleTextureSheet PARTICLE_SHEET_LIT = new ParticleTextureSheet() {
		@Override
		public void begin(BufferBuilder builder, TextureManager textureManager) {
			RenderSystem.disableBlend();
			RenderSystem.depthMask(true);
			RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
			builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
		}

		@Override
		public void draw(Tessellator tessellator) {
			tessellator.draw();
		}

		public String toString() {
			return "PARTICLE_SHEET_LIT";
		}
	};
	ParticleTextureSheet CUSTOM = new ParticleTextureSheet() {
		@Override
		public void begin(BufferBuilder builder, TextureManager textureManager) {
			RenderSystem.depthMask(true);
			RenderSystem.disableBlend();
		}

		@Override
		public void draw(Tessellator tessellator) {
		}

		public String toString() {
			return "CUSTOM";
		}
	};
	ParticleTextureSheet NO_RENDER = new ParticleTextureSheet() {
		@Override
		public void begin(BufferBuilder builder, TextureManager textureManager) {
		}

		@Override
		public void draw(Tessellator tessellator) {
		}

		public String toString() {
			return "NO_RENDER";
		}
	};

	/**
	 * Called to set up OpenGL render state for drawing particles of a given type.
	 * 
	 * @param builder the buffer particles will draw to in {@link net.minecraft.client.particle.Particle#buildGeometry(VertexConsumer, Camera, float)}
	 * @param textureManager texture loading context
	 */
	void begin(BufferBuilder builder, TextureManager textureManager);

	/**
	 * Called after all particles of a {@link ParticleTextureSheet} have finished drawing.
	 * 
	 * @param tessellator the {@code Tessellator} all particles in this sheet drew to
	 */
	void draw(Tessellator tessellator);
}
