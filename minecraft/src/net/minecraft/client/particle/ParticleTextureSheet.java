package net.minecraft.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
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
 * Defines rendering setup and draw logic for particles based on their requirements for depth checking, textures, and transparency.
 * 
 * <p>
 * Each {@link Particle} returns a sheet in {@link Particle#getType()}.
 * When particles are rendered, each sheet will be drawn once.
 * {@link #begin(Tessellator, TextureManager)} is first called to set up render state.
 */
@Environment(EnvType.CLIENT)
public interface ParticleTextureSheet {
	ParticleTextureSheet TERRAIN_SHEET = new ParticleTextureSheet() {
		@Override
		public BufferBuilder begin(Tessellator tessellator, TextureManager textureManager) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.depthMask(true);
			RenderSystem.setShader(GameRenderer::getParticleProgram);
			RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
			return tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
		}

		public String toString() {
			return "TERRAIN_SHEET";
		}
	};
	ParticleTextureSheet PARTICLE_SHEET_OPAQUE = new ParticleTextureSheet() {
		@Override
		public BufferBuilder begin(Tessellator tessellator, TextureManager textureManager) {
			RenderSystem.disableBlend();
			RenderSystem.depthMask(true);
			RenderSystem.setShader(GameRenderer::getParticleProgram);
			RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
			return tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
		}

		public String toString() {
			return "PARTICLE_SHEET_OPAQUE";
		}
	};
	ParticleTextureSheet PARTICLE_SHEET_TRANSLUCENT = new ParticleTextureSheet() {
		@Override
		public BufferBuilder begin(Tessellator tessellator, TextureManager textureManager) {
			RenderSystem.depthMask(true);
			RenderSystem.setShader(GameRenderer::getParticleProgram);
			RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			return tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
		}

		public String toString() {
			return "PARTICLE_SHEET_TRANSLUCENT";
		}
	};
	ParticleTextureSheet CUSTOM = new ParticleTextureSheet() {
		@Override
		public BufferBuilder begin(Tessellator tessellator, TextureManager textureManager) {
			RenderSystem.depthMask(true);
			RenderSystem.disableBlend();
			return tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
		}

		public String toString() {
			return "CUSTOM";
		}
	};
	ParticleTextureSheet NO_RENDER = new ParticleTextureSheet() {
		@Nullable
		@Override
		public BufferBuilder begin(Tessellator tessellator, TextureManager textureManager) {
			return null;
		}

		public String toString() {
			return "NO_RENDER";
		}
	};

	/**
	 * Called to set up OpenGL render state for drawing particles of a given type.
	 * 
	 * @param textureManager texture loading context
	 */
	@Nullable
	BufferBuilder begin(Tessellator tessellator, TextureManager textureManager);
}
