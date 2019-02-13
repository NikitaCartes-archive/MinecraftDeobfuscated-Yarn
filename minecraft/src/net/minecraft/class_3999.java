package net.minecraft;

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
public interface class_3999 {
	class_3999 field_17827 = new class_3999() {
		@Override
		public void method_18130(BufferBuilder bufferBuilder, TextureManager textureManager) {
			GuiLighting.disable();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
			textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR_LMAP);
		}

		@Override
		public void method_18131(Tessellator tessellator) {
			tessellator.draw();
		}

		public String toString() {
			return "TERRAIN_SHEET";
		}
	};
	class_3999 field_17828 = new class_3999() {
		@Override
		public void method_18130(BufferBuilder bufferBuilder, TextureManager textureManager) {
			GuiLighting.disable();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
			textureManager.bindTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEX);
			bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR_LMAP);
		}

		@Override
		public void method_18131(Tessellator tessellator) {
			tessellator.draw();
		}

		public String toString() {
			return "PARTICLE_SHEET_OPAQUE";
		}
	};
	class_3999 field_17829 = new class_3999() {
		@Override
		public void method_18130(BufferBuilder bufferBuilder, TextureManager textureManager) {
			GuiLighting.disable();
			GlStateManager.depthMask(false);
			textureManager.bindTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEX);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.alphaFunc(516, 0.003921569F);
			bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR_LMAP);
		}

		@Override
		public void method_18131(Tessellator tessellator) {
			tessellator.draw();
		}

		public String toString() {
			return "PARTICLE_SHEET_TRANSLUCENT";
		}
	};
	class_3999 field_17830 = new class_3999() {
		@Override
		public void method_18130(BufferBuilder bufferBuilder, TextureManager textureManager) {
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
			textureManager.bindTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEX);
			GuiLighting.disable();
			bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR_LMAP);
		}

		@Override
		public void method_18131(Tessellator tessellator) {
			tessellator.draw();
		}

		public String toString() {
			return "PARTICLE_SHEET_LIT";
		}
	};
	class_3999 field_17831 = new class_3999() {
		@Override
		public void method_18130(BufferBuilder bufferBuilder, TextureManager textureManager) {
			GlStateManager.depthMask(true);
			GlStateManager.disableBlend();
		}

		@Override
		public void method_18131(Tessellator tessellator) {
		}

		public String toString() {
			return "CUSTOM";
		}
	};
	class_3999 field_17832 = new class_3999() {
		@Override
		public void method_18130(BufferBuilder bufferBuilder, TextureManager textureManager) {
		}

		@Override
		public void method_18131(Tessellator tessellator) {
		}

		public String toString() {
			return "NO_RENDER";
		}
	};

	void method_18130(BufferBuilder bufferBuilder, TextureManager textureManager);

	void method_18131(Tessellator tessellator);
}
