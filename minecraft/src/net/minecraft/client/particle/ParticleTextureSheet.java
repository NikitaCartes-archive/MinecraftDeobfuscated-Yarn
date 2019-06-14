package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;

@Environment(EnvType.CLIENT)
public interface ParticleTextureSheet {
	ParticleTextureSheet TERRAIN_SHEET = new ParticleTextureSheet() {
		@Override
		public void method_18130(BufferBuilder bufferBuilder, TextureManager textureManager) {
			GuiLighting.disable();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
			textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			bufferBuilder.method_1328(7, VertexFormats.field_1584);
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
		public void method_18130(BufferBuilder bufferBuilder, TextureManager textureManager) {
			GuiLighting.disable();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
			textureManager.bindTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEX);
			bufferBuilder.method_1328(7, VertexFormats.field_1584);
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
		public void method_18130(BufferBuilder bufferBuilder, TextureManager textureManager) {
			GuiLighting.disable();
			GlStateManager.depthMask(false);
			textureManager.bindTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEX);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.alphaFunc(516, 0.003921569F);
			bufferBuilder.method_1328(7, VertexFormats.field_1584);
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
		public void method_18130(BufferBuilder bufferBuilder, TextureManager textureManager) {
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
			textureManager.bindTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEX);
			GuiLighting.disable();
			bufferBuilder.method_1328(7, VertexFormats.field_1584);
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
		public void method_18130(BufferBuilder bufferBuilder, TextureManager textureManager) {
			GlStateManager.depthMask(true);
			GlStateManager.disableBlend();
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
		public void method_18130(BufferBuilder bufferBuilder, TextureManager textureManager) {
		}

		@Override
		public void draw(Tessellator tessellator) {
		}

		public String toString() {
			return "NO_RENDER";
		}
	};

	void method_18130(BufferBuilder bufferBuilder, TextureManager textureManager);

	void draw(Tessellator tessellator);
}
