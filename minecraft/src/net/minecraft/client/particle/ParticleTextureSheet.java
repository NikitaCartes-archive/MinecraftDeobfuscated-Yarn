package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;

@Environment(EnvType.CLIENT)
public interface ParticleTextureSheet {
	ParticleTextureSheet TERRAIN_SHEET = new ParticleTextureSheet() {
		@Override
		public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.depthMask(true);
			textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
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
		public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
			RenderSystem.disableBlend();
			RenderSystem.depthMask(true);
			textureManager.bindTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
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
		public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
			RenderSystem.depthMask(true);
			textureManager.bindTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.alphaFunc(516, 0.003921569F);
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
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
		public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
			RenderSystem.disableBlend();
			RenderSystem.depthMask(true);
			textureManager.bindTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
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
		public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
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
		public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
		}

		@Override
		public void draw(Tessellator tessellator) {
		}

		public String toString() {
			return "NO_RENDER";
		}
	};

	void begin(BufferBuilder bufferBuilder, TextureManager textureManager);

	void draw(Tessellator tessellator);
}
