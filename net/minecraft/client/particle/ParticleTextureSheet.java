/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

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
 * Defines rendering setup and draw logic for particles based on their requirements for depth checking, textures, and transparency.
 * 
 * <p>
 * Each {@link Particle} returns a sheet in {@link Particle#getType()}.
 * When particles are rendered, each sheet will be drawn once.
 * {@link #begin(BufferBuilder, TextureManager)} is first called to set up render state, and after each particle has emitted geometry, {@link #draw(Tessellator)} is called to draw to a target buffer.
 */
@Environment(value=EnvType.CLIENT)
public interface ParticleTextureSheet {
    public static final ParticleTextureSheet TERRAIN_SHEET = new ParticleTextureSheet(){

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
    public static final ParticleTextureSheet PARTICLE_SHEET_OPAQUE = new ParticleTextureSheet(){

        @Override
        public void begin(BufferBuilder builder, TextureManager textureManager) {
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.setShader(GameRenderer::getParticleProgram);
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
    public static final ParticleTextureSheet PARTICLE_SHEET_TRANSLUCENT = new ParticleTextureSheet(){

        @Override
        public void begin(BufferBuilder builder, TextureManager textureManager) {
            RenderSystem.depthMask(true);
            RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
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
    public static final ParticleTextureSheet PARTICLE_SHEET_LIT = new ParticleTextureSheet(){

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
    public static final ParticleTextureSheet CUSTOM = new ParticleTextureSheet(){

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
    public static final ParticleTextureSheet NO_RENDER = new ParticleTextureSheet(){

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
     * @param builder the buffer particles will draw to in {@link Particle#buildGeometry(VertexConsumer, Camera, float)}
     * @param textureManager texture loading context
     */
    public void begin(BufferBuilder var1, TextureManager var2);

    /**
     * Called after all particles of a sheet have finished drawing.
     * 
     * @param tessellator the {@code Tessellator} all particles in this sheet drew to
     */
    public void draw(Tessellator var1);
}

